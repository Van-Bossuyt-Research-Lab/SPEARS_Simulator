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

package com.csm.rover.simulator.environments.rover.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.environments.rover.modifiers.PlasmaFractalGen;
import com.csm.rover.simulator.environments.rover.modifiers.ScalingModifier;
import com.csm.rover.simulator.environments.rover.modifiers.TruncateModifier;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;

import java.awt.*;
import java.util.Map;

@Populator(type = "Rover", name = "Hazards", coordinates = {"x", "y"}, parameters = {"hzrd_mono", "hzrd_rough"})
public class TerrainHazardsPop extends EnvironmentPopulator {

    public TerrainHazardsPop(){
        super("Rover", "Hazards", 10);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        boolean mono = params.get("hzrd_mono") == 1;
        int size = ((TerrainMap)map).getSize()+1;
        double rough = params.get("hzrd_rough");
        ArrayGrid<Float> plasma1 = getLayer(size+size%2, rough, 3);
        ArrayGrid<Float> plasma2 = getLayer(size+size%2, rough*20, 8);
        ArrayGrid<Float> added = new FloatArrayArrayGrid();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                added.put(x, y, (plasma1.get(x, y) + plasma2.get(x, y)));
            }
        }
        added = new TruncateModifier().modify(
                new ScalingModifier().modify(
                        new TerrainMap(((TerrainMap) map).getSize(), 1, added),
                        ParamMap.newParamMap().addParameter("range", 10).build()),
                ParamMap.newParamMap().addParameter("places", 0).build()
        ).rawValues();
        RecursiveGridList<Double> hzrds = RecursiveGridList.newGridList(Double.class, 2);
        for (int x = 0; x < size-1; x++){
            for (int y = 0; y < size-1; y++){
                double value = (double)added.get(x, y);
                hzrds.put(mono ? (value >=7 ? 10 : 0) : (value > 10 ? 10 : value), x-size/2, y-size/2);
            }
        }
        return hzrds;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return (value) -> value < 5 ? null : new Color((int)((11-value)*20+100), (int)((11-value)*20+100), (int)((11-value)*20+100));
    }

    private ArrayGrid<Float> getLayer(int size, double rough, int range){
        return new ScalingModifier().modify(
                new PlasmaFractalGen().modify(
                        null,
                        ParamMap.newParamMap()
                                .addParameter("size", size)
                                .addParameter("detail", 1)
                                .addParameter("rough", rough).build()),
                    ParamMap.newParamMap().addParameter("range", range).build()).rawValues();
    }

}
