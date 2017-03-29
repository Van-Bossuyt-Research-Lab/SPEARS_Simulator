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

package com.csm.rover.simulator.test.objects.PhysicsModels;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;

import java.util.Map;

@PhysicsModel(type = "Sub", name = "Test Sub", parameters = {})
public class ProtectedSubPhysics extends PlatformPhysicsModel {
    protected ProtectedSubPhysics() {
        super("Sub");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
    public void start() {

    }

    @Override
    public void setPlatformName(String name) {

    }

    @Override
    public void initializeState(PlatformState state) {

    }

    @Override
    public PlatformState getState() {
        return null;
    }

    @Override
    public void setEnvironment(PlatformEnvironment enviro) {

    }
}
