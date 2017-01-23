package com.csm.rover.simulator.test.objects.states;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

@State(type="Sub")
public class NoncreatableUAVState extends PlatformState {

    private NoncreatableUAVState() {
        super(PlatformState.builder("UAV"));
    }

}
