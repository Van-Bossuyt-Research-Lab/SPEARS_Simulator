package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.satellite.SatelliteObject;
import com.csm.rover.simulator.sub.SubObject;
import com.csm.rover.simulator.wrapper.NamesAndTags;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
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
		try {

			JsonFactory f = new JsonFactory();
			JsonGenerator g = f.createGenerator(new File("jtest.json"), JsonEncoding.UTF8);
			g.writeStartObject();
			g.writeObjectFieldStart("namesAndTags");
			g.writeFieldName("GroundNames");
			g.writeStartArray();
			for(int j=0; j<namesAndTags.getGroundNames().size(); j++){
				g.writeString(namesAndTags.getNames().get(j));}
			g.writeEndArray();
			g.writeFieldName("RoverNames");
			g.writeStartArray();
			for(int j=0; j<namesAndTags.getRoverNames().size(); j++){
				g.writeString(namesAndTags.getRoverNames().get(j));
			}
			g.writeEndArray();
			g.writeFieldName("SatelliteNames");
			g.writeStartArray();
			for(int j=0; j<namesAndTags.getSatelliteNames().size(); j++){
				g.writeString(namesAndTags.getSatelliteNames().get(j));
			}
			g.writeEndArray();
			g.writeFieldName("GroundTags");
			g.writeStartArray();
			for(int j=0; j<namesAndTags.getGroundTags().size(); j++){
				g.writeString(namesAndTags.getGroundTags().get(j));
			}
			g.writeEndArray();
			g.writeFieldName("RoverTags");
			g.writeStartArray();
			for(int j=0; j<namesAndTags.getRoverTags().size(); j++){
				g.writeString(namesAndTags.getRoverTags().get(j));
			}
			g.writeEndArray();
			g.writeFieldName("SatelliteTags");
			g.writeStartArray();
			for(int j=0; j<namesAndTags.getSatelliteTags().size(); j++){
				g.writeString(namesAndTags.getSatelliteTags().get(j));
			}
			g.writeEndArray();
			g.writeEndObject();
			g.writeStartObject();;
			g.writeObjectFieldStart("Runtime");
			g.writeNumberField("Runtime",runtime);
			g.writeBooleanField("acclelerated", accelerated);
			g.writeEndObject();
			g.writeStartObject();
			g.writeObjectFieldStart("Map");
			g.writeBooleanField("RunFromFile",false);
			g.writeNumberField("mapRough", mapRough);
			g.writeNumberField("mapDetail", mapDetail);
			g.close();

		}
		catch (IOException e){
			System.out.println("IOexception");
		}
		catch(NullPointerException f){
			System.out.println("Null");
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
		out.writeObject(this);
		out.close();

	}
	
}
