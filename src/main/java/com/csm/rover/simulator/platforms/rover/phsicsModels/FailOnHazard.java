package com.csm.rover.simulator.platforms.rover.phsicsModels;

public class FailOnHazard extends RoverPhysicsModel {

	private static final long serialVersionUID = 1973210335815945685L;
	
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
		if (MAP.isPointInHazard(super.location)){
			failed = true;
		}
	}
	
	@Override
	public FailOnHazard clone(){
		return new FailOnHazard(this);
	}
	
}
