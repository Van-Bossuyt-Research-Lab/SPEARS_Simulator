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

import java.awt.*;
import java.util.Map;

@Populator(type = "Rover", name = "Rock Pop", parameters = { "paramA", "paramB" }, coordinates = { "x", "y" })
public class RockPop  extends EnvironmentPopulator {

    public RockPop() {
        super("Rover", "Rock Pop", 10);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        RecursiveGridList<Double> out = RecursiveGridList.newGridList(Double.class, 2);
        out.put(3.14, 3, 4);
        out.put(params.containsKey("use") ? params.get("use") : 0., 0, 0);
        return out;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return (d) -> Color.GREEN;
    }
}
