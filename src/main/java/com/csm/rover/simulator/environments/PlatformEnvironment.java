package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.platforms.Platform;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public abstract class PlatformEnvironment {

    protected final String platform_type;

    protected EnvironmentMap map;

    private EnvironmentModifier baseGen;
    private ArrayList<EnvironmentModifier> modifiers;

    protected Map<String, EnvironmentPopulator> populators;

    protected PlatformEnvironment(String type){
        platform_type = type;
        modifiers = new ArrayList<>();
        populators = new TreeMap<>();
    }

    public final String getType(){
        return platform_type;
    }

    public final void placePlatform(Platform platform){
        if (!platform.getType().equals(platform)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, platform.getType()));
        }
        platform.setEnvironment(this);
        doPlacePlatform(platform);
    }

    abstract protected void doPlacePlatform(Platform platform);

    public final void setBaseGenerator(EnvironmentModifier mod){
        if (!baseGen.isGenerator()){
            throw new IllegalArgumentException("Base Generator must be a generation modifier");
        }
        baseGen = mod;
    }

    public final void addModifier(EnvironmentModifier mod){
        if (mod.isGenerator()){
            throw new IllegalArgumentException("Modifiers cannot be generators");
        }
        modifiers.add(mod);
    }

    public final void addPopulator(String name, EnvironmentPopulator pop){
        if (!pop.getType().equals(platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, pop.getType()));
        }
        if (populators.containsKey(name)){
            throw new IllegalArgumentException("A populator of name \"{}\" is already defined");
        }
        populators.put(name, pop);
    }

    public final void build(Map<String, Double> params){
        map = baseGen.modify(null, params);
        for (EnvironmentModifier mod : modifiers){
            map = mod.modify(map, params);
        }
        for (String pop : populators.keySet()){
            populators.get(pop).build(map, params);
        }
        buildActions();
    }

    protected void buildActions(){}

}
