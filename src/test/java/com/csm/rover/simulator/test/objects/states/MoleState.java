package com.csm.rover.simulator.test.objects.states;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

@State(type="Mole")
public class MoleState extends PlatformState {
    public MoleState() {
        super(PlatformState.builder("Mole"));
    }
}
