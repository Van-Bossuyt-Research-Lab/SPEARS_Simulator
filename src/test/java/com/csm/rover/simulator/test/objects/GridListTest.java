package com.csm.rover.simulator.test.objects;

import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GridListTest {

    private ObjectMapper mapper;

    @Before
    public void setup(){
        mapper = new ObjectMapper();
    }

    @Test
    public void gridListSerializationTest(){
        RecursiveGridList<String> gridList = RecursiveGridList.newGridList(String.class, 2);
        gridList.put("hello", 3, 9);
        gridList.put("bye", 0, 2);

        try {
            String json = mapper.writeValueAsString(gridList);
            String expected = "{\"layers\":2,\"values\":[{\"coordinates\":[3,9],\"value\":\"hello\"},{\"coordinates\":[0,2],\"value\":\"bye\"}]}";

            assertEquals(expected, json);
        }
        catch (JsonProcessingException e) {
            fail();
        }
    }

    @Test
    public void gridListDeserializationTest(){
        RecursiveGridList<String> expected = RecursiveGridList.newGridList(String.class, 2);
        expected.put("hello", 3, 9);
        expected.put("bye", 0, 2);

        String json = "{\"layers\":2,\"values\":[{\"coordinates\":[3,9],\"value\":\"hello\"},{\"coordinates\":[0,2],\"value\":\"bye\"}]}";
        try {
            RecursiveGridList<Object> gridList = mapper.readValue(json, RecursiveGridList.class);
            assertEquals(expected, gridList);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

}
