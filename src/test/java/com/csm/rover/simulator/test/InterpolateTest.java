package com.csm.rover.simulator.test;

import org.junit.Assert;
import org.junit.Test;

public class InterpolateTest {

    private final double TOLERANCE = 0.00001;

    public double interpolate1D(double point0, double point1, double l){
        return (1-l)*point0 + l*point1;
    }

    @Test
    public void test1D() {
        //Constant
        for (double i = 0; i <= 1; i += 0.1) {
            Assert.assertEquals(4, interpolate1D(4, 4, i), TOLERANCE);
        }
        //Growing
        for (double i = 0; i <= 1; i += 0.1) {
            Assert.assertEquals(i, interpolate1D(0, 1, i), TOLERANCE);
        }
        //Shrinking
        for (double i = 0; i <= 1; i += 0.1){
            Assert.assertEquals(1-2*i, interpolate1D(1, -1, i), TOLERANCE);
        }
    }

    private double interpolate2D(double point00, double point01, double point10, double point11,
                                 double x, double y){
        return point00*(1-x)*(1-y) + point01*(1-x)*y + point10*x*(1-y) + point11*x*y;
    }

    @Test
    public void test2DCorners(){
        Assert.assertEquals(2, interpolate2D(2, 3, 4, 5, 0, 0), TOLERANCE);
        Assert.assertEquals(3, interpolate2D(2, 3, 4, 5, 0, 1), TOLERANCE);
        Assert.assertEquals(4, interpolate2D(2, 3, 4, 5, 1, 0), TOLERANCE);
        Assert.assertEquals(5, interpolate2D(2, 3, 4, 5, 1, 1), TOLERANCE);
    }

    @Test
    public void test2DRise(){
        for (double i = 0; i <= 1; i += 0.1){
            for (double j = 0; j <= 1; j += 0.1){
                Assert.assertEquals(i, interpolate2D(0, 0, 1, 1, i, j), TOLERANCE);
            }
        }
    }

    @Test
    public void test2DSaddle(){
        Assert.assertEquals(3.5, interpolate2D(2, 5, 2, 5, 0.5, 0.5), TOLERANCE);
    }

    private double interpolate3D(double point000, double point100, double point010, double point001,
                                 double point011, double point110, double point101, double point111,
                                 double x, double y, double z){
        return point000*(1-x)*(1-y)*(1-z) + point001*(1-x)*(1-y)*z + point010*(1-x)*y*(1-z) + point100*x*(1-y)*(1-z) +
                point011*(1-x)*y*z + point110*x*y*(1-z) + point101*x*(1-y)*z + point111*x*y*z;
    }

    @Test
    public void test3DCorners(){
        Assert.assertEquals(2, interpolate3D(2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0), TOLERANCE);
        Assert.assertEquals(3, interpolate3D(2, 3, 4, 5, 6, 7, 8, 9, 1, 0, 0), TOLERANCE);
        Assert.assertEquals(4, interpolate3D(2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 0), TOLERANCE);
        Assert.assertEquals(5, interpolate3D(2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 1), TOLERANCE);
        Assert.assertEquals(6, interpolate3D(2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 1), TOLERANCE);
        Assert.assertEquals(7, interpolate3D(2, 3, 4, 5, 6, 7, 8, 9, 1, 1, 0), TOLERANCE);
        Assert.assertEquals(8, interpolate3D(2, 3, 4, 5, 6, 7, 8, 9, 1, 0, 1), TOLERANCE);
        Assert.assertEquals(9, interpolate3D(2, 3, 4, 5, 6, 7, 8, 9, 1, 1, 1), TOLERANCE);
    }

    @Test
    public void test3DRising(){
        for (double i = 0; i <= 1; i += 0.1){
            for (double j = 0; j <= 1; j += 0.1){
                for (double k = 0; k <= 1; k += 0.1){
                    Assert.assertEquals(2*i, interpolate3D(0, 2, 0, 0, 0, 2, 2, 2, i, j, k), TOLERANCE);
                }
            }
        }
    }

}
