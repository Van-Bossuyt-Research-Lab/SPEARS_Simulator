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

package com.spears.platforms.rover;

import com.spears.platforms.rover.autoCode.RoverAutonomousCode;
import com.spears.platforms.rover.physicsModels.RoverPhysicsModel;
import com.spears.wrapper.Globals;
import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RoverObjectTest {

    private RoverPhysicsModel physics;
    private RoverAutonomousCode autoCode;
    private RoverObject rover;
    private RoverState state;

    @Before
    public void reset(){
        resetGlobal();
        makePlatform();
    }

    private void resetGlobal(){
        Inject.field("singleton_instance").of(Globals.getInstance()).with(null);
    }

    private void makePlatform(){
        physics = mock(RoverPhysicsModel.class);
        autoCode = mock(RoverAutonomousCode.class);
        state = spy(new RoverState());
        doReturn(state).when(physics).getState();
        rover = new RoverObject();
        Inject.field("physicsModel").of(rover).with(physics);
        Inject.field("autonomousCodeModel").of(rover).with(autoCode);
        Inject.field("name").of(rover).with("Rover 1");
    }

    @Test
    public void testStartPhysics(){
        rover.start();
        verify(physics).start();
    }

    @Test
    public void testStartCode() throws InterruptedException {
        when(autoCode.nextCommand(anyLong(), any())).thenReturn("");
        rover.start();
        Globals.getInstance().startTime(false);
        Thread.sleep(12000);
        verify(autoCode).nextCommand(10000, state);
    }

    @Test
    public void testDelayCommand() throws InterruptedException {
        when(autoCode.nextCommand(anyLong(), any())).thenReturn("delay");
        rover.start();
        Globals.getInstance().startTime(false);
        Thread.sleep(12000);
        verify(autoCode).nextCommand(11000, state);
    }

    @Test
    public void testPassesCommand() throws InterruptedException {
        when(autoCode.nextCommand(anyLong(), any())).thenReturn("move");
        rover.start();
        Globals.getInstance().startTime(false);
        Thread.sleep(12000);
        verify(physics, atLeastOnce()).sendDriveCommand("move");
    }

    @Test
    public void coverEnums(){
        MotorState.values();
        RoverWheels.values();
    }

}
