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

package com.csm.rover.simulator.platforms.sub;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

/**
 * Implementation of PlatformState for the Sub Platform.
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
