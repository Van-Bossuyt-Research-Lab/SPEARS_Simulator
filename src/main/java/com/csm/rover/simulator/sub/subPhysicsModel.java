package com.csm.rover.simulator.sub;

import java.io.Serializable;

import objects.DecimalPoint;
import objects.SyncronousThread;
import wrapper.Access;
import wrapper.Globals;

public class SubPhysicsModel implements Serializable, Cloneable; {
	
	private static final long serialversionUID = 1L;

	private final int L=0, R=1, F=3, B=4;

	private final double g=9.81;

	protected String subName;
	public final double time_step = 0.01;

	protected final double total_mass=  ;
	protected final double total_volume = ;
	protected final double wingspan =  ;
	protected final double dist_CMtoP_xy = ;
	protected final double dist_CMtoP_z = ;
	protected final double moment_of_inertia =  ;
	protected final double prop_inertia = ;
	protected final double prop-axle_radius = ;
	
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

	protected int[] motor_power = new int[] {  250, 250, 250, 250 }; // assigned motor powers
	protected int[] motor_states = new int[] { 0, 0, 0, 0 }; // assigned motor states
	protected double[] prop_speed = { 0, 0, 0, 0 }; //rad/s	
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
	protected double direction_xy = 0; //rad off of positive X
	protected double direction_z =0; //rad off xy plane
	protected double speed = 0; //m/s
	protected double angular_velocity_xy = 0; //rad/s
	protected double angular_velocity_z = 0; //rad/s
	protected double acceleration_xy = 0; //m/s^2
	protected double acceleration_z = 0; //m/s^2
	protected double angular_acceleration_xy = 0; //rad/s^2
	protected double angular_acceleration_xy = 0; //rad/s^2

	public RoverPhysicsModel() {}
	
	protected SubPhysicsModel(SubPhysicsModel origin){
		SubName = origin.SubName;
		battery_max_charge = origin.battery_max_charge;
		motor_power = origin.motor_power.clone();
		motor_states = origin.motor_states.clone();
		prop_speed = origin.wheel_speed.clone();
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
		direction_xy = origin.direction_xy;
		direction_z = origin.direction_z;
		speed = origin.speed;
		angular_velocity_xy = origin.angular_velocity_xy;
		angular_velocity_z = origin.angular_velocity_z;
		acceleration = origin.acceleration;
		angular_acceleration_xy = origin.angular_acceleration_xy;
	}

	public void initalizeConditions(String name, double bat_charge, double temp){
		SubName = name;
		battery_charge = bat_charge;
		battery_max_charge = bat_charge;
		battery_temperature = temp;
		winding_temp = new double[] { temp, temp, temp, temp };
		motor_temp = new double[] { temp, temp, temp, temp };
	}

	public void start(){
		new SyncronousThread((int) (time_step*1000), new Runnable(){
			public void run(){
				try {
					excecute();
					//System.out.println(SubName + "-physics\t" + Globals.TimeMillis);
					//SubEvents.updateStats();
				}
				catch (Exception e){
					Globals.reportError("RoverDriveModel", "execute", e);
				}
			}
		},
		SyncronousThread.FOREVER, SubName+"-physics");
	}

	public void excecute() throws Exception {
		// Motor Currents, based on voltage
		motor_current[L] += ( motor_power[L]*motor_states[L]/255.0*battery_voltage - motor_voltage_transform*wheel_speed[L] - motor_current[L]*motor_resistance) / motor_inductance * time_step;
		motor_current[R] += ( motor_power[R]*motor_states[R]/255.0*battery_voltage - motor_voltage_transform*wheel_speed[R] - motor_current[R]*motor_resistance) / motor_inductance * time_step;
		motor_current[F] += ( motor_power[F]*motor_states[F]/255.0*battery_voltage - motor_voltage_transform*wheel_speed[F] - motor_current[F]*motor_resistance) / motor_inductance * time_step;
		motor_current[B] += ( motor_power[B]*motor_states[B]/255.0*battery_voltage - motor_voltage_transform*wheel_speed[B] - motor_current[B]*motor_resistance) / motor_inductance * time_step;
		// min currents at 0, motor cannot generate current
		/*if (motor_current[L]*motor_states[FL] <= 0){
			motor_current[L] = 0;
		}
		if (motor_current[R]*motor_states[FR] <= 0){
			motor_current[R] = 0;
		}
		if (motor_current[F]*motor_states[BL] <= 0){
			motor_current[F] = 0;
		}
		if (motor_current[B]*motor_states[BR] <= 0){
			motor_current[B] = 0;
		}*/
		// angular motor speeds, based on torques
		prop_speed[L] += 1/prop_inertia * ( motor_energy_transform*motor_current[L] - friction_axle*prop_speed[L]/prop_radius) * time_step;
		prop_speed[R] += 1/prop_inertia * ( motor_energy_transform*motor_current[R] - friction_axle*prop_speed[R]/prop_radius) * time_step;
		prop_speed[F] += 1/prop_inertia * ( motor_energy_transform*motor_current[F] - friction_axle*prop_speed[F]/prop_radius) * time_step;
		prop_speed[B] += 1/prop_inertia * ( motor_energy_transform*motor_current[B] - friction_axle*prop_speed[B]/prop_radius) * time_step;
		// Acceleration changes based on forces
		acceleration_xy = 1/rover_mass*(prop_thrust[L]+prop_thust[R]-drag_xy)*cos(phi)
		angular_acceleration_xy = 1/moment_of_inertia * ((motor_arm*(slip[FR] + slip[BR] - slip[FL] - slip[BL])*Math.cos(gamma) - motor_arm*(4*fric_gr_all)));
		angular_acceleration_z =  1/moment_of_inertia *
		// Speed changes based on Acceleration
		speed_x += (acceleration_xy * time_step)*Math.sin(theta);
		speed_y += (acceleration_xy * time_step)*Math.cos(theta);
		speed_z += acceleration_z * time_step
		angular_velocity_xy += angular_acceleration * time_step;
		angular_velocity_z += angular_acceleartion * time_step;

		// Calculate new location
		location.offsetThis(speed_x*time_step, speed_y*time_step, speed_z*time_step);
		direction_xy = (direction + angular_velocity_xy*time_step + 2*Math.PI) % (2*Math.PI);
		direction_z = (direction + angular_velocity_z*time_step + 2*Math.PI) % (2*Math.PI);
		// report new location to map
		Access.updatesubLocation(subName, location, direction_xy, direction_z);
		
		//Determining the current of the battery and the change in the stored charge
		if (motor_current[FL]*motor_states[L] <= 0){
			motor_current[L] = 0;
		}
		if (motor_current[FR]*motor_states[R] <= 0){
			motor_current[R] = 0;
		}
		if (motor_current[BL]*motor_states[F] <= 0){
			motor_current[F] = 0;
		}
		if (motor_current[BR]*motor_states[B] <= 0){
			motor_current[B] = 0;
		}
		battery_current = Math.abs(motor_current[FL]) + Math.abs(motor_current[FR]) + Math.abs(motor_current[BL]) + Math.abs(motor_current[BR]);
		double battery_change = battery_charge / capacitance_battery / resistance_parasite + battery_current;
		double cp_change = battery_current - (battery_cp_charge / capacitance_cp / resistance_cp());
		battery_charge -= battery_change * time_step;
		battery_cp_charge += cp_change * time_step;
		battery_voltage = battery_charge/capacitance_battery - battery_cp_charge/capacitance_cp - resistance_s*battery_current;
		SOC = 1 - (battery_max_charge - battery_charge) / battery_max_charge;
			//System.out.println("Vb: " + battery_voltage + "\tVm: " + getMotorVoltage(FR) + "\tQcp: " + battery_cp_charge + "\tIb: " + battery_current + "\tIm: " + motor_current[FR]);
		
		//Determining the temperature of the battery
		battery_temperature += ((resistance_parasite*Math.pow(battery_change-battery_current, 2) + resistance_s*Math.pow(battery_current, 2) + getcapacitance_cp()*Math.pow(battery_current-cp_change, 2)) - battery_heat_transfer*(battery_temperature - Access.getMapTemperatureAtPoint(location)) / battery_thermal_cap) * time_step;
		//Determining the temperature of the motor coils
		winding_temp[FL] += ((motor_resistance*Math.pow(motor_current[FL], 2) - winding_heat_transfer*(winding_temp[FL] - motor_temp[FL])) / winding_thermal_cap) * time_step;
		winding_temp[FR] += ((motor_resistance*Math.pow(motor_current[FR], 2) - winding_heat_transfer*(winding_temp[FR] - motor_temp[FR])) / winding_thermal_cap) * time_step;
		winding_temp[BL] += ((motor_resistance*Math.pow(motor_current[BL], 2) - winding_heat_transfer*(winding_temp[BL] - motor_temp[BL])) / winding_thermal_cap) * time_step;
		winding_temp[BR] += ((motor_resistance*Math.pow(motor_current[BR], 2) - winding_heat_transfer*(winding_temp[BR] - motor_temp[BR])) / winding_thermal_cap) * time_step;
		//Determining the surface temperature of the motor
		motor_temp[FL] += ((winding_heat_transfer*(winding_temp[FL] - motor_temp[FL]) - motor_surface_heat_transfer*(motor_temp[FL] - Access.getMapTemperatureAtPoint(location))) / motor_thermal_cap) * time_step;
		motor_temp[FR] += ((winding_heat_transfer*(winding_temp[FR] - motor_temp[FR]) - motor_surface_heat_transfer*(motor_temp[FR] - Access.getMapTemperatureAtPoint(location))) / motor_thermal_cap) * time_step;
		motor_temp[BL] += ((winding_heat_transfer*(winding_temp[BL] - motor_temp[BL]) - motor_surface_heat_transfer*(motor_temp[BL] - Access.getMapTemperatureAtPoint(location))) / motor_thermal_cap) * time_step;
		motor_temp[BR] += ((winding_heat_transfer*(winding_temp[BR] - motor_temp[BR]) - motor_surface_heat_transfer*(motor_temp[BR] - Access.getMapTemperatureAtPoint(location))) / motor_thermal_cap) * time_step;				
	}