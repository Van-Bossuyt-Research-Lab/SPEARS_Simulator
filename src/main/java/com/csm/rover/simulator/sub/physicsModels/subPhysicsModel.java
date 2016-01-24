package com.csm.rover.simulator.sub.physicsModels;

import java.io.Serializable;

import com.csm.rover.simulator.map.SubMap;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.rover.MotorState;
import com.csm.rover.simulator.sub.subProp;
import com.csm.rover.simulator.wrapper.Globals;




public class subPhysicsModel implements Serializable, Cloneable {

	private static final long serialversionUID = 1L;
	protected static SubMap SMAP;

	private final int L = 0, R = 1, F = 3, B = 4;

	private final double g = 9.81;

	protected String subName;
	public final double time_step = 0.01;
	// NEED REAL VALUES!
	protected final double total_mass = 1.0;
	protected final double total_volume = 1.0;
	protected final double dist_CMtoP_xy = 1.0;
	protected final double dist_CMtoP_z = 1.0;
	protected final double moment_of_inertia = 1.0;
	protected final double prop_inertia = 1.0;
	protected final double prop_radius = 1.0;
	protected final double sub_width = 1.0;
	protected final double sub_length = 1.0;
	protected final double sub_inertia = 1.0;

	protected final double motor_energy_transform = 0.035;
	protected final double motor_voltage_transform = 0.571;
	protected final double motor_resistance = 3; //Ohm
	protected final double motor_inductance = 0.11455; //H
	protected final double friction_axle = 0.0002621;
	protected final double friction_gr = .65;
	protected final double friction_s = 1.2;
	protected final double gamma = Math.atan(1 / sub_width);

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

	protected int[] motor_power = new int[]{250, 250, 250, 250}; // assigned motor powers
	protected int[] motor_states = new int[]{0, 0, 0, 0}; // assigned motor states
	protected double[] prop_speed = {0, 0, 0, 0}; //rad/s
	protected double[] motor_current = {0, 0, 0, 0}; //A

	protected double battery_charge = 0; //C
	protected double battery_cp_charge = 0; //C
	protected double battery_voltage = 12; //V
	protected double battery_current = 0; //A
	protected double SOC = 1;

	protected double battery_temperature = 30; //*c
	protected double[] winding_temp = {30, 30, 30, 30}; //*c
	protected double[] motor_temp = {30, 30, 30, 30}; //*c
	protected double[] prop_thrust = {0, 0, 0, 0};

	protected double[] location = {0, 0, 0}; //m x m from center of map
	protected double theta = 0; //rad off of positive X
	protected double phi = 0; //rad off xy plane
	protected double speed = 0; //m/s
	protected double speed_x = 0; //m/s
	protected double speed_y = 0; //m/s
	protected double speed_z = 0; //m/s
	protected double angular_velocity_xy = 0; //rad/s
	protected double angular_velocity_z = 0; //rad/s
	protected double acceleration_xy = 0; //m/s^2
	protected double acceleration_z = 0; //m/s^2
	protected double angular_acceleration_xy = 0; //rad/s
	protected double angular_acceleration_z = 0; //rad/s
	protected double drag_xy = 0;
	protected double drag_z = 0;


	public subPhysicsModel() {
	}

	protected subPhysicsModel(subPhysicsModel origin) {
		subName = origin.subName;
		battery_max_charge = origin.battery_max_charge;
		motor_power = origin.motor_power.clone();
		motor_states = origin.motor_states.clone();
		prop_speed = origin.prop_speed.clone();
		motor_current = origin.motor_current.clone();
		battery_charge = origin.battery_charge;
		battery_cp_charge = origin.battery_cp_charge;
		battery_voltage = origin.battery_voltage;
		battery_current = origin.battery_current;
		SOC = origin.SOC;
		battery_temperature = origin.battery_temperature;
		winding_temp = origin.winding_temp.clone();
		motor_temp = origin.motor_temp.clone();
		location = origin.location.clone();
		theta = origin.theta;
		phi = origin.phi;
		speed = origin.speed;
		angular_velocity_xy = origin.angular_velocity_xy;
		angular_velocity_z = origin.angular_velocity_z;
		acceleration_xy = origin.acceleration_xy;
		acceleration_z = origin.acceleration_z;
		angular_acceleration_xy = origin.angular_acceleration_xy;
		angular_acceleration_z = origin.angular_acceleration_z;
		prop_thrust = origin.prop_thrust;
	}

	public void initalizeConditions(String name, double bat_charge, double temp) {
		subName = name;
		battery_charge = bat_charge;
		battery_max_charge = bat_charge;
		battery_temperature = temp;
		winding_temp = new double[]{temp, temp, temp, temp};
		motor_temp = new double[]{temp, temp, temp, temp};
	}

	public void start() {
		new SynchronousThread((int) (time_step * 1000), new Runnable() {
			public void run() {
				try {
					excecute();
					//System.out.println(SubName + "-physics\t" + Globals.TimeMillis);
					//SubEvents.updateStats();
				} catch (Exception e) {
					System.out.println("sync thread exception");
				}
			}
		},
				SynchronousThread.FOREVER, subName + "-physics");
	}

	public void excecute() throws Exception {
		// Motor Currents, based on voltage
		motor_current[L] += (motor_power[L] * motor_states[L] / 255.0 * battery_voltage - motor_voltage_transform * prop_speed[L] - motor_current[L] * motor_resistance) / motor_inductance * time_step;
		motor_current[R] += (motor_power[R] * motor_states[R] / 255.0 * battery_voltage - motor_voltage_transform * prop_speed[R] - motor_current[R] * motor_resistance) / motor_inductance * time_step;
		motor_current[F] += (motor_power[F] * motor_states[F] / 255.0 * battery_voltage - motor_voltage_transform * prop_speed[F] - motor_current[F] * motor_resistance) / motor_inductance * time_step;
		motor_current[B] += (motor_power[B] * motor_states[B] / 255.0 * battery_voltage - motor_voltage_transform * prop_speed[B] - motor_current[B] * motor_resistance) / motor_inductance * time_step;
		// min currents at 0, motor cannot generate current
		/*if (motor_current[L]*motor_states[L] <= 0){
			motor_current[L] = 0;
		}
		if (motor_current[R]*motor_states[R] <= 0){
			motor_current[R] = 0;
		}
		if (motor_current[F]*motor_states[F] <= 0){
			motor_current[F] = 0;
		}
		if (motor_current[B]*motor_states[B] <= 0){
			motor_current[B] = 0;
		}*/
		// angular motor speeds, based on torques
		prop_speed[L] += 1 / prop_inertia * (motor_energy_transform * motor_current[L] - friction_axle * prop_speed[L] / prop_radius) * time_step;
		prop_speed[R] += 1 / prop_inertia * (motor_energy_transform * motor_current[R] - friction_axle * prop_speed[R] / prop_radius) * time_step;
		prop_speed[F] += 1 / prop_inertia * (motor_energy_transform * motor_current[F] - friction_axle * prop_speed[F] / prop_radius) * time_step;
		prop_speed[B] += 1 / prop_inertia * (motor_energy_transform * motor_current[B] - friction_axle * prop_speed[B] / prop_radius) * time_step;
		// Acceleration changes based on forces
		acceleration_xy = 1 / total_mass * ((prop_thrust[L] + prop_thrust[R] - drag_xy) * Math.cos(phi) + (prop_thrust[F] + prop_thrust[B] - drag_z) * Math.sin(phi));
		acceleration_z = 1 / total_mass * ((prop_thrust[L] + prop_thrust[R] - drag_xy) * Math.sin(phi) + (prop_thrust[F] + prop_thrust[B] - drag_z) * Math.cos(phi));
		angular_acceleration_xy = 1 / moment_of_inertia * ((prop_thrust[R] - prop_thrust[L] * dist_CMtoP_xy));
		angular_acceleration_z = 1 / moment_of_inertia * (prop_thrust[F] - prop_thrust[B]) * dist_CMtoP_z;
		// Speed changes based on Acceleration
		speed_x += (acceleration_xy * time_step) * Math.sin(theta);
		speed_y += (acceleration_xy * time_step) * Math.cos(theta);
		speed_z += acceleration_z * time_step;
		angular_velocity_xy += angular_acceleration_xy * time_step;
		angular_velocity_z += angular_acceleration_z * time_step;

		// Calculate new location
		location[0] = +(speed_x * time_step);
		location[1] = +speed_y * time_step;
		location[2] = +speed_z * time_step;
		theta = (theta + angular_velocity_xy * time_step + 2 * Math.PI) % (2 * Math.PI);
		phi = (phi + angular_velocity_z * time_step + 2 * Math.PI) % (2 * Math.PI);
		// report new location to map

		//Determining the current of the battery and the change in the stored charge
		if (motor_current[L] * motor_states[L] <= 0) {
			motor_current[L] = 0;
		}
		if (motor_current[R] * motor_states[R] <= 0) {
			motor_current[R] = 0;
		}
		if (motor_current[F] * motor_states[F] <= 0) {
			motor_current[F] = 0;
		}
		if (motor_current[B] * motor_states[B] <= 0) {
			motor_current[B] = 0;
		}
	}

	//System.out.println("Vb: " + battery_voltage + "\tVm: " + getMotorVoltage(R) + "\tQcp: " + battery_cp_charge + "\tIb: " + battery_current + "\tIm: " + motor_current[R])


	private double resistance_cp() { // get the resistance of the CP resistor as a function of SOC
		return R_cp0 + R_cp1 * Math.exp(R_cp2 * (1 - SOC));
	}

	public double getprop_radius() {
		return prop_radius;
	}

	public double getProp_inertia() {
		return prop_inertia;
	}

	public double getsub_width() {
		return sub_width;
	}

	public double getsub_length() {
		return sub_length;
	}

	public double getmotor_arm() {
		return dist_CMtoP_xy;
	}

	public double getsub_mass() {
		return total_mass;
	}

	public double getsub_inertia() {
		return sub_inertia;
	}

	public double getmotor_energy_transform() {
		return motor_energy_transform;
	}

	public double getmotor_voltage_transform() {
		return motor_voltage_transform;
	}

	public double getmotor_resistance() {
		return motor_resistance;
	}

	public double getmotor_inductance() {
		return motor_inductance;
	}

	public double getfriction_axle() {
		return friction_axle;
	}

	public double getfriction_gr() {
		return friction_gr;
	}

	public double getfriction_s() {
		return friction_s;
	}

	public double getgamma() {
		return gamma;
	}

	public double getR_cp0() {
		return R_cp0;
	}

	public double getR_cp1() {
		return R_cp1;
	}

	public double getR_cp2() {
		return R_cp2;
	}

	public double getcapacitance_battery() {
		return capacitance_battery;
	}

	public double getcapacitance_cp() {
		return capacitance_cp;
	}

	public double getresistance_s() {
		return resistance_s;
	}

	public double getresistance_parasite() {
		return resistance_parasite;
	}

	public double getbattery_max_charge() {
		return battery_max_charge;
	}

	public double getbattery_heat_transfer() {
		return battery_heat_transfer;
	}

	public double getbattery_thermal_cap() {
		return battery_thermal_cap;
	}

	public double getwinding_heat_transfer() {
		return winding_heat_transfer;
	}

	public double getwinding_thermal_cap() {
		return winding_thermal_cap;
	}

	public double getmotor_surface_heat_transfer() {
		return motor_surface_heat_transfer;
	}

	public double getmotor_thermal_cap() {
		return motor_thermal_cap;
	}

	public int[] getMotor_power() {
		return motor_power;
	}

	public int getMotor_power(int motor) {
		return motor_power[motor];
	}

	public void setMotorPower(subProp motor, int motor_power) {
		if (motor_power < 0) {
			motor_power = 0;
		} else if (motor_power > 255) {
			motor_power = 255;
		}
		this.motor_power[motor.getValue()] = motor_power;
	}

	public int[] getMotorStates() {
		return motor_states;
	}

	public void setMotorState(subProp wheel, MotorState state) {
		this.motor_states[wheel.getValue()] = state.getValue();
	}

	public double[] getMotor_current() {
		return motor_current;
	}

	public double getMotorCurrent(subProp prop) {
		return motor_current[prop.getValue()];
	}

	public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}

	public double getTheta() {
		return theta;
	}

	public double getPhi() {
		return phi;
	}

	public void setTheta(double direction) {
		this.theta = direction;
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

	public double[] getProp_speed() {
		return prop_speed;
	}

	public double getPropSpeed(subProp prop) {
		return prop_speed[prop.getValue()];
	}

	public double getSOC() {
		return SOC;
	}

	public double getSpeed() {
		return speed;
	}

	public double getMotorTemp(subProp motor) {
		return this.motor_temp[motor.getValue()];
	}


	public double[] getWindingTemp() {
		return winding_temp;
	}

	public double[] getMotorTemp() {
		return motor_temp;
	}


	public double getAngularVelocity_xy() {
		return angular_velocity_xy;
	}

	public double getAngularVelocity_z() {
		return angular_velocity_z;
	}

	public double getAcceleration_xy() {
		return acceleration_xy;
	}

	public double getAcceleration_z() {
		return acceleration_z;
	}

	public double getAngularAcceleration_xy() {
		return angular_acceleration_xy;
	}

	public double getAngularAcceleration_z() {
		return angular_acceleration_z;
	}

	public void setMotorPower(int[] motor_power) {
		this.motor_power = motor_power;
	}

	public void setDirection_xy(double dir) {
		theta = dir;
	}

	public static void setSubMap(SubMap map){ SMAP = map;}
}
