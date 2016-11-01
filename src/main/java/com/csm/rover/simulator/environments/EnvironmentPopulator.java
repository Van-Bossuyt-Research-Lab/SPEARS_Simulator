package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.util.Map;

public abstract class EnvironmentPopulator {

    @JsonProperty("type")
    protected final String platform_type;
    @JsonProperty("name")
    protected final String name;

    @JsonProperty("values")
    protected RecursiveGridList<Double> value_map;
    @JsonProperty("default")
    protected final double default_value;

    protected EnvironmentPopulator(String type, String name, double default_value){
        platform_type = type;
        this.name = name;
        this.default_value = default_value;
    }

    public final String getType(){
        return platform_type;
    }

    public final void build(EnvironmentMap map, Map<String, Double> params){
        if (!map.getType().equals(platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, map.getType()));
        }
        value_map = doBuild(map, params);
    }

    abstract protected RecursiveGridList<Double> doBuild(final EnvironmentMap map, final Map<String, Double> params);

    public final String getName(){
        return name;
    }

    public double getValue(double... coordinates){
        if (value_map == null){
            return default_value;
        }
        int[] coords = new int[coordinates.length];
        for (int i = 0; i < coordinates.length; i++){
            coords[i] = (int)Math.floor(coordinates[i]);
        }
        try {
            return new Double(value_map.get(coords) + "");
        }
        catch (IndexOutOfBoundsException e){
            return default_value;
        }
    }

    @JsonIgnore
    public abstract PopulatorDisplayFunction getDisplayFunction();

}
