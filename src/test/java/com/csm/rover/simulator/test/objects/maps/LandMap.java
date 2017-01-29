package com.csm.rover.simulator.test.objects.maps;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.annotations.Map;

@Map(type = "Rover")
public class LandMap extends EnvironmentMap {

    public LandMap() {
        super("Rover");
    }

}
