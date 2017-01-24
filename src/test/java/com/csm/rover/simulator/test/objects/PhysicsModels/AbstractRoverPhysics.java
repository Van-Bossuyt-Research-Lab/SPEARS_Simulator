package com.csm.rover.simulator.test.objects.PhysicsModels;

import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;

import java.util.Map;

@PhysicsModel(type = "Rover", name = "Generic", parameters = {})
public abstract class AbstractRoverPhysics extends PlatformPhysicsModel {

    protected AbstractRoverPhysics() {
        super("UAV");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
    public void start() {

    }

    @Override
    public void updatePhysics() {

    }

    @Override
    public void setPlatformName(String name) {

    }

    @Override
    public void initializeState(PlatformState state) {

    }

    @Override
    public PlatformState getState() {
        return null;
    }
}
