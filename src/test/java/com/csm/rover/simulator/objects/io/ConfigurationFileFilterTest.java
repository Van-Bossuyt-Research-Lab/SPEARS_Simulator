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
