package com.csm.rover.simulator.rover.autoCode;

import com.csm.rover.simulator.control.InterfaceAccess;
import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.wrapper.Globals;

import java.io.*;
import java.util.Map;

public abstract class RoverAutonomousCode implements Serializable, Cloneable {

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
			write.write(message + "\t\t" + InterfaceAccess.CODE.DateTime.toString("[MM/dd/yyyy hh:mm:ss.") + (Globals.getInstance().timeMillis %1000) + "]\r\n");
			write.flush();
			write.close();
		}
		catch (NullPointerException e){
			if (!tried){
				tried = true;
				logFile = new File("Logs/" + roverName + " Log " + InterfaceAccess.CODE.DateTime.toString("MM-dd-yyyy hh-mm") + ".txt");
				Globals.getInstance().writeToLogFile(roverName, "Writing rover's autonomous log file to: " + logFile.getAbsolutePath());
				writeToLog(message);
			}
			else {
				e.printStackTrace();
				Globals.getInstance().writeToLogFile(roverName, "Rover's autonomous log file failed to initalize.");
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public abstract RoverAutonomousCode clone();
	
}
