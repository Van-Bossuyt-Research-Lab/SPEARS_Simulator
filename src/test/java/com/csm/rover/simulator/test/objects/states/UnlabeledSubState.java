package com.csm.rover.simulator.test.objects.states;

import com.csm.rover.simulator.platforms.PlatformState;

public class UnlabeledSubState extends PlatformState {

    public UnlabeledSubState() {
        super(PlatformState.builder("Sub"));
    }

}
