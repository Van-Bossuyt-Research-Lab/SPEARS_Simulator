package com.csm.rover.simulator.objects.io;

import org.junit.Test;

import javax.swing.filechooser.FileFilter;
import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnviroFileFilterTest {

    @Test
    public void testAcceptsFolder(){
        File folder = mock(File.class);
        when(folder.isDirectory()).thenReturn(true);
        when(folder.getName()).thenReturn("testDir");
        when(folder.getAbsolutePath()).thenReturn(new File("testDir").getAbsolutePath());

        assert new EnvrioFileFilter("Rover").accept(folder);
    }

    @Test
    public void testAcceptsGood(){
        File good = new File("test.rover.env");

        assert new EnvrioFileFilter("Rover").accept(good);
    }

    @Test
    public void testAcceptsWildcard(){
        File wild1 = new File("test.rover.env");
        File wild2 = new File("test.sub.env");

        FileFilter filter = new EnvrioFileFilter("*");
        assert filter.accept(wild1);
        assert filter.accept(wild2);
    }

    @Test
    public void testAcceptsCase(){
        File wAcKY = new File("Test.RoveR.ENV");

        assert new EnvrioFileFilter("Rover").accept(wAcKY);
    }

    @Test
    public void testRejectsTooShort(){
        File shortName = new File("test.env");

        assert !new EnvrioFileFilter("*").accept(shortName);
    }

    @Test
    public void testRejectsType(){
        File rover = new File("test.rover.env");

        assert !new EnvrioFileFilter("Sub").accept(rover);
    }

    @Test
    public void testRejectsBad(){
        File bad = new File("test.mine.why");

        assert !new EnvrioFileFilter("*").accept(bad);
    }

}
