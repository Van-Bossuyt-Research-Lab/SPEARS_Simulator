package com.csm.rover.simulator.objects.io;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.mock;

public class TypeConfigTest {

    @Test
    public void testClone(){
        TypeConfig config1 = new TypeConfig("UAV", new File("test.map"), Arrays.asList(mock(PlatformConfig.class)));
        TypeConfig config2 = config1.clone();

        assert config1 != config2;
        Assert.assertEquals(config1, config2);
    }

    @Test
    public void testUnmodPlatforms(){
        TypeConfig config = new TypeConfig("UAV", "test.map", new ArrayList<>());
        try {
            config.platformConfigs.add(mock(PlatformConfig.class));
        } catch (Exception e) {}
        Assert.assertEquals(0, config.platformConfigs.size());
    }

}
