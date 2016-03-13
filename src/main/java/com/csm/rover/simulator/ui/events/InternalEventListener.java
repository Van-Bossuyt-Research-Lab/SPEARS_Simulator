package com.csm.rover.simulator.ui.events;

public interface InternalEventListener<T> {
	
	public void internalEvent(InternalEvent<T> e);

}
