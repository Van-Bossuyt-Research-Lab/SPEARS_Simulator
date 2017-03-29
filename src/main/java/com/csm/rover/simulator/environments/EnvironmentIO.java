package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.objects.io.EnvrioFileFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * A static utility class for saving and loading {@link PlatformEnvironment Environments}.
 */
public class EnvironmentIO {
    private static final Logger LOG = LogManager.getLogger(EnvironmentIO.class);

    private static Map<String, PlatformEnvironment> environmentHolder = new TreeMap<>();

    /**
     * Appends the provided file with the correct suffix for its platform type.  If the file is already valid, it is
     * returned without changes.
     *
     * @param platform The platform type of the environment to be saved
     * @param file The current file
     * @return The new file that should be used
     */
    public static File appendSuffix(String platform, File file){
        if ((new EnvrioFileFilter(platform)).accept(file)){
            return file;
        }
        else {
            return new File(file.getParent(), file.getName()+"."+platform.toLowerCase()+".env");
        }
    }

    /**
     * Saves the given environment to the designated file using JSON.
     *
     * @param enviro The environment to save
     * @param file The file to save it to
     *
     * @throws IllegalArgumentException If the provided file has the wrong extension.  See {@link #appendSuffix(String, File)}
     */
    public static void saveEnvironment(PlatformEnvironment enviro, File file){
        if (!(new EnvrioFileFilter(enviro.platform_type)).accept(file)){
            throw new IllegalArgumentException("This file has an invalid suffix: "+file.getName());
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            environmentHolder.put(file.getAbsolutePath(), enviro);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, enviro);
            LOG.log(Level.INFO, "Environment saved at {}", file.getAbsolutePath());
        }
        catch (IOException e) {
            LOG.log(Level.ERROR, "Failed to save Environment", e);
        }
    }

    /**
     * Loads and returns the platform environment saved to the given file.  If the file was saved using
     * {@link #saveEnvironment(PlatformEnvironment, File) saveEnvironment()} during this session, the environment
     * will be loaded from memory instead of the file.
     *
     * @param file The file location
     * @param clazz Class of the Environment to be loaded
     * @param <T> Type of environment to be loaded, must extend {@link PlatformEnvironment}
     *
     * @return Loaded platform environment
     *
     * @throws IOException If the file is not found or the JSON cannot be parsed
     */
    public static <T extends PlatformEnvironment> T loadEnvironment(File file, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            try {
                if (environmentHolder.containsKey(file.getAbsolutePath())) {
                    LOG.log(Level.INFO, "Pulling environment at {} from RAM archive", file.getAbsolutePath());
                    return (T) environmentHolder.get(file.getAbsolutePath());
                }
            }
            catch (ClassCastException ex){
                LOG.log(Level.WARN, "Pull from archive failed");
            }
            LOG.log(Level.INFO, "Reading environment from {}", file.getAbsolutePath());
            return mapper.readValue(file, clazz);
        }
        catch (IOException e) {
            LOG.log(Level.ERROR, "Failed to read in JSON file", e);
            throw new IOException(e);
        }
    }

}
