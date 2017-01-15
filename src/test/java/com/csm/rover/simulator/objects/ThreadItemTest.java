package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.wrapper.Globals;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ThreadItemTest {

    Globals globalMock;
    SynchronousThread threadMock;

    @Before
    public void setUpTests(){
        globalMock = mock(Globals.class);
        Inject.field("singleton_instance").of(Globals.getInstance()).with(globalMock);
        threadMock = mock(SynchronousThread.class);
    }

    @Test
    public void testInitialOffset(){
        ThreadItem.offset = 0;
        int delay = 20;
        ThreadItem item1 = new ThreadItem("", delay, 0, threadMock);
        assert item1.getNext() == delay;
        assert ThreadItem.offset == 1;
        ThreadItem item2 = new ThreadItem("", delay, 0, threadMock);
        assert item2.getNext() == delay+1;
    }

    @Test
    public void testAdvance(){
        long start = 500;
        int delay = 10;
        when(globalMock.timeMillis()).thenReturn(start);
        ThreadItem item = new ThreadItem("", delay, 0, threadMock);
        item.advance();
        assert item.getNext() == start+delay;
    }

    @Test
    public void testName(){
        ThreadItem item = new ThreadItem("item", 0, 0, threadMock);
        assertEquals(item.getName(), "item");
    }

    @Test
    public void testGrantPermission(){
        ThreadItem item = new ThreadItem("item", 10, 0, threadMock);
        item.grantPermission();
        assert item.hasPermission();
        verify(threadMock, atLeastOnce()).Shake();
    }

    @Test
    public void testRevokePermission(){
        ThreadItem item = new ThreadItem("item", 10, 0, threadMock);
        item.grantPermission();
        item.revokePermission();
        assert !item.hasPermission();
    }

    @Test
    public void testSuspension(){
        ThreadItem item = new ThreadItem("item", 10, 0, threadMock);
        assert item.getState() != ThreadItem.STATES.SUSPENDED;
        item.suspend();
        assert item.getState() == ThreadItem.STATES.SUSPENDED;
        item.unSuspend();
        assert item.getState() != ThreadItem.STATES.SUSPENDED;
    }

    @Test
    public void testSetRunning(){
        ThreadItem item = new ThreadItem("item", 10, 0, threadMock);
        item.setRunning(true);
        assert item.getState() == ThreadItem.STATES.RUNNING;
    }

    @Test
    public void testFinishedStates(){
        ThreadItem item = new ThreadItem("item", 10, 0, threadMock);
        assert item.isFinished();
        item.setRunning(true);
        assert !item.isFinished();
        item.setRunning(false);
        item.suspend();
        assert item.isFinished();
    }

    @Test
    public void testReset(){
        ThreadItem item = new ThreadItem("item", 10, 0, threadMock);
        item.setRunning(true);
        item.reset();
        assert item.getState() == ThreadItem.STATES.WAITING;
    }

    @Test
    public void testStates(){
        ThreadItem item = new ThreadItem("item", 10, 0, threadMock);
        item.suspend();
        Assert.assertEquals(ThreadItem.STATES.SUSPENDED, item.getState());
        item.setRunning(true);
        Assert.assertEquals(ThreadItem.STATES.SUSPENDED, item.getState());
        item.unSuspend();
        Assert.assertEquals(ThreadItem.STATES.RUNNING, item.getState());
        item.grantPermission();
        Assert.assertEquals(ThreadItem.STATES.RUNNING, item.getState());
        item.setRunning(false);
        Assert.assertEquals(ThreadItem.STATES.PERMISSION, item.getState());
        item.markFinished();
        Assert.assertEquals(ThreadItem.STATES.COMPLETE, item.getState());
        item.revokePermission();
        item.reset();
        Assert.assertEquals(ThreadItem.STATES.WAITING, item.getState());
    }

}
