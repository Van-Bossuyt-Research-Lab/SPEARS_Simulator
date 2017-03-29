package com.csm.rover.simulator.objects.io;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationFileFilterTest {

    private static ConfigurationFileFilter filter;

    @BeforeClass
    public static void setup(){
        filter = new ConfigurationFileFilter();
    }

    @Test
    public void testAcceptsFolder(){
        File folder = mock(File.class);
        when(folder.isDirectory()).thenReturn(true);
        when(folder.getName()).thenReturn("testDir");
        when(folder.getAbsolutePath()).thenReturn(new File("testDir").getAbsolutePath());

        assert filter.accept(folder);
    }

    @Test
    public void testAcceptsGood(){
        File goodFile = new File("configuration.json.cfg");

        assert filter.accept(goodFile);
    }

    @Test
    public void testRejectsBad(){
        File badFile = new File("configuration.cjg.json");

        assert !filter.accept(badFile);
    }

}
