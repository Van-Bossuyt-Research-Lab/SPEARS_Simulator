package com.csm.rover.simulator.platforms.sub;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

/**
 * Created by PHM-Lab1 on 4/29/2016.
 */
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
                        .addParameter("pitch", Double.class, Math.PI / 2.0)
                        .addParameter("yaw", Double.class, Math.PI / 2.0)
                        .addParameter("speed", Double.class, 0.)
                        .addParameter("pitch_velocity", Double.class, 0.)
                        .addParameter("yaw_velocity", Double.class, 0.)
                        .addParameter("acceleration", Double.class, 0.)
                        .addParameter("pitch_acceleration", Double.class, 0.)
                        .addParameter("yaw_acceleration", Double.class, 0.)
        );
    }
}
