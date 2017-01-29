package com.csm.rover.simulator.test.objects.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.test.objects.maps.ProtectedSeaMap;

import java.util.Map;

@Modifier(type = "Sub", name = "Current Mod", parameters = {})
public class ProtectedCurrentMod extends EnvironmentModifier<ProtectedSeaMap> {

    protected ProtectedCurrentMod() {
        super("Sub");
    }

    @Override
    protected ProtectedSeaMap doModify(ProtectedSeaMap map, Map<String, Double> params) {
        return null;
    }

}
