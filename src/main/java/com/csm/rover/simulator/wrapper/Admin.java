package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.control.PopUp;
import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.map.io.TerrainMapReader;
import com.csm.rover.simulator.map.modifiers.NormalizeMapMod;
import com.csm.rover.simulator.map.modifiers.PlasmaGeneratorMod;
import com.csm.rover.simulator.map.modifiers.SurfaceSmoothMod;
import com.csm.rover.simulator.objects.RunConfiguration;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.satellite.SatelliteObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class Admin {

//	public static Form GUI;
	private Globals GLOBAL;

    private static HumanInterfaceAbstraction HI;

    //Runtime Variables
    private TerrainMap terrainMap;
    private ArrayList<RoverObject> rovers;
    private ArrayList<SatelliteObject> satellites;
    private SerialBuffers serialBuffers;

	public static void main(String[] args) {
		Admin admin = getInstance();
		if (args.length == 0) {
            HI = new HiForm();
			boolean go = false;
			File config = new File("default.cfg");
			if (config.exists()) {
				if ((new PopUp()).showConfirmDialog("A quick run configuration file has been found.  Would you like to run the simulator from the file?", "Quick Run", PopUp.YES_NO_OPTIONS) == PopUp.YES_OPTION) {
					go = true;
				}
			}
			if (go) {
				//TODO run without gui
				try {
					admin.beginSimulation(new RunConfiguration(new File("default.cfg")));
				}
				catch (Exception e){
					Globals.getInstance().reportError("Admin", "main", e);
				}
			}
		}
		else {
			//TODO Load from console config things
		}
	}

	//TODO clean up this interface for OCP
	private Admin(){
		GLOBAL = Globals.getInstance();
        terrainMap = new TerrainMap();
	}

    private static Optional<Admin> singleton_instance = Optional.empty();
    public static Admin getInstance(){
        if (!singleton_instance.isPresent()){
            singleton_instance = Optional.of(new Admin());
        }
        return singleton_instance.get();
    }

    public static HumanInterfaceAbstraction getCurrentInterface(){
        return HI;
    }

	public void beginSimulation(RunConfiguration config){
		//TODO add option to toggle time shifted executions
		if (config.rovers.size() == 0 ||config.satellites.size() == 0){
			System.err.println("Invalid Configuration.  Requires at least 1 rover and 1 satellite.");
			return;
		}
        else {
            this.rovers = config.rovers;
            this.satellites = config.satellites;
        }

        serialBuffers = new SerialBuffers(config.namesAndTags.getTags(), HI);
        RoverObject.setSerialBuffers(serialBuffers);
        SatelliteObject.setSerialBuffers(serialBuffers);

		if (config.mapFromFile){
			try {
				if (!config.mapFile.exists()){
					throw new Exception();
				}
				terrainMap = TerrainMapReader.loadMap(config.mapFile);
				GLOBAL.writeToLogFile("Start Up", "Using Map File: " + config.mapFile.getName());
			}
			catch (Exception e){
				System.err.println("Invalid Map File");
				e.printStackTrace();
				return;
			}
		}
        else {
            terrainMap.addMapModifier(new PlasmaGeneratorMod(config.mapRough));
            terrainMap.addMapModifier(new SurfaceSmoothMod());
            terrainMap.addMapModifier(new NormalizeMapMod());
            terrainMap.generateLandscape(config.mapSize, config.mapDetail);
			terrainMap.generateTargets(config.monoTargets, config.targetDensity);
			terrainMap.generateHazards(config.monoHazards, config.hazardDensity);
			GLOBAL.writeToLogFile("Start Up", "Using Random Map");
		}
        RoverObject.setTerrainMap(terrainMap);

        HI.initialize(config.namesAndTags, serialBuffers, rovers, satellites, terrainMap);

		if (config.accelerated){
			GLOBAL.writeToLogFile("Start Up", "Accelerating Simulation");
            HI.viewAccelerated(config.runtime);
		}

        for (RoverObject rover : rovers){
            rover.start();
        }
        for (SatelliteObject sat : satellites){
            sat.start();
        }
        HI.start();
		GLOBAL.startTime(config.accelerated);

		HI.updateSerialBuffers();
	}

}
