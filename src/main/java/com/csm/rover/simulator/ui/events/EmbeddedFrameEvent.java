package com.csm.rover.simulator.ui.events;

import com.csm.rover.simulator.ui.frame.EmbeddedFrame;

public class EmbeddedFrameEvent {

	public static enum Action { ADDED, REMOVED, MOVED, RESIZED, MAXIMIZED, ICONIFIED, CLOSED }
	
	private Action action;
	private EmbeddedFrame component;
	private Object old_state;
	private Object new_state;
	
	private EmbeddedFrameEvent() {}
	
	public static Builder builder(){
		return new Builder();
	}

	public static class Builder {
		
		private EmbeddedFrameEvent event;
		
		private Builder(){
			event = new EmbeddedFrameEvent();
			event.action = null;
			event.component = null;
			event.old_state = null;
			event.new_state = null;
		}
	
		public Builder setAction(Action action){
			event.action = action;
			return this;
		}
		
		public Builder setComponent(EmbeddedFrame frame){
			event.component = frame;
			return this;
		}
		
		public Builder setOldState(Object state){
			event.old_state = state;
			return this;
		}
		
		public Builder setNewState(Object state){
			event.new_state = state;
			return this;
		}
		
		public EmbeddedFrameEvent build(){
			return event;
		}
		
	}

	public Action getAction(){
		return action;
	}

	public EmbeddedFrame getComponent(){
		return component;
	}

	public Object getOldState(){
		return old_state;
	}

	public Object getNewState(){
		return new_state;
	}
	
}
