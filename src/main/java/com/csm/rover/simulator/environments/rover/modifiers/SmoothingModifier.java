package com.csm.rover.simulator.environments.rover.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.objects.util.ArrayGrid;

import java.util.Map;

public class SmoothingModifier extends EnvironmentModifier<TerrainMap> {


    protected SmoothingModifier() {
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
        return new TerrainMap(map.getSize(), map.getSize(), values);
    }

}
