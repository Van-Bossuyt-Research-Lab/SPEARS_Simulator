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
import com.spears.environments.annotations.Populator;
import com.spears.objects.util.RecursiveGridList;

import com.spears.ui.visual.PopulatorDisplayFunction;

import java.util.Map;

@Populator(type = "Mole", name = "Crystal Pop", parameters = {"size"}, coordinates = {"x", "y", "z"})
public class UnknownPop extends EnvironmentPopulator {

    public UnknownPop() {
        super("Mole", "Crystal Pop", -1);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        RecursiveGridList<Double> out = RecursiveGridList.newGridList(Double.class, 2);
        out.put(0., 0, 0);
        out.put(0.5, 0, 1);
        out.put(0.5, 1, 0);
        out.put(1., 1, 1);
        //out.put(skipping for test, 0, 2);
        out.put(1.6, 1, 2);
        out.put(1.3, 2, 0);
        out.put(1.6, 2, 1);
        out.put(2., 2, 2);
        return out;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return null;
    }

}
