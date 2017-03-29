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

package com.spears.objects;

import com.spears.wrapper.Globals;
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
        Thread.sleep(200);
        verify(globalsMock, times(times)).threadCheckIn(name);
        verify(globalsMock, atLeastOnce()).checkOutThread(name);
    }

}
