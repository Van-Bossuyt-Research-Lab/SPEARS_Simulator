package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.environments.EnvironmentIO;
import com.csm.rover.simulator.environments.EnvironmentRegistry;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.objects.CoverageIgnore;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.objects.io.ConfigurationFileFilter;
import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.objects.io.RunConfiguration;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.PlatformRegistry;
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
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CancellationException;

/**
 * The main class for the application.  Initializes the program and initiates simulation runs.
 * Many methods in this class are protected only for testing purposes and should nto be called.
 */
public class Admin {
	private static final Logger LOG = LogManager.getLogger(Admin.class);

	private Globals GLOBAL;

    private Map<String, PlatformEnvironment> environments;
    private Map<String, List<Platform>> platforms;

    /**
     * Program main method.  Pass a filepath into args to autorun in command line mode.
     *
     * @param args Program arguments
     */
    public static void main(String[] args) {
        LOG.log(Level.INFO, "Program runtime log for SPEARS simulation software");
		Admin admin = getInstance();
        admin.checkRegistries();
        admin.checkFolders();
		if (args.length == 0) {
			LOG.log(Level.INFO, "Starting simulator in GUI mode");
            admin.setUpGUI();
			if (admin.useSave()) {
				try {
					admin.startWithGUI(admin.getRunConfigFile());
				}
                catch (CancellationException e){
                    admin.promptStartup();
                }
				catch (Exception e){
					LOG.log(Level.ERROR, "Simulator failed to start", e);
                    admin.alertFailed(e);
                    admin.shutDownSimulator(2);
				}
			}
            else {
                admin.promptStartup();
            }
		}
		else {
			LOG.log(Level.INFO, "Stating simulator in Command Line mode");
			File cfgFile = new File(args[0]);
            if (admin.verifyFile(cfgFile)){
                try {
                    admin.beginSimulation(admin.getRunConfigFile(cfgFile));
                }
                catch (Exception e){
                    LOG.log(Level.ERROR, "configuration file failed to initiate", e);
					admin.shutDownSimulator(2);
                }
            }
            else {
                LOG.log(Level.FATAL, "Expected a valid file path to a JSON configuration file.  Got: \"{}\"", cfgFile.getAbsolutePath());
				admin.shutDownSimulator(3);
            }
		}
	}

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

    /**
     * Triggers the registries to populate
     */
    @CoverageIgnore
    protected void checkRegistries(){
        RegistryManager.checkRegistries();
    }

    /**
     * Checks that run-critical directories exist and creates them if they do not
     */
    @CoverageIgnore
    protected void checkFolders(){
        String[] requiredFolders = { "Temp", "Logs" };
        for (String folder : requiredFolders){
            File f = new File(folder);
            if (!f.exists()){
                if (!f.mkdirs()){
                    LOG.log(Level.WARN, "Failed to create directory " + folder);
                }
            }
        }
    }

    /**
     * Initializes the {@link UiFactory}
     */
    @CoverageIgnore
    protected void setUpGUI() {
        MainMenu menu = UiFactory.getMainMenu();
        menu.setCloseOperation(this::shutDownSimulator);
        menu.setVolumeListener(SoundPlayer::setVolume);

        UiFactory.getApplication().show();
    }

    /**
     * Populates and displays the startup GUI dialog.
     */
    @CoverageIgnore
    protected void promptStartup(){
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

    /**
     * Initializes a simulation run with the GUI.
     *
     * @param config RunConfiguration to use
     */
    @CoverageIgnore
    protected void startWithGUI(RunConfiguration config){
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

    /**
     * Prompts the user to select a configuration file to be opened.
     *
     * @return Extracted RunConfiguration
     * @throws Exception If the file could not be found or parsed
     */
    @CoverageIgnore
    protected RunConfiguration getRunConfigFile() throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new ConfigurationFileFilter());
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            return getRunConfigFile(chooser.getSelectedFile());
        }
        else {
            throw new CancellationException();
        }
    }

    /**
     * Extracts a RunConfiguration from a save file.
     *
     * @param file File to open
     * @return Extracted RunConfiguration
     * @throws Exception If the file could not be found or parsed
     */
    @CoverageIgnore
    protected RunConfiguration getRunConfigFile(File file) throws Exception {
        return RunConfiguration.fromFile(file);
    }

    /**
     * Checks that the file is a valid configuration file.
     *
     * @param f File to check
     * @return True is f exists and has the appropriate extension
     */
    protected boolean verifyFile(File f){
        return f.exists() && getFileType(f).equals("cfg");
    }

    /**
     * Asks the user if they want to load a saved file
     *
     * @return True if they do
     */
    @CoverageIgnore
    protected boolean useSave(){
        return UiFactory.newPopUp()
                .setMessage("Would you like to open a saved run configuration?")
                .setSubject("Load Config")
                .showConfirmDialog(PopUp.Buttons.YES_NO_OPTIONS) == PopUp.Options.YES_OPTION;
    }

    private String getFileType(File file){
        String filename = file.getName();
        return filename.substring(filename.lastIndexOf(".")+1, filename.length());
    }

    /**
     * Displays a pop-up notification for an exception.
     *
     * @param e The thrown exception
     */
    @CoverageIgnore
    protected void alertFailed(Exception e){
        UiFactory.newPopUp().setMessage(e.getMessage()).setSubject("Failed to Start").showConfirmDialog(PopUp.Buttons.DEFAULT_OPTIONS);
    }

    /**
     * Initializes a simulation run form the configuration and starts running.
     *
     * @param config RunConfiguration to use
     */
	protected void beginSimulation(RunConfiguration config){
        ObjectMapper mapper = new ObjectMapper();
        try {
            LOG.log(Level.INFO, "Starting simulation with configuration:\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config));
        } catch (JsonProcessingException e) {}
        for (String type : config.getTypes()){
            try {
                PlatformEnvironment enviro = makeEnvironment(config.getEnvironmentFile(type), type);
                environments.put(type, enviro);

                platforms.put(type, new ArrayList<>());
                for (PlatformConfig platformConfig : config.getPlatforms(type)) {
                    Platform platform = makePlatform(platformConfig);
                    platforms.get(type).add(platform);
                    enviro.addPlatform(platform);
                    platform.start();
                }
            }
            catch (IOException e){
                LOG.log(Level.ERROR, "Failed to read in Environment - Terminating", e);
                shutDownSimulator(1);
            }
        }

        GLOBAL.startTime(config.accelerated);
        if (config.accelerated){
            GLOBAL.setUpAcceleratedRun(config.runtime);
        }
	}

    /**
     * Loads a environment from a file.  See {@link EnvironmentIO#loadEnvironment(File, Class)}.
     *
     * @param file File to use
     * @param type Type of environment
     * @return The environment
     * @throws IOException If the file could not be loaded
     */
    @CoverageIgnore
    protected PlatformEnvironment makeEnvironment(File file, String type) throws IOException {
        return EnvironmentIO.loadEnvironment(file, EnvironmentRegistry.getEnvironment(type));
    }

    /**
     * Generates a new Platform from a configuration.  See {@link Platform#buildFromConfiguration(PlatformConfig)}.
     *
     * @param config Config to use
     * @return new Platform
     */
    @CoverageIgnore
    protected Platform makePlatform(PlatformConfig config){
        return Platform.buildFromConfiguration(config);
    }

    /**
     * Shuts down SPEARS.  See {@link System#exit(int)}.
     *
     * @param status Exit status code
     */
    @CoverageIgnore
    void shutDownSimulator(int status){
        if (status == 0){
            shutDownSimulator();
        }
        System.exit(status);
    }

    /**
     * Shuts down SPEARS.  See {@link System#exit(int) System.exit(0)}.
     */
    @CoverageIgnore
    void shutDownSimulator(){
        LOG.log(Level.INFO, "Exiting SPEARS");
        System.exit(0);
    }

}
