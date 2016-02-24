package com.csm.rover.simulator.platforms.rover.phsicsModels;

import com.csm.rover.simulator.map.PlanetParametersList;
import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import com.csm.rover.simulator.platforms.rover.MotorState;
import com.csm.rover.simulator.platforms.rover.RoverWheels;
import com.csm.rover.simulator.wrapper.Admin;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Map;

//TODO add implementation with better time stepping
@PhysicsModel(type="Rover", name="Default")
public class RoverPhysicsModel extends PlatformPhysicsModel implements Serializable {
	private static final Logger LOG = LogManager.getLogger(RoverPhysicsModel.class);

	private static final long serialVersionUID = 1L;
	
	private final int FL = 0, FR = 1, BL = 2, BR = 3;

    //TODO variable planet params
	private PlanetParametersList planetParams = new PlanetParametersList();
	protected static TerrainMap MAP;

	protected String roverName;
	public final double time_step = 0.01; // time step of physics, in seconds
	
	protected final double wheel_radius = 0.0476; //m
	protected final double wheel_mass = 0.064; //kg
	protected final double rover_width = 0.438; //m
	protected final double rover_length = 0.229; //m
	protected final double motor_arm = Math.sqrt(Math.pow(rover_width, 2) + Math.pow(rover_length, 2)) / 2.0; //m
	protected final double rover_mass = 2.266; //kg
	protected final double rover_inertia = 0.1025; //kg*m^2
	protected final double wheel_inertia = 0.5 * wheel_mass * Math.pow(wheel_radius, 2); //kg*m^2
	
	protected final double motor_energy_transform = 0.035;
	protected final double motor_voltage_transform = 0.571;
	protected final double motor_resistance = 3; //Ohm
	protected final double motor_inductance = 0.11455; //H
	protected final double friction_axle = 0.0002621;
	protected final double friction_gr = .65;
	protected final double friction_s = 1.2;
	protected final double gamma = Math.atan(1/rover_width);
	
	protected final double R_cp0 = 0.07; //Ohm
	protected final double R_cp1 = 0.01; //Ohm
	protected final double R_cp2 = 3;
	protected final double capacitance_battery = 12000; //F
	protected final double capacitance_cp = 0.2; //F
	protected final double resistance_s = 0.01; //Ohm
	protected final double resistance_parasite = 100000000; //Ohm
	protected double battery_max_charge = 140000; //C
	
	protected final double battery_heat_transfer = 10; //J/s/*c
	protected final double battery_thermal_cap = 170; //J/K
	
	protected final double winding_heat_transfer = 2; //J/s/*c
	protected final double winding_thermal_cap = 1.7; //J/*c
	protected final double motor_surface_heat_transfer = 0.9; //J/s/*c
	protected final double motor_thermal_cap = 0.8; //J/*c	
	
	protected double fric_gr_all = 0;
	protected double[] slip = { 0, 0, 0, 0 };		
	
	protected int[] motor_power = new int[] {  250, 250, 250, 250 }; // assigned motor powers
	protected int[] motor_states = new int[] { 0, 0, 0, 0 }; // assigned motor states
	protected double[] wheel_speed = { 0, 0, 0, 0 }; //rad/s	
	protected double[] motor_current = { 0, 0, 0, 0 }; //A
	
	protected double battery_charge = 0; //C
	protected double battery_cp_charge = 0; //C
	protected double battery_voltage = 12; //V
	protected double battery_current = 0; //A
	protected double SOC = 1;
	
	protected double battery_temperature = 30; //*c
	protected double[] winding_temp = { 30, 30, 30, 30 }; //*c
	protected double[] motor_temp = { 30, 30, 30, 30 }; //*c
	
	protected DecimalPoint location = new DecimalPoint(); //m x m from center of map
	protected double direction = 0; //rad off of positive X
	protected double speed = 0; //m/s
	protected double angular_velocity = 0; //rad/s
	protected double acceleration = 0; //m/s^2
	protected double angular_acceleration = 0; //rad/s^2
	protected double slip_acceleration = 0; //m/s^2
	protected double slip_velocity = 0; //m/s
	
	public RoverPhysicsModel() {
        super("Rover");
    }

    public static void setTerrainMap(TerrainMap map){
        MAP = map;
    }

    @Override
	public void constructParameters(Map<String, Double> params) {}
	
	public void initalizeConditions(String name, double bat_charge, double temp){
		roverName = name;
		battery_charge = bat_charge;
		battery_max_charge = bat_charge;
		battery_temperature = temp;
		winding_temp = new double[] { temp, temp, temp, temp };
		motor_temp = new double[] { temp, temp, temp, temp };
	}

    @Override
	public void start(){
		new SynchronousThread((int) (time_step*1000), new Runnable(){
			public void run(){
				try {
					updatePhysics();
					//System.out.println(roverName + "-physics\t" + Globals.getInstance.timeMillis);
					//RoverEvents.updateStats();
				}
				catch (Exception e){
					LOG.log(Level.ERROR, String.format("Rover %s failed to execute", roverName), e);
				}
			}
		},
		SynchronousThread.FOREVER, roverName+"-physics");
	}

    @Override
	public void updatePhysics() {
		// Motor Currents, based on voltage
		motor_current[FL] += ( motor_power[FL]*motor_states[FL]/255.0*battery_voltage - motor_voltage_transform*wheel_speed[FL] - motor_current[FL]*motor_resistance) / motor_inductance * time_step;
		motor_current[FR] += ( motor_power[FR]*motor_states[FR]/255.0*battery_voltage - motor_voltage_transform*wheel_speed[FR] - motor_current[FR]*motor_resistance) / motor_inductance * time_step;
		motor_current[BL] += ( motor_power[BL]*motor_states[BL]/255.0*battery_voltage - motor_voltage_transform*wheel_speed[BL] - motor_current[BL]*motor_resistance) / motor_inductance * time_step;
		motor_current[BR] += ( motor_power[BR]*motor_states[BR]/255.0*battery_voltage - motor_voltage_transform*wheel_speed[BR] - motor_current[BR]*motor_resistance) / motor_inductance * time_step;
		// min currents at 0, motor cannot generate current
		/*if (motor_current[FL]*motor_states[FL] <= 0){
			motor_current[FL] = 0;
		}
		if (motor_current[FR]*motor_states[FR] <= 0){
			motor_current[FR] = 0;
		}
		if (motor_current[BL]*motor_states[BL] <= 0){
			motor_current[BL] = 0;
		}
		if (motor_current[BR]*motor_states[BR] <= 0){
			motor_current[BR] = 0;
		}*/
		// angular motor speeds, based on torques
		wheel_speed[FL] += 1/wheel_inertia * ( motor_energy_transform*motor_current[FL] - wheel_radius*slip[FL] + wheel_radius*fric_gr_all*Math.cos(gamma) - friction_axle*wheel_speed[FL]/wheel_radius) * time_step;
		wheel_speed[FR] += 1/wheel_inertia * ( motor_energy_transform*motor_current[FR] - wheel_radius*slip[FR] - wheel_radius*fric_gr_all*Math.cos(gamma) - friction_axle*wheel_speed[FR]/wheel_radius) * time_step;
		wheel_speed[BL] += 1/wheel_inertia * ( motor_energy_transform*motor_current[BL] - wheel_radius*slip[BL] + wheel_radius*fric_gr_all*Math.cos(gamma) - friction_axle*wheel_speed[BL]/wheel_radius) * time_step;
		wheel_speed[BR] += 1/wheel_inertia * ( motor_energy_transform*motor_current[BR] - wheel_radius*slip[BR] - wheel_radius*fric_gr_all*Math.cos(gamma) - friction_axle*wheel_speed[BR]/wheel_radius) * time_step;
		// translational friction, approximately the same for all wheels
		fric_gr_all = friction_gr * motor_arm * angular_velocity;
		// Slip forces on wheels, based on speed differences
		slip[FL] = friction_s * (wheel_speed[FL]*wheel_radius - speed);
		slip[FR] = friction_s * (wheel_speed[FR]*wheel_radius - speed);
		slip[BL] = friction_s * (wheel_speed[BL]*wheel_radius - speed);
		slip[BR] = friction_s * (wheel_speed[BR]*wheel_radius - speed);
		// Acceleration changes based on forces
		acceleration = 1/rover_mass*(slip[FL] + slip[BL] + slip[FR] + slip[BR]) - planetParams.getgrav_accel()*Math.sin(MAP.getIncline(location, direction));
		angular_acceleration = 1/rover_inertia * ((motor_arm*(slip[FR] + slip[BR] - slip[FL] - slip[BL])*Math.cos(gamma) - motor_arm*(4*fric_gr_all)));
		// Speed changes based on Acceleration
		speed += acceleration * time_step;
		angular_velocity += angular_acceleration * time_step;
			//System.out.println(round(wheel_speed[FL]) + " rad/s -> " + round(slip[FL]) + " N");
			//System.out.println(round(wheel_speed[FR]) + " rad/s -> " + round(slip[FR]) + " N -> " + round(acceleration) + " m/s^2 -> " + round(speed) + " m/s");
			//System.out.println(round(wheel_speed[BL]) + " rad/s -> " + round(slip[BL]) + " N -> " + round(angular_acceleration) + " rad/s^2 -> " + round(angular_velocity) + " rad/s");
			//System.out.println(round(wheel_speed[BR]) + " rad/s -> " + round(slip[BR]) + " N");
		// Calculate the amount the rover slips sideways
		slip_acceleration = (-friction_gr*slip_velocity*4 - rover_mass*planetParams.getgrav_accel()*Math.sin(MAP.getCrossSlope(location, direction)) / rover_mass);
		slip_velocity += slip_acceleration * time_step;
		// Calculate new location
		location.offsetThis(speed*time_step*Math.cos(direction), speed*time_step*(Math.sin(direction)));
		//TODO													  + here??
		location.offsetThis(slip_velocity*time_step*Math.cos(direction-Math.PI/2.0), slip_velocity*time_step*(Math.sin(direction-Math.PI/2.0)));
		direction = (direction + angular_velocity*time_step + 2*Math.PI) % (2*Math.PI);
		// report new location to map
        Admin.getCurrentInterface().updateRover(roverName, location, direction);
		
		//Determining the current of the battery and the change in the stored charge
		if (motor_current[FL]*motor_states[FL] <= 0){
			motor_current[FL] = 0;
		}
		if (motor_current[FR]*motor_states[FR] <= 0){
			motor_current[FR] = 0;
		}
		if (motor_current[BL]*motor_states[BL] <= 0){
			motor_current[BL] = 0;
		}
		if (motor_current[BR]*motor_states[BR] <= 0){
			motor_current[BR] = 0;
		}
		battery_current = Math.abs(motor_current[FL]) + Math.abs(motor_current[FR]) + Math.abs(motor_current[BL]) + Math.abs(motor_current[BR]);
		double battery_change = battery_charge / capacitance_battery / resistance_parasite + battery_current;
		double cp_change = battery_current - (battery_cp_charge / capacitance_cp / resistance_cp());
		battery_charge -= battery_change * time_step;
		battery_cp_charge += cp_change * time_step;
		battery_voltage = battery_charge/capacitance_battery - battery_cp_charge/capacitance_cp - resistance_s*battery_current;
		SOC = 1 - (battery_max_charge - battery_charge) / battery_max_charge;
			//System.out.println("Vb: " + battery_voltage + "\tVm: " + getMotorVoltage(FR) + "\tQcp: " + battery_cp_charge + "\tIb: " + battery_current + "\tIm: " + motor_current[FR]);

		//TODO temp map stuff here
		double temperature = -30;

		//Determining the temperature of the battery
		battery_temperature += ((resistance_parasite*Math.pow(battery_change-battery_current, 2) + resistance_s*Math.pow(battery_current, 2) + getcapacitance_cp()*Math.pow(battery_current-cp_change, 2)) - battery_heat_transfer*(battery_temperature - temperature) / battery_thermal_cap) * time_step;
		//Determining the temperature of the motor coils
		winding_temp[FL] += ((motor_resistance*Math.pow(motor_current[FL], 2) - winding_heat_transfer*(winding_temp[FL] - motor_temp[FL])) / winding_thermal_cap) * time_step;
		winding_temp[FR] += ((motor_resistance*Math.pow(motor_current[FR], 2) - winding_heat_transfer*(winding_temp[FR] - motor_temp[FR])) / winding_thermal_cap) * time_step;
		winding_temp[BL] += ((motor_resistance*Math.pow(motor_current[BL], 2) - winding_heat_transfer*(winding_temp[BL] - motor_temp[BL])) / winding_thermal_cap) * time_step;
		winding_temp[BR] += ((motor_resistance*Math.pow(motor_current[BR], 2) - winding_heat_transfer*(winding_temp[BR] - motor_temp[BR])) / winding_thermal_cap) * time_step;
		//Determining the surface temperature of the motor
		motor_temp[FL] += ((winding_heat_transfer*(winding_temp[FL] - motor_temp[FL]) - motor_surface_heat_transfer*(motor_temp[FL] - temperature)) / motor_thermal_cap) * time_step;
		motor_temp[FR] += ((winding_heat_transfer*(winding_temp[FR] - motor_temp[FR]) - motor_surface_heat_transfer*(motor_temp[FR] - temperature)) / motor_thermal_cap) * time_step;
		motor_temp[BL] += ((winding_heat_transfer*(winding_temp[BL] - motor_temp[BL]) - motor_surface_heat_transfer*(motor_temp[BL] - temperature)) / motor_thermal_cap) * time_step;
		motor_temp[BR] += ((winding_heat_transfer*(winding_temp[BR] - motor_temp[BR]) - motor_surface_heat_transfer*(motor_temp[BR] - temperature)) / motor_thermal_cap) * time_step;
	}
	
	private double resistance_cp(){ // get the resistance of the CP resistor as a function of SOC
		return R_cp0+R_cp1*Math.exp(R_cp2*(1-SOC));
	}
	
	public double getwheel_radius(){
		return wheel_radius;
	}

	public double getwheel_mass(){
		return wheel_mass;
	}

	public double getrover_width(){
		return rover_width;
	}

	public double getrover_length(){
		return rover_length;
	}

	public double getmotor_arm(){
		return motor_arm;
	}

	public double getrover_mass(){
		return rover_mass;
	}

	public double getrover_inertia(){
		return rover_inertia;
	}

	public double getwheel_inertia(){
		return wheel_inertia;
	}

	public double getmotor_energy_transform(){
		return motor_energy_transform;
	}

	public double getmotor_voltage_transform(){
		return motor_voltage_transform;
	}

	public double getmotor_resistance(){
		return motor_resistance;
	}

	public double getmotor_inductance(){
		return motor_inductance;
	}

	public double getfriction_axle(){
		return friction_axle;
	}

	public double getfriction_gr(){
		return friction_gr;
	}

	public double getfriction_s(){
		return friction_s;
	}

	public double getgamma(){
		return gamma;
	}

	public double getR_cp0(){
		return R_cp0;
	}

	public double getR_cp1(){
		return R_cp1;
	}

	public double getR_cp2(){
		return R_cp2;
	}

	public double getcapacitance_battery(){
		return capacitance_battery;
	}

	public double getcapacitance_cp(){
		return capacitance_cp;
	}

	public double getresistance_s(){
		return resistance_s;
	}

	public double getresistance_parasite(){
		return resistance_parasite;
	}

	public double getbattery_max_charge(){
		return battery_max_charge;
	}

	public double getbattery_heat_transfer(){
		return battery_heat_transfer;
	}

	public double getbattery_thermal_cap(){
		return battery_thermal_cap;
	}

	public double getwinding_heat_transfer(){
		return  winding_heat_transfer;
	}

	public double getwinding_thermal_cap(){
		return winding_thermal_cap;
	}

	public double getmotor_surface_heat_transfer(){
		return motor_surface_heat_transfer;
	}

	public double getmotor_thermal_cap(){
		return motor_thermal_cap;
	}

	public int[] getMotor_power() {
		return motor_power;
	}
	
	public int getMotor_power(int motor) {
		return motor_power[motor];
	}

	public void setMotorPower(RoverWheels motor, int motor_power) {
		if (motor_power < 0){
			motor_power = 0;
		}
		else if (motor_power > 255){
			motor_power = 255;
		}
		this.motor_power[motor.getValue()] = motor_power;
	}

	public int[] getMotorStates() {
		return motor_states;
	}

	public void setMotorState(RoverWheels wheel, MotorState state) {
		this.motor_states[wheel.getValue()] = state.getValue();
	}

	public double[] getMotor_current() {
		return motor_current;
	}
	
	public double getMotorCurrent(RoverWheels wheel) {
		return motor_current[wheel.getValue()];
	}

	public DecimalPoint getLocation() {
		return location;
	}

	public void setLocation(DecimalPoint location) {
		this.location = location;
	}

	public double getDirection() {
		return direction;
	}

	public void setDirection(double direction) {
		this.direction = direction;
	}

	public double getFrictionAxle() {
		return friction_axle;
	}

	public double getFriction_gr() {
		return friction_gr;
	}

	public double getFriction_s() {
		return friction_s;
	}

	public double[] getWheelSpeed() {
		return wheel_speed;
	}
	
	public double getWheelSpeed(RoverWheels wheel) {
		return wheel_speed[wheel.getValue()];
	}

	public double getSOC() {
		return SOC;
	}

	public double getSpeed() {
		return speed;
	}

	public double getSlip_acceleration() {
		return slip_acceleration;
	}

	public double getSlipVelocity() {
		return slip_velocity;
	}

	public void setBatteryCharge(double battery_charge) {
		this.battery_charge = battery_charge;
	}
	
	public double getMotorTemp(RoverWheels motor){
		return this.motor_temp[motor.getValue()];
	}

	public double getBatteryVoltage() {
		return battery_voltage;
	}

	public double[] getWindingTemp() {
		return winding_temp;
	}

	public double[] getMotorTemp() {
		return motor_temp;
	}

	public double getBatteryCharge() {
		return battery_charge;
	}

	public double getBatteryCurrent() {
		return battery_current;
	}

	public double getAngularVelocity() {
		return angular_velocity;
	}

	public double getAcceleration() {
		return acceleration;
	}

	public double getAngularAcceleration() {
		return angular_acceleration;
	}

	public void setMotorPower(int[] motor_power) {
		this.motor_power = motor_power;
	}

	public double getBatteryTemperature() {
		return battery_temperature;
	}

	public double getBattery_cp_charge() {
		return battery_cp_charge;
	}
	
}
