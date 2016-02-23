package com.csm.rover.simulator.platforms.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AutonomousCodeModel {

    String type();
    String name();

}
