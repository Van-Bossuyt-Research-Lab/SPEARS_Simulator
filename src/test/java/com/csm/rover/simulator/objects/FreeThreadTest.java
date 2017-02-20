package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.wrapper.Globals;
import org.junit.*;
import org.laughingpanda.beaninject.Inject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FreeThreadTest {

    private int actions;
    private final Runnable action = () -> actions++;
    private static Globals globalsMock;

    @BeforeClass
    public static void mockGlobals(){
        globalsMock = mock(Globals.class);
        Inject.field("singleton_instance").of(globalsMock).with(globalsMock);
        when(globalsMock.getTimeScale()).thenReturn(1.);
    }

    @Before
    public void setup(){
        actions = 0;
    }

    @Test(timeout = 30)
    public void testTimes() throws InterruptedException {
        int times = 7;
        FreeThread thread = new FreeThread(1, action, times, "testTread");
        while (thread.isAlive()){
            Thread.sleep(1);
        }
        Assert.assertEquals(times, actions);
    }

    private boolean name_passed = false;
    @Test
    public void testName() throws InterruptedException {
        String name = "hfuwiyglsi";
        new FreeThread(1, () -> name_passed = Thread.currentThread().getName().equals(name), 1, name).join();
        Thread.sleep(10);
        assert name_passed;
    }

    @Test
    public void testStop() throws InterruptedException {
        FreeThread thread = new FreeThread(10, action, FreeThread.FOREVER, "testThread");
        thread.Stop();
        Thread.sleep(2);
        assert !thread.isAlive();
    }

    private boolean delay_passed = false;
    @Test
    public void testDelay() throws InterruptedException {
        int delay = 1234;
        int tolerance = 10;
        long start = System.currentTimeMillis();
        new FreeThread(delay, () -> {
            delay_passed = true;
            assert Math.abs((System.currentTimeMillis() - start) - delay) <= tolerance;
        }, 1, "testThread");
        Thread.sleep(2*delay);
        assert delay_passed;
    }

    private boolean delay_accel_passed = false;
    @Test
    public void testDelayOnAccel() throws InterruptedException {
        try {
            Inject.field("singleton_instance").of(globalsMock).with(null);
            Globals.getInstance();
            double accel = Globals.getInstance().getTimeAccelerant();
            when(globalsMock.getTimeScale()).thenReturn(accel);
            Inject.field("singleton_instance").of(globalsMock).with(globalsMock);

            int delay = 1234;
            int tolerance = 10;
            long start = System.currentTimeMillis();
            new FreeThread(delay, () -> {
                delay_accel_passed = true;
                assert Math.abs((System.currentTimeMillis() - start) - delay) <= tolerance;
            }, 1, "testThread");
            Thread.sleep(2 * delay);
            assert delay_accel_passed;
        }
        finally {
            mockGlobals();
        }
    }

}
