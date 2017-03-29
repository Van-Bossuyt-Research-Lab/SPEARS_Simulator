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

import com.csm.rover.simulator.objects.io.RunConfiguration;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CancellationException;

import static org.mockito.Mockito.*;

public class StartUpTest {

    private Admin adminMock;

    @Before
    public void reset(){
        adminMock = mock(Admin.class);
        Inject.field("singleton_instance").of(Admin.getInstance()).with(Optional.of(adminMock));
    }

    @AfterClass
    public static void clearAdmin(){
        Inject.field("singleton_instance").of(Admin.getInstance()).with(Optional.empty());
    }

    @Test(timeout = 5000)
    public void testStart() throws Exception {
        when(adminMock.useSave()).thenReturn(true);
        when(adminMock.getRunConfigFile()).thenReturn(null);

        Admin.main(new String[0]);
        verify(adminMock).checkRegistries();
        verify(adminMock).checkFolders();
    }

    @Test(timeout = 5000)
    public void testUseSave() throws Exception {
        when(adminMock.useSave()).thenReturn(true);
        RunConfiguration cfg = mock(RunConfiguration.class);
        when(adminMock.getRunConfigFile()).thenReturn(cfg);

        Admin.main(new String[0]);
        verify(adminMock).startWithGUI(cfg);
    }

    @Test(timeout = 5000)
    public void testDontUseSave(){
        when(adminMock.useSave()).thenReturn(false);

        Admin.main(new String[0]);
        verify(adminMock).promptStartup();
    }

    @Test(timeout = 5000)
    public void testUseSaveCanceled() throws Exception {
        when(adminMock.useSave()).thenReturn(true);
        when(adminMock.getRunConfigFile()).thenThrow(CancellationException.class);

        Admin.main(new String[0]);
        verify(adminMock).promptStartup();
    }

    @Test(timeout = 5000)
    public void testUseSaveFailed() throws Exception {
        when(adminMock.useSave()).thenReturn(true);
        when(adminMock.getRunConfigFile()).thenThrow(new Exception("This is a test"));

        Admin.main(new String[0]);
        verify(adminMock).shutDownSimulator(2);
    }

    @Test(timeout = 5000)
    public void testCommandStart() throws Exception {
        File testCfg = new File("test.cfg");
        when(adminMock.verifyFile(testCfg)).thenReturn(true);
        RunConfiguration cfg = mock(RunConfiguration.class);
        when(adminMock.getRunConfigFile(testCfg)).thenReturn(cfg);

        Admin.main(new String[]{testCfg.getName()});
        verify(adminMock).beginSimulation(cfg);
    }

    @Test(timeout = 5000)
    public void testCommandFailed() throws Exception {
        File testCfg = new File("test.cfg");
        when(adminMock.verifyFile(testCfg)).thenReturn(true);
        RunConfiguration cfg = mock(RunConfiguration.class);
        when(adminMock.getRunConfigFile(testCfg)).thenThrow(Exception.class);

        Admin.main(new String[]{testCfg.getName()});
        verify(adminMock).shutDownSimulator(2);
    }

    @Test(timeout = 5000)
    public void testCommandBadFile(){
        File testCfg = new File("test.cfg");
        when(adminMock.verifyFile(testCfg)).thenReturn(false);

        Admin.main(new String[]{testCfg.getName()});
        verify(adminMock).shutDownSimulator(3);
    }

}
