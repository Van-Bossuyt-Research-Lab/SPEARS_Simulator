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
