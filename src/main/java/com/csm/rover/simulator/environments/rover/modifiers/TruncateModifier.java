package com.csm.rover.simulator.environments.rover.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;

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
        for (int i = 0; i < map.rawValues().size(); i++){
            for (int j = 0; j < map.rawValues().size(); j++){
                values.put(i, j, (float)(Math.round(map.rawValues().get(i, j)*offset)/offset));
            }
        }
        return new TerrainMap(params.get("size").intValue(), params.get("detail").intValue(), values);
    }
}
