package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.wrapper.NamesAndTags;
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

public class RunConfiguration implements Cloneable {
	@JsonIgnore
	private static final Logger LOG = LogManager.getLogger(RunConfiguration.class);

	@JsonIgnore
	private static final ObjectMapper mapper = new ObjectMapper();

	@JsonIgnore
	public NamesAndTags namesAndTags;
    @JsonProperty("platforms")
	public List<TypeConfig> types;
	public boolean accelerated;
	public int runtime;

	@JsonCreator
	public RunConfiguration(@JsonProperty("platforms") List<TypeConfig> types,
							@JsonProperty("accelerated") boolean accelerated,
							@JsonProperty("runtime") int runtime) {
		this.types = Collections.unmodifiableList(types);
		this.accelerated = accelerated;
		this.runtime = runtime;
		this.namesAndTags = NamesAndTags.newFromPlatforms(types);
	}


	private RunConfiguration(RunConfiguration orig){
		this.namesAndTags = orig.namesAndTags.clone();
		this.types = Collections.unmodifiableList(orig.types);
		this.accelerated = orig.accelerated;
		this.runtime = orig.runtime;
	}
	
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

	public void save(File file) throws Exception {
		LOG.log(Level.INFO, "Saving run configuration to: {}", file.getAbsolutePath());
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
		}
		catch (Exception e){
			LOG.log(Level.WARN, "Configuration failed to save to file", e);
			throw e;
		}
	}

    @JsonIgnore
    public List<String> getTypes(){
        List<String> out = new ArrayList<>();
        for (TypeConfig config : types){
            out.add(config.type);
        }
        return out;
    }

    public File getEnvironmentFile(String type){
        for (TypeConfig config : types){
            if (config.type.equals(type)){
                return config.mapFile;
            }
        }
        throw new IllegalArgumentException("No platforms found of type \'" + type + "\'");
    }

	public List<PlatformConfig> getPlatforms(String type){
        for (TypeConfig config : types){
            if (config.type.equals(type)){
                return config.platformConfigs;
            }
        }
        throw new IllegalArgumentException("No platforms found of type \'" + type + "\'");
	}

	@Override
	public RunConfiguration clone(){
		return new RunConfiguration(this);
	}

    public static Builder newConfig(){
        return new Builder();
    }

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

        public TypeBuilder newType(String type){
            return new TypeBuilder(this, type);
        }

        public RunConfiguration build(){
            if (types.size() > 0 && accelerated.isPresent() && (!accelerated.get() || runtime > 0)) {
                return new RunConfiguration(types, accelerated.get(), runtime);
            }
            else {
                throw new IllegalStateException("The builder is not fully initialized");
            }
        }

    }

    public static class TypeBuilder {

        private Builder build;

        private String type;
        private File file;
        private List<PlatformConfig> platforms;

        private TypeBuilder(Builder build, String type){
            this.build = build;
            this.type = type;
            this.platforms = new ArrayList<>();
        }

        public TypeBuilder setMapFile(File map){
            this.file = map;
            return this;
        }

        public TypeBuilder addPlatform(PlatformConfig config){
            platforms.add(config);
            return this;
        }

        private Builder build(){
            if (this.file != null && platforms.size() > 0) {
                build.types.add(new TypeConfig(type, file, platforms));
                return build;
            }
            else {
                throw new IllegalStateException("Builder not fully initialized");
            }
        }

    }
	
}
