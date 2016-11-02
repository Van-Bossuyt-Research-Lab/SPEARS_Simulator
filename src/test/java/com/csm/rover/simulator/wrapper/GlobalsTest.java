package com.csm.rover.simulator.wrapper;

import org.junit.Test;

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

}
