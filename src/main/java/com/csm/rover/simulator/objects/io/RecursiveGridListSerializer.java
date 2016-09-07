package com.csm.rover.simulator.objects.io;

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
            for (int i : key){
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
