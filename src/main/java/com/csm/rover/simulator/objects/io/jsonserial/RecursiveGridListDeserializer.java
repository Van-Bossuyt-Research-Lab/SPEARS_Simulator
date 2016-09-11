package com.csm.rover.simulator.objects.io.jsonserial;

import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class RecursiveGridListDeserializer extends JsonDeserializer<RecursiveGridList> {

    @Override
    public RecursiveGridList<Object> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int layers = node.get("layers").asInt();
        RecursiveGridList<Object> gridList = RecursiveGridList.newGridList(Object.class, layers);
        JsonNode valuesNode = node.get("values");
        for (int i = 0; i < valuesNode.size(); i++){
            JsonNode valueNode = valuesNode.get(i);
            JsonNode coordsNode = valueNode.get("coordinates");
            double[] coords = new double[layers];
            for (int j = 0; j < layers; j++){
                coords[j] = coordsNode.get(j).asInt();
            }
            JsonNode actValNode = valueNode.get("value");
            Object value;
            if (actValNode.isInt()){
                value = actValNode.asInt();
            }
            if (actValNode.isDouble()){
                value = actValNode.asDouble();
            }
            if (actValNode.isFloat()){
                value = actValNode.asDouble();
            }
            else {
                value = actValNode.asText("null");
            }
            gridList.put(value, coords);
        }
        return gridList;
    }

}
