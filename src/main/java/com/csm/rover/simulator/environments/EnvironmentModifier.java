package com.csm.rover.simulator.environments;

import java.util.Map;

public abstract class EnvironmentModifier {

    protected final String platform_type;
    protected final boolean generator;

    protected EnvironmentModifier(String type){
        platform_type = type;
        generator = false;
    }

    protected EnvironmentModifier(String type, boolean generator){
        platform_type = type;
        this.generator = generator;
    }

    public String getType(){
        return platform_type;
    }

    public boolean isGenerator(){
        return generator;
    }

    public final EnvironmentMap modify(EnvironmentMap map, Map<String, Double> params){
        if (!generator && !map.getType().equals(platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, map.getType()));
        }
        return doModify(map, params);
    }

    abstract protected EnvironmentMap doModify(final EnvironmentMap map, final Map<String, Double> params);

}