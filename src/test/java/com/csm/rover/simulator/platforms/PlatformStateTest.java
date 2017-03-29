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

package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.test.objects.states.MoleState;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class PlatformStateTest {

    private static final double TOLERANCE = 0.0000001;

    private PlatformState state;

    @Before
    public void resetState(){
        state = new MoleState();
    }

    @Test
    public void testExpected(){
        Set<String> set = new TreeSet<>();
        set.addAll(Arrays.asList("toes", "age", "species", "strength", "weight", "name"));
        assertEquals(set, state.expectedValues());
    }

    @Test
    public void testRequired(){
        Set<String> set = new TreeSet<>();
        set.addAll(Arrays.asList("weight", "name", "strength"));
        assertEquals(set, state.requiredValues());
    }

    @Test
    public void testOptional(){
        Set<String> set = new TreeSet<>();
        set.addAll(Arrays.asList("age", "toes", "species"));
        assertEquals(set, state.optionalValues());
    }

    @Test
    public void testSet_Double(){
        state.set("weight", 4.);
        assertEquals(4, state.get("weight"), TOLERANCE);
    }

    @Test
    public void testSet_DoubleArray(){
        state.set("toes", new Double[]{ 3., 2., 4., 5. });
        assertArrayEquals(new Double[]{ 3., 2., 4., 5. }, state.get("toes"), TOLERANCE);
    }

    @Test
    public void testSet_String(){
        state.set("name", "Gerald");
        assertEquals("Gerald", state.get("name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSet_WrongType(){
        state.set("weight", "heavy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSet_UnknownParam(){
        state.set("gender", "male");
    }

    @Test
    public void testGetDefault_String(){
        assertEquals("Star-nose", state.get("species"));
    }

    @Test
    public void testGetDefault_Double(){
        assertEquals(4.9, state.get("age"), TOLERANCE);
    }

    @Test
    public void testGetDefault_DoubleArray(){
        assertArrayEquals(new Double[]{5., 5., 5., 5.}, state.get("toes"), TOLERANCE);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNoDefault_Double(){
        state.get("weight");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNoDefault_String(){
        state.get("name");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNoDefault_DoubleArray(){
        state.get("strength");
    }

    @Test(expected = ClassCastException.class)
    public void testWrongType_Double(){
        String species = state.get("age");
    }

    @Test(expected = ClassCastException.class)
    public void testWrongType_DoubleArray(){
        Double age = state.get("toes");
    }

    @Test(expected = ClassCastException.class)
    public void testWrongType_String(){
        Double[] toes = state.get("species");
    }

    @Test
    public void testGetType_Double(){
        assertEquals(Double.class, state.getParameterType("age"));
    }

    @Test
    public void testGetType_DoubleArray(){
        assertEquals(Double[].class, state.getParameterType("toes"));
    }

    @Test
    public void testGetType_String(){
        assertEquals(String.class, state.getParameterType("name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetType_Unknown(){
        state.getParameterType("nose");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemove_Double(){
        state.set("weight", 12.);
        state.remove("weight");
        state.get("weight");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemove_DoubleArray(){
        state.set("strength", new Double[] {34., 18., 10., 0.});
        state.remove("strength");
        state.get("strength");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemove_String(){
        state.set("name", "Daisy");
        state.remove("name");
        state.get("name");
    }

    @Test
    public void testRemoveToDefault_Double(){
        state.set("age", 2.);
        state.remove("age");
        assertEquals(4.9, state.get("age"), TOLERANCE);
    }

    @Test
    public void testRemoveToDefault_DoubleArray(){
        state.set("toes", new Double[]{3., 5., 4., 3.});
        state.remove("toes");
        assertArrayEquals(new Double[]{5., 5., 5., 5.}, state.get("toes"), TOLERANCE);
    }

    @Test
    public void testRemoveToDefault_String(){
        state.set("species", "brown");
        state.remove("species");
        assertEquals("Star-nose", state.get("species"));
    }

    @Test
    public void testCopy(){
        state.set("name", "Daisy");
        state.set("age", 5.);
        PlatformState state2 = state.immutableCopy();
        assertEquals("Daisy", state2.get("name"));
        assertEquals(5., state2.get("age"), TOLERANCE);
    }

    @Test(expected = IllegalAccessError.class)
    public void testReadOnly_Set(){
        state.immutableCopy().set("name", "Ronald");
    }

    @Test(expected = IllegalAccessError.class)
    public void testReadOnly_Remove(){
        state.set("weight", 5.7);
        state.immutableCopy().remove("weight");
    }

    @Test
    public void testOverride(){
        Map<String, Object> paramMap = new TreeMap<>();
        paramMap.put("weight", 4.5);
        paramMap.put("gender", "male");
        paramMap.put("toes", 2.3);
        state.overrideValues(paramMap);
        assertArrayEquals(new Double[]{5., 5., 5., 5.}, state.get("toes"), TOLERANCE);
        assertEquals(4.5, state.get("weight"), TOLERANCE);
    }

    private static void assertArrayEquals(Double[] expected, Double[] value, double delta){
        assertEquals(expected.length, value.length);
        for (int i = 0; i < expected.length; i++){
            assertEquals(expected[i], value[i], delta);
        }
    }

}
