package wrapper;

import objects.DecimalPoint;
import rover.RoverAutonomusCode;

public class BlankRoverAuto extends RoverAutonomusCode {

	public BlankRoverAuto() {
		super("NULL", "NULL");
		// TODO Auto-generated constructor stub
	}

	@Override
	public String nextCommand(long milliTime, DecimalPoint location,
			double direction, double acceleration, double angular_acceleration,
			double wheel_speed_FL, double wheel_speed_FR,
			double wheel_speed_BL, double wheel_speed_BR,
			double motor_current_FL, double motor_current_FR,
			double motor_current_BL, double motor_current_BR,
			double motor_temp_FL, double motor_temp_FR, double motor_temp_BL,
			double motor_temp_BR, double battery_voltage,
			double battery_current, double battery_temp, double battery_charge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RoverAutonomusCode clone() {
		return new BlankRoverAuto();
	}

}
