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

package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.test.objects.PhysicsModels.RoverPhysics;
import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import java.util.TreeMap;

import static org.mockito.Mockito.*;

public class PlatformPhysicsTest {

    private PlatformPhysicsModel model;

    @Before
    public void setup(){
        model = new RoverPhysics();
        Inject.field("command_handlers").of(model).with(new TreeMap<>());
    }

    @Test
    public void testDriveCommands(){
        DriveCommandHandler handler = mock(DriveCommandHandler.class);
        model.addCommandHandler("test", handler);
        double[] params = new double[] { 0.2, -1 };
        model.sendDriveCommand("test", params);

        verify(handler).processCommand(params);
    }

    @Test
    public void testNoErrorOnUnknown(){
        model.sendDriveCommand("unknown");
    }

    @Test
    public void testOverwrite(){
        DriveCommandHandler handler1 = mock(DriveCommandHandler.class);
        model.addCommandHandler("test", handler1);
        DriveCommandHandler handler2 = mock(DriveCommandHandler.class);
        model.addCommandHandler("test", handler2);
        double[] params = new double[] { 0.2, -1 };
        model.sendDriveCommand("test", params);

        verifyZeroInteractions(handler1);
        verify(handler2).processCommand(params);
    }

}
