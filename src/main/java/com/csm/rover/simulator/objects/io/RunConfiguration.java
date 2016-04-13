package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.control.InterfaceCode;
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
import java.util.*;

public class RunConfiguration implements Cloneable {
	@JsonIgnore
	private static final Logger LOG = LogManager.getLogger(InterfaceCode.class);

	@JsonIgnore
	private static final ObjectMapper mapper = new ObjectMapper();

	@JsonIgnore
	public NamesAndTags namesAndTags;
	public ArrayList<PlatformConfig> platforms;
	public File mapFile;
	public boolean accelerated;
	public int runtime;

	public RunConfiguration(NamesAndTags namesAndTags,
							ArrayList<PlatformConfig> platforms,
							File mapFile,
							boolean accelerated,
							int runtime) {
		this.namesAndTags = namesAndTags;
		this.platforms = platforms;
		this.mapFile = mapFile;
		this.accelerated = accelerated;
		this.runtime = runtime;
	}

	@JsonCreator
	public RunConfiguration(@JsonProperty("platforms") ArrayList<PlatformConfig> platforms,
							@JsonProperty("mapFile") File mapFile,
							@JsonProperty("accelerated") boolean accelerated,
							@JsonProperty("runtime") int runtime) {
		this.platforms = platforms;
		this.mapFile = mapFile;
		this.accelerated = accelerated;
		this.runtime = runtime;
		this.namesAndTags = NamesAndTags.newFromPlatforms(platforms);
	}

	private RunConfiguration(RunConfiguration orig){
		this.namesAndTags = orig.namesAndTags.clone();
		this.platforms = new ArrayList<>(orig.platforms);
		this.mapFile = new File(orig.mapFile.getAbsolutePath());
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

	public void Save(File file) throws Exception {
		LOG.log(Level.INFO, "Saving run configuration to: {}", file.getAbsolutePath());
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
		}
		catch (Exception e){
			LOG.log(Level.WARN, "Configuration failed to save to file", e);
			throw e;
		}
	}

	public ArrayList<PlatformConfig> getPlatforms(String type){
		ArrayList<PlatformConfig> out = new ArrayList<>();
		for (PlatformConfig cnfg : this.platforms){
			if (cnfg.getType().equals(type)){
				out.add(cnfg);
			}
		}
		if (out.size() == 0){
			throw new IndexOutOfBoundsException("No platforms of type \""+type+"\" registered");
		}
		return out;
	}

	@Override
	public RunConfiguration clone(){
		return new RunConfiguration(this);
	}
	
}
