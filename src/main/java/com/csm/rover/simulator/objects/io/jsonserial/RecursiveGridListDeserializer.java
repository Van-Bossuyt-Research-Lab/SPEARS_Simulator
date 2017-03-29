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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class RecursiveGridListDeserializer extends JsonDeserializer<RecursiveGridList> {

    @Override
    public RecursiveGridList<Object> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int layers = node.get("layers").asInt();
        RecursiveGridList<Object> gridList = RecursiveGridList.newGridList(Object.class, layers);
        JsonNode valuesNode = node.get("values");
        for (int i = 0; i < valuesNode.size(); i++){
            JsonNode valueNode = valuesNode.get(i);
            JsonNode coordsNode = valueNode.get("coordinates");
            int[] coords = new int[layers];
            for (int j = 0; j < layers; j++){
                coords[j] = coordsNode.get(j).asInt();
            }
            JsonNode actValNode = valueNode.get("value");
            Object value;
            if (actValNode.isInt()){
                value = actValNode.asInt();
            }
            else if (actValNode.isFloat() || actValNode.isDouble()){
                value = actValNode.asDouble();
            }
            else {
                value = actValNode.asText("null");
            }
            gridList.put(value, coords);
        }
        return gridList;
    }

}
