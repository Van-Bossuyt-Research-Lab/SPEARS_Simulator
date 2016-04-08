package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.control.InterfaceCode;
import com.csm.rover.simulator.wrapper.NamesAndTags;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class RunConfiguration {
	@JsonIgnore
	private static final Logger LOG = LogManager.getLogger(InterfaceCode.class);

	@JsonIgnore
	private static final ObjectMapper mapper = new ObjectMapper();

	public NamesAndTags namesAndTags;
	public ArrayList<PlatformConfig> platforms;
	public File mapFile;
	public boolean accelerated;
	public int runtime;
	
	public RunConfiguration(NamesAndTags namesAndTags,
							ArrayList<PlatformConfig> platforms,
							File mapFile,
			boolean accelerated, int runtime) {
		this.namesAndTags = namesAndTags;
		this.platforms = platforms;
		this.mapFile = mapFile;
		this.accelerated = accelerated;
		this.runtime = runtime;
	}

	private RunConfiguration(RunConfiguration orig){
		this.namesAndTags = orig.namesAndTags.clone();
		this.platforms = new ArrayList<>(orig.platforms);
		this.mapFile = new File(orig.mapFile.getAbsolutePath());
		this.accelerated = orig.accelerated;
		this.runtime = orig.runtime;
	}
	
	public static RunConfiguration fromFile(File save) throws Exception {
		mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(save, RunConfiguration.class);
		}
		catch (Exception e){
			LOG.log(Level.ERROR, "Configuration failed to read from file", e);
			throw e;
		}
	}

	public void Save(File file) throws Exception {
		mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
	}
	
}
