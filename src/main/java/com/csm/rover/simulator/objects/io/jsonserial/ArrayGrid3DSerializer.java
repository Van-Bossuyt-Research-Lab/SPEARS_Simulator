package com.csm.rover.simulator.objects.io.jsonserial;

import com.csm.rover.simulator.objects.io.MyJsonProcessingException;
import com.csm.rover.simulator.objects.util.ArrayGrid3D;
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
