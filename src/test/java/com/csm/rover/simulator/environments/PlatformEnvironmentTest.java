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

package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.test.objects.environments.LandEnv;
import com.csm.rover.simulator.test.objects.maps.LandMap;
import com.csm.rover.simulator.test.objects.platforms.RoverPlatform;
import com.csm.rover.simulator.test.objects.platforms.UAVPlatform;
import com.csm.rover.simulator.test.objects.populators.RockPop;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PlatformEnvironmentTest {

    private double TOLERANCE = 0.00001;

    @Test(expected = IllegalArgumentException.class)
    public void testRejectPlatform(){
        PlatformEnvironment enviro = new LandEnv();
        enviro.addPlatform(new UAVPlatform());
    }

    private int calls = 0;
    @Test
    public void testRunsAddAction(){
        PlatformEnvironment enviro = new LandEnv();
        enviro.setPlatformAddedActions(() -> calls++);
        enviro.addPlatform(new RoverPlatform());
        assert calls > 0;
    }

    @Test
    public void testSetsEnviro(){
        PlatformEnvironment enviro = new LandEnv();
        RoverPlatform plat = spy(new RoverPlatform());
        enviro.addPlatform(plat);
        verify(plat).setEnvironment(enviro);
    }

    @Test
    public void testListPops(){
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        pops.put("Rock Pop", new RockPop());
        PlatformEnvironment enviro = new LandEnv(pops);
        Assert.assertEquals(Arrays.asList("Rock Pop"), enviro.getPopulators());
    }

    @Test
    public void testPopValue(){
        EnvironmentPopulator rocks = new RockPop();
        rocks.build(new LandMap(), Collections.emptyMap());
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        pops.put("Rock Pop", rocks);
        PlatformEnvironment enviro = new LandEnv(pops);
        Assert.assertEquals(3.14, enviro.getPopulatorValue("Rock Pop", 3, 4), TOLERANCE);
    }

    @Test
    public void testPopIs(){
        EnvironmentPopulator rocks = new RockPop();
        rocks.build(new LandMap(), Collections.emptyMap());
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        pops.put("Rock Pop", rocks);
        PlatformEnvironment enviro = new LandEnv(pops);
        assert enviro.isPopulatorAt("Rock Pop", 3, 4);
    }

    @Test
    public void testPopIsNot(){
        EnvironmentPopulator rocks = new RockPop();
        rocks.build(new LandMap(), Collections.emptyMap());
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        pops.put("Rock Pop", rocks);
        PlatformEnvironment enviro = new LandEnv(pops);
        assert !enviro.isPopulatorAt("Rock Pop", 0, 0);
    }

    @Test
    public void testPopIsDefault(){
        EnvironmentPopulator rocks = new RockPop();
        rocks.build(new LandMap(), Collections.emptyMap());
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        pops.put("Rock Pop", rocks);
        PlatformEnvironment enviro = new LandEnv(pops);
        assert enviro.isPopulatorAt("Rock Pop", 5, 12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPopValue(){
        PlatformEnvironment enviro = new LandEnv();
        enviro.getPopulatorValue("High", 5, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPopIs(){
        PlatformEnvironment enviro = new LandEnv();
        enviro.isPopulatorAt("High", 5, 7);
    }

    @Test
    public void getPlatforms(){
        Platform plat1 = new RoverPlatform();
        Platform plat2 = new RoverPlatform();
        PlatformEnvironment enviro = new LandEnv();
        enviro.addPlatform(plat1);
        enviro.addPlatform(plat2);
        Assert.assertEquals(Arrays.asList(plat1, plat2), enviro.getPlatforms());
    }

}
