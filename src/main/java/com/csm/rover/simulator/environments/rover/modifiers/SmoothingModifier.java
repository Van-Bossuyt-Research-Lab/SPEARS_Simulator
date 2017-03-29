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

package com.csm.rover.simulator.environments.rover.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.objects.util.ArrayGrid;

import java.util.Map;

@Modifier(name="Smoother", type="Rover", parameters={})
public class SmoothingModifier extends EnvironmentModifier<TerrainMap> {

    public SmoothingModifier() {
        super("Rover");
    }

    @Override
    protected TerrainMap doModify(TerrainMap map, Map<String, Double> params) {
        ArrayGrid<Float> values = map.rawValues();
        ArrayGrid<Float> values2 = map.rawValues();
        int count = 9;
        for (int x = 0; x < values.getWidth(); x++){
            for (int y = 0; y < values.getHeight(); y++){
                if (x > 2 && y > 2 && x < values.getWidth()-2 && y < values.getHeight()-2) {
                    if (count % 4 == 0) {
                        values.put(x, y, (values2.get(x, y) + values2.get(x - 1, y - 2) + values2.get(x + 1, y + 2))/3.f);
                    }
                    else if (count % 4 == 1) {
                        values.put(x, y, (values2.get(x, y) + values2.get(x - 2, y - 1) + values2.get(x + 2, y + 1))/3.f);
                    }
                    else if (count % 4 == 2) {
                        values.put(x, y, (values2.get(x, y) + values2.get(x - 2, y + 1) + values2.get(x + 2, y - 1))/3.f);
                    }
                    else {
                        values.put(x, y, (values2.get(x, y) + values2.get(x - 1, y + 2) + values2.get(x + 1, y - 2))/3.f);
                    }
                }
            }
            count = (count +1) % Integer.MAX_VALUE;
        }
        return new TerrainMap(params.get("size").intValue(), params.get("detail").intValue(), values);
    }

}
