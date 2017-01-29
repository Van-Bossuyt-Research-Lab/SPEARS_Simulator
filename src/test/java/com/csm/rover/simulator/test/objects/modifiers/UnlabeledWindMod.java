package com.csm.rover.simulator.test.objects.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.test.objects.maps.UnlabeledSkyMap;

import java.util.Map;

public class UnlabeledWindMod extends EnvironmentModifier<UnlabeledSkyMap> {

    public UnlabeledWindMod() {
        super("UAV");
    }

    @Override
    protected UnlabeledSkyMap doModify(UnlabeledSkyMap map, Map<String, Double> params) {
        return null;
    }
}
