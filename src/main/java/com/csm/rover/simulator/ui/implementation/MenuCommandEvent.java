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
