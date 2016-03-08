package com.csm.rover.simulator.platforms.satellite;

import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.State;

@State(type="Satellite")
public class SatelliteState extends PlatformState {

    public SatelliteState(){
        super(PlatformState.builder("Satellite")
                .addParameter("altitude", Double.class, 0.)
                .addParameter("orbitIncline", Double.class, 0.)
                .addParameter("theta", Double.class, 0.)
        );
    }

}
