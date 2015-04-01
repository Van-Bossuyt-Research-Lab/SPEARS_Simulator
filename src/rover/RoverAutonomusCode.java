package rover;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import objects.DecimalPoint;
import wrapper.Access;
import wrapper.Globals;

public abstract class RoverAutonomusCode implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String roverName;
	
	private File logFile;
	
	public RoverAutonomusCode(String name, String rover){
		this.name = name;
		roverName = rover;
	}
	
	public RoverAutonomusCode(RoverAutonomusCode rac){
		this.name = rac.name;
		this.roverName = rac.roverName;
	}
	
	abstract public String nextCommand(
			long milliTime,
			DecimalPoint location,
			double direction,
			double acceleration,
			double angular_acceleration,
			double wheel_speed_FL,
			double wheel_speed_FR,
			double wheel_speed_BL,
			double wheel_speed_BR,
			double motor_current_FL,
			double motor_current_FR,
			double motor_current_BL,
			double motor_current_BR,
			double motor_temp_FL,
			double motor_temp_FR,
			double motor_temp_BL,
			double motor_temp_BR,
			double battery_voltage,
			double battery_current,
			double battery_temp,
			double battery_charge
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
			write.write(message + "\t\t" + Access.INTERFACE.DateTime.toString("[MM/dd/yyyy hh:mm:ss.") + (Globals.TimeMillis%1000) + "]\r\n");
			write.flush();
			write.close();
		}
		catch (NullPointerException e){
			if (!tried){
				tried = true;
				logFile = new File("Logs/" + roverName + " Log " + Access.INTERFACE.DateTime.toString("MM-dd-yyyy hh-mm") + ".txt");
				Globals.writeToLogFile(roverName, "Writing rover's autonomous log file to: " + logFile.getAbsolutePath());
				writeToLog(message);
			}
			else {
				e.printStackTrace();
				Globals.writeToLogFile(roverName, "Rover's autonomous log file failed to initalize.");
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public abstract RoverAutonomusCode clone();
	
}
