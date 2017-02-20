package com.csm.rover.simulator.test.objects.physicsModels;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;

import java.util.Map;

@PhysicsModel(type = "Sub", name = "Test Sub", parameters = {})
public class ProtectedSubPhysics extends PlatformPhysicsModel {
    protected ProtectedSubPhysics() {
        super("Sub");
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

    @Override
    public void setEnvironment(PlatformEnvironment enviro) {

    }
}
