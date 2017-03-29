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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.util.*;

class FrameRegistry {
	private static final Logger LOG = LogManager.getLogger(FrameRegistry.class);

	static {
		miscFrames = new TreeMap<>();
		platformDisplays = new TreeMap<>();
		environmentDisplays = new TreeMap<>();
		findFrames();
	}

    private FrameRegistry() {}
	
	private static Map<String, Class<? extends EmbeddedFrame>> miscFrames;
	private static Map<String, Class<? extends PlatformDisplay>> platformDisplays;
	private static Map<String, Class<? extends EnvironmentDisplay>> environmentDisplays;
	
	private static void findFrames(){
		LOG.log(Level.INFO, "Compiling frame registry:");
		Reflections reflect = new Reflections("com.spears.ui");
		Set<Class<? extends EmbeddedFrame>> frameclasses = reflect.getSubTypesOf(EmbeddedFrame.class);
        for (Class<? extends EmbeddedFrame> frameclass : frameclasses){
        	FrameMarker marker = frameclass.getAnnotation(FrameMarker.class);
            if (marker == null){
                continue;
            }
        	String name = marker.name();
        	try {
				EmbeddedFrame frame = frameclass.newInstance();
                if (frame instanceof PlatformDisplay){
                    if (marker.platform().equals("")){
                        LOG.log(Level.WARN, "PlatformDisplay " + name + " does not specify a platform type");
                    }
                    platformDisplays.put(marker.platform(), (Class<? extends PlatformDisplay>)frameclass);
                }
                else if (frame instanceof EnvironmentDisplay){
                    if (marker.platform().equals("")){
                        LOG.log(Level.WARN, "EnvironmentDisplay " + name + " does not specify a platform type");
                    }
                    environmentDisplays.put(marker.platform(), (Class<? extends EnvironmentDisplay>)frameclass);
                }
                else {
                    miscFrames.put(name, frameclass);
                }
	        	LOG.log(Level.INFO, "Registered EmbeddedFrame \"{}\" under \"{}\"", frameclass.toString(), name);
			}
            catch (InstantiationException | IllegalAccessException e) {
				LOG.log(Level.WARN, "Class {} cannot be instantiated, requires default constructor", name);
			}
            catch (ClassCastException e){
                LOG.log(Level.WARN, "The display " + name + " could not be cast and failed to register", e);
            }
        }
        LOG.log(Level.INFO, "Completed frame registry");
	}
	
	private static String getClassName(Class<?> frameclass) {
		String classstr = frameclass.toString();
		return classstr.substring(classstr.lastIndexOf('.')+1);
	}

	static List<String> getFrameClasses(){
		return new ArrayList<>(miscFrames.keySet());
	}
	
	static Class<? extends EmbeddedFrame> getFrameClass(String frame){
		if (miscFrames.containsKey(frame)){
			return miscFrames.get(frame);
		}
		else {
			throw new IndexOutOfBoundsException("There is no frame registered to the name \""+frame+"\"");
		}
	}

    static Class<? extends PlatformDisplay> getPlatformDisplay(String type){
        if (platformDisplays.containsKey(type)){
            return platformDisplays.get(type);
        }
        else {
            throw new IllegalArgumentException("No platform display is registered for type " + type);
        }
    }

    static Class<? extends EnvironmentDisplay> getEnvironmentDisplay(String type){
        if (environmentDisplays.containsKey(type)){
            return environmentDisplays.get(type);
        }
        else {
            throw new IllegalArgumentException("No environment display is registered for type " + type);
        }
    }
	
}
