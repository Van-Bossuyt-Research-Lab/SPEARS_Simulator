package com.csm.rover.simulator.objects;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RK4Test {

    private static final double TOLERANCE = 0.000001;

    @Test
    public void testRK4(){
        assertEquals(1.86506, RK4.advance(this::function, 0.01, 2.3, 1.75, 3), TOLERANCE);
    }

    private double function(double time, double y, double... others){
        return time*Math.pow(y, 3) + others[0];
    }

}
