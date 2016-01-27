package com.csm.rover.simulator.rover.autoCode;

import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.DatedFileAppenderImpl;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.wrapper.Globals;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.*;
import java.util.Map;

public abstract class RoverAutonomousCode implements Serializable, Cloneable {
	private static final Logger LOG = LogManager.getLogger(RoverAutonomousCode.class);

	private static final long serialVersionUID = 1L;

	protected static TerrainMap MAP;

	private String name;
	private String roverName;
	
	private File logFile;
	
	public RoverAutonomousCode(String name, String rover){
		this.name = name;
		roverName = rover;
	}

    public static void setTerrainMap(TerrainMap map){
        MAP = map;
    }
	
	public RoverAutonomousCode(RoverAutonomousCode rac){
		this.name = rac.name;
		this.roverName = rac.roverName;
	}
	
	abstract public String nextCommand(
			long milliTime,
			DecimalPoint location,
			double direction,
			Map<String, Double> parameters
	);
	
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

	private String generateFilepath(){
		DateTime date = new DateTime();
		return String.format("%s/%s_%s.log", DatedFileAppenderImpl.Log_File_Name, roverName, date.toString(DateTimeFormat.forPattern("MM-dd-yyyy_HH.mm")));
	}
	
	public abstract RoverAutonomousCode clone();
	
}
