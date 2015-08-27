package objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import rover.RoverObject;
import satellite.SatelliteObject;

public class RunConfiguration implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String fileCode = "yak";
	
	public boolean mapFromFile;
	public Map<String, String> roverNames;
	public RoverObject[] rovers;
	public Map<String, String> satelliteNames;
	public SatelliteObject[] satellites;
	public String[] tags;
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
	
	public RunConfiguration(Map<String, String> roverNames,
			RoverObject[] rovers, Map<String, String> satelliteNames,
			SatelliteObject[] satellites, String[] tags, File mapFile,
			boolean accelerated, int runtime) {
		mapFromFile = true;
		this.roverNames = roverNames;
		this.rovers = rovers;
		this.satelliteNames = satelliteNames;
		this.satellites = satellites;
		this.tags = tags;
		this.mapFile = mapFile;
		this.accelerated = accelerated;
		this.runtime = runtime;
	}

	public RunConfiguration(Map<String, String> roverNames,
			RoverObject[] rovers, Map<String, String> satelliteNames,
			SatelliteObject[] satellites, String[] tags, double mapRough,
			int mapSize, int mapDetail, double targetDensity,
			double hazardDensity, boolean monoTargets, boolean monoHazards, boolean accelerated, int runtime) {
		mapFromFile = false;
		this.roverNames = roverNames;
		this.rovers = rovers;
		this.satelliteNames = satelliteNames;
		this.satellites = satellites;
		this.tags = tags;
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
			throw new Exception("Invalid File Version");
		}
		this.mapFromFile = input.mapFromFile;
		this.roverNames = input.roverNames;
		this.rovers = input.rovers;
		this.satelliteNames = input.satelliteNames;
		this.satellites = input.satellites;
		this.tags = input.tags;
		this.mapFile = input.mapFile;
		this.mapRough = input.mapRough;
		this.mapSize = input.mapSize;
		this.mapDetail = input.mapDetail;
		this.targetDensity = input.targetDensity;
		this.hazardDensity = input.hazardDensity;
		this.accelerated = input.accelerated;
		this.runtime = input.runtime;
	}

	public void Save(File file) throws Exception {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
		out.writeObject(this);
		out.close();
	}
	
}
