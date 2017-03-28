package com.csm.rover.simulator.platforms.sub;

import com.csm.rover.simulator.platforms.sub.physicsModels.subPhysicsModel;
import com.csm.rover.simulator.platforms.sub.subAuto.SubAutonomousCode;
import com.csm.rover.simulator.wrapper.Globals;
import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

public class SubObjectTest {

    private subPhysicsModel physics;
    private SubAutonomousCode autoCode;
    private SubObject sub;
    private SubState state;

    @Before
    public void reset(){
        resetGlobal();
        makePlatform();
    }

    private void resetGlobal(){
        Inject.field("singleton_instance").of(Globals.getInstance()).with(null);
    }

    private void makePlatform(){
        physics = mock(subPhysicsModel.class);
        autoCode = mock(SubAutonomousCode.class);
        state = spy(new SubState());
        doReturn(state).when(physics).getState();
        sub = new SubObject();
        Inject.field("physicsModel").of(sub).with(physics);
        Inject.field("autonomousCodeModel").of(sub).with(autoCode);
        Inject.field("name").of(sub).with("Sub 1");
    }

    @Test
    public void testStartPhysics(){
        sub.start();
        verify(physics).start();
    }

    @Test
    public void testStartCode() throws InterruptedException {
        when(autoCode.nextCommand(anyLong(), any())).thenReturn("");
        sub.start();
        Globals.getInstance().startTime(false);
        Thread.sleep(12000);
        verify(autoCode).nextCommand(10000, state);
    }

    @Test
    public void testDelayCommand() throws InterruptedException {
        when(autoCode.nextCommand(anyLong(), any())).thenReturn("delay");
        sub.start();
        Globals.getInstance().startTime(false);
        Thread.sleep(12000);
        verify(autoCode).nextCommand(11000, state);
    }

    @Test
    public void testPassesCommand() throws InterruptedException {
        when(autoCode.nextCommand(anyLong(), any())).thenReturn("move");
        sub.start();
        Globals.getInstance().startTime(false);
        Thread.sleep(12000);
        verify(physics, atLeastOnce()).sendDriveCommand("move");
    }

    @Test
    public void coverEnums(){
        SubProp.values();
    }

}
