package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.wrapper.NamesAndTags;

import java.io.*;
import java.util.ArrayList;

public class RunConfiguration implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String fileCode = "ent";
	
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
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(save.getAbsolutePath()));
		RunConfiguration input = (RunConfiguration) in.readObject();
		if (!this.fileCode.equals(input.fileCode)){
			in.close();
			throw new Exception("Invalid File Version");
		}
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
		in.close();
	}

	public void Save(File file) throws Exception {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
		out.writeObject(this);
		out.close();
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
