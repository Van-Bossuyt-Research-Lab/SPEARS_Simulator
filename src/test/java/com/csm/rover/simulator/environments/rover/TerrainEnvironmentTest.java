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

package com.csm.rover.simulator.environments.rover;

import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.test.objects.populators.RockPop;
import com.csm.rover.simulator.wrapper.Globals;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TerrainEnvironmentTest {

    private static final double TOLERANCE = 0.00000001;

    @Test(expected = IllegalArgumentException.class)
    public void testJsonTypeCheck(){
        new TerrainEnvironment("Sub", mock(TerrainMap.class), Collections.emptyMap());
    }

    @Test
    public void testGetHeight(){
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
        assertEquals(0f, env.getHeightAt(new DecimalPoint(0, 0)), TOLERANCE);
    }

    @Test
    public void testKillAtMapEdge() throws InterruptedException {
        Globals globalMock = mock(Globals.class);
        Inject.field("singleton_instance").of(globalMock).with(globalMock);
        new Thread(() -> {
            TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
            env.getHeightAt(new DecimalPoint(10, 10));
        }, "Rover 1-Physics").start();
        Thread.sleep(2000);
        verify(globalMock).killThread("Rover 1-Physics");
    }

    private boolean failOOB = true;
    @Test
    public void testErrorDrawOOB() throws InterruptedException {
        Globals globalMock = mock(Globals.class);
        Inject.field("singleton_instance").of(globalMock).with(globalMock);
        new Thread(() -> {
            TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
            try {
                env.getHeightAt(new DecimalPoint(10, 10));
            }
            catch (ArrayIndexOutOfBoundsException e){
                failOOB = false;
            }
        }, "awt-draw").start();
        Thread.sleep(2000);
        assert !failOOB;
    }

    @Test
    public void testGetSlope(){
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
        assertEquals(-Math.PI/4., env.getSlopeAt(new DecimalPoint(0, 0), Math.PI/2.), TOLERANCE);
    }

    @Test
    public void testGetCrossSlope(){
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
        assertEquals(Math.atan(0.5), env.getCrossSlopeAt(new DecimalPoint(0, 0), Math.PI/2.), TOLERANCE);
    }

    @Test
    public void testLookupPop(){
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        EnvironmentPopulator pop = new RockPop();
        pop.build(getAMap(), ParamMap.emptyParamMap());
        pops.put("Rock Pop", pop);
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), pops);
        assertEquals(3.14, env.getPopulatorValue("Rock Pop", new DecimalPoint(3.1, 4.1)), TOLERANCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLookupUnknownPop(){
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
        env.getPopulatorValue("No Pop", new DecimalPoint(0, 0));
    }

    @Test
    public void testIsPop(){
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        EnvironmentPopulator pop = new RockPop();
        pop.build(getAMap(), ParamMap.emptyParamMap());
        pops.put("Rock Pop", pop);
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), pops);
        assert env.isPopulatorAt("Rock Pop", new DecimalPoint(3, 4));
    }

    @Test
    public void testNoPop(){
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        EnvironmentPopulator pop = new RockPop();
        pop.build(getAMap(), ParamMap.emptyParamMap());
        pops.put("Rock Pop", pop);
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), pops);
        assert !env.isPopulatorAt("Rock Pop", new DecimalPoint(0, 0));
    }

    @Test
    public void testGetSize(){
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
        assertEquals(2, env.getSize());
    }

    @Test
    public void testGetDetail(){
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
        assertEquals(1, env.getDetail());
    }

    @Test
    public void testGetMaxHeight(){
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
        assertEquals(1, env.getMaxHeight(), TOLERANCE);
    }

    @Test
    public void testGravity(){
        TerrainEnvironment env = new TerrainEnvironment("Rover", getAMap(), Collections.emptyMap());
        assertEquals(9.81, env.getGravity(), TOLERANCE);
    }

    private TerrainMap getAMap(){
        return new TerrainMap(2, 1, new Float[][]{
                new Float[] {1f, 1f, -0.5f},
                new Float[] {-0.5f, 0f, 0.5f},
                new Float[] {-1f, -1f, -0.5f}
        });
    }

}
