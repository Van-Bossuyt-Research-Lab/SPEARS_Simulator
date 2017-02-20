package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.FreeThread;

abstract class DisplayWindow extends EmbeddedFrame {

    static {
        count = 0;
    }

    private static int count;

    private int interval = 100;
    private boolean started = false;

    final void start(){
        new FreeThread(interval, this::update, FreeThread.FOREVER, "display"+count);
        started = true;
        count++;
    }

    abstract protected void update();

    protected void setInterval(int millisec){
        if (millisec <= 0){
            throw new IllegalArgumentException("Delay time must be >= 0");
        }
        if (started){
            throw new IllegalStateException("The display is already started, cannot change interval");
        }
        interval = millisec;
    }

}
