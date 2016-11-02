package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FloatArrayArrayGridTest {

    private ArrayGrid<Float> grid;

    @Before
    public void setup(){
        grid = new FloatArrayArrayGrid();
    }

    @Test
    public void testAddToBlank(){
        grid.put(5, 4, 1.2f);

        assertEquals(6, grid.getWidth());
        assertEquals(5, grid.getHeight());
        assertEquals(1.2f, grid.get(5, 4), 0.001);
    }

    @Test
    public void testAddCol(){
        ArrayList<Float> col = new ArrayList<Float>();
        col.add(1.4f);
        col.add(1.5f);
        col.add(1.6f);
        col.add(1.7f);
        grid.addColumnAt(2, col);

        assertEquals(3, grid.getWidth());
        assertEquals(4, grid.getHeight());
        assertEquals(1.6f, grid.get(2, 2), 0.001);
    }

    @Test
    public void testAddRow(){
        ArrayList<Float> row = new ArrayList<Float>();
        row.add(1.4f);
        row.add(1.5f);
        row.add(1.6f);
        row.add(1.7f);
        row.add(1.8f);
        grid.addRowAt(5, row);
        System.out.println(grid);
        assertEquals(5, grid.getWidth());
        assertEquals(6, grid.getHeight());
        assertEquals(1.5f, grid.get(1, 5), 0.001);
    }

    @Test
    public void jsonSerializationTest(){
        ArrayGrid<Float> grid = new FloatArrayArrayGrid();
        grid.put(0, 0, 5f);
        grid.put(1, 1, 5f);
        grid.put(1, 3, 2f);
        grid.put(0, 2, 9f);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(grid);
            assertArrayEquals("[[5.0,0.0],[0.0,5.0],[9.0,0.0],[0.0,2.0]]".toCharArray(), json.toCharArray());
        }
        catch (JsonProcessingException e) {
            fail();
        }
    }

    @Test
    public void jsonDeserializerTest(){
        ArrayGrid<Float> grid = new FloatArrayArrayGrid();
        grid.put(0, 0, 5f);
        grid.put(1, 1, 5f);
        grid.put(1, 3, 2f);
        grid.put(0, 2, 9f);

        String json = "[[5.0,0.0],[0.0,5.0],[9.0,0.0],[0.0,2.0]]";

        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayGrid<Float> obj = mapper.readValue(json, ArrayGrid.class);
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
