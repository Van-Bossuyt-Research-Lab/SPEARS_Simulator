package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ArrayGridSerializer extends JsonSerializer<ArrayGrid> {

    @Override
    public void serialize(ArrayGrid arrayGrid, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        for (int j = 0; j < arrayGrid.getHeight(); j++){
            jsonGenerator.writeStartArray();
            for (int i = 0; i < arrayGrid.getWidth(); i++){
                try{
                    jsonGenerator.writeObject(arrayGrid.get(i, j));
                }
                catch (ArrayIndexOutOfBoundsException e){
                    throw new MyJsonProcessingException(e);
                }
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndArray();
    }
}
