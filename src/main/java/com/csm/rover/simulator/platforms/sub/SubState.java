package com.csm.rover.simulator.platforms.sub;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

/**
 * Created by PHM-Lab1 on 4/29/2016.
 */
@State(type = "Sub")
public class SubState extends PlatformState {
    public SubState(){
        super(PlatformState.builder("Sub"));
    }
}
