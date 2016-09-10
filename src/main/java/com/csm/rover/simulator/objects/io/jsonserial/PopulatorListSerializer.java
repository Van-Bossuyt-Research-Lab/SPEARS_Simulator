package com.csm.rover.simulator.objects.io.jsonserial;

import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

public class PopulatorListSerializer extends JsonSerializer<Map<String, EnvironmentPopulator>> {

    @Override
    public void serialize(Map<String, EnvironmentPopulator> map, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        for (EnvironmentPopulator pop : map.values()){
            jsonGenerator.writeObject(pop);
        }
        jsonGenerator.writeEndArray();
    }

}
