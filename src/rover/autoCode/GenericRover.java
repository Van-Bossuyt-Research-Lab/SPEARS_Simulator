package rover.autoCode;

import objects.DecimalPoint;
import rover.RoverAutonomusCode;

public class GenericRover extends RoverAutonomusCode {

	private long lastActionTime = 0;
	private int action = 0;
	private int seconds = 1;
	
	public GenericRover(String roverName, int sec){
		super("Generic", roverName);
		seconds = sec;
	}
	
	public String nextCommand(
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
	){
		if (milliTime-lastActionTime > 1000*seconds){
			lastActionTime = milliTime;
			action++;
			if (action%11 == 0){
				return "photo";
			}
			if (action%5 < 2){
				return "move";
			}
			else if (action%5 < 5){
				return "turnFR";
			}
			else {
				return "";
			}
		}
		else {
			return "";
		}
	}
	
}
