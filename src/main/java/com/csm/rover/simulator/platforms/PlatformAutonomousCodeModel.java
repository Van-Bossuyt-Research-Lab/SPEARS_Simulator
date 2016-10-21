package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.environments.PlatformEnvironment;

import java.util.Map;

public abstract class PlatformAutonomousCodeModel {

    protected final String platform_type;

    protected PlatformAutonomousCodeModel(String type){
        this.platform_type = type;
    }

    public abstract void setEnvironment(PlatformEnvironment enviro);

    public final String getType(){
        return platform_type;
    }

    public abstract void constructParameters(Map<String, Double> params);

    public abstract String nextCommand(long milliTime, final PlatformState state);

}
