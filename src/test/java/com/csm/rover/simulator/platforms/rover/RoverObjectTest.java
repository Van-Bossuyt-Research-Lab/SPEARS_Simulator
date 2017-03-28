package com.csm.rover.simulator.platforms.rover;

import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.platforms.rover.autoCode.RoverAutonomousCode;
import com.csm.rover.simulator.platforms.rover.physicsModels.RoverPhysicsModel;
import com.csm.rover.simulator.wrapper.Globals;
import java.util.Map;
import java.util.TreeMap;
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
