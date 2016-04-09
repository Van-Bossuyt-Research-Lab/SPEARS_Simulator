package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.control.PopUp;
import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.map.io.TerrainMapReader;
import com.csm.rover.simulator.map.modifiers.NormalizeMapMod;
import com.csm.rover.simulator.map.modifiers.PlasmaGeneratorMod;
import com.csm.rover.simulator.map.modifiers.SurfaceSmoothMod;
import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.objects.io.RunConfiguration;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.PlatformRegistry;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.platforms.satellite.SatelliteObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class Admin {
	private static final Logger LOG = LogManager.getLogger(Admin.class);

//	public static Form GUI;
	private Globals GLOBAL;

    private static HumanInterfaceAbstraction HI;

    //Runtime Variables
    private TerrainMap terrainMap;
    private ArrayList<PlatformConfig> roverCfgs;
    private ArrayList<PlatformConfig> satCfgs;
    private SerialBuffers serialBuffers;

	public static void main(String[] args) {
        LOG.log(Level.INFO, "Program runtime log for SPEARS simulation software");
        PlatformRegistry.fillRegistry();
		Admin admin = getInstance();
		if (args.length == 0) {
			LOG.log(Level.INFO, "Starting simulator in GUI mode");
            HI = new HiForm();
			boolean go = false;
			File config = new File("config.json");
			if (config.exists()) {
				if ((new PopUp()).showConfirmDialog("A quick run configuration file has been found.  Would you like to run the simulator from the file?", "Quick Run", PopUp.YES_NO_OPTIONS) == PopUp.YES_OPTION) {
					go = true;
				}
			}
			if (go) {
				try {
					admin.beginSimulation(RunConfiguration.fromFile(config));
				}
				catch (Exception e){
					LOG.log(Level.ERROR, "Simulator failed to start", e);
					(new PopUp()).showConfirmDialog(e.getMessage(), "Failed to Start", PopUp.DEFAULT_OPTIONS);
					System.exit(2);
				}
			}
		}
		else {
			LOG.log(Level.INFO, "Stating simulator in Command Line mode");
			HI = new HiCmd();
            File cfgFile = new File(args[0]);
            if (cfgFile.exists() && getFileType(cfgFile).equals("cfg")){
                try {
                    admin.beginSimulation(RunConfiguration.fromFile(cfgFile));
                }
                catch (Exception e){
                    LOG.log(Level.ERROR, "configuration file failed to initiate", e);
					System.exit(2);
                }
            }
            else {
                LOG.log(Level.FATAL, "Expected a valid file path to a JSON configuration file.  Got: \"{}\"", cfgFile.getAbsolutePath());
				System.exit(3);
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
		this.roverCfgs = config.getPlatforms("Rover");
		this.satCfgs = config.getPlatforms("Satellite");
		if (roverCfgs.size() == 0 || satCfgs.size() == 0){
			LOG.log(Level.WARN, "Invalid Configuration.  Requires at least 1 rover and 1 satellite.");
			return;
		}

        serialBuffers = new SerialBuffers(config.namesAndTags.getTags(), HI);
        RoverObject.setSerialBuffers(serialBuffers);
        SatelliteObject.setSerialBuffers(serialBuffers);

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
        RoverObject.setTerrainMap(terrainMap);

		ArrayList<RoverObject> rovers = buildRoversFromConfig(roverCfgs);
		ArrayList<SatelliteObject> satellites = buildSatellitesFromConfig(satCfgs);

        HI.initialize(config.namesAndTags, serialBuffers, rovers, satellites, terrainMap);

		if (config.accelerated){
			LOG.log(Level.INFO, "Start Up: Accelerating Simulation");
            Globals.getInstance().setUpAcceleratedRun(HI, 3600000 * config.runtime);
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

	private ArrayList<RoverObject> buildRoversFromConfig(ArrayList<PlatformConfig> configs){
		ArrayList<RoverObject> out = new ArrayList<>();
		for (PlatformConfig config : configs){
            out.add(Platform.<RoverObject>buildFromConfiguration(config));
		}
        return out;
	}

	private ArrayList<SatelliteObject> buildSatellitesFromConfig(ArrayList<PlatformConfig> configs){
        ArrayList<SatelliteObject> out = new ArrayList<>();
        for (PlatformConfig config : configs){
            out.add(Platform.<SatelliteObject>buildFromConfiguration(config));
        }
        return out;
	}

}
