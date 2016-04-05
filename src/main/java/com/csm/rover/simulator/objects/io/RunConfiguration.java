package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.control.InterfaceCode;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.platforms.satellite.SatelliteObject;
import com.csm.rover.simulator.wrapper.NamesAndTags;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class RunConfiguration implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String fileCode = "ent";

	private static final Logger LOG = LogManager.getLogger(InterfaceCode.class);
	public boolean mapFromFile;
	public NamesAndTags namesAndTags;
	public ArrayList<PlatformConfig> platforms;
	public File mapFile;
	public double mapRough;
	public int mapSize;
	public int mapDetail;
	public double targetDensity;
	public double hazardDensity;
	public boolean monoTargets;
	public boolean monoHazards;
	public boolean accelerated;
	public int runtime;
	
	public RunConfiguration(NamesAndTags namesAndTags,
							ArrayList<PlatformConfig> platforms,
							File mapFile,
			boolean accelerated, int runtime) {
		mapFromFile = true;
		this.namesAndTags = namesAndTags;
		this.platforms = platforms;
		this.mapFile = mapFile;
		this.accelerated = accelerated;
		this.runtime = runtime;
	}

	public RunConfiguration(NamesAndTags namesAndTags,
                            ArrayList<PlatformConfig> platforms,
                            double mapRough,
							int mapSize,
							int mapDetail,
							double targetDensity,
							double hazardDensity,
							boolean monoTargets,
							boolean monoHazards,
							boolean accelerated,
							int runtime) {
		mapFromFile = false;
		this.namesAndTags = namesAndTags;
		this.platforms = platforms;
		this.mapRough = mapRough;
		this.mapSize = mapSize;
		this.mapDetail = mapDetail;
		this.targetDensity = targetDensity;
		this.hazardDensity = hazardDensity;
		this.monoTargets = monoTargets;
		this.monoHazards = monoHazards;
		this.accelerated = accelerated;
		this.runtime = runtime;
	}
	
	public RunConfiguration(File save) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		platforms= mapper.readValue(save, new TypeReference<List<PlatformConfig>>() {
		});
		ArrayList<String> rovername = new ArrayList<String>();
		ArrayList<String> rovertag = new ArrayList<String>();
		ArrayList<String> satname = new ArrayList<String>();
		ArrayList<String> sattag = new ArrayList<String>();
		for(int i =0; i<platforms.size();i++){
			if(platforms.get(i).getType() == "Rover"){
				rovername.add(platforms.get(i).getScreenName());
				rovertag.add(platforms.get(i).getID());
			}
			if(platforms.get(i).getType() == "Satellite"){
				satname.add(platforms.get(i).getScreenName());
				sattag.add(platforms.get(i).getID());
			}
		}
		namesAndTags = new NamesAndTags(rovername,rovertag,satname,sattag);
		mapFromFile = true;
		mapFile = new File("C:\\Users\\PHM-Lab2\\Documents\\test map.map");



	}

	public void Save(File file) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writerWithDefaultPrettyPrinter().writeValue(file, platforms);


	}

	public ArrayList<PlatformConfig> getPlatforms(String type){
		ArrayList<PlatformConfig> out = new ArrayList<PlatformConfig>();
		for (PlatformConfig cnfg : this.platforms){
			if (cnfg.getType().equals(type)){
				out.add(cnfg);
			}
		}
		return out;
	}
	
}
