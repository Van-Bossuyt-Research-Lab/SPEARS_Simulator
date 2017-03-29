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

import java.io.File;
import java.util.*;

public class RunConfigurationTest {

    private static List<PlatformConfig> rovers;
    private static TypeConfig a, b, c;

    @BeforeClass
    public static void makeTypeMocks(){
        rovers = Arrays.asList(PlatformConfig.builder().setType("Rover")
        .setID("r1").setScreenName("Rover 1").setAutonomousModel("NULL", new TreeMap<>())
        .setPhysicsModel("NULL", new TreeMap<>()).build());
        a = new TypeConfig("Sub", "sub.map", new ArrayList<>());
        b = new TypeConfig("Rover", "rover.map", rovers);
        c = new TypeConfig("UAV", "uav.map", new ArrayList<>());
    }

    @Test
    public void testClone(){
        try {
            List<TypeConfig> types = new ArrayList<>();
            types.add(a);
            RunConfiguration cnfg = new RunConfiguration(types, false, -1);
            RunConfiguration clone = cnfg.clone();
            cnfg.types.add(b);
            assert !cnfg.equals(clone);
        }
        catch (UnsupportedOperationException e){
            //inedible structure passes too
        }
    }

    @Test
    public void testEquals(){
        RunConfiguration run1 = new RunConfiguration(Arrays.asList(a, c), true, 34);
        RunConfiguration run2 = new RunConfiguration(Arrays.asList(a, c), true, 34);
        Assert.assertEquals(run1, run2);
    }

    @Test
    public void testNotEquals(){
        RunConfiguration run1 = new RunConfiguration(Arrays.asList(a, c), true, 34);

        Assert.assertNotEquals(run1, new RunConfiguration(Arrays.asList(c), true, 34));
        Assert.assertNotEquals(run1, new RunConfiguration(Arrays.asList(a, c), false, 34));
        Assert.assertNotEquals(run1, new RunConfiguration(Arrays.asList(a, c), true, 12));
    }

    @Test
    public void testNotEquals_really(){
        Assert.assertNotEquals(new RunConfiguration(Arrays.asList(a), false, 2), "Yeah no");
    }

    @Test
    public void testSaveLoad() throws Exception {
        File file = new File("test.cfg");
        RunConfiguration out = new RunConfiguration(Arrays.asList(a), true, 12);
        out.save(file);
        RunConfiguration in = RunConfiguration.fromFile(file);
        file.delete();
        Assert.assertEquals(out, in);
    }

    @Test(expected = Exception.class)
    public void testErrorOnLoad() throws Exception {
        RunConfiguration.fromFile(new File("Im_not.here"));
    }

    @Test(expected = Exception.class)
    public void testErrorOnSave() throws Exception {
        File file = new File("error.cfg");
        file.createNewFile();
        file.setWritable(false);
        try {
            RunConfiguration out = new RunConfiguration(Arrays.asList(a), true, 12);
            out.save(file);
        }
        catch (Exception e){
            throw e;
        }
        finally {
            file.delete();
        }
    }

    @Test
    public void testGetTypes(){
        RunConfiguration run = new RunConfiguration(Arrays.asList(a, c), true, 1);
        Assert.assertEquals(Arrays.asList("Sub", "UAV"), run.getTypes());
    }

    @Test
    public void testGetPlatforms(){
        RunConfiguration run = new RunConfiguration(Arrays.asList(a, b), false, 3);
        Assert.assertEquals(rovers, run.getPlatforms("Rover"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPlatforms(){
        RunConfiguration run = new RunConfiguration(Arrays.asList(a, b), false, 3);
        run.getPlatforms("UAV");
    }

    @Test
    public void testGetMapFile(){
        RunConfiguration run = new RunConfiguration(Arrays.asList(b, c), false, 0);
        Assert.assertEquals(new File(new File("uav.map").getAbsolutePath()), run.getEnvironmentFile("UAV"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoMaps(){
        RunConfiguration run = new RunConfiguration(Arrays.asList(b, c), false, 0);
        run.getEnvironmentFile("Sub");
    }

}
