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

package com.csm.rover.simulator.platforms.rover;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

/**
 * PlatformState implementation for the Rover Platform.
 */
@State(type="Rover")
public class RoverState extends PlatformState {

    public RoverState(){
        super(
                PlatformState.builder("Rover")
                        .addParameter("motor_state", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("motor_power", Double[].class, new Double[]{250., 250., 250., 250.})
                        .addParameter("motor_current", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("motor_voltage", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("motor_temp", Double[].class, new Double[]{-30., -30., -30., -30.})
                        .addParameter("wheel_speed", Double[].class, new Double[]{0., 0., 0., 0.})
                        .addParameter("battery_charge", Double.class, 0.)
                        .addParameter("battery_voltage", Double.class, 12.)
                        .addParameter("battery_current", Double.class, 0.)
                        .addParameter("battery_temp", Double.class, -30.)
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
