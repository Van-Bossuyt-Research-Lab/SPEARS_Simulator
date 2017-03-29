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

package com.spears.environments.sub.modifiers;

import com.spears.environments.EnvironmentModifier;
import com.spears.environments.annotations.Modifier;
import com.spears.environments.sub.AquaticMap;
import com.spears.objects.util.ArrayGrid3D;

import java.util.Map;

@Modifier(name="Smoother", type="Sub", parameters={})
public class Smoothing3D extends EnvironmentModifier<AquaticMap> {

    public Smoothing3D() {
        super("Sub");
    }

    @Override
    protected AquaticMap doModify(AquaticMap map, Map<String, Double> params) {
        ArrayGrid3D<Float> values = map.rawValues();
        ArrayGrid3D<Float> values2 = map.rawValues();
        int count = 9;
        for (int x = 0; x < values.getWidth(); x++){
            for (int y = 0; y < values.getHeight(); y++) {
                for (int z = 0; z < values.getDepth(); z++) {
                    if (x > 2 && y > 2 && z > 2 && x < values.getWidth() - 2 && y < values.getHeight() - 2 && z < values.getDepth() - 2) {
                        if (count % 4 == 0) {
                            values.put(x, y, z, (values2.get(x, y, z) + values2.get(x - 1, y - 2, z-2) + values2.get(x + 1, y + 2, z +2)) / 3.f);
                        }
                        else if (count % 4 == 1) {
                            values.put(x, y, z, (values2.get(x, y, z) + values2.get(x - 2, y - 1, z-2) + values2.get(x + 2, y + 1, z +2)) / 3.f);
                        }
                        else if (count % 4 == 2) {
                            values.put(x, y, z, (values2.get(x, y, z) + values2.get(x - 2, y + 1, z-2) + values2.get(x + 2, y - 1, z +2))/ 3.f);
                        }
                        else {
                            values.put(x, y, z, (values2.get(x, y, z) + values2.get(x - 1, y + 2, z-2) + values2.get(x + 1, y - 2, z +2)) / 3.f);
                        }
                    }
                }
            }
            count = (count +1) % Integer.MAX_VALUE;
        }
        return new AquaticMap(params.get("size").intValue(), params.get("detail").intValue(), values);
    }

}

