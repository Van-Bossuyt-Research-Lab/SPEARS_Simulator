package com.csm.rover.simulator.platforms.satellite;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;

import java.util.Map;

@PhysicsModel(type="Satellite", name="Default", parameters={})
public class SatelliteParametersList extends PlatformPhysicsModel {

    public SatelliteParametersList(){
        super("Satellite");
    }

    @Override
    public void constructParameters(Map<String, Double> params){

    }

    @Override
    public void setEnvironment(PlatformEnvironment environment){}

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
