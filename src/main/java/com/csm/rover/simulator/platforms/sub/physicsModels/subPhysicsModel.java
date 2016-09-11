package com.csm.rover.simulator.platforms.sub.physicsModels;

import com.csm.rover.simulator.environments.sub.AquaticMap;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.platforms.DriveCommandHandler;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import com.csm.rover.simulator.platforms.rover.MotorState;
import com.csm.rover.simulator.platforms.sub.SubProp;
import com.csm.rover.simulator.platforms.sub.SubState;
import com.csm.rover.simulator.wrapper.Admin;

import java.util.Map;

@PhysicsModel(type= "Sub", name="Default", parameters = {})
public class subPhysicsModel extends PlatformPhysicsModel {

	private static final long serialversionUID = 1L;
	protected static AquaticMap SMAP;

	private final int L = 0, R = 1, F = 3, B = 4;

	private final double g = 9.81;

	private SubState sub_state;

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
	protected double[] direction = {theta, phi};
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
		super("Sub");
	}

	private void establishDriveResponses() {
		super.addCommandHandler(SubDriveCommands.DRIVE_FORWARD.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorState(SubProp.F, MotorState.RELEASE);
				setMotorState(SubProp.B, MotorState.RELEASE);
				setMotorState(SubProp.R, MotorState.FORWARD);
				setMotorState(SubProp.L, MotorState.FORWARD);
			}
		});
		super.addCommandHandler(SubDriveCommands.DRIVE_BACKWARD.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorState(SubProp.F, MotorState.RELEASE);
				setMotorState(SubProp.B, MotorState.RELEASE);
				setMotorState(SubProp.R, MotorState.BACKWARD);
				setMotorState(SubProp.L, MotorState.BACKWARD);
			}
		});
		super.addCommandHandler(SubDriveCommands.SPIN_CCW.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorState(SubProp.F, MotorState.RELEASE);
				setMotorState(SubProp.B, MotorState.RELEASE);
				setMotorState(SubProp.R, MotorState.FORWARD);
				setMotorState(SubProp.L, MotorState.BACKWARD);
			}
		});
		super.addCommandHandler(SubDriveCommands.SPIN_CW.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorState(SubProp.F, MotorState.RELEASE);
				setMotorState(SubProp.B, MotorState.RELEASE);
				setMotorState(SubProp.R, MotorState.BACKWARD);
				setMotorState(SubProp.L, MotorState.FORWARD);
			}
		});
		super.addCommandHandler(SubDriveCommands.STOP.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorState(SubProp.F, MotorState.RELEASE);
				setMotorState(SubProp.B, MotorState.RELEASE);
				setMotorState(SubProp.R, MotorState.RELEASE);
				setMotorState(SubProp.L, MotorState.RELEASE);
			}
		});
		super.addCommandHandler(SubDriveCommands.TURN_FRONT_LEFT.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorState(SubProp.F, MotorState.RELEASE);
				setMotorState(SubProp.B, MotorState.RELEASE);
				setMotorState(SubProp.R, MotorState.FORWARD);
				setMotorState(SubProp.L, MotorState.RELEASE);
			}
		});
		super.addCommandHandler(SubDriveCommands.TURN_FRONT_RIGHT.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorState(SubProp.F, MotorState.RELEASE);
				setMotorState(SubProp.B, MotorState.RELEASE);
				setMotorState(SubProp.R, MotorState.RELEASE);
				setMotorState(SubProp.L, MotorState.FORWARD);
			}
		});
		super.addCommandHandler(SubDriveCommands.TURN_BACK_LEFT.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorState(SubProp.F, MotorState.RELEASE);
				setMotorState(SubProp.B, MotorState.RELEASE);
				setMotorState(SubProp.R, MotorState.BACKWARD);
				setMotorState(SubProp.L, MotorState.RELEASE);
			}
		});
		super.addCommandHandler(SubDriveCommands.TURN_BACK_RIGHT.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorState(SubProp.F, MotorState.RELEASE);
				setMotorState(SubProp.B, MotorState.RELEASE);
				setMotorState(SubProp.R, MotorState.RELEASE);
				setMotorState(SubProp.L, MotorState.BACKWARD);
			}
		});
		super.addCommandHandler(SubDriveCommands.CHANGE_MOTOR_PWR.getCmd(), new DriveCommandHandler() {
			@Override
			public void processCommand(double[] params) {
				setMotorPower((int) params[0], (int) params[1]);
			}
		});
	}

	public static void setSubMap(AquaticMap map){
		SMAP = map;
	}

	@Override
	public void setPlatformName(String name) {
		this.subName = name;
	}

	@Override
	public void constructParameters(Map<String, Double> params) {}

	@Override
	public void start(){
		new SynchronousThread((int) (time_step*1000), new Runnable(){
			public void run(){
				try {
					updatePhysics();
					//System.out.println(roverName + "-physics\t" + Globals.getInstance.timeMillis);
					//roverEvents.updateStats();
				}
				catch (Exception e){
					//LOG.log(Level.ERROR, String.format("rover %s failed to execute", subName), e);
				}
			}
		},
				SynchronousThread.FOREVER, subName+"-physics");
	}

	@Override
	public void initializeState(PlatformState state) {
		if (state.getType().equals("Sub")){
			try {
				this.sub_state = (SubState)state;
				this.location[1] = state.<Double>get("x");
				this.location[2] = state.<Double>get("y");
				//this.location[3] = state.<Double>get("z");
				this.direction = state.get("direction");
				double temp = -30; //TODO temp
				battery_charge = battery_max_charge;
				battery_temperature = temp;
				winding_temp = new double[] { temp, temp, temp, temp };
				motor_temp = new double[] { temp, temp, temp, temp };
				return;
			}
			catch (ClassCastException e){
				//Let the error throw
			}
		}
		//throw new IllegalArgumentException("The given state is not a subState");
	}

	@Override
	public PlatformState getState() {
		return sub_state.immutableCopy();
	}

	@Override
	public void updatePhysics() {
		// Motor Currents, based on voltage
		motor_current[F] += ( motor_power[F]*motor_states[F]/255.0*battery_voltage - motor_voltage_transform*prop_speed[F] - motor_current[F]*motor_resistance) / motor_inductance * time_step;
		motor_current[R] += ( motor_power[R]*motor_states[R]/255.0*battery_voltage - motor_voltage_transform*prop_speed[R] - motor_current[R]*motor_resistance) / motor_inductance * time_step;
		motor_current[L] += ( motor_power[L]*motor_states[L]/255.0*battery_voltage - motor_voltage_transform*prop_speed[L] - motor_current[L]*motor_resistance) / motor_inductance * time_step;
		motor_current[B] += ( motor_power[B]*motor_states[B]/255.0*battery_voltage - motor_voltage_transform*prop_speed[B] - motor_current[B]*motor_resistance) / motor_inductance * time_step;
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
		prop_speed[F] += 1/prop_inertia * motor_energy_transform*motor_current[F] * time_step;
		prop_speed[R] += 1/prop_inertia *  motor_energy_transform*motor_current[R]  * time_step;
		prop_speed[L] += 1/prop_inertia * motor_energy_transform*motor_current[L] * time_step;
		prop_speed[B] += 1/prop_inertia * motor_energy_transform*motor_current[B] * time_step;
		// Acceleration changes based on forces
		//                acceleration_xy = 1/total_mass*(slip[FL] + slip[BL] + slip[FR] + slip[BR]) - planetParams.getgrav_accel()*Math.sin(MAP.getIncline(location, direction));
		//                angular_acceleration = 1/rover_inertia * ((motor_arm*(slip[FR] + slip[BR] - slip[FL] - slip[BL])*Math.cos(gamma) - motor_arm*(4*fric_gr_all)));
		// Speed changes based on Acceleration
		speed_x += acceleration_xy * time_step * Math.cos(theta)* Math.cos(phi);
		speed_y += acceleration_xy * time_step * Math.sin(theta)* Math.cos(phi);
		speed_z += acceleration_z * time_step;
		angular_velocity_xy += angular_acceleration_xy * time_step;
		angular_velocity_z += angular_acceleration_z * time_step;
		//System.out.println(round(prop_speed[FL]) + " rad/s -> " + round(slip[FL]) + " N");
		//System.out.println(round(prop_speed[FR]) + " rad/s -> " + round(slip[FR]) + " N -> " + round(acceleration) + " m/s^2 -> " + round(speed) + " m/s");
		//System.out.println(round(prop_speed[BL]) + " rad/s -> " + round(slip[BL]) + " N -> " + round(angular_acceleration) + " rad/s^2 -> " + round(angular_velocity) + " rad/s");
		//System.out.println(round(prop_speed[BR]) + " rad/s -> " + round(slip[BR]) + " N");
		// Calculate the amount the rover slips sideways
		// Calculate new location
		//                       location.offsetThis(speed*time_step*Math.cos(direction), speed*time_step*(Math.sin(direction)));
		//TODO													  + here??
		//                           location.offsetThis(slip_velocity*time_step*Math.cos(direction-Math.PI/2.0), slip_velocity*time_step*(Math.sin(direction-Math.PI/2.0)));
		theta = (theta + angular_velocity_xy*time_step + 2*Math.PI) % (2*Math.PI);
		System.out.println(getLocation());

		// update state
		sub_state.set("x", location[1]);
		sub_state.set("y", location[2]);
		sub_state.set("z", location[3]);
		sub_state.set("direction", direction);
		sub_state.set("motor_power", convertToDoubleArray(motor_power));
		sub_state.set("motor_state", convertToDoubleArray(motor_states));
		sub_state.set("motor_current", convertToDoubleArray(motor_current));
		sub_state.set("motor_voltage", getMotorVoltage());
		sub_state.set("motor_temp", convertToDoubleArray(motor_temp));
		sub_state.set("prop_speed", convertToDoubleArray(prop_speed));
		sub_state.set("battery_charge", battery_charge);
		sub_state.set("battery_voltage", battery_voltage);
		sub_state.set("battery_current", battery_current);
		sub_state.set("battery_temp", battery_temperature);
		sub_state.set("speed", speed);
		sub_state.set("angular_velocity_xy", angular_velocity_xy);
		sub_state.set("angular_velocity_z", angular_velocity_z);
		sub_state.set("acceleration_xy", acceleration_xy);
		sub_state.set("acceleration_z", acceleration_z);
		sub_state.set("angular_acceleration_xy", angular_acceleration_xy);
		sub_state.set("angular_acceleration_z", angular_acceleration_z);

		// report new location to map
		Admin.getCurrentInterface().updateSub(subName, location, direction);

		//Determining the current of the battery and the change in the stored charge
		if (motor_current[F]*motor_states[F] <= 0){
			motor_current[F] = 0;
		}
		if (motor_current[R]*motor_states[R] <= 0){
			motor_current[R] = 0;
		}
		if (motor_current[L]*motor_states[L] <= 0){
			motor_current[L] = 0;
		}
		if (motor_current[B]*motor_states[B] <= 0){
			motor_current[B] = 0;
		}
		battery_current = Math.abs(motor_current[F]) + Math.abs(motor_current[R]) + Math.abs(motor_current[L]) + Math.abs(motor_current[B]);
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
		battery_temperature += ((resistance_parasite*Math.pow(battery_change-battery_current, 2) + resistance_s*Math.pow(battery_current, 2) + capacitance_cp*Math.pow(battery_current-cp_change, 2)) - battery_heat_transfer*(battery_temperature - temperature) / battery_thermal_cap) * time_step;
		//Determining the temperature of the motor coils
		winding_temp[F] += ((motor_resistance*Math.pow(motor_current[F], 2) - winding_heat_transfer*(winding_temp[F] - motor_temp[F])) / winding_thermal_cap) * time_step;
		winding_temp[R] += ((motor_resistance*Math.pow(motor_current[R], 2) - winding_heat_transfer*(winding_temp[R] - motor_temp[R])) / winding_thermal_cap) * time_step;
		winding_temp[L] += ((motor_resistance*Math.pow(motor_current[L], 2) - winding_heat_transfer*(winding_temp[L] - motor_temp[L])) / winding_thermal_cap) * time_step;
		winding_temp[B] += ((motor_resistance*Math.pow(motor_current[B], 2) - winding_heat_transfer*(winding_temp[B] - motor_temp[B])) / winding_thermal_cap) * time_step;
		//Determining the surface temperature of the motor
		motor_temp[F] += ((winding_heat_transfer*(winding_temp[F] - motor_temp[L]) - motor_surface_heat_transfer*(motor_temp[F] - temperature)) / motor_thermal_cap) * time_step;
		motor_temp[R] += ((winding_heat_transfer*(winding_temp[R] - motor_temp[R]) - motor_surface_heat_transfer*(motor_temp[R] - temperature)) / motor_thermal_cap) * time_step;
		motor_temp[L] += ((winding_heat_transfer*(winding_temp[L] - motor_temp[L]) - motor_surface_heat_transfer*(motor_temp[L] - temperature)) / motor_thermal_cap) * time_step;
		motor_temp[B] += ((winding_heat_transfer*(winding_temp[B] - motor_temp[B]) - motor_surface_heat_transfer*(motor_temp[B] - temperature)) / motor_thermal_cap) * time_step;
	}

	private double resistance_cp(){ // get the resistance of the CP resistor as a function of SOC
		return R_cp0+R_cp1*Math.exp(R_cp2*(1-SOC));
	}

	private Double[] convertToDoubleArray(double[] array){
		Double[] out = new Double[array.length];
		for (int i = 0; i < out.length; i++){
			out[i] = array[i];
		}
		return out;
	}

	private Double[] convertToDoubleArray(int[] array){
		Double[] out = new Double[array.length];
		for (int i = 0; i < out.length; i++){
			out[i] = (double)array[i];
		}
		return out;
	}

	private Double[] getMotorVoltage(){
		Double[] out = new Double[4];
		for (int i = 0; i < out.length; i++){
			out[i] = motor_power[i]*motor_states[i]/255.0*battery_voltage;
		}
		return out;
	}

	private int getMotor_power(int motor) {
		return motor_power[motor];
	}

	private void setMotorPower(int motor, int motor_power) {
		if (motor_power < 0){
			motor_power = 0;
		}
		else if (motor_power > 255){
			motor_power = 255;
		}
		this.motor_power[motor] = motor_power;
	}

	private void setMotorState(SubProp prop, MotorState state) {
		this.motor_states[prop.getValue()] = state.getValue();
	}

	public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}

	public double[] getDirection() {
		return direction;
	}

	public void setDirection(double[] direction) {
		this.direction = direction;
	}

}
