package com.csm.rover.simulator.test.objects.platforms;

import com.csm.rover.simulator.platforms.Platform;

@com.csm.rover.simulator.platforms.annotations.Platform(type="Rover")
public class RoverPlatform extends Platform {

    public RoverPlatform() {
        super("Rover");
    }

    @Override
    public void start() {

    }
}
