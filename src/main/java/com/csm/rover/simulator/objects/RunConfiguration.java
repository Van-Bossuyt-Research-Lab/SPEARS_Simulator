package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.satellite.SatelliteObject;
import com.csm.rover.simulator.sub.SubObject;
import com.csm.rover.simulator.wrapper.NamesAndTags;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class RunConfiguration implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String fileCode = "ent";
	
	public boolean mapFromFile;
	public NamesAndTags namesAndTags;
	public ArrayList<RoverObject> rovers;
	public ArrayList<SatelliteObject> satellites;
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
	public ObjectMapper mapper;
	
	public RunConfiguration(NamesAndTags namesAndTags,
							ArrayList<RoverObject> rovers,
							ArrayList<SatelliteObject> satellites,
							File mapFile,
			boolean accelerated, int runtime) {
		mapFromFile = true;
		this.namesAndTags = namesAndTags;
		this.rovers = rovers;
		this.satellites = satellites;
		this.mapFile = mapFile;
		this.accelerated = accelerated;
		this.runtime = runtime;
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
		mapper.setDateFormat(outputFormat);

		mapper.setSerializationInclusion(Include.NON_EMPTY);
		try {
			mapper.writeValue(System.out, this.rovers);
		}
		catch (IOException e) {
			System.out.println("IOexception");
		}

	}

	public RunConfiguration(NamesAndTags namesAndTags,
                            ArrayList<RoverObject> rovers,
							ArrayList<SatelliteObject> satellites,
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
		this.rovers = rovers;
		this.satellites = satellites;
		this.mapRough = mapRough;
		this.mapSize = mapSize;
		this.mapDetail = mapDetail;
		this.targetDensity = targetDensity;
		this.hazardDensity = hazardDensity;
		this.monoTargets = monoTargets;
		this.monoHazards = monoHazards;
		this.accelerated = accelerated;
		this.runtime = runtime;

		try {
			RoverObject rvr=this.rovers.get(1);

			System.out.println(mapper.writeValueAsString(rvr));
		}
		catch (IOException e){
			System.out.println("IOexception");
		}
		catch(NullPointerException f){
			System.out.println("Null");
		}
	}
	
	public RunConfiguration(File save) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(save.getAbsolutePath()));
		RunConfiguration input = (RunConfiguration) in.readObject();
		if (!this.fileCode.equals(input.fileCode)){
			in.close();
			throw new Exception("Invalid File Version");
		}
		this.mapFromFile = input.mapFromFile;
		this.namesAndTags = input.namesAndTags;
		this.rovers = input.rovers;
		this.satellites = input.satellites;
		this.mapFile = input.mapFile;
		this.mapRough = input.mapRough;
		this.mapSize = input.mapSize;
		this.mapDetail = input.mapDetail;
		this.monoTargets = input.monoTargets;
		this.monoHazards = input.monoHazards;
		this.targetDensity = input.targetDensity;
		this.hazardDensity = input.hazardDensity;
		this.accelerated = input.accelerated;
		this.runtime = input.runtime;
		in.close();

		try {
			mapper.writeValue(System.out, this.rovers);
		}
		catch (IOException e) {
			System.out.println("IOexception");
		}
	}

	public void Save(File file) throws Exception {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
		mapper.setDateFormat(outputFormat);

		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.writeValue(System.out, this);
		mapper.writeValue(new FileOutputStream(file.getAbsolutePath()), this);
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
		out.writeObject(this);
		out.close();

	}
	
}
