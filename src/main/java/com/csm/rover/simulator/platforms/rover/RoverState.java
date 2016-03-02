package com.csm.rover.simulator.platforms.rover;

import com.csm.rover.simulator.platforms.PlatformState;

public class RoverState extends PlatformState {

    public RoverState(){
        super("Rover", new String[]{
                "x",
                "y",
                "dir"
        });
        super.setDefaultValue("x", 0);
        super.setDefaultValue("y", 0);
        super.setDefaultValue("dir", Math.PI/2.);
    }

}
