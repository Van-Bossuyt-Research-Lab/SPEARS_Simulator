package com.csm.rover.simulator.rover.autoCode;

import com.csm.rover.simulator.objects.DecimalPoint;

public class GenericRover extends RoverAutonomusCode {

	private long lastActionTime = 0;
	private int action = 0;
	private int seconds = 1;
	private int power = 250;
	
	public GenericRover(String roverName, int sec){
		super("Generic", roverName);
		seconds = sec;
	}
	
	public GenericRover(GenericRover in) {
		super(in);
		this.lastActionTime = in.lastActionTime;
		this.action = in.action;
		this.seconds = in.seconds;
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
			double battery_temp,
			double battery_charge
	){
		if (milliTime-lastActionTime > 1000*seconds){
			lastActionTime = milliTime;
			action++;
			if (action%11 == 0){
				power -= 50;
				return "chngmtr0" + power;
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

	public GenericRover clone(){
		return new GenericRover(this);
	}
	
}
