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

package com.csm.rover.simulator.platforms.rover.autoCode;

import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type="Rover", name="Generic", parameters={"sec"})
public class GenericRover extends RoverAutonomousCode {

	private static final long serialVersionUID = -5883548370057346938L;
	
	private long lastActionTime = 0;
	private int action = 0;
	private int seconds = 1;
	private int power = 250;
	
	public GenericRover(){
		super("Generic", "Generic");
	}

    @Override
    public void constructParameters(Map<String, Double> params) {
        seconds = (int)params.get("sec").doubleValue();
    }

    @Override
    public String doNextCommand(
			long milliTime,
			DecimalPoint location,
			double direction,
            Map<String, Double> params
	){
        super.writeToLog(String.format("x:%g\ty:%g\tz:%g\tdir:%g", location.getX(), location.getY(), environment.getHeightAt(location), direction));
		if (milliTime-lastActionTime > 1000*seconds){
			lastActionTime = milliTime;
			action++;
			if (action%5 < 2){
				return "move";
			}
			else if (action%5 < 5){
				return "turnFR";
			}
			else {
				return "";
			}
		}
		else {
			return "";
		}
	}
	
}
