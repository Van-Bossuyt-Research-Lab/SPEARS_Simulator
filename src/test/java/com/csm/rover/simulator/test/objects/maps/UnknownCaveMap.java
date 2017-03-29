package com.csm.rover.simulator.test.objects.maps;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.annotations.Map;

@Map(type = "Mole")
public class UnknownCaveMap extends EnvironmentMap {

    public UnknownCaveMap() {
        super("Mole");
    }

    @Override
    protected boolean isEqual(EnvironmentMap other) {
        return this == other;
    }
}
