package com.csm.rover.simulator.platforms.satellite;

import com.csm.rover.simulator.platforms.PlatformState;

public class SatelliteState extends PlatformState {

    public SatelliteState(){
        super("Satellite", new String[] {
                "altitude",
                "orbitIncline",
                "theta"
        });
        super.setDefaultValue("altitude", 0);
        super.setDefaultValue("orbitIncline", 0);
        super.setDefaultValue("theta", 0);
    }

}
