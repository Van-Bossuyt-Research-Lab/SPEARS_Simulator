package com.csm.rover.simulator.platforms;

public abstract class PlatformAutonomousCodeModel {

    protected final String platform_type;

    protected PlatformAutonomousCodeModel(String type){
        this.platform_type = type;
    }

    public final String getType(){
        return platform_type;
    }

//    public abstract void constructParameters(Map<String, Double> params);
//    public abstract Map<String, Double> getParameters();



}
