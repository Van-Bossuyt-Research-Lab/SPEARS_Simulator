package com.csm.rover.simulator.objects.util;

import org.junit.Assert;
import org.junit.Test;

import java.awt.Point;

public class DecimalPointTest {

    private static final double TOLERANCE = 0.000001;

    @Test
    public void initNull(){
        DecimalPoint point = new DecimalPoint();
        Assert.assertEquals(0, point.getX(), TOLERANCE);
        Assert.assertEquals(0, point.getY(), TOLERANCE);
    }

    @Test
    public void initInt(){
        int x = 5, y = 9;
        DecimalPoint point = new DecimalPoint(x, y);
        Assert.assertEquals(x, point.getX(), TOLERANCE);
        Assert.assertEquals(y, point.getY(), TOLERANCE);
    }

    @Test
    public void initDouble(){
        double x = 7.26, y = -2.38;
        DecimalPoint point = new DecimalPoint(x, y);
        Assert.assertEquals(x, point.getX(), TOLERANCE);
        Assert.assertEquals(y, point.getY(), TOLERANCE);
    }

    @Test
    public void testRound(){
        DecimalPoint point = new DecimalPoint(4.3, 1.9);
        Assert.assertEquals(4, point.roundX());
        Assert.assertEquals(2, point.roundY());

        point = new DecimalPoint(-6.6, -4.5);
        Assert.assertEquals(-7, point.roundX());
        Assert.assertEquals(-4, point.roundY());
    }

    @Test
    public void testOffsetThis(){
        double x = -8.3, y = 2.6;
        double dx = 0.4, dy = -6.3;
        DecimalPoint point = new DecimalPoint(x, y);
        point.offsetThis(dx, dy);
        Assert.assertEquals(x+dx, point.getX(), TOLERANCE);
        Assert.assertEquals(y+dy, point.getY(), TOLERANCE);
    }

    @Test
    public void testOffset1(){
        double x = 7.2, y = -6.6;
        double dx = 12.8, dy = -3;
        DecimalPoint point = new DecimalPoint(x, y);
        DecimalPoint point2 = point.offset(dx, dy);
        Assert.assertEquals(x, point.getX(), TOLERANCE);
        Assert.assertEquals(y, point.getY(), TOLERANCE);
        Assert.assertEquals(x+dx, point2.getX(), TOLERANCE);
        Assert.assertEquals(y+dy, point2.getY(), TOLERANCE);
    }

    @Test
    public void testOffset2(){
        double x = 3.1, y = -5.5;
        double dx = -8, dy = 7.2;
        DecimalPoint point = new DecimalPoint(x, y);
        DecimalPoint point2 = point.offset(new DecimalPoint(dx, dy));
        Assert.assertEquals(x, point.getX(), TOLERANCE);
        Assert.assertEquals(y, point.getY(), TOLERANCE);
        Assert.assertEquals(x+dx, point2.getX(), TOLERANCE);
        Assert.assertEquals(y+dy, point2.getY(), TOLERANCE);
    }

    @Test
    public void testScale(){
        double scale = 5.1;
        double x = 3.2, y = -2.6;
        DecimalPoint point = new DecimalPoint(x, y);
        DecimalPoint point2 = point.scale(scale);
        Assert.assertEquals(x, point.getX(), TOLERANCE);
        Assert.assertEquals(y, point.getY(), TOLERANCE);
        Assert.assertEquals(x*scale, point2.getX(), TOLERANCE);
        Assert.assertEquals(y*scale, point2.getY(), TOLERANCE);
    }

    @Test
    public void testToPoint(){
        double x = 0.6, y = 6.2;
        DecimalPoint dpnt = new DecimalPoint(x, y);
        Point pnt = dpnt.toPoint();
        Assert.assertEquals(1, pnt.getX(), TOLERANCE);
        Assert.assertEquals(6, pnt.getY(), TOLERANCE);
    }

    @Test
    public void testClone(){
        double x = -5.8, y = 2.1;
        DecimalPoint point1 = new DecimalPoint(x, y);
        DecimalPoint point2 = point1.clone();
        point2.offsetThis(4, 7);
        Assert.assertEquals(x, point1.getX(), TOLERANCE);
        Assert.assertEquals(y, point1.getY(), TOLERANCE);
    }

}
