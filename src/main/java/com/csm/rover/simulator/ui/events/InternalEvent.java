package com.csm.rover.simulator.ui.events;

import javax.swing.JComponent;

public class InternalEvent<T> {

	private JComponent component;
	private String action;
	private T old_state;
	private T new_state;
	
	public InternalEvent(JComponent component, String action, T old_state,
			T new_state) {
		super();
		this.component = component;
		this.action = action;
		this.old_state = old_state;
		this.new_state = new_state;
	}

	public JComponent getComponent() {
		return component;
	}

	public String getAction() {
		return action;
	}

	public T getOldState() {
		return old_state;
	}

	public T getNewState() {
		return new_state;
	}

	@Override
	public String toString() {
		return "InternalEvent [component=" + component + ", action=" + action
				+ ", old_state=" + old_state + ", new_state=" + new_state + "]";
	}
	
}
