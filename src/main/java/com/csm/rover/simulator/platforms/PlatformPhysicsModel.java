package com.csm.rover.simulator.platforms;

public abstract class PlatformPhysicsModel {

    protected final String platform_type;

    protected PlatformPhysicsModel(String type){
        this.platform_type = type;
    }

    public final String getType(){
        return platform_type;
    }

//    public abstract void constructParameters(Map<String, Double> params);
//    public abstract Map<String, Double> getParameters();

}
