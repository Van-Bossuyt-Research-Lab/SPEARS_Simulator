package com.csm.rover.simulator.platforms.rover.autoCode;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.rover.TerrainEnvironment;
import com.csm.rover.simulator.objects.io.DatedFileAppenderImpl;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;
import com.csm.rover.simulator.platforms.rover.RoverWheels;
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

@AutonomousCodeModel(type="Rover", name="parent", parameters = {})
public abstract class RoverAutonomousCode extends PlatformAutonomousCodeModel {
	private static final Logger LOG = LogManager.getLogger(RoverAutonomousCode.class);

	protected TerrainEnvironment environment;

	private String name;
	private String roverName;
	
	private File logFile;
	
	public RoverAutonomousCode(String name, String rover){
        super("Rover");
		this.name = name;
		roverName = rover;
	}

	@Override
	public void setEnvironment(PlatformEnvironment enviro){
		if (enviro.getType().equals(platform_type)){
			environment = (TerrainEnvironment)enviro;
		}
		else {
			throw new IllegalArgumentException("The given environment has the wrong type: " + enviro.getType());
		}
	}

    @Override
    public String nextCommand(long millitime, final PlatformState state){
        if (!state.getType().equals("Rover")){
            throw new IllegalArgumentException("The provided state is not a RoverState");
        }
        return doNextCommand(millitime, new DecimalPoint(state.<Double>get("x"), state.<Double>get("y")),
                state.<Double>get("direction"), getAutonomousParameters(state));
    }

    protected abstract String doNextCommand(long milliTime, DecimalPoint location,
                                          double direction, Map<String, Double> params);

    public void setRoverName(String name){
		roverName = name;
	}
	
	public String getName(){
		return name;
	}
	
	private boolean tried = false;
	protected void writeToLog(String message){
		try {
			BufferedWriter write = new BufferedWriter(new FileWriter(logFile, true));
			write.write(message + "\t\t" + new DateTime().toString(DateTimeFormat.forPattern("[MM/dd/yyyy hh:mm:ss.SS]\r\n")));
			write.flush();
			write.close();
		}
		catch (NullPointerException e){
			if (!tried){
				tried = true;
				logFile = new File(generateFilepath());
                logFile.getParentFile().mkdirs();
				LOG.log(Level.INFO, "Writing rover {}'s autonomous log file to: {}", roverName, logFile.getAbsolutePath());
				writeToLog(message);
			}
			else {
                LOG.log(Level.ERROR, "Rover " + roverName + "'s autonomous log file failed to initialize.", e);
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

    private Map<String, Double> getAutonomousParameters(PlatformState state){
        String[] required = new String[] { "acceleration", "angular_acceleration", "battery_voltage", "battery_current",
                "battery_temp", "battery_charge" };
        String[] fromLists = new String[] { "wheel_speed", "motor_current", "motor_temp" };
        Map<String, Double> params = new TreeMap<>();
        for (String param : required){
            params.put(param, state.<Double>get(param));
        }
        for (String param : fromLists){
            Double[] list = state.get(param);
            for (int i = 0; i < list.length; i++){
                for (RoverWheels wheel : RoverWheels.values()){
                    if (wheel.getValue() == i && wheel.name().length() < 4){
                        params.put(param+"_"+wheel.name(), list[i]);
                        break;
                    }
                }
            }
        }
        return params;
    }

	private String generateFilepath(){
		DateTime date = new DateTime();
		return String.format("%s/%s_%s.log", DatedFileAppenderImpl.Log_File_Name, roverName, date.toString(DateTimeFormat.forPattern("MM-dd-yyyy_HH.mm")));
	}

}
