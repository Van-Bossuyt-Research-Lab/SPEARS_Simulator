package com.csm.rover.simulator.platforms;

public abstract class Platform {

    protected final String platform_type;

    protected Platform(String type){
        this.platform_type = type;
    }

    public final String getType(){
        return platform_type;
    }

}
