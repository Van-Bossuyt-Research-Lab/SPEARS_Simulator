package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.ui.sound.SoundPlayer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class UiConfiguration {
    private static final Logger LOG = LogManager.getLogger(UiConfiguration.class);

    private static final File configFile;
    private static Optional<UiConfiguration> instance = Optional.empty();

    static {
        configFile = new File("ui.json");
        load();
        setupListeners();
    }

    private static void load(){
        if (configFile.exists()){
            ObjectMapper mapper = new ObjectMapper();
            try {
                instance = Optional.of(mapper.readValue(configFile, UiConfiguration.class));
            }
            catch (IOException e) {
                LOG.log(Level.WARN, "Failed to read UI config - using default values", e);
            }
            if (instance.isPresent()) {
                return;
            }
        }
        //Load the default values
        instance = Optional.of(new UiConfiguration(
                "HIGH",
                1,
                1,
                0.5,
                new HashMap<>()
        ));
    }

    private static void updateSave(){
        if (instance.isPresent()) {
            LOG.log(Level.DEBUG, "Saving UI configuration");
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writeValue(configFile, instance.get());
            }
            catch (IOException e) {
                LOG.log(Level.WARN, "Failed to save config file", e);
            }
        }
        else {
            LOG.log(Level.DEBUG, "Saving before instance is generated");
        }
    }

    private static void setupListeners(){
        InternalEventHandler.registerInternalListener((MenuCommandEvent e) -> {
            if (e.getAction().equals(MenuCommandEvent.Action.GRID_CHANGE)){
                char side = ((String)e.getValue()).charAt(0);
                int numb = Integer.parseInt(((String)e.getValue()).charAt(1)+"");
                if (side == 'R'){
                    changeDesktopDivsRight(numb);
                }
                else if (side == 'L'){
                    changeDesktopDivsLeft(numb);
                }
            }
        });
    }

    private String volume;
    private int desktop_divs_left, desktop_divs_right;
    private double desktop_center_line;
    private Map<String, String> platform_folders;

    @JsonCreator
    public UiConfiguration(
            @JsonProperty("volume") String volume,
            @JsonProperty("desktop_divs_left") int desktop_divs_left,
            @JsonProperty("desktop_divs_right") int desktop_divs_right,
            @JsonProperty("desktop_center_line") double desktop_center_line,
            @JsonProperty("platform_folders") Map<String, String> platform_folders
    ){
        if (instance.isPresent()){
            throw new IllegalStateException("UiConfiguration has already been initialized, cannot make 2 copies");
        }
        this.volume = volume;
        this.desktop_divs_left = desktop_divs_left;
        this.desktop_divs_right = desktop_divs_right;
        this.desktop_center_line = desktop_center_line;
        this.platform_folders = platform_folders;
    }

    @JsonProperty("volume")
    public String jsonVolume(){
        return volume;
    }

    @JsonProperty("desktop_divs_left")
    public int jsonDesktopDivsLeft(){
        return desktop_divs_left;
    }

    @JsonProperty("desktop_divs_right")
    public int jsonDesktopDivsRight(){
        return desktop_divs_right;
    }

    @JsonProperty("desktop_center_line")
    public double jsonDesktopCenterLine(){
        return desktop_center_line;
    }

    @JsonProperty("platform_folders")
    public Map<String, String> getAllFolders(){
        return Collections.unmodifiableMap(platform_folders);
    }

    static SoundPlayer.Volume getVolume(){
        switch (instance.get().volume){
            case "HIGH":
                return SoundPlayer.Volume.HIGH;
            case "LOW":
                return SoundPlayer.Volume.LOW;
            case "MUTE":
                return SoundPlayer.Volume.MUTE;
            default:
                LOG.log(Level.WARN, "Unknown volume value \'{}\' loaded", instance.get().volume);
                return SoundPlayer.Volume.MUTE;
        }
    }

    static void setVolume(SoundPlayer.Volume level){
        switch (level){
            case HIGH:
                instance.get().volume = "HIGH";
                break;
            case LOW:
                instance.get().volume = "LOW";
                break;
            case MUTE:
                instance.get().volume = "MUTE";
                break;
            default:
                LOG.log(Level.WARN, "null volume set in config");
                return;
        }
        updateSave();
    }

    public static int getDesktopDivsLeft(){
        return instance.get().desktop_divs_left;
    }

    public static void changeDesktopDivsLeft(int ddl){
        instance.get().desktop_divs_left = ddl;
        updateSave();
    }

    public static int getDesktopDivsRight(){
        return instance.get().desktop_divs_right;
    }

    public static void changeDesktopDivsRight(int ddr){
        instance.get().desktop_divs_right = ddr;
        updateSave();
    }

    public static double getDesktopCenterLine(){
        return instance.get().desktop_center_line;
    }

    public static void changeDesktopCenterLine(double dcl){
        instance.get().desktop_center_line = dcl;
        updateSave();
    }

    public static File getFolder(String platform){
        if (instance.get().platform_folders.containsKey(platform)){
            File file = new File(instance.get().platform_folders.get(platform));
            if (file.exists() && file.isDirectory()){
                return file;
            }
        }
        return new File(System.getProperty("user.home"));
    }

    public static void changeFolder(String platform, File folder){
        instance.get().platform_folders.put(platform, folder.getAbsolutePath());
        updateSave();
    }

}
