package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.satellite.SatelliteObject;
import com.csm.rover.simulator.wrapper.NamesAndTags;

import java.io.*;
import java.util.ArrayList;

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
	}

	public void Save(File file) throws Exception {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
		out.writeObject(this);
		out.close();
	}
	
}
