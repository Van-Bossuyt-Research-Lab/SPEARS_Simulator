package com.csm.rover.simulator.test.objects.PhysicsModels;

import com.csm.rover.simulator.platforms.annotations.PhysicsModel;

@PhysicsModel(type = "UAV", name = "Test UAV", parameters = {"paramA", "paramB"})
public class UAVPhysics extends AbstractUAVPhysics {

    public UAVPhysics() {}

}
