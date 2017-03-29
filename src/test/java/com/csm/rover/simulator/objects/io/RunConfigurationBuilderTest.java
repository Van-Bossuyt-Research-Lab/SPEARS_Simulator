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

package com.csm.rover.simulator.objects.io;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class RunConfigurationBuilderTest {

    private static TypeConfig a, b;

    @BeforeClass
    public static void makeTypeMocks(){
        a = new TypeConfig("Sub", "sub.map", new ArrayList<>());
        b = new TypeConfig("UAV", "uav.map", new ArrayList<>());
    }
    @Test
    public void testBuildsCorrect(){
        RunConfiguration good = new RunConfiguration(Arrays.asList(a, b), true, 5);
        RunConfiguration build = RunConfiguration.newConfig()
                .addType(a).addType(b)
                .setAccelerated(true).setRuntime(5)
                .build();
        Assert.assertEquals(good, build);
    }

    @Test(expected = IllegalStateException.class)
    public void testRejectMissing_types(){
        RunConfiguration build = RunConfiguration.newConfig()
                .setAccelerated(true).setRuntime(5)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testRejectMissing_accel(){
        RunConfiguration build = RunConfiguration.newConfig()
                .addType(a).addType(b)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testRejectMissing_runtime(){
        RunConfiguration build = RunConfiguration.newConfig()
                .addType(a).addType(b)
                .setAccelerated(true)
                .build();
    }

}
