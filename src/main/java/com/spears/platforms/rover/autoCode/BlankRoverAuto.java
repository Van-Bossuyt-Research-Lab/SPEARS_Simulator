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

package com.spears.platforms.rover.autoCode;

import com.spears.objects.util.DecimalPoint;
import com.spears.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type="Rover", name="NULL", parameters={})
public class BlankRoverAuto extends RoverAutonomousCode {

	private static final long serialVersionUID = 4270320886718139449L;

	public BlankRoverAuto() {
		super("NULL", "NULL");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
	public String doNextCommand(long milliTime, DecimalPoint location,
			double direction, Map<String, Double> params) {
		// TODO Auto-generated method stub
		return "";
	}

}
