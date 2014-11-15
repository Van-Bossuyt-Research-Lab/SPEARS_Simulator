

public class CLASS_NAME_HERE extends RoverAutonomusCode {

	//Declare any variables you want here
	

	public CLASS_NAME_HERE(String roverName){
		super("NAME_OF_VERSION", roverName);
	}
	
	@Override
	public String nextCommand(
		long milliTime,
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
	) {
	
		//Write processing code here
		
		
		
		return //nextCommand
	}

	// writeToLog(String message);
	
}