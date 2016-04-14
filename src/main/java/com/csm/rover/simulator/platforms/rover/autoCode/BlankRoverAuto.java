package com.csm.rover.simulator.platforms.rover.autoCode;

import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

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
