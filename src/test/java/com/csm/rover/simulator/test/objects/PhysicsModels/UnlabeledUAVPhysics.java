package com.csm.rover.simulator.test.objects.PhysicsModels;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;

import java.util.Map;

public class UnlabeledUAVPhysics extends PlatformPhysicsModel {
    public UnlabeledUAVPhysics() {
        super("UAV");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
    public void start() {

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
