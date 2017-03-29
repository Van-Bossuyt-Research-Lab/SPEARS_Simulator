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
