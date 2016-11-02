package com.csm.rover.simulator.environments.sub.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.sub.AquaticMap;
import com.csm.rover.simulator.objects.ArrayGrid3D;
import com.csm.rover.simulator.objects.FloatArrayArrayArrayGrid;

import java.util.Map;
import java.util.Random;

@Modifier(name="Empty Pool", type="Sub", parameters={"size", "detail"}, generator=true)
public class EmptyPoolGen extends EnvironmentModifier<AquaticMap> {

    public EmptyPoolGen() {
        super("Sub", true);
    }

    @Override
    protected AquaticMap doModify(AquaticMap map, Map<String, Double> params) {
        int size = params.get("size").intValue();
        int detail = params.get("detail").intValue();
        int true_size = size*detail + 1;
        ArrayGrid3D<Float> values = new FloatArrayArrayArrayGrid();

        Random rnd = new Random();

        float min = Float.MIN_VALUE;
        while (values.getWidth() < true_size){
            for (int x = 0; x < values.getWidth(); x++) {
                for (int y = 0; y < values.getHeight(); y++) {
                    for (int z = 0; y < values.getLength(); z++) {
                        float value = 0;
                        values.put(x, y, z, value);
                    }
                }
            }
        }

        ArrayGrid3D<Float> densityMap = new FloatArrayArrayArrayGrid();
        int xstart = (values.getWidth() - true_size)/2;
        int ystart = (values.getHeight() - true_size)/2;
        int zstart = (values.getLength() - true_size)/2;
        for (int x = 0; x < true_size; x++) {
            for (int y = 0; y < true_size; y++) {
                for (int z = 0; z < true_size; z++) {
                    densityMap.put(x, y, z, values.get(x + xstart, y + ystart, z + zstart)-min);
                }
            }
        }
        return new AquaticMap(size, detail, densityMap);
    }

}