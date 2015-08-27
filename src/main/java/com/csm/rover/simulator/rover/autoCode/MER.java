package com.csm.rover.simulator.rover.autoCode;

import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.wrapper.Access;
import com.csm.rover.simulator.wrapper.Globals;

public class MER extends RoverAutonomusCode {
	
	private static final double ANGLE_ERROR = Math.PI/16.0;
	
	private int completed = 0;
	private DecimalPoint[] targets;
	private int score = 0;

	public MER(DecimalPoint[] targets){
		super("MER", "MER");
		this.targets = targets;
	}
	
	private MER(MER origin) {
		super(origin);
		this.completed = origin.completed;
		this.targets = origin.targets;
		this.score = origin.score;
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
		super.writeToLog(milliTime + "\t" + location.getX() + "\t" + location.getY() + "\t" + Access.getMapHeightatPoint(location) + "\t" + score + "\t" + battery_charge + "\t" + (completed == targets.length));
		direction = (direction + 2*Math.PI) % (2*Math.PI);
		if (completed == targets.length){
			return "stop";
		}
		if (Math.abs(targets[completed].getX() - location.getX()) < 0.25 && Math.abs(targets[completed].getY() - location.getY()) < 0.25){
			score += Access.getTargetValue(location);
			completed++;
		}
		double targetAngle = Math.atan((targets[completed].getY() - location.getY()) / (double)(targets[completed].getX() - location.getX()));
		if (targets[completed].getX() < location.getX()){
			targetAngle += Math.PI;
		}
		targetAngle = (targetAngle + 2*Math.PI) % (2*Math.PI);
		//System.out.println(targetAngle + "\t" + (targets[completed].getY() - location.getY()) + "\t" + (targets[completed].getX() - location.getX()));
		if (Math.abs(Globals.subtractAngles(targetAngle, direction)) < ANGLE_ERROR){
			return "move";
		}
		else {
			if (Globals.subtractAngles(targetAngle, direction) > 0){
				return "spin_cw";
			}
			else {
				return "spin_ccw";
			}
		}
	}

	@Override
	public RoverAutonomusCode clone() {
		return new MER(this);
	}

}
