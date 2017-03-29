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

import com.spears.objects.io.MyJsonProcessingException;
import com.spears.objects.util.ArrayGrid3D;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ArrayGrid3DSerializer extends JsonSerializer<ArrayGrid3D> {

    @Override
    public void serialize(ArrayGrid3D arrayGrid3D, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        for (int i = 0; i < arrayGrid3D.getWidth(); i++){
            jsonGenerator.writeStartArray();
            for (int j = 0; j < arrayGrid3D.getHeight(); j++){
                jsonGenerator.writeStartArray();
                for (int k = 0; k < arrayGrid3D.getDepth(); k++){
                    try {
                        jsonGenerator.writeObject(arrayGrid3D.get(i, j, k));
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        throw new MyJsonProcessingException(e);
                    }
                }
                jsonGenerator.writeEndArray();
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndArray();
    }

}
