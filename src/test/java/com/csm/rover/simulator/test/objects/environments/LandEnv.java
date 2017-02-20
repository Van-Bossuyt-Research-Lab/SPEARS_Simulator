package com.csm.rover.simulator.test.objects.environments;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Environment;

@Environment(type = "Rover")
public class LandEnv extends PlatformEnvironment {

    public LandEnv() {
        super("Rover");
    }

    @Override
    public int getSize() {
        return 0;
    }
}
