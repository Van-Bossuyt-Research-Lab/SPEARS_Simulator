/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csm.rover.simulator.objects.io;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A data structure/IO object which defines how simulations should run.
 */
public class RunConfiguration implements Cloneable {
	@JsonIgnore
	private static final Logger LOG = LogManager.getLogger(RunConfiguration.class);

	@JsonIgnore
	private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Unmodifiable list of the platform-environments to run.
     */
    @JsonProperty("platforms")
	public final List<TypeConfig> types;

    /**
     * Whether the simulation should be accelerated.
     */
	public final boolean accelerated;

    /**
     * Time the simulation should run.  Only used when accelerated.
     */
	public final int runtime;

    /**
     * Constructor for JSON.  Recommended using the {@link #newConfig() Builder}.
     *
     * @param types List of Platform listings
     * @param accelerated Whether to accelerate the run
     * @param runtime Length should run
     */
	@JsonCreator
	public RunConfiguration(@JsonProperty("platforms") List<TypeConfig> types,
							@JsonProperty("accelerated") boolean accelerated,
							@JsonProperty("runtime") int runtime) {
		this.types = Collections.unmodifiableList(types);
		this.accelerated = accelerated;
		this.runtime = runtime;
	}

	private RunConfiguration(RunConfiguration orig){
		this.types = Collections.unmodifiableList(orig.types);
		this.accelerated = orig.accelerated;
		this.runtime = orig.runtime;
	}

    /**
     * Load a saved configuration from file.
     *
     * @param save File where the configuration is saved
     * @return New RunConfiguration
     *
     * @throws Exception On IOException or JsonExceptions
     */
	public static RunConfiguration fromFile(File save) throws Exception {
		LOG.log(Level.INFO, "Loading saved configuration file from: {}", save.getAbsolutePath());
		mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(save, RunConfiguration.class);
		}
		catch (JsonParseException e){
			LOG.log(Level.ERROR, "File was not parsable to RunConfiguration", e);
			throw new Exception("File was not parsable to RunConfiguration", e);
		}
		catch (JsonMappingException e){
			LOG.log(Level.ERROR, "RunConfiguration could not be mapped from file", e);
			throw new Exception("RunConfiguration could not be mapped from file", e);
		}
		catch (IOException e){
			LOG.log(Level.ERROR, "File could not be found/accessed", e);
			throw new Exception("File could not be found/accessed", e);
		}
	}

    /**
     * Saves the current RunConfiguration to the given file.
     *
     * @param file File destination
     * @throws Exception If save fails
     */
	public void save(File file) throws Exception {
		LOG.log(Level.INFO, "Saving run configuration to: {}", file.getAbsolutePath());
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
		}
		catch (Exception e){
			LOG.log(Level.WARN, "Configuration failed to save to file", e);
			throw new Exception("Configuration failed to save", e);
		}
	}

    /**
     * Returns the type names of all the types used in this configuration.
     *
     * @return List of type names
     */
    @JsonIgnore
    public List<String> getTypes(){
        List<String> out = new ArrayList<>();
        for (TypeConfig config : types){
            out.add(config.type);
        }
        return out;
    }

    /**
     * Returns a file for the Environment configured for the request platform type.
     *
     * @param type The desired type name
     * @return File to the {@link com.csm.rover.simulator.environments.PlatformEnvironment PlatformEnvironment}
     *
     * @throws IllegalArgumentException If the requested type is not used in this config
     */
    public File getEnvironmentFile(String type){
        for (TypeConfig config : types){
            if (config.type.equals(type)){
                return config.mapFile;
            }
        }
        throw new IllegalArgumentException("No platforms found of type \'" + type + "\'");
    }

    /**
     * Returns a list of the configurations for the platforms configured for the given type.
     *
     * @param type The desired type name
     * @return List of {@link PlatformConfig} for the config
     *
     * @throws IllegalArgumentException If the requested type is not defined in this config
     */
	public List<PlatformConfig> getPlatforms(String type){
        for (TypeConfig config : types){
            if (config.type.equals(type)){
                return config.platformConfigs;
            }
        }
        throw new IllegalArgumentException("No platforms found of type \'" + type + "\'");
	}

    @Override
    public boolean equals(Object other){
        if (other == this){
            return true;
        }
        if (other instanceof RunConfiguration){
            RunConfiguration o = (RunConfiguration)other;
            return o.accelerated == this.accelerated &&
                    (!this.accelerated || (o.runtime == this.runtime)) &&
                    o.types.equals(this.types);
        }
        return false;
    }

	@Override
	public RunConfiguration clone(){
		return new RunConfiguration(this);
	}

    /**
     * Builder pattern access.  Recommended way to create new RunConfigurations.
     *
     * @return A new Builder object
     */
    public static Builder newConfig(){
        return new Builder();
    }

    /**
     * Builder class
     */
    public static class Builder {

        private List<TypeConfig> types;
        private Optional<Boolean> accelerated;
        private int runtime;

        private Builder(){
            types = new ArrayList<>();
            accelerated = Optional.empty();
            runtime = 0;
        }

        public Builder setAccelerated(boolean accel){
            accelerated = Optional.of(accel);
            return this;
        }

        public Builder setRuntime(int min){
            runtime = min;
            return setAccelerated(true);
        }

        public Builder addType(TypeConfig config){
            this.types.add(config);
            return this;
        }

        /**
         * Fails if any field is not defined.
         *
         * @return Fully build RunConfiguration
         */
        public RunConfiguration build(){
            if (types.size() > 0 && accelerated.isPresent() && (!accelerated.get() || runtime > 0)) {
                return new RunConfiguration(types, accelerated.get(), runtime);
            }
            else {
                throw new IllegalStateException("The builder is not fully initialized");
            }
        }

    }
	
}
