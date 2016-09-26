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
import java.util.Optional;

class UiConfiguration {
    private static final Logger LOG = LogManager.getLogger(UiConfiguration.class);

    private static final File configFile;
    private static Optional<UiConfiguration> instance = Optional.empty();

    static {
        configFile = new File("ui.config.json");
        load();
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
                "HIGH"
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

    private String volume;

    @JsonCreator
    public UiConfiguration(@JsonProperty("volume") String volume){
        if (instance.isPresent()){
            throw new IllegalStateException("UiConfiguration has already been initialized, cannot make 2 copies");
        }
        this.volume = volume;
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

    @JsonProperty("volume")
    public String jsonVolume(){
        return volume;
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

}
