package com.csm.rover.simulator.test.objects.PhysicsModels;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;

@PhysicsModel(type = "Rover", name = "Test Rover", parameters = {"paramA", "paramB"})
public class RoverPhysics extends AbstractRoverPhysics {

    public RoverPhysics() {}

    @Override
    public void setEnvironment(PlatformEnvironment enviro) {

    }
}
