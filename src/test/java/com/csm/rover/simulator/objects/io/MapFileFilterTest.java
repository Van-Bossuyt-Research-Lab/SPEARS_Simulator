package com.csm.rover.simulator.objects.io;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapFileFilterTest {

    private static MapFileFilter filter;

    @BeforeClass
    public static void createFilter(){
        filter = new MapFileFilter();
    }

    @Test
    public void testFolder(){
        File folder = mock(File.class);
        when(folder.isDirectory()).thenReturn(true);
        assert filter.accept(folder);
    }

    @Test
    public void testMap(){
        assert testSuffix("map");
        assert testSuffix("MAP");
    }

    @Test
    public void testOther(){
        assert !testSuffix("doc");
        assert !testSuffix("png");
        assert !testSuffix("json");
        assert !testSuffix("log");
    }

    private boolean testSuffix(String suffix){
        File file = new File("test."+suffix);
        return filter.accept(file);
    }

}
