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

import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;
import com.csm.rover.simulator.objects.util.GenericArrayGrid;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ArrayGridDeserializer extends JsonDeserializer<ArrayGrid> {

    @Override
    public ArrayGrid deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Object[][] values = jsonParser.readValueAs(Object[][].class);
        values = invert(values);
        try {
            return new FloatArrayArrayGrid(castToFloatArray(values));
        }
        catch (ClassCastException e) {
            return new GenericArrayGrid<>(values);
        }
    }

    private Object[][] invert(Object[][] orig){
        Object[][] out = new Object[orig[0].length][orig.length];
        for (int i = 0; i < orig.length; i++){
            for (int j = 0; j < orig[0].length; j++){
                out[j][i] = orig[i][j];
            }
        }
        return out;
    }

    private Float[][] castToFloatArray(Object[][] input) throws ClassCastException {
        Float[][] out = new Float[input.length][input[0].length];
        for (int i = 0; i < input.length; i++){
            for (int j = 0; j < input[0].length; j++){
                out[i][j] = ((Double)input[i][j]).floatValue();
            }
        }
        return out;
    }

}
