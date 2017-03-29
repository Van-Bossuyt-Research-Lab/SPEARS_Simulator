/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.ui.implementation;

class EmbeddedFrameEvent {

	static enum Action { ADDED, MOVED, RESIZED, MAXIMIZED, ICONIFIED, CLOSED }
	
	private Action action;
	private EmbeddedFrame component;
	private Object old_state;
	private Object new_state;
	
	private EmbeddedFrameEvent() {}
	
	static Builder builder(){
		return new Builder();
	}

	static class Builder {
		
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
