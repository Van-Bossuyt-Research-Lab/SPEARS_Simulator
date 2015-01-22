package rover.autoCode;

import objects.DecimalPoint;
import rover.RoverAutonomusCode;
import wrapper.Access;

public class JumpingRover extends RoverAutonomusCode {

	private long lastActionTime = 0;
	int coint = 0;
	
	public JumpingRover(String roverName){
		super("Generic", roverName);
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
		if (milliTime-lastActionTime > 0){
			location.offsetThis(.1, -.1);
			System.out.print(this.coint + "\t");
			double h = Access.getMapHeightatPoint(location);
			System.out.println("\t" + h);
			lastActionTime = milliTime;
			return "";
		}
		else {
			return "";
		}
	}
	
}
