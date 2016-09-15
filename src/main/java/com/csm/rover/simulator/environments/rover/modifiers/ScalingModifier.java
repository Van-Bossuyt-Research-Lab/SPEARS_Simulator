package com.csm.rover.simulator.environments.rover.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;

import java.util.Map;

@Modifier(type = "Rover", name = "Scaling", parameters = {"range"})
public class ScalingModifier extends EnvironmentModifier<TerrainMap> {

    public ScalingModifier() {
        super("Rover");
    }

    @Override
    protected TerrainMap doModify(TerrainMap map, Map<String, Double> params) {
        double range = params.get("range");
        int true_size = map.getSize()*map.getDetail();

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
