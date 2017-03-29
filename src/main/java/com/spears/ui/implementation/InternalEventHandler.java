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

import java.util.ArrayList;
import java.util.List;

class InternalEventHandler {
	
	static {
		menuListeners = new ArrayList<>();
		frameListeners = new ArrayList<>();
	}
	
	private static List<MenuCommandListener> menuListeners;
	private static List<EmbeddedFrameListener> frameListeners;
	
	static void registerInternalListener(MenuCommandListener menuListen){
		menuListeners.add(menuListen);
	}
	
	static void registerInternalListener(EmbeddedFrameListener frameListen){
		frameListeners.add(frameListen);
	}
	
	static void fireInternalEvent(MenuCommandEvent menuEvent){
		List<MenuCommandListener> listeners = new ArrayList<>(menuListeners);
		for (MenuCommandListener menuListen : listeners){
			menuListen.menuAction(menuEvent);
		}
	}
	
	static void fireInternalEvent(EmbeddedFrameEvent frameEvent){
		List<EmbeddedFrameListener> listeners = new ArrayList<>(frameListeners);
		for (EmbeddedFrameListener frameListen : listeners){
			frameListen.frameChanged(frameEvent);
		}
	}
	
}
