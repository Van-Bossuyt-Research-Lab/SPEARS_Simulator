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

package com.spears.objects.io;

import com.spears.environments.EnvironmentMap;
import com.spears.environments.EnvironmentPopulator;
import com.spears.objects.CoverageIgnore;
import com.spears.objects.util.RecursiveGridList;
import com.spears.ui.visual.PopulatorDisplayFunction;
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

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return null;
    }
}
