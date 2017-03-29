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

package com.spears.objects.io.jsonserial;

import com.spears.environments.EnvironmentPopulator;
import com.spears.objects.io.GenericEnvironmentalPopulator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PopulatorListDeserializer extends JsonDeserializer<Map<String, EnvironmentPopulator>> {

    @Override
    public Map<String, EnvironmentPopulator> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Map<String, EnvironmentPopulator> out = new HashMap<>();
        GenericEnvironmentalPopulator[] pops = jsonParser.readValueAs(GenericEnvironmentalPopulator[].class);
        for (GenericEnvironmentalPopulator pop : pops){
            out.put(pop.getName(), pop);
        }
        return out;
    }

}
