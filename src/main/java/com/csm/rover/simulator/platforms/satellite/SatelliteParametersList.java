package com.csm.rover.simulator.platforms.satellite;

import com.csm.rover.simulator.platforms.PlatformPhysicsModel;

import java.io.Serializable;
import java.util.Map;

public class SatelliteParametersList extends PlatformPhysicsModel implements Serializable {

    public SatelliteParametersList(){
        super("Satellite");
    }

	private static final long serialVersionUID = 1L;

    public void constructParameters(Map<String, Double> params){

    }

}
