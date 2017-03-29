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

import org.junit.Assert;
import org.junit.Test;

public class DecimalPoint3DTest {

    private static final double TOLERANCE = 0.000001;

    @Test
    public void initNull(){
        DecimalPoint3D point = new DecimalPoint3D();
        Assert.assertEquals(0, point.getX(), TOLERANCE);
        Assert.assertEquals(0, point.getY(), TOLERANCE);
        Assert.assertEquals(0, point.getZ(), TOLERANCE);
    }

    @Test
    public void init(){
        double x = 7.26, y = -2.38, z = 12.5;
        DecimalPoint3D point = new DecimalPoint3D(x, y, z);
        Assert.assertEquals(x, point.getX(), TOLERANCE);
        Assert.assertEquals(y, point.getY(), TOLERANCE);
        Assert.assertEquals(z, point.getZ(), TOLERANCE);
    }

    @Test
    public void testRound(){
        DecimalPoint3D point = new DecimalPoint3D(4.3, 1.9, 2.4);
        Assert.assertEquals(4, point.roundX());
        Assert.assertEquals(2, point.roundY());
        Assert.assertEquals(2, point.roundZ());

        point = new DecimalPoint3D(-6.6, -4.5, -0.9);
        Assert.assertEquals(-7, point.roundX());
        Assert.assertEquals(-4, point.roundY());
        Assert.assertEquals(-1, point.roundZ());
    }

    @Test
    public void testOffsetThis(){
        double x = -8.3, y = 2.6, z = -4.1;
        double dx = 0.4, dy = -6.3, dz = 8.1;
        DecimalPoint3D point = new DecimalPoint3D(x, y, z);
        point.offsetThis(dx, dy, dz);
        Assert.assertEquals(x+dx, point.getX(), TOLERANCE);
        Assert.assertEquals(y+dy, point.getY(), TOLERANCE);
        Assert.assertEquals(z+dz, point.getZ(), TOLERANCE);
    }

    @Test
    public void testOffset1(){
        double x = 7.2, y = -6.6, z = 3.5;
        double dx = 12.8, dy = -3, dz = -9.1;
        DecimalPoint3D point = new DecimalPoint3D(x, y, z);
        DecimalPoint3D point2 = point.offset(dx, dy, dz);
        Assert.assertEquals(x, point.getX(), TOLERANCE);
        Assert.assertEquals(y, point.getY(), TOLERANCE);
        Assert.assertEquals(z, point.getZ(), TOLERANCE);
        Assert.assertEquals(x+dx, point2.getX(), TOLERANCE);
        Assert.assertEquals(y+dy, point2.getY(), TOLERANCE);
        Assert.assertEquals(z+dz, point2.getZ(), TOLERANCE);
    }

    @Test
    public void testOffset2(){
        double x = 3.1, y = -5.5, z = 4.4;
        double dx = -8, dy = 7.2, dz = -10;
        DecimalPoint3D point = new DecimalPoint3D(x, y, z);
        DecimalPoint3D point2 = point.offset(new DecimalPoint3D(dx, dy, dz));
        Assert.assertEquals(x, point.getX(), TOLERANCE);
        Assert.assertEquals(y, point.getY(), TOLERANCE);
        Assert.assertEquals(z, point.getZ(), TOLERANCE);
        Assert.assertEquals(x+dx, point2.getX(), TOLERANCE);
        Assert.assertEquals(y+dy, point2.getY(), TOLERANCE);
        Assert.assertEquals(z+dz, point2.getZ(), TOLERANCE);
    }

    @Test
    public void testScale(){
        double scale = 0.45;
        double x = 3.2, y = -2.6, z = 8.9;
        DecimalPoint3D point = new DecimalPoint3D(x, y, z);
        DecimalPoint3D point2 = point.scale(scale);
        Assert.assertEquals(x, point.getX(), TOLERANCE);
        Assert.assertEquals(y, point.getY(), TOLERANCE);
        Assert.assertEquals(z, point.getZ(), TOLERANCE);
        Assert.assertEquals(x*scale, point2.getX(), TOLERANCE);
        Assert.assertEquals(y*scale, point2.getY(), TOLERANCE);
        Assert.assertEquals(z*scale, point2.getZ(), TOLERANCE);
    }

    @Test
    public void testClone(){
        double x = -5.8, y = 2.1, z = 7.6;
        DecimalPoint3D point1 = new DecimalPoint3D(x, y, z);
        DecimalPoint3D point2 = point1.clone();
        point2.offsetThis(4, 7, -3);
        Assert.assertEquals(x, point1.getX(), TOLERANCE);
        Assert.assertEquals(y, point1.getY(), TOLERANCE);
        Assert.assertEquals(z, point1.getZ(), TOLERANCE);
    }

}
