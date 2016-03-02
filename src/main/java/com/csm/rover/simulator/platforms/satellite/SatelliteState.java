package com.csm.rover.simulator.platforms.satellite;

import com.csm.rover.simulator.platforms.PlatformState;

public class SatelliteState extends PlatformState {

    public double altitude, orbitIncline, theta;

    public SatelliteState(){
        super("Satellite");
    }

    public static SatelliteState defaultState(){
        SatelliteState state = new SatelliteState();
        state.altitude = 0;
        state.orbitIncline = 0;
        state.theta = 0;
        return state;
    }

}
