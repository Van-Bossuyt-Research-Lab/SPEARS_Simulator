package com.csm.rover.simulator.platforms;

import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import java.util.TreeMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class PlatformPhysicsTest {

    private PlatformPhysicsModel model;

    @Before
    public void setup(){
        model = mock(PlatformPhysicsModel.class);
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
