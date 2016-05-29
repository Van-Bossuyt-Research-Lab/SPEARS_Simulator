package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.platforms.Platform;

import java.util.*;

public abstract class PlatformEnvironment<P extends Platform, M extends EnvironmentMap> {

    protected final String platform_type;

    protected M map;

    protected List<P> platforms;

    private EnvironmentModifier baseGen;
    private ArrayList<EnvironmentModifier> modifiers;

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

    public final void addModifier(EnvironmentModifier mod){
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
    public final void build(Map<String, Double> params){
        map = (M)baseGen.modify(null, params);
        for (EnvironmentModifier mod : modifiers){
            map = (M)mod.modify(map, params);
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

    @SuppressWarnings("unchecked")
    public final <T> T getPopulatorValue(String pop, double... cooordinates){
        if (populators.containsKey(pop)){
            T out;
            try {
                out = (T)populators.get(pop).getValue(cooordinates);
                return out;
            }
            catch (ClassCastException e){
                throw new RuntimeException("Wrong return type request", e);
            }
        }
        else {
            throw new IllegalArgumentException("No populator with name "+pop);
        }
    }

}
