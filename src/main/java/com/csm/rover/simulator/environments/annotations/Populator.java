package com.csm.rover.simulator.environments.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Populator {

    String type();
    String name();
    String[] coordinates();
    String[] parameters();

}
