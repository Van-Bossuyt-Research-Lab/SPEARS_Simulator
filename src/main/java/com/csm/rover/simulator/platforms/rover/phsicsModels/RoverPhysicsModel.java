package com.csm.rover.simulator.platforms.rover.phsicsModels;

import com.csm.rover.simulator.environments.rover.TerrainEnvironment;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.DriveCommandHandler;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import com.csm.rover.simulator.platforms.rover.MotorState;
import com.csm.rover.simulator.platforms.rover.RoverState;
import com.csm.rover.simulator.platforms.rover.RoverWheels;
import com.csm.rover.simulator.wrapper.Admin;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

//TODO add implementation with better time stepping
@PhysicsModel(type="Rover", name="Default", parameters={})
public class RoverPhysicsModel extends PlatformPhysicsModel {
	private static final Logger LOG = LogManager.getLogger(RoverPhysicsModel.class);
	
	private final int FL = 0, FR = 1, BL = 2, BR = 3;

    private RoverState rov_state;

	protected static TerrainEnvironment MAP;

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

    protected double battery_cp_charge = 0; //C
    protected double SOC = 1;

    protected double[] winding_temp = { 30, 30, 30, 30 }; //*c

	protected int[] motor_power = new int[] {  250, 250, 250, 250 }; // assigned motor powers
	protected int[] motor_states = new int[] { 0, 0, 0, 0 }; // assigned motor states
	protected double[] wheel_speed = { 0, 0, 0, 0 }; //rad/s
	protected double[] motor_current = { 0, 0, 0, 0 }; //A

	protected double battery_charge = 0; //C
	protected double battery_voltage = 12; //V
	protected double battery_current = 0; //A

	protected double battery_temperature = 30; //*c
	protected double[] motor_temp = { 30, 30, 30, 30 }; //*c

	protected DecimalPoint location = new DecimalPoint(); //m x m from center of map (intermediate value for RK4)
    protected DecimalPoint location2 = new DecimalPoint(); //m x m from center of map (intermediate value for RK4)
    protected DecimalPoint location3 = new DecimalPoint(); //m x m from center of map (intermediate value for RK4)
    protected DecimalPoint location4 = new DecimalPoint(); //m x m from center of map (intermediate value for RK4)
    protected DecimalPoint locationf = new DecimalPoint(); //m x m from center of map
	protected double direction = 0; //rad off of positive X(intermediate value for RK4)
    protected double direction2 = 0; //rad off of positive X (intermediate value for RK4)
    protected double direction3 = 0; //rad off of positive X(intermediate value for RK4)
    protected double direction4 = 0; //rad off of positive X(intermediate value for RK4)
    protected double directionf = 0; //rad off of positive X
	protected double speed = 0; //m/s(intermediate value for RK4)
    protected double speed2 = 0; //m/s(intermediate value for RK4)
    protected double speed3 = 0; //m/s(intermediate value for RK4)
    protected double speed4 = 0; //m/s(intermediate value for RK4)
    protected double speedf = 0; //m/s
	protected double angular_velocity = 0; //rad/s(intermediate value for RK4)
    protected double angular_velocity2 = 0; //rad/s(intermediate value for RK4)
    protected double angular_velocity3 = 0; //rad/s(intermediate value for RK4)
    protected double angular_velocity4 = 0; //rad/s(intermediate value for RK4)
    protected double angular_velocityf = 0; //rad/s
	protected double acceleration = 0; //m/s^2(intermediate value for RK4)
    protected double acceleration2 = 0; //m/s^2(intermediate value for RK4)
    protected double acceleration3 = 0; //m/s^2(intermediate value for RK4)
    protected double acceleration4 = 0; //m/s^2(intermediate value for RK4)
    protected double accelerationf = 0; //m/s^2
	protected double angular_acceleration = 0; //rad/s^2(intermediate value for RK4)
    protected double angular_acceleration2 = 0; //rad/s^2(intermediate value for RK4)
    protected double angular_acceleration3 = 0; //rad/s^2(intermediate value for RK4)
    protected double angular_acceleration4 = 0; //rad/s^2(intermediate value for RK4)
    protected double angular_accelerationf = 0; //rad/s^2
	protected double slip_acceleration = 0; //m/s^2(intermediate value for RK4)
    protected double slip_acceleration2 = 0; //m/s^2(intermediate value for RK4)
    protected double slip_acceleration3 = 0; //m/s^2(intermediate value for RK4)
    protected double slip_acceleration4 = 0; //m/s^2(intermediate value for RK4)
    protected double slip_accelerationf = 0; //m/s^2
	protected double slip_velocity = 0; //m/s(intermediate value for RK4)
    protected double slip_velocity2 = 0; //m/s(intermediate value for RK4)
    protected double slip_velocity3 = 0; //m/s(intermediate value for RK4)
    protected double slip_velocity4 = 0; //m/s(intermediate value for RK4)
    protected double slip_velocityf = 0; //m/s

	public RoverPhysicsModel() {
        super("Rover");
        establishDriveResponses();
    }

    private void establishDriveResponses() {
        super.addCommandHandler(RoverDriveCommands.DRIVE_FORWARD.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorState(RoverWheels.FL, MotorState.FORWARD);
                setMotorState(RoverWheels.BL, MotorState.FORWARD);
                setMotorState(RoverWheels.BR, MotorState.FORWARD);
                setMotorState(RoverWheels.FR, MotorState.FORWARD);
            }
        });
        super.addCommandHandler(RoverDriveCommands.DRIVE_BACKWARD.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorState(RoverWheels.FL, MotorState.BACKWARD);
                setMotorState(RoverWheels.BL, MotorState.BACKWARD);
                setMotorState(RoverWheels.BR, MotorState.BACKWARD);
                setMotorState(RoverWheels.FR, MotorState.BACKWARD);
            }
        });
        super.addCommandHandler(RoverDriveCommands.SPIN_CW.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorState(RoverWheels.FL, MotorState.FORWARD);
                setMotorState(RoverWheels.BL, MotorState.FORWARD);
                setMotorState(RoverWheels.BR, MotorState.BACKWARD);
                setMotorState(RoverWheels.FR, MotorState.BACKWARD);
            }
        });
        super.addCommandHandler(RoverDriveCommands.SPIN_CCW.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorState(RoverWheels.FL, MotorState.BACKWARD);
                setMotorState(RoverWheels.BL, MotorState.BACKWARD);
                setMotorState(RoverWheels.BR, MotorState.FORWARD);
                setMotorState(RoverWheels.FR, MotorState.FORWARD);
            }
        });
        super.addCommandHandler(RoverDriveCommands.STOP.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorState(RoverWheels.FL, MotorState.RELEASE);
                setMotorState(RoverWheels.BL, MotorState.RELEASE);
                setMotorState(RoverWheels.BR, MotorState.RELEASE);
                setMotorState(RoverWheels.FR, MotorState.RELEASE);
            }
        });
        super.addCommandHandler(RoverDriveCommands.TURN_FRONT_LEFT.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorState(RoverWheels.FL, MotorState.RELEASE);
                setMotorState(RoverWheels.BL, MotorState.RELEASE);
                setMotorState(RoverWheels.BR, MotorState.FORWARD);
                setMotorState(RoverWheels.FR, MotorState.FORWARD);
            }
        });
        super.addCommandHandler(RoverDriveCommands.TURN_FRONT_RIGHT.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorState(RoverWheels.FL, MotorState.FORWARD);
                setMotorState(RoverWheels.BL, MotorState.FORWARD);
                setMotorState(RoverWheels.BR, MotorState.RELEASE);
                setMotorState(RoverWheels.FR, MotorState.RELEASE);
            }
        });
        super.addCommandHandler(RoverDriveCommands.TURN_BACK_LEFT.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorState(RoverWheels.FL, MotorState.RELEASE);
                setMotorState(RoverWheels.BL, MotorState.RELEASE);
                setMotorState(RoverWheels.BR, MotorState.BACKWARD);
                setMotorState(RoverWheels.FR, MotorState.BACKWARD);
            }
        });
        super.addCommandHandler(RoverDriveCommands.TURN_BACK_RIGHT.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorState(RoverWheels.FL, MotorState.BACKWARD);
                setMotorState(RoverWheels.BL, MotorState.BACKWARD);
                setMotorState(RoverWheels.BR, MotorState.RELEASE);
                setMotorState(RoverWheels.FR, MotorState.RELEASE);
            }
        });
        super.addCommandHandler(RoverDriveCommands.CHANGE_MOTOR_PWR.getCmd(), new DriveCommandHandler() {
            @Override
            public void processCommand(double[] params) {
                setMotorPower((int) params[0], (int) params[1]);
            }
        });
    }

    public static void setTerrainMap(TerrainEnvironment map){
        MAP = map;
    }

    @Override
    public void setPlatformName(String name) {
        this.roverName = name;
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
    public void initializeState(PlatformState state) {
        if (state.getType().equals("Rover")){
            try {
                this.rov_state = (RoverState)state;
                this.location = new DecimalPoint(state.<Double>get("x"), state.<Double>get("y"));
                this.direction = state.get("direction");
                double temp = -30; //TODO temp
                battery_charge = battery_max_charge;
                battery_temperature = temp;
                winding_temp = new double[] { temp, temp, temp, temp };
                motor_temp = new double[] { temp, temp, temp, temp };
            }
            catch (ClassCastException e){
                LOG.log(Level.ERROR, "Failed to read state");
            }
        }
        else {
            throw new IllegalArgumentException("The given state is not a RoverState");
        }
    }

    @Override
    public PlatformState getState() {
        return rov_state.immutableCopy();
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
		// Speed changes based on Acceleration
		speed2 = speed + acceleration * time_step*0.5;
		angular_velocity2 = angular_velocity+angular_acceleration * time_step*0.5;
        slip_velocity2 += slip_acceleration * time_step*0.5;;
		// Calculate new location
        location2= location;
        location2.offsetThis(speed*time_step*0.5*Math.cos(direction), speed*time_step*0.5*(Math.sin(direction)));
		location2.offsetThis(slip_velocity*time_step*0.5*Math.cos(direction-Math.PI/2.0), slip_velocity*time_step*0.5*(Math.sin(direction-Math.PI/2.0)));
		direction2 = (direction + angular_velocity*time_step*0.5 + 2*Math.PI) % (2*Math.PI);
        acceleration2 = 1/rover_mass*(slip[FL] + slip[BL] + slip[FR] + slip[BR]) - MAP.getGravity()*Math.sin(MAP.getSlopeAt(location2, direction2));
        angular_acceleration2 = 1/rover_inertia * ((motor_arm*(slip[FR] + slip[BR] - slip[FL] - slip[BL])*Math.cos(gamma) - motor_arm*(4*fric_gr_all)));
        slip_acceleration2 = (-friction_gr*slip_velocity2*4 - rover_mass*MAP.getGravity()*Math.sin(MAP.getCrossSlopeAt(location2, direction2)) / rover_mass);

        // Speed changes based on Acceleration
        speed3 = speed2 + acceleration2 * time_step*0.5;
        angular_velocity3 = angular_velocity2+angular_acceleration2 * time_step*0.5;
        slip_velocity3 = slip_acceleration2 * time_step*0.5;;
        // Calculate new location
        location3= location2;
        location3.offsetThis(speed2*time_step*0.5*Math.cos(direction2), speed2*time_step*0.5*(Math.sin(direction2)));
        location3.offsetThis(slip_velocity2*time_step*0.5*Math.cos(direction2-Math.PI/2.0), slip_velocity2*time_step*0.5*(Math.sin(direction2-Math.PI/2.0)));
        direction3 = (direction2 + angular_velocity2*time_step*0.5 + 2*Math.PI) % (2*Math.PI);
        acceleration3 = 1/rover_mass*(slip[FL] + slip[BL] + slip[FR] + slip[BR]) - MAP.getGravity()*Math.sin(MAP.getSlopeAt(location3, direction3));
        angular_acceleration3 = 1/rover_inertia * ((motor_arm*(slip[FR] + slip[BR] - slip[FL] - slip[BL])*Math.cos(gamma) - motor_arm*(4*fric_gr_all)));
        slip_acceleration3 = (-friction_gr*slip_velocity3*4 - rover_mass*MAP.getGravity()*Math.sin(MAP.getCrossSlopeAt(location3, direction3)) / rover_mass);

        speed4 = speed3 + acceleration3 * time_step*0.5;
        angular_velocity4 = angular_velocity3+angular_acceleration3 * time_step*0.5;
        slip_velocity4 = slip_acceleration3 * time_step*0.5;;
        // Calculate new location
        location4= location3;
        location4.offsetThis(speed3*time_step*0.5*Math.cos(direction3), speed3*time_step*0.5*(Math.sin(direction3)));
        location4.offsetThis(slip_velocity3*time_step*0.5*Math.cos(direction3-Math.PI/2.0), slip_velocity3*time_step*0.5*(Math.sin(direction3-Math.PI/2.0)));
        direction4 = (direction3 + angular_velocity3*time_step*0.5 + 2*Math.PI) % (2*Math.PI);
        acceleration4 = 1/rover_mass*(slip[FL] + slip[BL] + slip[FR] + slip[BR]) - MAP.getGravity()*Math.sin(MAP.getSlopeAt(location3, direction4));
        angular_acceleration4 = 1/rover_inertia * ((motor_arm*(slip[FR] + slip[BR] - slip[FL] - slip[BL])*Math.cos(gamma) - motor_arm*(4*fric_gr_all)));
        slip_acceleration4 = (-friction_gr*slip_velocity4*4 - rover_mass*MAP.getGravity()*Math.sin(MAP.getCrossSlopeAt(location4, direction4)) / rover_mass);

        speedf = speed +(time_step/6.0)*(acceleration+2*acceleration2+2*acceleration3+acceleration4);
        angular_velocityf = angular_velocity + (time_step/6.0)*(angular_acceleration+2.0*angular_acceleration2+2.0*angular_acceleration3+angular_acceleration4);
        slip_velocityf = slip_velocity + (time_step/6.0)*(slip_acceleration+2*slip_acceleration2+2.0*slip_acceleration3+slip_acceleration4);
        // Calculate new location
        locationf= location;
        locationf.offsetThis((time_step/6.0)*(speed+2.0*speed2+2.0*speed3+speed4)*Math.cos(directionf), (time_step/6.0)*(speed+2.0*speed2+2.0*speed3+speed4)*(Math.sin(directionf)));
        locationf.offsetThis((time_step/6.0)*(slip_velocity+2.0*slip_velocity2+2.0*slip_velocity3+slip_velocity4)*Math.cos(directionf-Math.PI/2.0), (time_step/6.0)*(slip_velocity+2.0*slip_velocity2+2.0*slip_velocity3+slip_velocity4)*(Math.sin(directionf-Math.PI/2.0)));
        directionf = ((time_step/6.0)*(angular_velocity+2.0*angular_velocity2+2.0*angular_velocity3+angular_velocity4) + 2*Math.PI) % (2*Math.PI);

        // update state
        rov_state.set("x", locationf.getX());
        rov_state.set("y", locationf.getY());
        rov_state.set("direction", directionf);
        rov_state.set("motor_power", convertToDoubleArray(motor_power));
        rov_state.set("motor_state", convertToDoubleArray(motor_states));
        rov_state.set("motor_current", convertToDoubleArray(motor_current));
        rov_state.set("motor_voltage", getMotorVoltage());
        rov_state.set("motor_temp", convertToDoubleArray(motor_temp));
        rov_state.set("wheel_speed", convertToDoubleArray(wheel_speed));
        rov_state.set("battery_charge", battery_charge);
        rov_state.set("battery_voltage", battery_voltage);
        rov_state.set("battery_current", battery_current);
        rov_state.set("battery_temp", battery_temperature);
        rov_state.set("speed", speedf);
        rov_state.set("angular_velocity", angular_velocityf);
        rov_state.set("acceleration", accelerationf);
        rov_state.set("angular_acceleration", angular_accelerationf);
        rov_state.set("slip_acceleration", slip_accelerationf);
        rov_state.set("slip_velocity", slip_velocityf);

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
		battery_temperature += ((resistance_parasite*Math.pow(battery_change-battery_current, 2) + resistance_s*Math.pow(battery_current, 2) + capacitance_cp*Math.pow(battery_current-cp_change, 2)) - battery_heat_transfer*(battery_temperature - temperature) / battery_thermal_cap) * time_step;
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

    private void setMotorState(RoverWheels wheel, MotorState state) {
		this.motor_states[wheel.getValue()] = state.getValue();
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

}
