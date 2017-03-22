package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.test.objects.states.MoleState;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

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

    @Test
    public void testSet_UnknownParam(){

    }

    @Test
    public void testGetDefault_String(){

    }

    @Test
    public void testGetDefault_Double(){

    }

    @Test
    public void testGetDefault_DoubleArray(){

    }

    @Test
    public void testNoDefault_Double(){

    }

    @Test
    public void testNoDefault_String(){

    }

    @Test
    public void testNoDefault_DoubleArray(){

    }

    @Test
    public void testWrongType_Double(){

    }

    @Test
    public void testWrongType_DoubleArray(){

    }

    @Test
    public void testWrongType_String(){

    }

    @Test
    public void testGetType_Double(){

    }

    @Test
    public void testGetType_DoubleArray(){

    }

    @Test
    public void testGetType_String(){

    }

    @Test
    public void testGetType_Unknown(){

    }

    @Test
    public void testRemove_Double(){

    }

    @Test
    public void testRemove_DoubleArray(){

    }

    @Test
    public void testRemove_String(){

    }

    @Test
    public void testRemoveToDefault_Double(){

    }

    @Test
    public void testRemoveToDefault_DoubleArray(){

    }

    @Test
    public void testRemoveToDefault_String(){

    }

    @Test
    public void testCopy(){

    }

    @Test
    public void testReadOnly_Set(){

    }

    @Test
    public void testReadOnly_Remove(){

    }

    @Test
    public void testOverride(){

    }

    private static void assertArrayEquals(Double[] expected, Double[] value, double delta){
        assertEquals(expected.length, value.length);
        for (int i = 0; i < expected.length; i++){
            assertEquals(expected[i], value[i], delta);
        }
    }

}
