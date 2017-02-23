package com.csm.rover.simulator.test.objects.maps;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.annotations.Map;

@Map(type = "Sub")
public class ProtectedSeaMap extends EnvironmentMap {

    protected ProtectedSeaMap() {
        super("Sub");
    }
}
