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

package com.csm.rover.simulator.test.objects.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.objects.util.RecursiveGridList;

import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import java.util.Map;

@Populator(type = "UAV", name = "Cloud Pop", parameters = {}, coordinates = { "x", "y", "z" })
public class PrivateCloudPop extends EnvironmentPopulator {

    private PrivateCloudPop() {
        super("UAV", "Cloud Pop", 1);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        return null;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return null;
    }
}
