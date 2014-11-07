package rover;

import objects.DecimalPoint;

public class RoverObj {
	
	public static final int FORWARD = 1, BACKWARD = -1, RELEASE = 0; // possible motor states
	public static final int FL = 0, BL = 1, BR = 2, FR = 3; // identifiers on wheels

	private String name;
	private RoverParametersList params;
	private RoverAutonomusCode autoCode;
	
	private DecimalPoint location; //mxm from center of map
	private double direction; //rad
	private double speed = 0; //m/s
	private double angular_velocity = 0; //rad/s
	private double acceleration = 0; //m/s^2
	private double angular_acceleration = 0; //rad/s^2
	private double slip_acceleration = 0; //m/s^2
	private double slip_velocity = 0; //m/s
	
	private int[] motor_power = new int[] { 150, 150, 150, 150 }; // assigned motor powers
	private int[] motor_states = new int[] { 0, 0, 0, 0 }; // assigned motor states
	private double[] wheel_speed = { 0, 0, 0, 0 }; //rad/s	
	private double[] motor_current = { 0, 0, 0, 0 }; //A
	
	private double battery_charge = 144000; //C
	private double battery_cp_charge = 0; //C
	private double battery_voltage = 12; //V
	private double battery_current = 0; //A
	private double SOC = 1;
	
	private boolean temp_initalized = false;
	private double battery_temperature = 22; //*c
	private double[] winding_temp = { 22, 22, 22, 22 }; //*c
	private double[] motor_temp = { 22, 22, 22, 22 }; //*c
	
	public RoverObj(String name, RoverParametersList param, RoverAutonomusCode code, DecimalPoint loc, double dir){
		this.name = name;
		params = param;
		autoCode = code;
		location = loc;
		direction = dir;
	}
	
	public void start(){
		
	}
	
	public String getName(){
		return name;
	}
	
	public RoverParametersList getParameters(){
		return params;
	}
	
	public DecimalPoint getLocation(){
		return location;
	}
	
	public double getDirection(){
		return direction;
	}
	
	public double getBatteryVoltage(){
		return battery_voltage;
	}

	public double getBatteryCharge(){
		return battery_charge;
	}
	
	public double getBatterCPCharge(){
		return battery_cp_charge;
	}
	
	public double getSOC(){
		return SOC;
	}
	
	public double getBatteryCurrent(){
		return battery_current;
	}

	public double getSpeed(){
		return  speed;
	}

	public double getAngularVelocity(){
		return angular_velocity;
	}
	
	public double getSlipVelocity(){
		return slip_velocity;
	}

	public double getAcceleration(){
		return acceleration;
	}

	public double getAngularAcceleration(){
		return angular_acceleration;
	}
	
	public double getSlipAcceleration(){
		return slip_acceleration;
	}

	public double getWheelSpeed(int which){
		if (0 <= which && which < 4){
			return wheel_speed[which];
		}
		return 0;
	}
	
	public double getMotorCurrent(int which){
		if (0 <= which && which < 4){
			return motor_current[which];
		}
		return 0;
	}
	
	public double getMotorVoltage(int which){
		if (0 <= which && which < 4){
			return (motor_power[which]/255.0)*battery_voltage*motor_states[which];
		}
		return 0;
	}
	
	public double getBatteryTemperature(){
		return battery_temperature;
	}
	
	public double getMotorTemp(int which){
		if (0 <= which && which < 4){
			return motor_temp[which];
		}
		return 0;
	}
	
}
