package com.csm.rover.simulator.platforms.rover;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

@State(type="Rover")
public class RoverState extends PlatformState {

    public RoverState(){
        super(
                PlatformState.builder("Rover")
                        .addParameter("motor_power", Double[].class, new Double[]{250., 250., 250., 250.})
                        .addParameter("motor_states", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("wheel_speed", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("motor_current", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("battery_charge", Double.class, 0.)
                        .addParameter("battery_voltage", Double.class, 12.)
                        .addParameter("battery_current", Double.class, 0.)
                        .addParameter("battery_temperature", Double.class, -30.)
                        .addParameter("motor_temp", Double[].class, new Double[]{-30., -30., -30., -30.})
                        .addParameter("x", Double.class, 0.)
                        .addParameter("y", Double.class, 0.)
                        .addParameter("direction", Double.class, Math.PI / 2.0)
                        .addParameter("speed", Double.class, 0.)
                        .addParameter("angular_velocity", Double.class, 0.)
                        .addParameter("acceleration", Double.class, 0.)
                        .addParameter("angular_acceleration", Double.class, 0.)
                        .addParameter("slip_acceleration", Double.class, 0.)
                        .addParameter("slip_velocity", Double.class, 0.)
        );
    }

}
