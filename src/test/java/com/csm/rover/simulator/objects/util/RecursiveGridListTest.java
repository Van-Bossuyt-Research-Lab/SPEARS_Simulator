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
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class RecursiveGridListTest {

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
            String expected1 = "{\"layers\":2,\"values\":[{\"coordinates\":[3,9],\"value\":\"hello\"},{\"coordinates\":[0,2],\"value\":\"bye\"}]}";
            String expected2 = "{\"layers\":2,\"values\":[{\"coordinates\":[0,2],\"value\":\"bye\"},{\"coordinates\":[3,9],\"value\":\"hello\"}]}";

            assertThat(json, either(is(expected1)).or(is(expected2)));
        }
        catch (JsonProcessingException e) {
            fail();
        }
    }

    @Test
    public void gridListFloatSerialization(){
        RecursiveGridList<Float> gridList = RecursiveGridList.newGridList(Float.class, 2);
        gridList.put(5f, 3, 9);
        gridList.put(3.21f, 0, 2);

        try {
            String json = mapper.writeValueAsString(gridList);
            System.out.println(json);
            RecursiveGridList<Object> gridList2 = mapper.readValue(json, RecursiveGridList.class);

            assertEquals(gridList, gridList2);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void gridListDeserializationTest_String(){
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

    @Test
    public void gridListDeserializationTest_Int(){
        RecursiveGridList<Integer> expected = RecursiveGridList.newGridList(Integer.class, 2);
        expected.put(6, 3, 9);
        expected.put(-12, 0, 2);

        String json = "{\"layers\":2,\"values\":[{\"coordinates\":[3,9],\"value\":6},{\"coordinates\":[0,2],\"value\":-12}]}";
        try {
            RecursiveGridList<Object> gridList = mapper.readValue(json, RecursiveGridList.class);
            assertEquals(expected, gridList);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void gridListDeserializationTest_Double(){
        RecursiveGridList<Double> expected = RecursiveGridList.newGridList(Double.class, 2);
        expected.put(5.36, 3, 9);
        expected.put(8., 0, 2);

        String json = "{\"layers\":2,\"values\":[{\"coordinates\":[3,9],\"value\":5.36},{\"coordinates\":[0,2],\"value\":8.00}]}";
        try {
            RecursiveGridList<Object> gridList = mapper.readValue(json, RecursiveGridList.class);
            assertEquals(expected, gridList);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void gridListDeserializationTest_Float(){
        RecursiveGridList<Float> expected = RecursiveGridList.newGridList(Float.class, 2);
        expected.put(-21f, 3, 9);
        expected.put(4.09f, 0, 2);

        String json = "{\"layers\":2,\"values\":[{\"coordinates\":[3,9],\"value\":-21.00},{\"coordinates\":[0,2],\"value\":4.09}]}";
        try {
            RecursiveGridList<Object> gridList = mapper.readValue(json, RecursiveGridList.class);
            assertEquals(expected, gridList);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testPutOutOfBounds(){
        RecursiveGridList<String> rgl = RecursiveGridList.newGridList(String.class, 2);
        rgl.put("hi", 5, 6, 0);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetBadCoords1(){
        RecursiveGridList<String> rgl = RecursiveGridList.newGridList(String.class, 2);
        rgl.put("puppy", 5, 4);
        rgl.get(1);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetBadCoords2(){
        RecursiveGridList<String> rgl = RecursiveGridList.newGridList(String.class, 2);
        rgl.put("puppy", 5, 4);
        rgl.get(1, 4, 5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBadRange(){
        RecursiveGridList<String> rgl = RecursiveGridList.newGridList(String.class, 3);
        rgl.put("kitten", 0, 0, 4);
        rgl.get(0, 0, 7);
    }

    @Test
    public void testKeySet(){
        RecursiveGridList<String> rgl = RecursiveGridList.newGridList(String.class, 3);
        rgl.put("hi", 9, 1, 3);
        rgl.put("bonjour", 0, 0, 0);
        rgl.put("aloha", 5, 2, 3);

        Set<int[]> keysraw = rgl.keySet();
        Set<List<Integer>> keys = new HashSet<>();
        for (int[] ints : keysraw){
            List<Integer> list = new ArrayList<>();
            for (int i : ints){
                list.add(i);
            }
            keys.add(list);
        }
        assert keys.size() == 3;
        assert keys.contains(Arrays.asList(9, 1, 3));
        assert keys.contains(Arrays.asList(0, 0, 0));
        assert keys.contains(Arrays.asList(5, 2, 3));
    }

    @Test
    public void testEquals(){
        RecursiveGridList<String> rgl1 = RecursiveGridList.newGridList(String.class, 2);
        rgl1.put("hi", 0, 4);
        RecursiveGridList<String> rgl2 = RecursiveGridList.newGridList(String.class, 2);
        RecursiveGridList<String> rgl3 = RecursiveGridList.newGridList(String.class, 2);
        rgl3.put("hi", 0, 4);
        RecursiveGridList<String> rgl4 = RecursiveGridList.newGridList(String.class, 3);

        assert !rgl1.equals("hi");
        assert !rgl2.equals(rgl3);
        assert rgl3.equals(rgl1);
        assert !rgl4.equals(rgl2);
    }

}
