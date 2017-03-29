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

package com.spears.objects.util;

import com.spears.objects.io.EnvrioFileFilter;
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
