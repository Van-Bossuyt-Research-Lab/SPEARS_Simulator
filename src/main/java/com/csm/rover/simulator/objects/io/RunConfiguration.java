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
		/*
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(save.getAbsolutePath()));
		RunConfiguration input = (RunConfiguration) in.readObject();
		if (!this.fileCode.equals(input.fileCode)){
			in.close();
			throw new Exception("Invalid File Version");
		}

		JsonFactory f = new JsonFactory();
		JsonParser jp = f.createParser(save);
		ObjectMapper m = new ObjectMapper();
		jp.nextToken();
		while(jp.nextToken() != JsonToken.END_OBJECT){
			String namefield = jp.getCurrentName();
			System.out.println("start");
			jp.nextToken();
			switch(namefield) {
				case("Runtime"):
					while (jp.nextToken() != JsonToken.END_OBJECT) {
						String fieldname = jp.getCurrentName();
						jp.nextToken();
						switch (fieldname){
							case("runtime"):
								this.runtime = jp.getNumberValue().intValue();
								break;
							case ("accelerated"):
								accelerated = jp.getBooleanValue();
								break;
						}
					}
					break;
				case ("Map"):
					while (jp.nextToken() != JsonToken.END_OBJECT) {
						String fieldname = jp.getCurrentName();
						jp.nextToken();
						switch (fieldname) {
							case ("mapFromFile"):
								mapFromFile = jp.getBooleanValue();
								break;
							case ("mapFile"):
								mapFile = new File(jp.getValueAsString());
								break;
							case ("mapRough"):
								mapRough = jp.getNumberValue().doubleValue();
								break;
							case ("mapDetail"):
								mapDetail = jp.getNumberValue().intValue();
								break;
							case ("mapSize"):
								mapSize = jp.getNumberValue().intValue();
								break;
							case ("monoTargets"):
								monoTargets = jp.getBooleanValue();
								break;
							case ("monoHazards"):
								monoHazards = jp.getBooleanValue();
								break;
							case ("targetdensity"):
								targetDensity = jp.getNumberValue().doubleValue();
								break;
							case ("hazardDensity"):
								hazardDensity = jp.getNumberValue().doubleValue();
								break;
						}
					}
					System.out.println("builtMap");
					break;
				case ("PlatformConfig"):
					Map<String, Double> physicsMap = Collections.emptyMap();
					Map<String, Double> autoMap = Collections.emptyMap();
					String physics = "";
					String autos = "";
					PlatformConfig.Builder pc = PlatformConfig.builder();
					while (jp.nextToken() != JsonToken.END_OBJECT) {
						String fieldname = jp.getCurrentName();
						jp.nextToken();
						switch (fieldname) {
							case ("ID"):
								pc.setID(jp.getText());
								break;
							case ("screenName"):
								pc.setScreenName(jp.getText());
								break;
							case ("type"):
								pc.setType(jp.getText());
								break;
							case ("physicsConfig"):
								physics = jp.getText();
								break;
							case ("autonomousConfig"):
								autos = jp.getText();
								break;
							case ("physicsConfigParams"):
								String a = "";
								String b = "";
								if(jp.nextToken() == JsonToken.START_ARRAY) {
									jp.nextToken();
									while (jp.nextToken() != JsonToken.END_ARRAY) {
										jp.nextToken();
										a = jp.getText();
										jp.nextToken();
										b = jp.getText();
										System.out.println(a);
										System.out.println(b);
										physicsMap.put(a, Double.parseDouble(b.trim()));
									}

								}
								break;
							case ("autonomousConfigParams"):
								jp.nextToken();
								a = "";
								b = "";
								if(jp.nextToken() == JsonToken.START_ARRAY) {

									while (jp.nextToken() != JsonToken.END_ARRAY) {

										a = jp.getText();
										jp.nextToken();
										b = jp.getText();
										System.out.println(a);
										System.out.println(b);
										autoMap.put(a, Double.parseDouble(b.trim()));
									}
									jp.nextToken();
								}
								break;
							case ("stateParams"):
								jp.nextToken();
								a = "";
								b = "";
								if(jp.nextToken() == JsonToken.START_ARRAY) {
									jp.nextToken();
									while (jp.nextToken() != JsonToken.END_ARRAY) {
										jp.nextToken();
										a = jp.getText();
										jp.nextToken();
										b = jp.getText();
										System.out.println(a);
										System.out.println(b);
										pc.addStateVariable(a, Double.parseDouble(b.trim()));
									}
								}
								break;
						}
					}
					pc.setPhysicsModel(physics, physicsMap);
					pc.setAutonomousModel(autos, autoMap);
					platforms.add(pc.build());
					break;
			}
			}
		jp.close();

		this.mapFromFile = input.mapFromFile;
		this.namesAndTags = input.namesAndTags;
		this.platforms = input.platforms;
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
		*/
		// in.close();
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
		mapDetail = 3;
		mapFromFile = false;
		mapRough = 1;
		mapSize = 50;
		targetDensity = 0.5;
		hazardDensity = 0.5;
		monoTargets = false;
		monoHazards = false;
		accelerated = false;
		runtime = 50;



	}

	public void Save(File file) throws Exception {
		/*
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
		out.writeObject(this);
		out.close();
		JsonFactory f = new JsonFactory();
		JsonGenerator g = f.createGenerator(new File("jtest.json"), JsonEncoding.UTF8);
		g.useDefaultPrettyPrinter();
		g.writeStartObject();
		g.writeObjectFieldStart("Runtime");
		g.writeNumberField("runtime", runtime);
		g.writeBooleanField("acclelerated", accelerated);
        g.writeEndObject();
		g.writeObjectFieldStart("Map");
		g.writeBooleanField("runFromFile", mapFromFile);
		g.writeFieldName("mapFile");
		g.writeString("test");
		g.writeNumberField("mapRough", mapRough);
		g.writeNumberField("mapDetail", mapDetail);
		g.writeNumberField("mapSize", mapSize);
		g.writeBooleanField("monoTargets", monoTargets);
		g.writeBooleanField("monoHazards", monoHazards);
		g.writeNumberField("targetDensity", targetDensity);
		g.writeNumberField("hazardDensity", hazardDensity);
        g.writeEndObject();
		for(int j=0; j<platforms.size(); j++) {
			g.writeObjectFieldStart("PlatformConfig");
			g.writeStringField("ID", platforms.get(j).getID());
			g.writeStringField("screenName", platforms.get(j).getScreenName());
			g.writeStringField("type", platforms.get(j).getType());
			g.writeStringField("physicsConfig", platforms.get(j).getPhysicsModelName());
			g.writeFieldName("physicsConfigParams");
			g.writeStartArray();
			for (String s : platforms.get(j).getPhysicsModelParameters().keySet()) {
				g.writeString(s);
				g.writeString(platforms.get(j).getPhysicsModelParameters().get(s).toString());
			}
			g.writeEndArray();
			g.writeStringField("autonomousConfig", platforms.get(j).getAutonomousModelName());
			g.writeFieldName("autonomousConfigParams");
			g.writeStartArray();
			for (String s : platforms.get(j).getAutonomousModelParameters().keySet()) {
				g.writeString(s);
				g.writeString(platforms.get(j).getAutonomousModelParameters().get(s).toString());
			}
			g.writeEndArray();
			g.writeFieldName("stateParams");
			g.writeStartArray();
			for (String s : platforms.get(j).getStateParameters().keySet()) {
				g.writeStartArray();
				g.writeString(s);
				g.writeString(platforms.get(j).getStateParameters().get(s).toString());
				g.writeEndArray();
			}
			g.writeEndArray();
			g.writeEndObject();
		}
		g.writeEndObject();
		g.close();
		*/

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
