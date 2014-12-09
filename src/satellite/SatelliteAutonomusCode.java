package satellite;

import objects.DecimalPoint;
import wrapper.Globals;

public abstract class SatelliteAutonomusCode {
	
	private String name;
	private String satelliteName;
	
	public SatelliteAutonomusCode(String name, String satellite){
		this.name = name;
		satelliteName = satellite;
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
			double battery_temp
	);
	
	public String getName(){
		return name;
	}
	
	protected void writeToLog(String message){
		Globals.writeToLogFile(satelliteName + ":" + name + " - Autonomous Code", message);
	}
	
}
