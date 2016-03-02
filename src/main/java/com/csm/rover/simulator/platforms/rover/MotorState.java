package com.csm.rover.simulator.platforms.rover;

public enum MotorState {

	FORWARD(1),
	BACKWARD(-1),
	RELEASE(0);
	
	private int value;
	
	MotorState(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
}
