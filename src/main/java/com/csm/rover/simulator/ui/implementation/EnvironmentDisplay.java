package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.environments.PlatformEnvironment;

abstract class EnvironmentDisplay extends DisplayWindow {

    protected final String platform_type;

    protected EnvironmentDisplay(String type){
        this.platform_type = type;
    }

    final void setEnvironment(PlatformEnvironment environment){
        if (environment.getType().equals(this.platform_type)){
            doSetEnvironment(environment);
            start();
        }
        else {
            throw new IllegalArgumentException("The provided environment did not match the expected type for this display");
        }
    }

    abstract void doSetEnvironment(PlatformEnvironment environment);

}
