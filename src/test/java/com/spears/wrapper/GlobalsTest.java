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

package com.spears.wrapper;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

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
        Thread.sleep(2000);
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

    @Test
    public void angleSubtractDegTest(){
        final double TOLERANCE = 0.00001;
        Globals globals = Globals.getInstance();
        Assert.assertEquals(-90, globals.subtractAnglesDeg(180, 90), TOLERANCE);
        Assert.assertEquals(90, globals.subtractAnglesDeg(180, 270), TOLERANCE);
        Assert.assertEquals(-45., globals.subtractAnglesDeg(22.5, 337.5), TOLERANCE);
        Assert.assertEquals(45, globals.subtractAnglesDeg(337.5, 22.5), TOLERANCE);
        Assert.assertEquals(globals.subtractAnglesDeg(22.5, 337.5), globals.subtractAnglesDeg(22.5, -22.5), TOLERANCE);
        Assert.assertEquals(globals.subtractAnglesDeg(337.5, 22.5), globals.subtractAnglesDeg(-22.5, 22.5), TOLERANCE);
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
