package com.csm.rover.simulator.wrapper;

import org.junit.After;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import static org.junit.Assert.fail;

public class GlobalsTest {

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
