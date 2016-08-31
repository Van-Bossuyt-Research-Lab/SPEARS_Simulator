package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.platforms.Platform;

import java.util.*;

public abstract class PlatformEnvironment<P extends Platform, M extends EnvironmentMap> {

    protected final String platform_type;

    protected M map;

    protected List<P> platforms;

    private EnvironmentModifier<M> baseGen;
    private ArrayList<EnvironmentModifier<M>> modifiers;

    private Map<String, EnvironmentPopulator> populators;

    private Optional<Runnable> buildActions;
    private Optional<Runnable> addedActions;

    protected PlatformEnvironment(String type){
        platform_type = type;
        modifiers = new ArrayList<>();
        populators = new TreeMap<>();
        platforms = new ArrayList<>();
        buildActions = Optional.empty();
        addedActions = Optional.empty();
    }

    public final String getType(){
        return platform_type;
    }

    public final void addPlatform(P platform){
        if (!platform.getType().equals(this.platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, platform.getType()));
        }
        platform.setEnvironment(this);
        platforms.add(platform);
        if (addedActions.isPresent()){
            addedActions.get().run();
        }
    }

    protected final void setPlatformAddedActions(Runnable run){
        addedActions = Optional.of(run);
    }

    public final void setBaseGenerator(EnvironmentModifier mod){
        if (!mod.getType().equals(this.platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, mod.getType()));
        }
        if (!baseGen.isGenerator()){
            throw new IllegalArgumentException("Base Generator must be a generation modifier");
        }
        baseGen = mod;
    }

    public final void addModifier(EnvironmentModifier<M> mod){
        if (!mod.getType().equals(this.platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, mod.getType()));
        }
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

    @SuppressWarnings("unchecked")
    public final void generate(Map<String, Double> params){
        map = baseGen.modify(null, params);
        for (EnvironmentModifier<M> mod : modifiers){
            map = mod.modify(map, params);
        }
        for (String pop : populators.keySet()){
            populators.get(pop).build(map, params);
        }
        if (buildActions.isPresent()){
            buildActions.get().run();
        }
    }

    protected final void setBuildActions(Runnable run){
        buildActions = Optional.of(run);
    }

    public final double getPopulatorValue(String pop, double... coordinates){
        if (populators.containsKey(pop)){
            return populators.get(pop).getValue(coordinates);
        }
        else {
            throw new IllegalArgumentException("No populator with name "+pop);
        }
    }

    public final boolean isPopulatorAt(String pop, double... coordinates){
        if (populators.containsKey(pop)){
            return populators.get(pop).getValue(coordinates) > 0;
        }
        else {
            throw new IllegalArgumentException("No populator with name "+pop);
        }
    }

}
