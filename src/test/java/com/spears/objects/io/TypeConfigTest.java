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

package com.spears.objects.io;

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
