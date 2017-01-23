package com.csm.rover.simulator.test.objects.PhysicsModels;

import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;

import java.util.Map;

@PhysicsModel(type = "UAV", name = "Generic", parameters = {})
public abstract class AbstractUAVPhysics extends PlatformPhysicsModel {

    protected AbstractUAVPhysics() {
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
