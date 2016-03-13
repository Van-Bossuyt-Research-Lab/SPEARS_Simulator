package com.csm.rover.simulator.ui.events;

import java.util.ArrayList;
import java.util.List;

public class InternalEventHandler {
	
	static {
		menuListeners = new ArrayList<>();
		frameListeners = new ArrayList<>();
	}
	
	private static List<MenuCommandListener> menuListeners;
	private static List<EmbeddedFrameListener> frameListeners;
	
	public static void registerInternalListener(MenuCommandListener menuListen){
		menuListeners.add(menuListen);
	}
	
	public static void registerInternalListener(EmbeddedFrameListener frameListen){
		frameListeners.add(frameListen);
	}
	
	public static void fireInternalEvent(MenuCommandEvent menuEvent){
		for (MenuCommandListener menuListen : menuListeners){
			menuListen.menuAction(menuEvent);
		}
	}
	
	public static void fireInternalEvent(EmbeddedFrameEvent frameEvent){
		for (EmbeddedFrameListener frameListen : frameListeners){
			frameListen.frameChanged(frameEvent);
		}
	}
	
}
