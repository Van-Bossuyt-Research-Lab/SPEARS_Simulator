package com.csm.rover.simulator.rover.autoCode;

import com.csm.rover.simulator.objects.DecimalPoint;

import java.util.Map;

public class BlankRoverAuto extends RoverAutonomusCode {

	private static final long serialVersionUID = 4270320886718139449L;

	public BlankRoverAuto() {
		super("NULL", "NULL");
		// TODO Auto-generated constructor stub
	}

	@Override
	public String nextCommand(long milliTime, DecimalPoint location,
			double direction, Map<String, Double> parameters) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public RoverAutonomusCode clone() {
		return new BlankRoverAuto();
	}

}
