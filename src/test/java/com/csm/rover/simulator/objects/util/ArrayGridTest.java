package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.GenericArrayGrid;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ArrayGridTest {

    private ArrayGrid<String> grid;

    @Before
    public void setup(){
        grid = new GenericArrayGrid<String>();
    }

    @Test
    public void testAddToBlank(){
        grid.put(5, 4, "Hi");

        assertEquals(6, grid.getWidth());
        assertEquals(5, grid.getHeight());
        assertEquals("Hi", grid.get(5, 4));
    }

    @Test
    public void testAddCol(){
        ArrayList<String> col = new ArrayList<String>();
        col.add("a");
        col.add("b");
        col.add("e");
        col.add("f");
        grid.addColumnAt(2, col);

        assertEquals(3, grid.getWidth());
        assertEquals(4, grid.getHeight());
        assertEquals("e", grid.get(2, 2));
    }

    @Test
    public void testAddRow(){
        ArrayList<String> row = new ArrayList<String>();
        row.add("a");
        row.add("b");
        row.add("c");
        row.add("e");
        row.add("f");
        grid.addRowAt(5, row);
        System.out.println(grid);
        assertEquals(5, grid.getWidth());
        assertEquals(6, grid.getHeight());
        assertEquals("b", grid.get(1, 5));
    }

    @Test
    public void jsonSerializationTest(){
        ArrayGrid<String> grid = new GenericArrayGrid<>();
        grid.put(0, 0, "a");
        grid.put(1, 1, "a");
        grid.put(1, 3, "b");
        grid.put(0, 2, "c");

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(grid);
            assertArrayEquals("[[\"a\",null],[null,\"a\"],[\"c\",null],[null,\"b\"]]".toCharArray(), json.toCharArray());
        }
        catch (JsonProcessingException e) {
            fail();
        }
    }

    @Test
    public void jsonDeserializerTest(){
        ArrayGrid<String> grid = new GenericArrayGrid<>();
        grid.put(0, 0, "a");
        grid.put(1, 1, "a");
        grid.put(1, 3, "b");
        grid.put(0, 2, "c");

        String json = "[[\"a\",null],[null,\"a\"],[\"c\",null],[null,\"b\"]]";

        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayGrid<String> obj = mapper.readValue(json, ArrayGrid.class);
            assertEquals(grid, obj);
        }
        catch (IOException e) {
            fail();
        }
    }

//    @Test
//    public void VisualTest(){
//        grid.add(2, 6, "A");
//        System.out.println(grid);
//        grid.add(8, 3, "B");
//        System.out.println(grid);
//        ArrayList<String> col = new ArrayList<String>();
//        col.add("a");
//        col.add("b");
//        col.add("e");
//        col.add("f");
//        grid.addColumn(col);
//        System.out.println(grid);
//        ArrayList<String> row = new ArrayList<String>();
//        row.add("a");
//        row.add("b");
//        row.add("c");
//        row.add("e");
//        row.add("f");
//        grid.addRowAt(0, row);
//        System.out.println(grid);
//    }

}
