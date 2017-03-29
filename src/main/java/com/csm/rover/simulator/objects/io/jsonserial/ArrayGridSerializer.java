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

import com.csm.rover.simulator.objects.io.MyJsonProcessingException;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ArrayGridSerializer extends JsonSerializer<ArrayGrid> {

    @Override
    public void serialize(ArrayGrid arrayGrid, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (int j = 0; j < arrayGrid.getHeight(); j++){
            jsonGenerator.writeStartArray();
            for (int i = 0; i < arrayGrid.getWidth(); i++){
                jsonGenerator.writeObject(arrayGrid.get(i, j));
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndArray();
    }
}
