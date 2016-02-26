package com.csm.rover.simulator.platforms;

public abstract class PlatformState {

    protected final String platform_type;

    protected PlatformState(String type){
        platform_type = type;
    }

    public String getType(){
        return platform_type;
    }

}
