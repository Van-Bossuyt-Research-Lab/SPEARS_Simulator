package com.csm.rover.simulator.platforms.sub.physicsModels;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.sub.AquaticEnvironment;
import com.csm.rover.simulator.objects.RK4;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.objects.util.DecimalPoint3D;
import com.csm.rover.simulator.platforms.DriveCommandHandler;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import com.csm.rover.simulator.platforms.rover.MotorState;
import com.csm.rover.simulator.platforms.sub.SubProp;
import com.csm.rover.simulator.platforms.sub.SubState;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@PhysicsModel(type= "Sub", name="Default", parameters = {})
public class subPhysicsModel extends PlatformPhysicsModel {
    private static final Logger LOG = LogManager.getLogger(subPhysicsModel.class);

	protected AquaticEnvironment environment;

	private final int L = 0, R = 1, F = 3, B = 4;

	private SubState sub_state;

	protected String subName;
	public final double time_step = 0.01;

	//TODO NEED REAL VALUES!
	protected final double total_mass = 1.0; //kg
	protected final double total_volume = 1.0; //m^3
	protected final double moment_of_inertia = 1.0;
	protected final double prop_inertia = 1.0;
	protected final double prop_radius = 1.0;
    protected final double prop_speed_transform = 1.0;
	protected final double sub_width = 1.0;
	protected final double sub_length = 1.0;
	protected final double sub_inertia = 1.0;

	protected final double motor_energy_transform = 0.035;
	protected final double motor_voltage_transform = 0.571;
	protected final double motor_resistance = 3; //Ohm
	protected final double motor_inductance = 0.11455; //H

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

	protected double battery_temperature = 7.22; //*c
	protected double[] winding_temp = {7.22, 7.22, 7.22, 7.22}; //*c
	protected double[] motor_temp = {7.22, 7.22, 7.22, 7.22}; //*c

	protected DecimalPoint3D location = new DecimalPoint3D(0, 0, 0); //m x m from center of map
	protected double[] orientation = {0, 0, 0}; //pitch, yaw, roll - pitch is axial off xy, yaw is axial off xz, roll is z off axial-normal
    protected double[] speed = {0, 0, 0}; //m/s  x, y, z

	public subPhysicsModel() {
		super("Sub");
        establishDriveResponses();
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

	@Override
	public void setEnvironment(PlatformEnvironment enviro){
		if (enviro.getType().equals(platform_type)){
			environment = (AquaticEnvironment)enviro;
		}
		else {
			throw new IllegalArgumentException("The given platform has the wrong type: " + enviro.getType());
		}
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
				}
				catch (Exception e){
					LOG.log(Level.ERROR, String.format("rover %s failed to execute", subName), e);
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
                this.location = new DecimalPoint3D(state.<Double>get("x"), state.<Double>get("y"), state.<Double>get("z"));
				this.orientation[0] = state.get("pitch");
                this.orientation[1] = state.get("yaw");
                this.orientation[2] = state.get("roll");
                double temp = 7.22; //TODO temp
				battery_charge = battery_max_charge;
				battery_temperature = temp;
				winding_temp = new double[] { temp, temp, temp, temp };
				motor_temp = new double[] { temp, temp, temp, temp };
				return;
			}
			catch (ClassCastException e){
				throw e;
			}
		}
		throw new IllegalArgumentException("The given state is not a subState");
	}

	@Override
	public PlatformState getState() {
		return sub_state.immutableCopy();
	}

                                                                              //others: motor_power, motor_state, prop_speed
    private final RK4.RK4Function motorCurrentFn = (double t, double current, double... others) -> (others[0]*others[1]/255.0*battery_voltage - motor_voltage_transform*others[2] - current*motor_resistance) / motor_inductance;
    private final RK4.RK4Function propSpeedFn = (double t, double speed, double... current) -> 1/prop_inertia * motor_energy_transform*current[0];
    private final RK4.RK4Function forceSumFnX =
            //others: speed[Y, Z], density, prop_speed[F, B, L, R], orientation[P, W, R]
            (double t, double speedX, double... others) -> {
                double speedY = others[0], speedZ = others[1];
                double density = others[2];
                double[] prop_speed = new double[] { others[3], others[4], others[5], others[6] };
                double pitch = others[7], yaw = others[8], roll = others[9];
                double axial_speed = speedX*Math.cos(pitch)*Math.cos(yaw) +
                        speedY*Math.cos(pitch)*Math.sin(yaw) +
                        speedZ*Math.sin(pitch);
                double normal_speed = -speedX*(Math.sin(yaw)*Math.sin(roll) + Math.cos(yaw)*Math.cos(roll)*Math.sin(pitch)) +
                        speedY*(Math.cos(yaw)*Math.sin(roll) - Math.cos(roll)*Math.sin(yaw)*Math.sin(pitch)) +
                        speedZ*Math.cos(pitch)*Math.cos(roll);
                double axial_force = density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[L] * (prop_speed_transform * prop_speed[L] - axial_speed) +
                        density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[R] * (prop_speed_transform * prop_speed[R] - axial_speed);
                double normal_force =  density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[F] * (prop_speed_transform * prop_speed[F] - normal_speed) +
                        density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[B] * (prop_speed_transform * prop_speed[B] - normal_speed);
                return (axial_force*Math.cos(pitch)*Math.cos(yaw) + normal_force*(-Math.sin(yaw)*Math.sin(roll) - Math.cos(yaw)*Math.cos(roll)*Math.sin(pitch))) / total_mass;
            };
    private final RK4.RK4Function forceSumFnY =
            //others: speed[X, Z], density, prop_speed[F, B, L, R], orientation[P, W, R]
            (double t, double speedY, double... others) -> {
                double speedX = others[0], speedZ = others[1];
                double density = others[2];
                double[] prop_speed = new double[] { others[3], others[4], others[5], others[6] };
                double pitch = others[7], yaw = others[8], roll = others[9];
                double axial_speed = speedX*Math.cos(pitch)*Math.cos(yaw) +
                        speedY*Math.cos(pitch)*Math.sin(yaw) +
                        speedZ*Math.sin(pitch);
                double normal_speed = -speedX*(Math.sin(yaw)*Math.sin(roll) + Math.cos(yaw)*Math.cos(roll)*Math.sin(pitch)) +
                        speedY*(Math.cos(yaw)*Math.sin(roll) - Math.cos(roll)*Math.sin(yaw)*Math.sin(pitch)) +
                        speedZ*Math.cos(pitch)*Math.cos(roll);
                double axial_force = density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[L] * (prop_speed_transform * prop_speed[L] - axial_speed) +
                        density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[R] * (prop_speed_transform * prop_speed[R] - axial_speed);
                double normal_force =  density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[F] * (prop_speed_transform * prop_speed[F] - normal_speed) +
                        density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[B] * (prop_speed_transform * prop_speed[B] - normal_speed);
                return (axial_force*Math.cos(pitch)*Math.sin(yaw) + normal_force*(Math.cos(yaw)*Math.sin(roll) - Math.cos(roll)*Math.sin(yaw)*Math.sin(pitch))) / total_mass;
            };
    private final RK4.RK4Function forceSumFnZ =
            //others: speed[X, Y], density, prop_speed[F, B, L, R], orientation[P, W, R]
            (double t, double speedZ, double... others) -> {
                double speedX = others[0], speedY = others[1];
                double density = others[2];
                double[] prop_speed = new double[] { others[3], others[4], others[5], others[6] };
                double pitch = others[7], yaw = others[8], roll = others[9];
                double axial_speed = speedX*Math.cos(pitch)*Math.cos(yaw) +
                        speedY*Math.cos(pitch)*Math.sin(yaw) +
                        speedZ*Math.sin(pitch);
                double normal_speed = -speedX*(Math.sin(yaw)*Math.sin(roll) + Math.cos(yaw)*Math.cos(roll)*Math.sin(pitch)) +
                        speedY*(Math.cos(yaw)*Math.sin(roll) - Math.cos(roll)*Math.sin(yaw)*Math.sin(pitch)) +
                        speedZ*Math.cos(pitch)*Math.cos(roll);
                double axial_force = density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[L] * (prop_speed_transform * prop_speed[L] - axial_speed) +
                        density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[R] * (prop_speed_transform * prop_speed[R] - axial_speed);
                double normal_force =  density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[F] * (prop_speed_transform * prop_speed[F] - normal_speed) +
                        density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[B] * (prop_speed_transform * prop_speed[B] - normal_speed);
                return (axial_force*Math.sin(pitch) + normal_force*Math.cos(pitch)*Math.cos(roll) + environment.getGravity()*total_volume*density - environment.getGravity()*total_mass) / total_mass;
           };
    private final RK4.RK4Function torqueSumFnP =
            //others: speed[Y, Z], density, prop_speed[F, B, L, R], orientation[P, W, R]
            (double t, double speedX, double... others) -> {
                double speedY = others[0], speedZ = others[1];
                double density = others[2];
                double[] prop_speed = new double[] { others[3], others[4], others[5], others[6] };
                double pitch = others[7], yaw = others[8], roll = others[9];
                double axial_speed = speedX*Math.cos(pitch)*Math.cos(yaw) +
                        speedY*Math.cos(pitch)*Math.sin(yaw) +
                        speedZ*Math.sin(pitch);
                double normal_speed = -speedX*(Math.sin(yaw)*Math.sin(roll) + Math.cos(yaw)*Math.cos(roll)*Math.sin(pitch)) +
                        speedY*(Math.cos(yaw)*Math.sin(roll) - Math.cos(roll)*Math.sin(yaw)*Math.sin(pitch)) +
                        speedZ*Math.cos(pitch)*Math.cos(roll);
                double axial_force = density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[L] * (prop_speed_transform * prop_speed[L] - axial_speed) +
                        density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[R] * (prop_speed_transform * prop_speed[R] - axial_speed);
                double normal_force =  density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[F] * (prop_speed_transform * prop_speed[F] - normal_speed) +
                        density * Math.PI * Math.pow(prop_radius, 2) * prop_speed_transform * prop_speed[B] * (prop_speed_transform * prop_speed[B] - normal_speed);
                return (axial_force*Math.cos(pitch)*Math.cos(yaw) + normal_force*(-Math.sin(yaw)*Math.sin(roll) - Math.cos(yaw)*Math.cos(roll)*Math.sin(pitch))) / total_mass;
            };

	@Override
	public void updatePhysics() {
		// Motor Currents, based on voltage
        motor_current[F] = RK4.advance(motorCurrentFn, time_step, 0, motor_current[F], motor_power[F], motor_states[F], prop_speed[F]);
        motor_current[B] = RK4.advance(motorCurrentFn, time_step, 0, motor_current[B], motor_power[B], motor_states[B], prop_speed[B]);
        motor_current[R] = RK4.advance(motorCurrentFn, time_step, 0, motor_current[R], motor_power[R], motor_states[R], prop_speed[R]);
        motor_current[L] = RK4.advance(motorCurrentFn, time_step, 0, motor_current[L], motor_power[L], motor_states[L], prop_speed[L]);

		// angular motor speeds, based on torques
		prop_speed[F] = RK4.advance(propSpeedFn, time_step, 0, prop_speed[F], motor_current[F]);
        prop_speed[B] = RK4.advance(propSpeedFn, time_step, 0, prop_speed[B], motor_current[B]);
        prop_speed[R] = RK4.advance(propSpeedFn, time_step, 0, prop_speed[R], motor_current[R]);
        prop_speed[L] = RK4.advance(propSpeedFn, time_step, 0, prop_speed[L], motor_current[L]);

        double density = environment.getDensityAt(location);

		// Speed changes based on Forces
        double vx = RK4.advance(forceSumFnX, time_step, 0, speed[0], speed[1], speed[2],
                density, prop_speed[F], prop_speed[B], prop_speed[L], prop_speed[R],
                orientation[0], orientation[1], orientation[2]);
        double vy = RK4.advance(forceSumFnY, time_step, 0, speed[1], speed[0], speed[2],
                density, prop_speed[F], prop_speed[B], prop_speed[L], prop_speed[R],
                orientation[0], orientation[1], orientation[2]);
        double vz = RK4.advance(forceSumFnX, time_step, 0, speed[2], speed[0], speed[1],
                density, prop_speed[F], prop_speed[B], prop_speed[L], prop_speed[R],
                orientation[0], orientation[1], orientation[2]);

        // Orientation changes based on Torques
        double wP = RK4.advance() ;
        double wW = ;
        double wR = ;

		// Updating state variables
        speed = new double[]{ vx, vy, vz };
        location = location.offset(new DecimalPoint3D(vx, vy, vz).scale(time_step));
        orientation[0] += wP * time_step;
        orientation[1] += wW * time_step;
        orientation[2] += wR * time_step;

        sub_state.set("x", location.getX());
        sub_state.set("y", location.getY());
        sub_state.set("z", location.getZ());
        sub_state.set("pitch", orientation[0]);
        sub_state.set("yaw", orientation[1]);
        sub_state.set("roll", orientation[2]);
        sub_state.set("speed", Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2)));

		// Determining the current of the battery and the change in the stored charge
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

		double temperature = 7.22;

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

        // update state
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

	public DecimalPoint3D getLocation() {
		return location;
	}

	public void setLocation(DecimalPoint3D location) {
		this.location = location;
	}

	public double[] getOrientation() {
		return orientation;
	}

	public void setOrientation(double[] orientation) {
		this.orientation = orientation;
	}

}
