/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.platforms.rover.physicsModels;

import com.spears.environments.PlatformEnvironment;
import com.spears.environments.rover.TerrainEnvironment;
import com.spears.objects.RK4;
import com.spears.objects.SynchronousThread;
import com.spears.objects.util.DecimalPoint;
import com.spears.platforms.DriveCommandHandler;
import com.spears.platforms.PlatformPhysicsModel;
import com.spears.platforms.PlatformState;
import com.spears.platforms.annotations.PhysicsModel;
import com.spears.platforms.rover.MotorState;
import com.spears.platforms.rover.RoverState;
import com.spears.platforms.rover.RoverWheels;
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

	protected static TerrainEnvironment environment;

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

    @Override
    public void setEnvironment(PlatformEnvironment enviro){
        if (enviro.getType().equals(platform_type)){
            environment = (TerrainEnvironment)enviro;
        }
        else {
            throw new IllegalArgumentException("The given environment has the wrong type: " + enviro.getType());
        }
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

    //others: motor_power, motor_state, prop_speed
    private final RK4.RK4Function motorCurrentFn = (double t, double current, double... others) -> (others[0]*others[1]/255.0*battery_voltage - motor_voltage_transform*others[2] - current*motor_resistance) / motor_inductance;
    private final RK4.RK4Function wheelSpeedFn =
            //others: current, slip
            (double t, double wheel_speed, double... others) -> {
                double current = others[0];
                double slip = others[1];
                return ( motor_energy_transform*current - wheel_radius*slip + wheel_radius*fric_gr_all*Math.cos(gamma) - friction_axle*wheel_speed/wheel_radius)/wheel_inertia;
            };
    private final RK4.RK4Function accelerationFn =
            //others: wheel_speed{FL, FR, BL, BR}, slope
            (double t, double speed, double... others) -> {
                double[] slip = new double[4];
                slip[FL] = friction_s * (others[0]*wheel_radius - speed);
                slip[FR] = friction_s * (others[1]*wheel_radius - speed);
                slip[BL] = friction_s * (others[2]*wheel_radius - speed);
                slip[BR] = friction_s * (others[3]*wheel_radius - speed);
                return (slip[FL] + slip[BL] + slip[FR] + slip[BR]) - environment.getGravity()*Math.sin(others[4])/rover_mass;
            };

    private void updatePhysics() {
		// Motor Currents, based on voltage
        motor_current[FL] = RK4.advance(motorCurrentFn, time_step, 0, motor_current[FL], motor_power[FL], motor_states[FL], wheel_speed[FL]);
        motor_current[FR] = RK4.advance(motorCurrentFn, time_step, 0, motor_current[FR], motor_power[FR], motor_states[FR], wheel_speed[FR]);
        motor_current[BL] = RK4.advance(motorCurrentFn, time_step, 0, motor_current[BL], motor_power[BL], motor_states[BL], wheel_speed[BL]);
        motor_current[BR] = RK4.advance(motorCurrentFn, time_step, 0, motor_current[BR], motor_power[BR], motor_states[BR], wheel_speed[BR]);

        // angular motor speeds, based on torques
        wheel_speed[FL] = RK4.advance(wheelSpeedFn, time_step, 0, wheel_speed[FL], motor_current[FL], slip[FL]);
        wheel_speed[FR] = RK4.advance(wheelSpeedFn, time_step, 0, wheel_speed[FR], motor_current[FR], slip[FR]);
        wheel_speed[BL] = RK4.advance(wheelSpeedFn, time_step, 0, wheel_speed[BL], motor_current[BL], slip[BL]);
        wheel_speed[BR] = RK4.advance(wheelSpeedFn, time_step, 0, wheel_speed[BR], motor_current[BR], slip[BR]);

        // translational friction, approximately the same for all wheels
		fric_gr_all = friction_gr * motor_arm * angular_velocity;
		// Slip forces on wheels, based on speed differences
		slip[FL] = friction_s * (wheel_speed[FL]*wheel_radius - speed);
		slip[FR] = friction_s * (wheel_speed[FR]*wheel_radius - speed);
		slip[BL] = friction_s * (wheel_speed[BL]*wheel_radius - speed);
		slip[BR] = friction_s * (wheel_speed[BR]*wheel_radius - speed);
        angular_acceleration = 1/rover_inertia * ((motor_arm*(slip[FR] + slip[BR] - slip[FL] - slip[BL])*Math.cos(gamma) - motor_arm*(4*fric_gr_all)));

        acceleration = accelerationFn.eval(0, speed, wheel_speed[FL], wheel_speed[FR], wheel_speed[BL], wheel_speed[BR], environment.getSlopeAt(location, direction));

        // Speed changes based on Acceleration
        speed = RK4.advance(accelerationFn, time_step, 0, speed, wheel_speed[FL], wheel_speed[FR], wheel_speed[BL], wheel_speed[BR], environment.getSlopeAt(location, direction));
        angular_velocity += angular_acceleration * time_step;
        // Calculate the amount the rover slips sideways
        slip_acceleration = (-friction_gr*slip_velocity*4 - rover_mass*environment.getGravity()*Math.sin(environment.getCrossSlopeAt(location, direction)) / rover_mass);
        slip_velocity += slip_acceleration * time_step;

        location.offsetThis(speed*time_step*Math.cos(direction), speed*time_step*Math.sin(direction));
        location.offsetThis(slip_velocity*time_step*Math.cos(direction-Math.PI/2.0), slip_velocity*time_step*(Math.sin(direction-Math.PI/2.0)));
        direction = (direction + angular_velocity*time_step + 2*Math.PI) % (2*Math.PI);

        // update state
        rov_state.set("x", location.getX());
        rov_state.set("y", location.getY());
        rov_state.set("direction", direction);
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
        rov_state.set("speed", speed);
        rov_state.set("angular_velocity", angular_velocity);
        rov_state.set("acceleration", acceleration);
        rov_state.set("angular_acceleration", angular_acceleration);
        rov_state.set("slip_acceleration", slip_acceleration);
        rov_state.set("slip_velocity", slip_velocity);

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
