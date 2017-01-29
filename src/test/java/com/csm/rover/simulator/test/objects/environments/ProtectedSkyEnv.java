package com.csm.rover.simulator.test.objects.environments;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Environment;

@Environment(type = "UAV")
public class ProtectedSkyEnv extends PlatformEnvironment {

    protected ProtectedSkyEnv() {
        super("UAV");
    }

}
