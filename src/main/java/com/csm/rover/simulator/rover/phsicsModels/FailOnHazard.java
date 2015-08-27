package com.csm.rover.simulator.rover.phsicsModels;

import com.csm.rover.simulator.wrapper.Access;

public class FailOnHazard extends RoverPhysicsModel {

	private boolean failed = false;
	
	public FailOnHazard() {}
	
	private FailOnHazard(FailOnHazard origin){
		super(origin);
		failed = origin.failed;
	}
	
	@Override
	public void excecute() throws Exception {
		if (failed){
			super.motor_states = new int[] { 0, 0, 0, 0 };
		}
		super.excecute();
		if (Access.isInHazard(super.location)){
			failed = true;
		}
	}
	
	@Override
	public FailOnHazard clone(){
		return new FailOnHazard(this);
	}
	
}
