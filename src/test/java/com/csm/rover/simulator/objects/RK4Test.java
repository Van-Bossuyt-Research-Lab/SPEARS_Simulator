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
