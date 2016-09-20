package com.csm.rover.simulator.ui.implementation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FrameMarker {

	String name();
	String platform() default "";
	
}
