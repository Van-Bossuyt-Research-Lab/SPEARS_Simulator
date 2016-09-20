package com.csm.rover.simulator.ui.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

public class FrameRegistry {
	private static final Logger LOG = LogManager.getLogger(FrameRegistry.class);

	static {
		frameList = new TreeMap<>();
		findFrames();
	}
	
	private static Map<String, Class<? extends EmbeddedFrame>> frameList;
	
	private static void findFrames(){
		LOG.log(Level.INFO, "Compiling frame registry:");
		Reflections reflect = new Reflections("com.csm.rover.simulator.ui");
		Set<Class<? extends EmbeddedFrame>> frameclasses = reflect.getSubTypesOf(EmbeddedFrame.class);
        for (Class<? extends EmbeddedFrame> frameclass : frameclasses){
        	FrameMarker marker = frameclass.getAnnotation(FrameMarker.class);
        	String name = marker == null ? getClassName(frameclass) : marker.name();
        	try {
				frameclass.newInstance();
	        	frameList.put(name, frameclass);
	        	LOG.log(Level.INFO, "Registered EmbeddedFrame \"{}\" under \"{}\"", frameclass.toString(), name);
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.log(Level.WARN, "Class {} cannot be instantiated, requires default constructor", name);
			}
        }
        LOG.log(Level.INFO, "Completed frame registry");
	}
	
	private static String getClassName(Class<?> frameclass) {
		String classstr = frameclass.toString();
		return classstr.substring(classstr.lastIndexOf('.')+1);
	}

	public static List<String> getFrameClasses(){
		return new ArrayList<>(frameList.keySet());
	}
	
	public static Class<? extends EmbeddedFrame> getFrameClass(String frame){
		if (frameList.containsKey(frame)){
			return frameList.get(frame);
		}
		else {
			throw new IndexOutOfBoundsException("There is no frame registered to the name \""+frame+"\"");
		}
	}
	
}
