package com.csm.rover.simulator.platforms.satellite;

import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.io.Serializable;
import java.util.Map;

@AutonomousCodeModel(type="Satellite", name="Default", parameters={})
public class SatelliteAutonomousCode extends PlatformAutonomousCodeModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	public SatelliteAutonomousCode(){
        super("Satellite");
		this.name = "Default";
	}
	
	public String getName(){
		return name;
	}

    @Override
    public void constructParameters(Map<String, Double> params) {}

    @Override
    public String nextCommand(long milliTime, PlatformState state) {
        return "";
    }
}
