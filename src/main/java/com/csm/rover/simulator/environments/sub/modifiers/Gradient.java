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

package com.csm.rover.simulator.environments.sub.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.sub.AquaticMap;
import com.csm.rover.simulator.objects.util.ArrayGrid3D;
import com.csm.rover.simulator.objects.util.FloatArrayArrayArrayGrid;

import java.util.Map;

@Modifier(name="Gradient", type="Sub", parameters={"size", "detail"}, generator=true)
public class Gradient extends EnvironmentModifier<AquaticMap> {

    public Gradient() {
        super("Sub", true);
    }

    @Override
    protected AquaticMap doModify(AquaticMap map, Map<String, Double> params) {
        int size = params.get("size").intValue();
        int detail = params.get("detail").intValue();
        int true_size = size*detail + 1;
        float val = 1;

        ArrayGrid3D<Float> densityMap = new FloatArrayArrayArrayGrid();
        densityMap.fillToSize(true_size, true_size, true_size, val);
        for(int i=0; i<true_size; i++){
            for(int j=0; j<true_size; j++){
                for(int k=0; k<true_size; k++){
                    densityMap.put(i,j,k, (float)i*5);
                }
            }
        }
        return new AquaticMap(size, detail, densityMap);
    }
}
