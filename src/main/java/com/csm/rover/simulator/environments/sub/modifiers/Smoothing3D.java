package com.csm.rover.simulator.environments.sub.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.sub.AquaticMap;
import com.csm.rover.simulator.objects.util.ArrayGrid3D;

import java.util.Map;

@Modifier(name="Smoother", type="Sub", parameters={})
public class Smoothing3D extends EnvironmentModifier<AquaticMap> {

    public Smoothing3D() {
        super("Sub");
    }

    @Override
    protected AquaticMap doModify(AquaticMap map, Map<String, Double> params) {
        ArrayGrid3D values = map.rawValues();
        ArrayGrid3D values2 = map.rawValues();
        int count = 9;
        for (int x = 0; x < values.getWidth(); x++){
            for (int y = 0; y < values.getHeight(); y++) {
                for (int z = 0; z < values.getDepth(); z++) {
                    if (x > 2 && y > 2 && x < values.getWidth() - 2 && y < values.getHeight() - 2) {
                        if (count % 4 == 0) {
                            values.put(x, y, z, (values2.get(x, y, z) + values2.get(x - 1, y - 2, z-3) + values2.get(x + 1, y + 2, z +3)) / 3.f);
                        } else if (count % 4 == 1) {
                            values.put(x, y, z, (values2.get(x, y, z) + values2.get(x - 2, y - 1, z-3) + values2.get(x + 2, y + 1, z +3)) / 3.f);
                        } else if (count % 4 == 2) {
                            values.put(x, y, z, (values2.get(x, y, z) + values2.get(x - 2, y + 1, z-3) + values2.get(x + 2, y - 1, z +3))/ 3.f);
                        } else {
                            values.put(x, y, z, (values2.get(x, y, z) + values2.get(x - 1, y + 2, z-3) + values2.get(x + 1, y - 2, z +3)) / 3.f);
                        }
                    }
                }
            }
            count = (count +1) % Integer.MAX_VALUE;
        }
        return new AquaticMap(params.get("size").intValue(), params.get("detail").intValue(), values);
    }

}

