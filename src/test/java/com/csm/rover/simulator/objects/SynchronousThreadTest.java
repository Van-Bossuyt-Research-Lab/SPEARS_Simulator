package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.wrapper.Globals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import static org.mockito.Mockito.*;

public class SynchronousThreadTest {

    private static Globals globalsMock;

    @BeforeClass
    public static void mockGlobals(){
        globalsMock = mock(Globals.class);
        Inject.field("singleton_instance").of(globalsMock).with(globalsMock);
        when(globalsMock.getTimeScale()).thenReturn(1.);
    }

    @Test
    public void testStart(){
        SynchronousThread thread1 = new SynchronousThread(20, () -> {}, SynchronousThread.FOREVER, "1");
        assert thread1.isAlive();
        SynchronousThread thread2 = new SynchronousThread(20, () -> {}, SynchronousThread.FOREVER, "2", true);
        assert thread2.isAlive();
        SynchronousThread thread3 = new SynchronousThread(20, () -> {}, SynchronousThread.FOREVER, "3", false);
        assert !thread3.isAlive();
    }

    @Test
    public void testRegister(){
        int delay = 40;
        String name1 = "first";
        SynchronousThread thread1 = new SynchronousThread(delay, () -> {}, SynchronousThread.FOREVER, name1);
        verify(globalsMock).registerNewThread(name1, delay, thread1);
        String name2 = "second";
        SynchronousThread thread2 = new SynchronousThread(delay, () -> {}, SynchronousThread.FOREVER, name2, false);
        verify(globalsMock).registerNewThread(name2, delay, thread2);
    }

    @Test
    public void testForever() throws InterruptedException {
        String name = "thread";
        int times = 5;
        when(globalsMock.getThreadRunPermission(name)).thenReturn(true);
        SynchronousThread thread = new SynchronousThread(5, () -> {}, times, name);
        for (int i = 0; i < times; i++){
            Thread.sleep(10);
            thread.Shake();
        }
        verify(globalsMock, times(times)).threadCheckIn(name);
        verify(globalsMock, atLeastOnce()).checkOutThread(name);
    }

}
