package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.platforms.satellite.SatelliteObject;
import com.csm.rover.simulator.wrapper.NamesAndTags;
import com.fasterxml.jackson.core.*;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class RunConfiguration implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String fileCode = "ent";
	
	public boolean mapFromFile;
	public NamesAndTags namesAndTags;
	public ArrayList<PlatformConfig> platforms;
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
		*/
		JsonFactory f = new JsonFactory();
		JsonParser jp = f.createParser(save);
		NamesAndTags NT = new NamesAndTags();
		jp.nextToken();
				while(jp.nextToken() != JsonToken.END_OBJECT){
					String namefield = jp.getCurrentName();
					jp.nextToken();

					if("Map".equals(namefield)){
						mapFromFile = jp.readValueAs(boolean.class);
						File fle = new File(jp.getValueAsString());
						mapFile = fle;
						mapRough = jp.readValueAs(double.class);
						mapDetail = jp.readValueAs(int.class);
						mapSize = jp.readValueAs(int.class);


					}
					if("TargetHazard".equals(namefield)){
						monoTargets = jp.readValueAs(boolean.class);
						monoHazards = jp.readValueAs(boolean.class);
						targetDensity = jp.readValueAs(double.class);
						hazardDensity = jp.readValueAs(double.class);
					}
					if("PlatformConfig".equals(namefield)){
						Iterator<String> ID = new Iterator<String>() {
							@Override
							public boolean hasNext() {
								return false;
							}

							@Override
							public String next() {
								return null;
							}
						};
						Iterator<String> names = new Iterator<String>() {
							@Override
							public boolean hasNext() {
								return false;
							}

							@Override
							public String next() {
								return null;
							}
						};
						Iterator<String> types = new Iterator<String>() {
							@Override
							public boolean hasNext() {
								return false;
							}

							@Override
							public String next() {
								return null;
							}
						};
						Iterator<String> physics = new Iterator<String>() {
							@Override
							public boolean hasNext() {
								return false;
							}

							@Override
							public String next() {
								return null;
							}
						};
						Iterator<String> autos = new Iterator<String>() {
							@Override
							public boolean hasNext() {
								return false;
							}

							@Override
							public String next() {
								return null;
							}
						};
						ID = jp.readValuesAs(String.class);
						names = jp.readValuesAs(String.class);
						types = jp.readValuesAs(String.class);
						physics = jp.readValuesAs(String.class);
						autos = jp.readValuesAs(String.class);
						while (names.hasNext()){
							PlatformConfig pc = PlatformConfig.builder().setID(ID.next())
									.setScreenName(names.next())
									.setType(types.next())
									.setPhysicsModel(physics.next())
									.setAutonomousModel(autos.next())
									.build();
							platforms.add(pc);
							ID.remove();
							names.remove();
							types.remove();
							physics.remove();
							autos.remove();
						}
					}
				}
		jp.close();
		/*
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
	}

	public void Save(File file) throws Exception {
		/*
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
		out.writeObject(this);
		out.close();
		*/
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
        g.writeEndObject();
		g.writeObjectFieldStart("TargetHazard");
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
				g.writeNumber(platforms.get(j).getPhysicsModelParameters().get(s));
			}
			g.writeEndArray();
			g.writeStringField("autonomousConfig", platforms.get(j).getAutonomousModelName());
			g.writeFieldName("autonomousConfigParams");
			g.writeStartArray();
			for (String s : platforms.get(j).getAutonomousModelParameters().keySet()) {
				g.writeString(s);
				g.writeNumber(platforms.get(j).getAutonomousModelParameters().get(s));
			}
			g.writeEndArray();
			g.writeFieldName("stateParams");
			g.writeStartArray();
			for (String s : platforms.get(j).getStateParameters().keySet()) {
				g.writeStartArray();
				g.writeString(s);
				g.writeNumber(platforms.get(j).getStateParameters().get(s));
				g.writeEndArray();
			}
			g.writeEndArray();
			g.writeEndObject();
		}
		g.writeEndObject();
		g.close();
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
