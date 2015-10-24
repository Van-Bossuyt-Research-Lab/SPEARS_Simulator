package com.csm.rover.simulator.test.objects;

import com.csm.rover.simulator.objects.ArrayGrid;
import com.csm.rover.simulator.objects.GenericArrayGrid;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

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
