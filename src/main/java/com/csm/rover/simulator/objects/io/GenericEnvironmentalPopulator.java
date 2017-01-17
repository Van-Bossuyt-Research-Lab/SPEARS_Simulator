package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.objects.CoverageIgnore;
import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * This populator class is used to read in all populators from JSON.  It is only for use by the JSON parser.
 */
public class GenericEnvironmentalPopulator extends EnvironmentPopulator {

    @JsonCreator
    public GenericEnvironmentalPopulator(@JsonProperty("type") String type, @JsonProperty("name") String name, @JsonProperty("default") double defaultValue, @JsonProperty("values") RecursiveGridList<Double> values){
        super(type, name, defaultValue);
        super.value_map = values;
    }

    /**
     * Throws an error if called.  This populator should be built from a file, not a method.
     *
     * @param map --
     * @param params --
     * @return --
     *
     * @throws IllegalAccessError This populator cannot be built
     */
    @CoverageIgnore
    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        throw new IllegalAccessError("This implementation is for loading saved objects only");
    }
}
