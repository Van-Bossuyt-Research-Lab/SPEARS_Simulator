package com.csm.rover.simulator.platforms;

import java.util.Map;

public abstract class PlatformPhysicsModel {

    protected final String platform_type;

    protected PlatformPhysicsModel(String type){
        this.platform_type = type;
    }

    public final String getType(){
        return platform_type;
    }

    public abstract void constructParameters(Map<String, Double> params);

    public abstract void start();
    public abstract void updatePhysics();

    public abstract void setPlatformName(String name);
    public abstract void initializeState(PlatformState state);
    public abstract PlatformState getState();

}
