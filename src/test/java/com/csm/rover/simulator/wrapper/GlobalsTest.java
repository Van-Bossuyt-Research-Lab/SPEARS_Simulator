package com.csm.rover.simulator.wrapper;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;
import org.reflections.util.FilterBuilder;

import static org.junit.Assert.fail;

public class GlobalsTest {

    @BeforeClass
    public static void cleanInstance(){
        Inject.field("singleton_instance").of(Globals.getInstance()).with(null);
    }

    @Test
    public void testRecursiveCall(){
        try {
            Globals.getInstance();
        }
        catch (StackOverflowError e){
            //Cannot create SynchronousThread inside of the Globals constructor
            fail();
        }
    }

    @Test
    public void getInstanceTest(){
        Globals globe1 = Globals.getInstance();
        Globals globe2 = Globals.getInstance();
        assert globe1 == globe2;
    }

    @Test
    public void clockInitTest(){
        Globals globals = Globals.getInstance();
        assert globals.clock != null;
        assert !globals.clock.isAlive();

    }

    @Test
    public void startTimeTest1(){
        Globals globals = Globals.getInstance();
        globals.startTime(false);
        assert globals.clock.isAlive();
    }

    @Test
    public void startTimeTest2(){
        Globals globals = Globals.getInstance();
        globals.startTime(true);
        assert globals.clock.isAlive();
    }

    @Test
    public void clockEventTest() throws InterruptedException {
        Globals globals = Globals.getInstance();
        globals.addClockIncrementEvent(() -> {
            globals.clock.Stop();
        });
        globals.startTime(false);
        Thread.sleep(1010);
        assert !globals.clock.isAlive();
    }

    @Test
    public void accelTimeTest() throws InterruptedException {
        Globals globals = Globals.getInstance();
        globals.addClockIncrementEvent(() -> {
            globals.clock.Stop();
        });
        globals.startTime(true);
        globals.clock.join();
        Thread.sleep(0, 110);
        assert !globals.clock.isAlive();
    }

    @Test
    public void angleSubtractTest(){
        final double TOLERANCE = 0.00001;
        Globals globals = Globals.getInstance();
        Assert.assertEquals(-Math.PI/2., globals.subtractAngles(Math.PI, Math.PI/2.), TOLERANCE);
        Assert.assertEquals(Math.PI/2., globals.subtractAngles(Math.PI, 3*Math.PI/2.), TOLERANCE);
        Assert.assertEquals(-Math.PI/4., globals.subtractAngles(Math.PI/8., 15*Math.PI/8.), TOLERANCE);
        Assert.assertEquals(Math.PI/4., globals.subtractAngles(15*Math.PI/8., Math.PI/8.), TOLERANCE);
        Assert.assertEquals(globals.subtractAngles(Math.PI/8., 15*Math.PI/8.), globals.subtractAngles(Math.PI/8., -Math.PI/8.), TOLERANCE);
        Assert.assertEquals(globals.subtractAngles(15*Math.PI/8., Math.PI/8.), globals.subtractAngles(-Math.PI/8., Math.PI/8.), TOLERANCE);
    }

    @After
    public void cleanUp(){
        try {
            Globals.getInstance().clock.Stop();
            Inject.field("singleton_instance").of(Globals.getInstance()).with(null);
            Thread.sleep(200);
        }
        catch (NullPointerException | InterruptedException e) {}
    }

}
