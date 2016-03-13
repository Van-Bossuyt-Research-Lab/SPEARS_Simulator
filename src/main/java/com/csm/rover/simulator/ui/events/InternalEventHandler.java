package com.csm.rover.simulator.ui.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.swing.JComponent;

public class InternalEventHandler {
	
	static {
		instance = Optional.empty();
	}

	private Map<JComponent, Map<String, List<InternalEventListener<?>>>> listeners;
	
	private InternalEventHandler(){
		listeners = new HashMap<>();
	}
	
	private static Optional<InternalEventHandler> instance;
	public static InternalEventHandler getInstance(){
		if (!instance.isPresent()){
			instance = Optional.of(new InternalEventHandler());
		}
		return instance.get();
	}
	
	public <T> void addInternalEventListener(JComponent component, String action, InternalEventListener<T> listen){
		if (!listeners.containsKey(component)){
			listeners.put(component, new TreeMap<String, List<InternalEventListener<?>>>());
		}
		if (!listeners.get(component).containsKey(action)){
			listeners.get(component).put(action, new ArrayList<InternalEventListener<?>>());
		}
		listeners.get(component).get(action).add(listen);
	}
	
	public <T> void fireInternalEvent(JComponent component, String action){
		fireInternalEvent(component, action, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void fireInternalEvent(JComponent component, String action, T oldval, T newval){
		Optional<ClassCastException> error = Optional.empty();
		if (listeners.containsKey(component) && listeners.get(component).containsKey(action)){
			InternalEvent<T> event = new InternalEvent<T>(component, action, oldval, newval);
			for (InternalEventListener<?> listen : listeners.get(component).get(action)){
				try {
					((InternalEventListener<T>)listen).internalEvent(event);
				}
				catch (ClassCastException ex){
					error = Optional.of(ex);
				}
			}
		}
		if (error.isPresent()){
			throw error.get();
		}
		
	}
	
}
