package com.csm.rover.simulator.environments.rover;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.rover.RoverObject;

import java.util.ArrayList;

@Environment(type="Rover")
public class TerrainEnvironment extends PlatformEnvironment {

    private ArrayList<RoverObject> rovers;

    public TerrainEnvironment(){
        super("Rover");
        rovers = new ArrayList<>();
    }

    @Override
    protected void doPlacePlatform(Platform platform) {
        rovers.add((RoverObject)platform);
    }
}
