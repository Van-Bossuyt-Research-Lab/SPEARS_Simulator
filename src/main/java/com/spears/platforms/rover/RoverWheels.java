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

package com.spears.platforms.rover;

import com.spears.objects.CoverageIgnore;

/**
 * An enum for identifying a motor/wheel in an array.
 */
public enum RoverWheels {
	
	FL(0),
	FRONT_LEFT(0),
	FR(1),
	FRONT_RIGHT(1),
	BL(2),
	BACK_LEFT(2),
	BR(3),
	BACK_RIGHT(3);
	
	private int value;
	
	RoverWheels(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}

	@CoverageIgnore
	public boolean equals(RoverWheels other){
		return value == other.value;
	}
}
