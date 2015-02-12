package rover;

import java.io.Serializable;

import objects.DecimalPoint;
import wrapper.Globals;

public abstract class RoverAutonomusCode implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String roverName;
	
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
	
	protected void writeToLog(String message){
		Globals.writeToLogFile(roverName + ":" + name + " - Autonomous Code", message);
	}
	
	public abstract RoverAutonomusCode clone();
	
}
