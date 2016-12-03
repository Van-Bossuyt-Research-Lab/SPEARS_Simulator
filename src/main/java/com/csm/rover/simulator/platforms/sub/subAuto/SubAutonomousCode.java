package com.csm.rover.simulator.platforms.sub.subAuto;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.sub.AquaticEnvironment;
import com.csm.rover.simulator.objects.DatedFileAppenderImpl;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;
import com.csm.rover.simulator.platforms.sub.SubProp;
import com.csm.rover.simulator.wrapper.Globals;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@AutonomousCodeModel(type = "Sub", name = "Default", parameters = {})
public abstract class SubAutonomousCode extends PlatformAutonomousCodeModel {
    private static final Logger LOG = LogManager.getLogger(SubAutonomousCode.class);

    protected AquaticEnvironment environment;

    private String name;
    private String subName;

    private File logFile;

    public SubAutonomousCode(String name, String sub){
        super("Sub");
        this.name = name;
        subName = sub;
    }
    @Override
    public final String nextCommand(long millitime, final PlatformState state){
        if (!state.getType().equals("Sub")){
            throw new IllegalArgumentException("The provided state is not a SubState");
        }
        return doNextCommand(millitime, new DecimalPoint(state.<Double>get("x"), state.<Double>get("y")),
                state.<Double>get("pitch"), getAutonomousParameters(state));
    }

    protected abstract String doNextCommand(long milliTime, DecimalPoint location,
                                            double direction, Map<String, Double> params);

    @Override
    public void setEnvironment(PlatformEnvironment environment){
        if (environment.getType().equals(platform_type)){
            this.environment = (AquaticEnvironment)environment;
        }
        else {
            throw new IllegalArgumentException("The given platform has the wrong type: " + environment.getType());
        }
    }

    public void setSubName(String name){
        subName = name;
    }

    public String getName(){
        return name;
    }

    private boolean tried = false;
    protected void writeToLog(String message){
        try {
            BufferedWriter write = new BufferedWriter(new FileWriter(logFile, true));
            write.write(message + "\t\t" + new DateTime().toString(DateTimeFormat.forPattern("[MM/dd/yyyy hh:mm:ss.")) + (Globals.getInstance().timeMillis %1000) + "]\r\n");
            write.flush();
            write.close();
        }
        catch (NullPointerException e){
            if (!tried){
                tried = true;
                logFile = new File(generateFilepath());
                logFile.getParentFile().mkdirs();
                LOG.log(Level.INFO, "Writing sub {}'s autonomous log file to: {}", subName, logFile.getAbsolutePath());
                writeToLog(message);
            }
            else {
                LOG.log(Level.ERROR, "Sub " + subName + "'s autonomous log file failed to initialize.", e);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private Map<String, Double> getAutonomousParameters(PlatformState state){
        String[] required = new String[] { "acceleration", "pitch_acceleration"};
        String[] fromLists = new String[] { "prop_speed", "motor_current", "motor_temp" };
        Map<String, Double> params = new TreeMap<>();
        for (String param : required){
            params.put(param, state.<Double>get(param));
        }
        for (String param : fromLists){
            Double[] list = state.get(param);
            for (int i = 0; i < list.length; i++){
                for (SubProp prop : SubProp.values()){
                    if (prop.getValue() == i && prop.name().length() < 4){
                        params.put(param+"_"+prop.name(), list[i]);
                        break;
                    }
                }
            }
        }
        return params;
    }

    private String generateFilepath(){
        DateTime date = new DateTime();
        return String.format("%s/%s_%s.log", DatedFileAppenderImpl.Log_File_Name, subName, date.toString(DateTimeFormat.forPattern("MM-dd-yyyy_HH.mm")));
    }

}