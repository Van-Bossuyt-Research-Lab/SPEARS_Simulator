package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.environments.EnvironmentIO;
import com.csm.rover.simulator.environments.EnvironmentRegistry;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.objects.io.ConfigurationFileFilter;
import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.objects.io.RunConfiguration;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.PlatformRegistry;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.platforms.satellite.SatelliteObject;
import com.csm.rover.simulator.ui.implementation.UiFactory;
import com.csm.rover.simulator.ui.sound.SoundPlayer;
import com.csm.rover.simulator.ui.sound.SpearsSound;
import com.csm.rover.simulator.ui.visual.AcceleratedView;
import com.csm.rover.simulator.ui.visual.MainMenu;
import com.csm.rover.simulator.ui.visual.PopUp;
import com.csm.rover.simulator.ui.visual.StartupWindow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class Admin {
	private static final Logger LOG = LogManager.getLogger(Admin.class);

	private Globals GLOBAL;

    //Runtime Variables
    private Map<String, PlatformEnvironment> environments;
    private Map<String, List<Platform>> platforms;


    public static void main(String[] args) {
        LOG.log(Level.INFO, "Program runtime log for SPEARS simulation software");
        RegistryManager.checkRegistries();
		Admin admin = getInstance();
		if (args.length == 0) {
			LOG.log(Level.INFO, "Starting simulator in GUI mode");
            admin.setUpGUI();
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileFilter(new ConfigurationFileFilter());
			if (UiFactory.newPopUp()
                            .setMessage("Would you like to open a saved run configuration?")
                            .setSubject("Load Config")
                            .showConfirmDialog(PopUp.Buttons.YES_NO_OPTIONS) == PopUp.Options.YES_OPTION &&
                    chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				try {
					admin.startWithGUI(RunConfiguration.fromFile(chooser.getSelectedFile()));
				}
				catch (Exception e){
					LOG.log(Level.ERROR, "Simulator failed to start", e);
                    UiFactory.newPopUp().setMessage(e.getMessage()).setSubject("Failed to Start").showConfirmDialog(PopUp.Buttons.DEFAULT_OPTIONS);
					System.exit(2);
				}
			}
            else {
                admin.promptStartup();
            }
		}
		else {
			LOG.log(Level.INFO, "Stating simulator in Command Line mode");
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

    private void setUpGUI() {
        MainMenu menu = UiFactory.getMainMenu();
        menu.setCloseOperation(this::shutDownSimulator);
        menu.setVolumeListener(SoundPlayer::setVolume);

        UiFactory.getApplication().show();
    }

    private void promptStartup(){
        StartupWindow startup = UiFactory.newStartUpWindow();
        for (String type : PlatformRegistry.getTypes()) {
            startup.registerPlatform(type);
            for (String code : PlatformRegistry.listAutonomousCodeModels(type)){
                startup.registerAutonomousCodeModel(type, code, PlatformRegistry.getParametersForAutonomousCodeModel(type, code));
            }
            for (String physics : PlatformRegistry.listPhysicsModels(type)){
                startup.registerPhysicsModel(type, physics, PlatformRegistry.getParametersForPhysicsModel(type, physics));
            }
            if (EnvironmentRegistry.getTypes().contains(type)) {
                for (String modifier : EnvironmentRegistry.listModifiers(type)) {
                    if (EnvironmentRegistry.getModifier(type, modifier).getAnnotation(Modifier.class).generator()){
                        startup.registerEnvironmentGenerator(type, modifier, EnvironmentRegistry.getParametersForModifier(type, modifier));
                    }
                    else {
                        startup.registerEnvironmentModifier(type, modifier, EnvironmentRegistry.getParametersForModifier(type, modifier));
                    }
                }
                for (String populator : EnvironmentRegistry.listPopulators(type)) {
                    startup.registerEnvironmentPopulator(type, populator, EnvironmentRegistry.getParametersForPopulator(type, populator));
                }
            }
        }
        startup.setStartUpAction(this::startWithGUI);
        startup.display();
    }

    private void startWithGUI(RunConfiguration config){
        beginSimulation(config);
        UiFactory.getApplication().start(environments, platforms);
        if (GLOBAL.isAccelerated()){
            UiFactory.getApplication().hide();
            AcceleratedView viewer = UiFactory.newAcceleratedView();
            viewer.setDuration(config.runtime);
            viewer.setAbortAction(this::shutDownSimulator);
            viewer.show();
            new SynchronousThread(config.runtime*60*1000-1000, () -> SoundPlayer.playSound(SpearsSound.GOODBYE), 1, "frame_closing");
        }
    }

    private static String getFileType(File file){
        String filename = file.getName();
        return filename.substring(filename.lastIndexOf(".")+1, filename.length());
    }

	//TODO clean up this interface for OCP
	private Admin(){
		GLOBAL = Globals.getInstance();
        environments = new TreeMap<>();
        platforms = new TreeMap<>();
	}

    private static Optional<Admin> singleton_instance = Optional.empty();
    public static Admin getInstance(){
        if (!singleton_instance.isPresent()){
            singleton_instance = Optional.of(new Admin());
        }
        return singleton_instance.get();
    }

	private void beginSimulation(RunConfiguration config){
        ObjectMapper mapper = new ObjectMapper();
        try {
            LOG.log(Level.INFO, "Starting simulation with configuration:\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config));
        } catch (JsonProcessingException e) {}

        List<String> ids = new ArrayList<>();
        for (String type : config.getTypes()){
            PlatformEnvironment enviro = EnvironmentIO.loadEnvironment(config.getEnvironmentFile(type), EnvironmentRegistry.getEnvironment(type));
            environments.put(type, enviro);

            platforms.put(type, new ArrayList<>());
            for (PlatformConfig platformConfig : config.getPlatforms(type)){
                Platform platform = Platform.buildFromConfiguration(platformConfig);
                ids.add(platformConfig.getID());
                platforms.get(type).add(platform);
                enviro.addPlatform(platform);
                platform.start();
            }
        }
        RoverObject.setSerialBuffers(new SerialBuffers(ids));

        GLOBAL.startTime(config.accelerated);
        if (config.accelerated){
            GLOBAL.setUpAcceleratedRun(config.runtime);
        }
	}

    void shutDownSimulator(){
        LOG.log(Level.INFO, "Exiting SPEARS");
        System.exit(0);
    }

}
