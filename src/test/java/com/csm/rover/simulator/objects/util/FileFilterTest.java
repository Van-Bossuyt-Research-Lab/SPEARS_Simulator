package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.io.EnvrioFileFilter;
import org.junit.Test;

import javax.swing.filechooser.FileFilter;
import java.io.File;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileFilterTest {

    @Test
    public void testEnv(){
        FileFilter filter = new EnvrioFileFilter("Rover");
        assertTrue(filter.accept(new File("c:/Users/mine/map.rover.env")));
        assertFalse(filter.accept(new File("c:/Users/mine/map.rover.map")));
    }

    @Test
    public void testEnoughSuffix(){
        FileFilter filter = new EnvrioFileFilter("Rover");
        assertFalse(filter.accept(new File("c:/Users/mine/map.map")));
        assertFalse(filter.accept(new File("c:/Users/mine/map.2.rover.map")));
    }

    @Test
    public void testWildCardPlatform(){
        FileFilter filter = new EnvrioFileFilter("*");
        assertTrue(filter.accept(new File("c:/Users/mine/map.rover.env")));
        assertTrue(filter.accept(new File("c:/Users/mine/map.satellite.env")));
    }

    @Test
    public void testPlatformGate(){
        FileFilter filter = new EnvrioFileFilter("Rover");
        assertTrue(filter.accept(new File("c:/Users/mine/map.rover.env")));
        assertFalse(filter.accept(new File("c:/Users/mine/map.satellite.env")));

    }

}
