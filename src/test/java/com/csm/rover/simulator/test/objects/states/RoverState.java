package com.csm.rover.simulator.test.objects.states;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

@State(type="Rover")
public class RoverState extends PlatformState {

    public RoverState() {
        super(PlatformState.builder("Rover").addParameter("state", Double.class));
    }

}
