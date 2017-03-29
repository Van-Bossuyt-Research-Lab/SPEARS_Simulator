package com.csm.rover.simulator.test.objects.PhysicsModels;

import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import com.csm.rover.simulator.test.objects.states.RoverState;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@PhysicsModel(type = "Rover", name = "Generic", parameters = {})
public abstract class AbstractRoverPhysics extends PlatformPhysicsModel {

    public Map<String, Double> paramMap = new TreeMap<>();

    protected AbstractRoverPhysics() {
        super("UAV");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {
        paramMap = Collections.unmodifiableMap(params);
    }

    @Override
    public void start() {

    }

    @Override
    public void setPlatformName(String name) {

    }

    private RoverState state;

    @Override
    public void initializeState(PlatformState state) {
        this.state = (RoverState)state;
    }

    @Override
    public PlatformState getState() {
        return state;
    }
}
