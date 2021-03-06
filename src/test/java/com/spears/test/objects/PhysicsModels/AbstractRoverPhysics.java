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

package com.spears.test.objects.PhysicsModels;

import com.spears.platforms.PlatformPhysicsModel;
import com.spears.platforms.PlatformState;
import com.spears.platforms.annotations.PhysicsModel;
import com.spears.test.objects.states.RoverState;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@PhysicsModel(type = "Rover", name = "Generic", parameters = {})
public abstract class AbstractRoverPhysics extends PlatformPhysicsModel {

    public Map<String, Double> paramMap = new TreeMap<>();

    protected AbstractRoverPhysics() {
        super("UAV");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {
        paramMap = Collections.unmodifiableMap(params);
    }

    @Override
    public void start() {

    }

    @Override
    public void setPlatformName(String name) {

    }

    private RoverState state;

    @Override
    public void initializeState(PlatformState state) {
        this.state = (RoverState)state;
    }

    @Override
    public PlatformState getState() {
        return state;
    }
}
