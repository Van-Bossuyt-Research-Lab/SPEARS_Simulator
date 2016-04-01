package com.csm.rover.simulator.environments;

public abstract class EnvironmentMap {

    protected final String platform_type;

    protected EnvironmentMap(String type){
        platform_type = type;
    }

    public String getType(){
        return platform_type;
    }
}
