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

package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.objects.io.RunConfiguration;
import com.csm.rover.simulator.objects.io.TypeConfig;
import com.csm.rover.simulator.platforms.Platform;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class AdminBeginTest {

    private Globals globalsMock;
    private Admin adminMock;

    private RunConfiguration configuration;
    private String type = "Rover";
    private File envFile = new File("test.rover.env");
    private PlatformEnvironment env;
    private PlatformConfig platformConfig;
    private Platform plat;

    @Before
    public void setup() throws IOException {
        globalsMock = mock(Globals.class);
        Inject.field("singleton_instance").of(Globals.getInstance()).with(globalsMock);
        Inject.field("singleton_instance").of(Admin.getInstance()).with(Optional.empty());
        adminMock = spy(Admin.getInstance());

        env = mock(PlatformEnvironment.class);
        Inject.field("platform_type").of(env).with(type);
        Inject.field("platforms").of(env).with(new ArrayList<>());
        Inject.field("addedActions").of(env).with(Optional.empty());
        platformConfig = mock(PlatformConfig.class);
        plat = mock(Platform.class);
        Inject.field("platform_type").of(plat).with(type);
        configuration = RunConfiguration.newConfig().addType(
                new TypeConfig(type, envFile, Arrays.asList(platformConfig)))
                .setAccelerated(false)
                .build();

        doReturn(plat).when(adminMock).makePlatform(any(PlatformConfig.class));
        doReturn(env).when(adminMock).makeEnvironment(any(File.class), anyString());
    }

    @AfterClass
    public static void tearDown(){
        Inject.field("singleton_instance").of(Globals.getInstance()).with(null);
        Inject.field("singleton_instance").of(Admin.getInstance()).with(Optional.empty());
    }

    @Test
    public void testStartsTime(){
        adminMock.beginSimulation(configuration);

        verify(globalsMock).startTime(false);
    }

    @Test
    public void testStartsAccel(){
        int time = 30;
        configuration = RunConfiguration.newConfig().addType(
                new TypeConfig(type, envFile, Arrays.asList(platformConfig)))
                .setAccelerated(true)
                .setRuntime(time)
                .build();

        adminMock.beginSimulation(configuration);
        verify(globalsMock).startTime(true);
        verify(globalsMock).setUpAcceleratedRun(time);
    }

    @Test
    public void testStartsPlatforms(){
        adminMock.beginSimulation(configuration);

        verify(plat).start();
    }

    @Test
    public void testAddsToEnv(){
        adminMock.beginSimulation(configuration);

        verify(env).addPlatform(plat);
    }

}
