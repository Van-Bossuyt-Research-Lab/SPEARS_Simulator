package com.csm.rover.simulator.test.objects.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.test.objects.maps.ProtectedSeaMap;

import java.util.Map;

@Modifier(type = "Sub", name = "Water Gen", parameters = {}, generator = true)
public class WaterGen extends EnvironmentModifier<ProtectedSeaMap> {

    public WaterGen() {
        super("Sub", true);
    }

    @Override
    protected ProtectedSeaMap doModify(ProtectedSeaMap map, Map<String, Double> params) {
        return map;
    }

}
