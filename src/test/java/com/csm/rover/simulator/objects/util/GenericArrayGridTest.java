package com.csm.rover.simulator.objects.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class GenericArrayGridTest {

    private ArrayGrid<String> grid;

    @Before
    public void setup(){
        grid = new GenericArrayGrid<>();
    }

    @Test
    public void testAddToBlank(){
        grid.put(5, 4, "Hi");

        assertEquals("Hi", grid.get(5, 4));
    }

    @Test
    public void testFill(){
        grid.fillToSize(10, 12);

        assert !grid.isEmpty();
        assertEquals(10, grid.getWidth());
        assertEquals(12, grid.getHeight());
        assertEquals(null, grid.get(5, 7));
    }

    @Test
    public void testAddCol(){
        ArrayList<String> col = new ArrayList<String>();
        col.add("a");
        col.add("b");
        col.add("e");
        col.add("f");
        grid.insertColumnAt(2, col);

        assertEquals(col, grid.getColumn(2));
    }

    @Test
    public void testAddRow(){
        ArrayList<String> row = new ArrayList<String>();
        row.add("a");
        row.add("b");
        row.add("c");
        row.add("e");
        row.add("f");
        grid.insertRowAt(5, row);

        assertEquals(row, grid.getRow(5));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_x(){
        grid.put(2, 4, "z");
        grid.get(3, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_y(){
        grid.put(2, 4, "z");
        grid.get(1, 5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBRow(){
        grid.put(2, 4, "z");
        grid.getRow(5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBColumn(){
        grid.put(2, 4, "z");
        grid.getColumn(3);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_xNeg(){
        grid.put(2, 4, "z");
        grid.get(-3, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_yNeg(){
        grid.put(2, 4, "z");
        grid.get(1, -5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBRowNeg(){
        grid.put(2, 4, "z");
        grid.getRow(-5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBColumnNeg(){
        grid.put(2, 4, "z");
        grid.getColumn(-3);
    }

    @Test
    public void testEmpty(){
        assert grid.isEmpty();
        assertEquals(0, grid.getHeight());
        assertEquals(0, grid.getWidth());
    }

    @Test
    public void testSize(){
        grid.put(5, 7, "a");

        assertEquals(6, grid.getWidth());
        assertEquals(8, grid.getHeight());
        assertEquals(48, grid.size());

        grid.put(12, 14, "1");
        assertEquals(13, grid.getWidth());
        assertEquals(15, grid.getHeight());
        assertEquals(195, grid.size());
    }

    @Test
    public void testEquals(){
        grid.put(2, 3, "u");
        grid.put(1, 8, "i");

        ArrayGrid<String> other = new GenericArrayGrid<>();
        other.put(2, 3, "u");
        other.put(1, 8, "i");

        assert grid.equals(other);
    }

    @Test
    public void testNotEqual(){
        grid.put(2, 3, "u");
        grid.put(1, 8, "i");

        ArrayGrid<String> other = new GenericArrayGrid<>();
        other.put(2, 3, "u");
        other.put(8, 1, "i");

        assert !grid.equals(other);
    }

    @Test
    public void testReallyNotEqual(){
        assert !grid.equals("a string");
    }

    @Test
    public void testClone(){
        grid.put(2, 5, "l");
        ArrayGrid<String> clone = grid.clone();

        assert grid.equals(clone);
        clone.put(6, 7, "w");
        assert !grid.equals(clone);
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

}
