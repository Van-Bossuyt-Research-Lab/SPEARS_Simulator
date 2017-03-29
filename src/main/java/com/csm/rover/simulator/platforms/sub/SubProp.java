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

package com.csm.rover.simulator.platforms.sub;

import com.csm.rover.simulator.objects.CoverageIgnore;

/**
 * Enum to track the location of motor/props in an array.
 */
public enum SubProp {
    L(0),
    LEFT(0),
    R(1),
    RIGHT(1),
    F(2),
    FRONT(2),
    B(3),
    BACK(3);

    private int value;

    SubProp(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    @CoverageIgnore
    public boolean equals(SubProp other){
        return value == other.value;
    }
}
