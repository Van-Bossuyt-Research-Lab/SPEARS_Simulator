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

package com.csm.rover.simulator.objects.io.jsonserial;

import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Set;

public class RecursiveGridListSerializer extends JsonSerializer<RecursiveGridList> {

    @Override
    public void serialize(RecursiveGridList gridList, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("layers", gridList.getDimension());
        Set<int[]> keys = gridList.keySet();
        jsonGenerator.writeArrayFieldStart("values");
        for (int[] key : keys){
            jsonGenerator.writeStartObject();
            jsonGenerator.writeArrayFieldStart("coordinates");
            for (Integer i : key){
                jsonGenerator.writeNumber(i);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeObjectField("value", gridList.get(key));
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }

}
