package com.csm.rover.simulator.ui.implementation;

import java.awt.event.ActionEvent;

class MenuCommandEvent {
	
	static enum Action { OPEN, NEW_SIM, VIEW_LOG, NEW_FRAME, GRID_CHANGE, DIVIDER_CHANGE, SHOW_FRAME,  SETTINGS }
	
	private Action action;
	private Object value;
	private ActionEvent origin;
	
	private MenuCommandEvent() {}
	
	static Builder builder(){
		return new Builder();
	}
	
	static class Builder {
		
		private MenuCommandEvent event;
		
		private Builder(){
			event = new MenuCommandEvent();
			event.action = null;
			event.value = null;
			event.origin = null;
		}
		
		public Builder setAction(Action action){
			event.action = action;
			return this;
		}
		
		public Builder setValue(Object value){
			event.value = value;
			return this;
		}
		
		public Builder setOrigin(ActionEvent orig){
			event.origin = orig;
			return this;
		}
		
		public MenuCommandEvent build(){
			return event;
		}
		
	}

	public Action getAction(){
		return action;
	}

	public Object getValue(){
		return value;
	}

	public ActionEvent getOrigin(){
		return origin;
	}

	@Override
	public String toString() {
		return "MenuCommandEvent [action=" + action + ", value=" + value
				+ ", origin=" + origin + "]";
	}

}
