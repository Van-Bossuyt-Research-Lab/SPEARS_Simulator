/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csm.rover.simulator.objects.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FloatArrayArrayGridTest {

    private ArrayGrid<Float> grid;
    private static final double TOLERANCE = 0.00001;

    @Before
    public void setup(){
        grid = new FloatArrayArrayGrid();
    }

    @Test
    public void testAddToBlank(){
        grid.put(5, 4, 1.2f);

        assertEquals(6, grid.getWidth());
        assertEquals(5, grid.getHeight());
        assertEquals(1.2f, grid.get(5, 4), TOLERANCE);
    }

    @Test
    public void testFill(){
        grid.fillToSize(3, 9);

        assertEquals(3, grid.getWidth());
        assertEquals(9, grid.getHeight());
        assertEquals(0f, grid.get(2, 8), TOLERANCE);
        assert !grid.isEmpty();
    }

    @Test
    public void testGrow(){
        grid.put(5, 4, 1.2f);

        assertEquals(6, grid.getWidth());
        assertEquals(5, grid.getHeight());
        assertEquals(30, grid.size());

        grid.put(7, 5, 2.2f);

        assertEquals(8, grid.getWidth());
        assertEquals(6, grid.getHeight());
    }

    @Test
    public void testEmpty(){
        assertEquals(0, grid.getWidth());
        assertEquals(0, grid.getHeight());
        assert grid.isEmpty();
    }

    @Test
    public void testAddCol(){
        ArrayList<Float> col = new ArrayList<>();
        col.add(1.4f);
        col.add(1.5f);
        col.add(1.6f);
        col.add(1.7f);
        grid.addColumn(col);

        assertEquals(1, grid.getWidth());
        assertEquals(4, grid.getHeight());
        assertEquals(col, grid.getColumn(0));
    }

    @Test
    public void testAddRow(){
        ArrayList<Float> row = new ArrayList<>();
        row.add(1.4f);
        row.add(1.5f);
        row.add(1.6f);
        row.add(1.7f);
        row.add(1.8f);
        grid.addRow(row);

        assertEquals(5, grid.getWidth());
        assertEquals(1, grid.getHeight());
        assertEquals(row, grid.getRow(0));
    }

    @Test
    public void testInsertCol(){
        grid.put(3, 5, 3.2f);

        ArrayList<Float> col = new ArrayList<>();
        col.add(1.4f);
        col.add(1.5f);
        grid.insertColumnAt(1, col);

        assertEquals(1.4f, grid.get(1, 0), TOLERANCE);
        assertEquals(3.2f, grid.get(4, 5), TOLERANCE);
        assertEquals(0, grid.get(1, 4), TOLERANCE);
    }

    @Test
    public void testInsertRow(){
        grid.put(6, 2, -1.7f);

        ArrayList<Float> row = new ArrayList<>();
        row.add(1.4f);
        row.add(1.5f);
        grid.insertRowAt(0, row);

        assertEquals(1.5f, grid.get(1, 0), TOLERANCE);
        assertEquals(-1.7f, grid.get(6, 3), TOLERANCE);
        assertEquals(0, grid.get(4, 0), TOLERANCE);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_x(){
        grid.put(2, 4, 3.2f);
        grid.get(3, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_y(){
        grid.put(2, 4, 1.6f);
        grid.get(1, 5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBRow(){
        grid.put(2, 4, 9.2f);
        grid.getRow(5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBColumn(){
        grid.put(2, 4, -100f);
        grid.getColumn(3);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_xNeg(){
        grid.put(2, 4, 3.2f);
        grid.get(-3, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_yNeg(){
        grid.put(2, 4, 1.6f);
        grid.get(1, -5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBRowNeg(){
        grid.put(2, 4, 9.2f);
        grid.getRow(-5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBColumnNeg(){
        grid.put(2, 4, -100f);
        grid.getColumn(-3);
    }

    @Test
    public void testEquals(){
        grid.put(3, 6, 2f);
        grid.put(1, 2, 0.21f);

        ArrayGrid<Float> other = new FloatArrayArrayGrid();
        other.put(3, 6, 2f);
        other.put(1, 2, 0.21f);

        assert grid.equals(other);
    }

    @Test
    public void testNotEquals(){
        grid.put(3, 7, 1f);

        ArrayGrid<Float> other = new FloatArrayArrayGrid();
        other.put(4, 6, 2f);

        assert !grid.equals(other);
    }

    @Test
    public void testTypeNotEquals(){
        grid.put(1, 5, 2.5f);

        ArrayGrid<String> other = new GenericArrayGrid<>();
        other.put(1, 5, "2.5");

        assert !grid.equals(other);
    }

    @Test
    public void testReallyNotEquals(){
        assert !grid.equals(1.58f);
    }

    @Test
    public void testClone(){
        grid.put(2, 6, 7f);
        grid.put(1, 5, 1.2f);

        ArrayGrid<Float> clone = grid.clone();
        assert grid.equals(clone);

        clone.put(1, 5, 2.1f);
        assert !grid.equals(clone);
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

}
