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

package com.spears.test.objects.populators;

import com.spears.environments.EnvironmentMap;
import com.spears.environments.EnvironmentPopulator;
import com.spears.objects.util.RecursiveGridList;

import com.spears.ui.visual.PopulatorDisplayFunction;

import java.util.Map;

public class UnlabeledKelpPop extends EnvironmentPopulator {

    public UnlabeledKelpPop() {
        super("Sub", "Kelp Pop", 0);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        RecursiveGridList<Double> out = RecursiveGridList.newGridList(Double.class, 3);
        out.put(5., 2, 6, -1);
        return out;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return null;
    }
}
