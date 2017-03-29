package com.csm.rover.simulator.test.objects.environments;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.test.objects.maps.LandMap;
import com.csm.rover.simulator.test.objects.platforms.RoverPlatform;

import java.util.Collections;
import java.util.Map;

@Environment(type = "Rover")
public class LandEnv extends PlatformEnvironment<RoverPlatform, LandMap> {

    public LandEnv() {
        super("Rover");
    }

    public LandEnv(Map<String, EnvironmentPopulator> pops){
        super("Rover", new LandMap(), pops);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
