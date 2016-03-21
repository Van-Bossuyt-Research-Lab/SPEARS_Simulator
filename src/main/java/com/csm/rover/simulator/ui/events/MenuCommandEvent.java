package com.csm.rover.simulator.ui.events;

import java.awt.event.ActionEvent;

public class MenuCommandEvent {
	
	public static enum Action { OPEN, NEW_SIM, VIEW_LOG, EXIT, NEW_FRAME, GRID_CHANGE, DIVIDER_CHANGE, SHOW_FRAME, VOLUME_CHANGE, SETTINGS }
	
	private Action action;
	private Object value;
	private ActionEvent origin;
	
	private MenuCommandEvent() {}
	
	public static Builder builder(){
		return new Builder();
	}
	
	public static class Builder {
		
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
