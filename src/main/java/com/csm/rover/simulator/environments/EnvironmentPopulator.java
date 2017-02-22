package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.util.Map;

/**
 * Populators add unique elements to environment in addition to the map.
 */
public abstract class EnvironmentPopulator {

    @JsonProperty("type")
    protected final String platform_type;
    @JsonProperty("name")
    protected final String name;

    @JsonProperty("values")
    protected RecursiveGridList<Double> value_map;
    @JsonProperty("default")
    protected final double default_value;

    /**
     * Class should be extended and defined with their respective platform.
     *
     * @param type Platform type name
     * @param name The populator name
     * @param default_value Value to use where the populator is not defined
     */
    protected EnvironmentPopulator(String type, String name, double default_value){
        platform_type = type;
        this.name = name;
        this.default_value = default_value;
    }

    public final String getType(){
        return platform_type;
    }

    /**
     * Public facing method to build the populator.  Rejects maps of the wrong type before calling
     * {@link #doBuild(EnvironmentMap, Map)}.
     *
     * @param map Map the populators should be added to
     * @param params Build parameters
     */
    public final void build(EnvironmentMap map, Map<String, Double> params){
        if (!map.getType().equals(platform_type)){
            throw new IllegalArgumentException(
                    String.format("Types do not match %s != %s", platform_type, map.getType()));
        }
        value_map = doBuild(map, params);
    }

    /**
     * Abstract method to be implemented with the build method for the populator.
     *
     * @param map The map the populator is coupled with
     * @param params Build parameters
     * @return {@link RecursiveGridList} of the same dimensionality as the map
     */
    abstract protected RecursiveGridList<Double> doBuild(final EnvironmentMap map, final Map<String, Double> params);

    public final String getName(){
        return name;
    }

    /**
     * Returns the value of the populator at the given coordinates.  Returns the default value if the coordinates are
     * out of range or {@link #build(EnvironmentMap, Map)} has not been run.  Populator values are defined to integer
     * resolution using {@link java.lang.Math#floor(double)}.
     *
     * @param coordinates Position coordinates, should be the same dimension as the map.
     * @return The value of the populator or default
     */
    public double getValue(double... coordinates){
        if (value_map == null){
            return default_value;
        }
        int[] coords = new int[coordinates.length];
        for (int i = 0; i < coordinates.length; i++){
            coords[i] = (int)Math.floor(coordinates[i]);
        }
        try {
            return value_map.get(coords);
        }
        catch (IndexOutOfBoundsException e){
            return default_value;
        }
    }

    /**
     * Abstract void should be overwritten to return a {@link PopulatorDisplayFunction} which converts the
     * {@link #getValue(double...) value} of the populator to a {@link java.awt.Color color} to be displayed on the map.
     *
     * @return A unique {@link PopulatorDisplayFunction}
     */
    @JsonIgnore
    public abstract PopulatorDisplayFunction getDisplayFunction();

}
