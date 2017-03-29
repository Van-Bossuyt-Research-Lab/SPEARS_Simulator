/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.objects.io.jsonserial.PopulatorListDeserializer;
import com.csm.rover.simulator.objects.io.jsonserial.PopulatorListSerializer;
import com.csm.rover.simulator.platforms.Platform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Parent class for maintaining and creating the environment the the platforms operate within.
 *
 * @param <P> Associated Platform class
 * @param <M> Associated Map class
 */
public abstract class PlatformEnvironment<P extends Platform, M extends EnvironmentMap> {
    private final static Logger LOG = LogManager.getLogger(PlatformEnvironment.class);

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

    /**
     * Blank start, initializes all collections as empty.
     *
     * @param type The platform type name
     */
    protected PlatformEnvironment(String type){
        platform_type = type;
        populators = new TreeMap<>();
        platforms = new ArrayList<>();
        buildActions = Optional.empty();
        addedActions = Optional.empty();
    }

    /**
     * Creates the environment using the specified map and populators.  Use for loading saved images.
     *
     * @param type The type name
     * @param map A generated map
     * @param pops Build populators
     */
    protected PlatformEnvironment(String type, M map, Map<String, EnvironmentPopulator> pops){
        this(type);
        populators = pops;
        this.map = map;
    }

    public final String getType(){
        return platform_type;
    }

    /**
     * Adds a platform to the environment.  Calls {@link Platform#setEnvironment(PlatformEnvironment) platform.setEnvironment(this)}
     * to give the platform access to the environment.
     *
     * @param platform Platform to be added
     *
     * @throws IllegalArgumentException If the platform is not of the same type as the environment
     */
    public void addPlatform(P platform){
        if (!platform.getType().equals(this.platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, platform.getType()));
        }
        platform.setEnvironment(this);
        platforms.add(platform);
        if (addedActions.isPresent()){
            addedActions.get().run();
        }
    }

    /**
     * Sets a single action to be executed when new platforms are added to the environment via
     * {@link #addPlatform(Platform)}.
     *
     * @param run The action
     */
    protected final void setPlatformAddedActions(Runnable run){
        addedActions = Optional.of(run);
    }

    /**
     * Builder init method.  Creates a new {@link MapBuilder} object to create a new map instance.
     *
     * @param gen The generator to be used.  See {@link EnvironmentModifier}
     * @param params Build parameters for the generator gen
     * @return Builder pattern for a new map
     */
    public final MapBuilder generateNewMap(EnvironmentModifier<M> gen, Map<String, Double> params){
        MapBuilder builder = new MapBuilder(platform_type);
        return builder.setBaseGenerator(gen, params);
    }

    /**
     * Builder pattern object used for the creation of new maps.
     */
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
            addAllParams(params);
            return this;
        }

        /**
         * Adds a modifier to generation sequence.  Modifiers will be applied to the map in the order they are added.
         *
         * @param mod Modifier to add
         * @param params Build parameters for mod
         * @return this
         *
         * @throws IllegalArgumentException If the modifier has the wrong type
         */
        public MapBuilder addMapModifier(EnvironmentModifier<M> mod, Map<String, Double> params) {
            if (!mod.getType().equals(this.platform_type)) {
                throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, mod.getType()));
            }
            if (mod.isGenerator()) {
                throw new IllegalArgumentException("Modifiers cannot be generators");
            }
            mods.add(mod);
            addAllParams(params);
            return this;
        }

        /**
         * Adds a new populator image to the environment.  Populators should not be built, they will be built as part of
         * {@link #generate()}.
         *
         * @param pop The populator to add
         * @param params Build parameters for pop
         * @return this
         *
         * @throws IllegalArgumentException If the populator has the wrong type
         * @throws IllegalArgumentException If the same populator has already been added
         */
        public MapBuilder addPopulator(EnvironmentPopulator pop, Map<String, Double> params) {
            String name = pop.getName();
            if (!pop.getType().equals(platform_type)) {
                throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, pop.getType()));
            }
            if (pops.containsKey(name)) {
                throw new IllegalArgumentException("A populator of name \"" + name + "\" is already defined");
            }
            pops.put(name, pop);
            addAllParams(params);
            return this;
        }

        /**
         * Generates a new map.  Map is saved in the environment.
         */
        public void generate(){
            doBuildMap(gen, mods, pops, params);
        }

        private void addAllParams(Map<String, Double> newParams){
            for (String key : newParams.keySet()){
                if (params.containsKey(key)){
                    LOG.log(Level.WARN, "Parameter \"" + key + "\" is being overwritten");
                }
                this.params.put(key, newParams.get(key));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doBuildMap(EnvironmentModifier<M> baseGen, List<EnvironmentModifier<M>> modifiers, Map<String, EnvironmentPopulator> pops, Map<String, Double> params){
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

    /**
     * Adds an action to be run after a map is built, at the end of {@link MapBuilder#generate()}.
     *
     * @param run The action
     */
    protected final void setBuildActions(Runnable run){
        buildActions = Optional.of(run);
    }

    /**
     * Returns the value of the populator at the given coordinates.
     *
     * @param pop Name of the populator to quarry.
     * @param coordinates Coordinates to quarry, should match the dimension of the map
     * @return The populator value
     *
     * @throws IllegalArgumentException If the populator name is not currently defined
     */
    public final double getPopulatorValue(String pop, double... coordinates){
        if (populators.containsKey(pop)){
            return populators.get(pop).getValue(coordinates);
        }
        else {
            throw new IllegalArgumentException("No populator with name "+pop);
        }
    }

    /**
     * Returns a true if a populator is defined for the given point.  That is the poulator value is greater than 0, not
     * based on the default value.
     *
     * @param pop The populator name
     * @param coordinates Coordinates to quarry, should match the dimension of the map
     * @return True if defined
     *
     * @throws IllegalArgumentException If the populator name is not currently defined
     */
    public final boolean isPopulatorAt(String pop, double... coordinates){
        if (populators.containsKey(pop)){
            return populators.get(pop).getValue(coordinates) > 0;
        }
        else {
            throw new IllegalArgumentException("No populator with name "+pop);
        }
    }

    /**
     * Lists all populators currently defined in the environment.
     *
     * @return A {@link java.util.List List} of names
     */
    @JsonIgnore
    public final List<String> getPopulators(){
        return Collections.unmodifiableList(new ArrayList<>(populators.keySet()));
    }

    /**
     * Returns the set of all platforms currently operating in the environment.
     *
     * @return A {@link java.util.List list} of platforms
     */
    @JsonIgnore
    public List<P> getPlatforms(){
        return Collections.unmodifiableList(platforms);
    }

    /**
     * @return The size of the map
     */
    public abstract int getSize();

    @Override
    public boolean equals(Object o){
        if (this == o){
            return true;
        }
        if (!(o instanceof PlatformEnvironment)){
            return false;
        }
        PlatformEnvironment other = (PlatformEnvironment)o;
        return this.platform_type.equals(other.platform_type) &&
                this.map.equals(other.map) &&
                this.populators.equals(other.populators);
    }

}
