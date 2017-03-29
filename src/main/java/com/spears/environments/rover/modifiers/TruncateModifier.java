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

@Modifier(type = "Rover", name = "Truncate", parameters = {"places"})
public class TruncateModifier extends EnvironmentModifier<TerrainMap> {

    public TruncateModifier(){
        super("Rover");
    }

    @Override
    protected TerrainMap doModify(TerrainMap map, Map<String, Double> params) {
        ArrayGrid<Float> values = new FloatArrayArrayGrid();
        double offset = Math.pow(10, params.get("places"));
        for (int i = 0; i < map.rawValues().getWidth(); i++){
            for (int j = 0; j < map.rawValues().getHeight(); j++){
                values.put(i, j, (float)(Math.round(map.rawValues().get(i, j)*offset)/offset));
            }
        }
        return new TerrainMap(map.getSize(), map.getDetail(), values);
    }
}
