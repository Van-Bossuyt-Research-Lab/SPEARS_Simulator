package com.csm.rover.simulator.rover.phsicsModels;

import java.util.Random;

import com.csm.rover.simulator.wrapper.Access;
import com.csm.rover.simulator.wrapper.Globals;

public class RiskOnHazard extends RoverPhysicsModel {
	
	private boolean failed = false;
	Random rnd = new Random();
	
	public RiskOnHazard() {}
	
	private RiskOnHazard(RiskOnHazard origin){
		super(origin);
		failed = origin.failed;
	}
	
	@Override
	public void excecute() throws Exception {
		if (failed){
			super.motor_states = new int[] { 0, 0, 0, 0 };
		}
		super.excecute();
		if (!failed && rnd.nextInt((int)(10000/super.time_step*60)) < Math.pow(Access.getHazardValue(location), 3)){ //a level 10 Hazard gives 10% chance of failure a minute, a level 1 hazard gives 0.01% chance of failure per minute
			Globals.writeToLogFile(roverName, Globals.TimeMillis + "\tRover failed in level " + Access.getHazardValue(location) + " Hazard.");
			failed = true;
		}
	}
	
	@Override
	public RiskOnHazard clone(){
		return new RiskOnHazard(this);
	}

}
