package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.GenericArrayGrid;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ArrayGridDeserializer extends JsonDeserializer<ArrayGrid> {

    @Override
    public ArrayGrid<Object> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Object[][] values = jsonParser.readValueAs(Object[][].class);
        return new GenericArrayGrid<>(invert(values));
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

}
