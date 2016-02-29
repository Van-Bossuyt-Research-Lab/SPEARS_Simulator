package com.csm.rover.simulator.platforms.rover.autoCode;

import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.DatedFileAppenderImpl;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;
import com.csm.rover.simulator.wrapper.Globals;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

@AutonomousCodeModel(type="Rover", name="parent")
public abstract class RoverAutonomousCode extends PlatformAutonomousCodeModel implements Serializable {
	private static final Logger LOG = LogManager.getLogger(RoverAutonomousCode.class);

	private static final long serialVersionUID = 1L;

	protected static TerrainMap MAP;

	private String name;
	private String roverName;
	
	private File logFile;
	
	public RoverAutonomousCode(String name, String rover){
        super("Rover");
		this.name = name;
		roverName = rover;
	}

    @Override
    public final String nextCommand(long millitime, final PlatformState state){
        if (!state.getType().equals("Rover")){
            throw new IllegalArgumentException("The provided state is not a RoverState");
        }
        return doNextCommand(millitime, new DecimalPoint(state.get("x"), state.get("y")), state.get("direction"),
                getAutonomousParameters(state));
    }

    protected abstract String doNextCommand(long milliTime, DecimalPoint location,
                                          double direction, Map<String, Double> parameters);

    public static void setTerrainMap(TerrainMap map){
        MAP = map;
    }
	
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
			write.write(message + "\t\t" + new DateTime().toString(DateTimeFormat.forPattern("[MM/dd/yyyy hh:mm:ss.")) + (Globals.getInstance().timeMillis %1000) + "]\r\n");
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
        String[] required = new String[] { "acceleration", "angular_acceleration", "wheel_speed_FL", "wheel_speed_FR",
                "wheel_speed_BL", "wheel_speed_BR", "motor_current_FL", "motor_current_FR", "motor_current_BL",
                "motor_current_BR", "motor_temp_FL", "motor_temp_FR", "motor_temp_BL", "motor_temp_BR",
                "battery_voltage", "battery_current", "battery_temp", "battery_charge" };
        Map<String, Double> params = new TreeMap<>();
        for (String param : required){
            params.put(param, state.get(param));
        }
        return params;
    }

	private String generateFilepath(){
		DateTime date = new DateTime();
		return String.format("%s/%s_%s.log", DatedFileAppenderImpl.Log_File_Name, roverName, date.toString(DateTimeFormat.forPattern("MM-dd-yyyy_HH.mm")));
	}

}
