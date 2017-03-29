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

package com.spears.environments.rover.modifiers;

import com.spears.environments.EnvironmentModifier;
import com.spears.environments.annotations.Modifier;
import com.spears.environments.rover.TerrainMap;
import com.spears.objects.util.ArrayGrid;
import com.spears.objects.util.FloatArrayArrayGrid;

import java.util.Map;

@Modifier(type = "Rover", name = "Scaling", parameters = {"range"})
public class ScalingModifier extends EnvironmentModifier<TerrainMap> {

    public ScalingModifier() {
        super("Rover");
    }

    @Override
    protected TerrainMap doModify(TerrainMap map, Map<String, Double> params) {
        double range = params.get("range");
        int true_size = map.getSize()*map.getDetail()+1;

        ArrayGrid<Float> values = map.rawValues();

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int x = 0; x < values.getWidth(); x++){
            for (int y = 0; y < values.getHeight(); y++){
                double val = values.get(x, y);
                if (val < min){
                    min = val;
                }
                else if (val > max){
                    max = val;
                }
            }
        }
        max -= min;

        ArrayGrid<Float> heightmap = new FloatArrayArrayGrid();
        for (int x = 0; x < true_size; x++) {
            for (int y = 0; y < true_size; y++) {
                heightmap.put(x, y, (float) ((values.get(x, y) - min) * (range / max)));
            }
        }
        return new TerrainMap(map.getSize(), map.getDetail(), heightmap);
    }

}
