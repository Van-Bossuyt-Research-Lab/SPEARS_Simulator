package com.csm.rover.simulator.environments.sub.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.sub.AquaticMap;
import com.csm.rover.simulator.objects.util.ArrayGrid3D;
import com.csm.rover.simulator.objects.util.FloatArrayArrayArrayGrid;

import java.util.Map;

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
        float val = 1;

        ArrayGrid3D<Float> densityMap = new FloatArrayArrayArrayGrid();
        densityMap.fillToSize(true_size, true_size, true_size, val);
        return new AquaticMap(size, detail, densityMap);
    }

}