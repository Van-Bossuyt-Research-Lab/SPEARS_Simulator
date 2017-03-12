package com.csm.rover.simulator.platforms.sub;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

@State(type = "Sub")
public class SubState extends PlatformState {
    public SubState(){
        super(
                PlatformState.builder("Sub")
                        .addParameter("motor_state", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("motor_power", Double[].class, new Double[]{250., 250., 250., 250.})
                        .addParameter("motor_current", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("motor_voltage", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("motor_temp", Double[].class, new Double[]{-30., -30., -30., -30.})
                        .addParameter("prop_speed", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("x", Double.class, 0.)
                        .addParameter("y", Double.class, 0.)
                        .addParameter("z", Double.class, 0.)
                        .addParameter("pitch", Double.class, 0.)
                        .addParameter("yaw", Double.class, 0.)
                        .addParameter("roll", Double.class, 0.)
                        .addParameter("speed", Double[].class, new Double[]{0., 0., 0.})
                        .addParameter("angular_speed", Double[].class, new Double[]{0., 0., 0.})
                        .addParameter("acceleration", Double[].class, new Double[]{0., 0., 0.})
                        .addParameter("angular_acceleration", Double[].class, new Double[]{0., 0., 0.})
        );
    }
}
