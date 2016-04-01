package com.csm.rover.simulator.environments;

import java.util.Map;

public abstract class EnvironmentPopulator {

    protected final String platform_type;

    protected EnvironmentPopulator(String type){
        platform_type = type;
    }

    public String getType(){
        return platform_type;
    }

    public void build(EnvironmentMap map, Map<String, Double> params){
        if (!map.getType().equals(platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, map.getType()));
        }
        doBuild(map, params);
    }

    abstract protected void doBuild(final EnvironmentMap map, final Map<String, Double> params);

}
