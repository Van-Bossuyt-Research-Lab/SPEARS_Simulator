package com.csm.rover.simulator.platforms.satellite;

import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.io.Serializable;

@AutonomousCodeModel(type="Satellite", name="parent")
public abstract class SatelliteAutonomusCode extends PlatformAutonomousCodeModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String satelliteName;
	
	public SatelliteAutonomusCode(String name, String satellite){
        super("Satellite");
		this.name = name;
		satelliteName = satellite;
	}
	
	public String getName(){
		return name;
	}

}
