package com.csm.rover.simulator.environments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class EnvironmentIO {
    private static final Logger LOG = LogManager.getLogger(EnvironmentIO.class);

    public static void saveEnvironment(PlatformEnvironment enviro, File file){
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, enviro);
        }
        catch (IOException e) {
            LOG.log(Level.ERROR, "Failed to save Environment", e);
        }
    }

    public static <T extends PlatformEnvironment> T loadEnvironment(File file, Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(file, clazz);
        }
        catch (IOException e) {
            LOG.log(Level.ERROR, "Failed to read in JSON file", e);
            System.exit(-1);
            return null;
        }
    }

}
