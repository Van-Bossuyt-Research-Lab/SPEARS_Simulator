package com.csm.rover.simulator.objects.io.jsonserial;

import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.objects.io.GenericEnvironmentalPopulator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PopulatorListDeserializer extends JsonDeserializer<Map<String, EnvironmentPopulator>> {

    @Override
    public Map<String, EnvironmentPopulator> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Map<String, EnvironmentPopulator> out = new HashMap<>();
        GenericEnvironmentalPopulator[] pops = jsonParser.readValueAs(GenericEnvironmentalPopulator[].class);
        for (GenericEnvironmentalPopulator pop : pops){
            out.put(pop.getName(), pop);
        }
        return out;
    }

}
