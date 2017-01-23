package com.csm.rover.simulator.test.objects.platforms;

import com.csm.rover.simulator.platforms.Platform;

@com.csm.rover.simulator.platforms.annotations.Platform(type = "UAV")
public class UAVPlatform extends Platform {

    public UAVPlatform() {
        super("UAV");
    }
}
