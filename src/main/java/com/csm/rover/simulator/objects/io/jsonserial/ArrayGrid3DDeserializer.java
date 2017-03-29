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
import com.csm.rover.simulator.objects.util.ArrayGrid3D;
import com.csm.rover.simulator.objects.util.FloatArrayArrayArrayGrid;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ArrayGrid3DDeserializer extends JsonDeserializer<ArrayGrid3D> {

    @Override
    public ArrayGrid3D deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Object[][][] values = jsonParser.readValueAs(Object[][][].class);
        try {
            return new FloatArrayArrayArrayGrid(castToFloatArray(values));
        }
        catch (ClassCastException e){
            throw new MyJsonProcessingException("No generic ArrayGrid3D found");
        }
    }

    private Float[][][] castToFloatArray(Object[][][] input) throws ClassCastException {
        Float[][][] out = new Float[input.length][input[0].length][input[0][0].length];
        for (int i = 0; i < input.length; i++){
            for (int j = 0; j < input[i].length; j++){
                for (int k = 0; k < input[i][j].length; k++) {
                    out[i][j][k] = ((Double) input[i][j][k]).floatValue();
                }
            }
        }
        return out;
    }

}
