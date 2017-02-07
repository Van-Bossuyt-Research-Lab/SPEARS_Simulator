package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.platforms.Platform;

abstract class PlatformDisplay extends DisplayWindow {

    protected final String platform_type;

    protected PlatformDisplay(String type){
        this.platform_type = type;
    }

    final void setPlatform(Platform platform){
        if (platform.getType().equals(this.platform_type)){
            doSetPlatform(platform);
            start();
        }
        else {
            throw new IllegalArgumentException("The provided platform did not match the expected type for this display");
        }
    }

    protected abstract void doSetPlatform(Platform platform);

}
