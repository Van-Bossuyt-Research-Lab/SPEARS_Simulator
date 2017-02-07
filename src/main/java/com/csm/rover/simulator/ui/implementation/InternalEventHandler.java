package com.csm.rover.simulator.ui.implementation;

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
