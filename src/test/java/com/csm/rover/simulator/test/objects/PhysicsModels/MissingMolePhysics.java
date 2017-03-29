package com.csm.rover.simulator.test.objects.PhysicsModels;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;

import java.util.Map;

@PhysicsModel(type = "Mole", name = "Star-nose", parameters = {"cuteness"})
public class MissingMolePhysics extends PlatformPhysicsModel {
    public MissingMolePhysics() {
        super("Mole");
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
