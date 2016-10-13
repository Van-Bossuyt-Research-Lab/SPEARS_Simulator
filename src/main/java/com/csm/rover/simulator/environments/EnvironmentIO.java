package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.objects.io.EnvrioFileFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class EnvironmentIO {
    private static final Logger LOG = LogManager.getLogger(EnvironmentIO.class);

    public static File appendSuffix(String platform, File file){
        if ((new EnvrioFileFilter(platform)).accept(file)){
            return file;
        }
        else {
            return new File(file.getParent(), file.getName()+"."+platform+".env");
        }
    }

    public static void saveEnvironment(PlatformEnvironment enviro, File file){
        if (!(new EnvrioFileFilter(enviro.platform_type)).accept(file)){
            throw new IllegalArgumentException("This file has an invalid suffix: "+file.getName());
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, enviro);
            LOG.log(Level.INFO, "Environment saved at {}", file.getAbsolutePath());
        }
        catch (IOException e) {
            LOG.log(Level.ERROR, "Failed to save Environment", e);
        }
    }

    public static <T extends PlatformEnvironment> T loadEnvironment(File file, Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper();
        try {
            LOG.log(Level.INFO, "Reading environment from {}" + file.getAbsolutePath());
            return mapper.readValue(file, clazz);
        }
        catch (IOException e) {
            LOG.log(Level.ERROR, "Failed to read in JSON file", e);
            System.exit(-1);
            return null;
        }
    }

}
