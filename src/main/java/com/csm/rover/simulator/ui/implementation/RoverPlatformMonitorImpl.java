package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.platforms.Platform;

@FrameMarker(name = "Rover Monitor", platform = "Rover")
class RoverPlatformMonitorImpl extends PlatformDisplay {

    RoverPlatformMonitorImpl(){
        super("Rover");
        initialize();
    }

    private void initialize(){
        setTitle("");
    }

    @Override
    public void doSetPlatform(Platform platform) {

    }

}
