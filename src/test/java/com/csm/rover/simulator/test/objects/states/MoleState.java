package com.csm.rover.simulator.test.objects.states;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

@State(type="Mole")
public class MoleState extends PlatformState {

    public MoleState() {
        super(PlatformState.builder("Mole")
            .addParameter("name", String.class)
            .addParameter("weight", Double.class)
            .addParameter("strength", Double[].class)
            .addParameter("species", String.class, "Star-nose")
            .addParameter("age", Double.class, 4.9)
            .addParameter("toes", Double[].class, new Double[]{5., 5., 5., 5.}));
    }

}
