package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.objects.io.jsonserial.PopulatorListDeserializer;
import com.csm.rover.simulator.objects.io.jsonserial.PopulatorListSerializer;
import com.csm.rover.simulator.platforms.Platform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;

public abstract class PlatformEnvironment<P extends Platform, M extends EnvironmentMap> {

    @JsonProperty("type")
    protected final String platform_type;

    @JsonProperty("map")
    protected M map;

    @JsonIgnore
    protected List<P> platforms;

    @JsonProperty("populators")
    @JsonSerialize(using = PopulatorListSerializer.class)
    @JsonDeserialize(using = PopulatorListDeserializer.class)
    private Map<String, EnvironmentPopulator> populators;

    @JsonIgnore
    private Optional<Runnable> buildActions;
    @JsonIgnore
    private Optional<Runnable> addedActions;

    protected PlatformEnvironment(String type){
        platform_type = type;
        populators = new TreeMap<>();
        platforms = new ArrayList<>();
        buildActions = Optional.empty();
        addedActions = Optional.empty();
    }

    protected PlatformEnvironment(String type, M map, Map<String, EnvironmentPopulator> pops){
        this(type);
        populators = pops;
        this.map = map;
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

    public final MapBuilder generateNewMap(EnvironmentModifier<M> gen, Map<String, Double> params){
        MapBuilder builder = new MapBuilder(platform_type);
        return builder.setBaseGenerator(gen, params);
    }

    public class MapBuilder {

        private final String platform_type;
        private EnvironmentModifier<M> gen;
        private List<EnvironmentModifier<M>> mods;
        private Map<String, EnvironmentPopulator> pops;

        private Map<String, Double> params;

        private MapBuilder(String type) {
            platform_type = type;
            mods = new ArrayList<>();
            pops = new HashMap<>();
            params = new HashMap<>();
        }

        private MapBuilder setBaseGenerator(EnvironmentModifier mod, Map<String, Double> params) {
            if (!mod.getType().equals(this.platform_type)) {
                throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, mod.getType()));
            }
            if (!mod.isGenerator()) {
                throw new IllegalArgumentException("Base Generator must be a generation modifier");
            }
            gen = mod;
            for (String key : params.keySet()){
                this.params.put(key, params.get(key));
            }
            return this;
        }

        public MapBuilder addMapModifier(EnvironmentModifier<M> mod, Map<String, Double> params) {
            if (!mod.getType().equals(this.platform_type)) {
                throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, mod.getType()));
            }
            if (mod.isGenerator()) {
                throw new IllegalArgumentException("Modifiers cannot be generators");
            }
            mods.add(mod);
            for (String key : params.keySet()){
                this.params.put(key, params.get(key));
            }
            return this;
        }

        public MapBuilder addPopulator(String name, EnvironmentPopulator pop, Map<String, Double> params) {
            if (!pop.getType().equals(platform_type)) {
                throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, pop.getType()));
            }
            if (populators.containsKey(name)) {
                throw new IllegalArgumentException("A populator of name \"{}\" is already defined");
            }
            pops.put(name, pop);
            for (String key : params.keySet()){
                this.params.put(key, params.get(key));
            }
            return this;
        }

        public void generate(){
            doBuildMap(gen, mods, pops, params);
        }
    }

    @SuppressWarnings("unchecked")
    private final void doBuildMap(EnvironmentModifier<M> baseGen, List<EnvironmentModifier<M>> modifiers, Map<String, EnvironmentPopulator> pops, Map<String, Double> params){
        map = baseGen.modify(null, params);
        for (EnvironmentModifier<M> mod : modifiers){
            map = mod.modify(map, params);
        }
        for (String pop : pops.keySet()){
            pops.get(pop).build(map, params);
            populators.put(pop, pops.get(pop));
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

    @JsonIgnore
    public final List<String> getPopulators(){
        return Collections.unmodifiableList(new ArrayList<>(populators.keySet()));
    }

    @JsonIgnore
    public List<P> getPlatforms(){
        return Collections.unmodifiableList(platforms);
    }

}
