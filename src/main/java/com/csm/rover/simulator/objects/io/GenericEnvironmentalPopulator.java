package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class GenericEnvironmentalPopulator extends EnvironmentPopulator {

    @JsonCreator
    public GenericEnvironmentalPopulator(@JsonProperty("type") String type, @JsonProperty("name") String name, @JsonProperty("default") double defaultValue, @JsonProperty("values") RecursiveGridList<Double> values){
        super(type, name, defaultValue);
        super.value_map = values;
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        throw new IllegalAccessError("This implementation is for loading saved objects only");
    }
}
