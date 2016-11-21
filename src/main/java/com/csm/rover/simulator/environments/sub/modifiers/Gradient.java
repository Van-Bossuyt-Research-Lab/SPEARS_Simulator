package com.csm.rover.simulator.environments.sub.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.sub.AquaticMap;
import com.csm.rover.simulator.objects.ArrayGrid3D;
import com.csm.rover.simulator.objects.FloatArrayArrayArrayGrid;

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
        int true_size = size*detail;
        float val= 1;

        ArrayGrid3D<Float> densityMap = new FloatArrayArrayArrayGrid();
        densityMap.fillToSize(true_size,true_size,true_size,val);
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
