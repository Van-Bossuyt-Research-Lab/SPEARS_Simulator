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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class Admin {
	private static final Logger LOG = LogManager.getFormatterLogger(Admin.class);

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
		LOG.log(Level.INFO, "Program runtime log for SPEARS simulation software");
		if (args.length == 0) {
			LOG.log(Level.INFO, "Starting simulator in GUI mode");
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
					admin.beginSimulation(new RunConfiguration(config));
				}
				catch (Exception e){
					LOG.log(Level.ERROR, "Simulator failed to start", e);
				}
			}
		}
		else {
			LOG.log(Level.INFO, "Stating simulator in Command Line mode");
			HI = new HiCmd();
            File cfgFile = new File(args[0]);
            if (cfgFile.exists() && getFileType(cfgFile).equals("cfg")){
                try {
                    admin.beginSimulation(new RunConfiguration(cfgFile));
                }
                catch (Exception e){
                    LOG.log(Level.ERROR, "cfg file failed to initiate", e);
                }
            }
            else {
                System.err.println("Expected a valid file path to a .cfg file.  Got: \"" + cfgFile.getAbsolutePath() + "\"");
            }
		}
	}

    private static String getFileType(File file){
        String filename = file.getName();
        return filename.substring(filename.lastIndexOf(".")+1, filename.length());
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
				LOG.log(Level.INFO, "Start Up: Using map file: {}", config.mapFile.getName());
			}
			catch (Exception e){
				LOG.log(Level.WARN, "Start Up: Invalid map file", e);
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
			LOG.log(Level.INFO, "Startup: Using random map");
		}
        RoverObject.setTerrainMap(terrainMap);

        HI.initialize(config.namesAndTags, serialBuffers, rovers, satellites, terrainMap);

		if (config.accelerated){
			LOG.log(Level.INFO, "Start Up: Accelerating Simulation");
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
