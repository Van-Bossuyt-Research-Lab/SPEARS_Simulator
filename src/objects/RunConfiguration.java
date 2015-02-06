package objects;

import java.io.File;

import rover.RoverObject;
import satellite.SatelliteObject;

public class RunConfiguration {
	
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
	
	public RunConfiguration(Map<String, String> roverNames,
			RoverObject[] rovers, Map<String, String> satelliteNames,
			SatelliteObject[] satellites, String[] tags, File mapFile) {
		mapFromFile = true;
		this.roverNames = roverNames;
		this.rovers = rovers;
		this.satelliteNames = satelliteNames;
		this.satellites = satellites;
		this.tags = tags;
		this.mapFile = mapFile;
	}

	public RunConfiguration(Map<String, String> roverNames,
			RoverObject[] rovers, Map<String, String> satelliteNames,
			SatelliteObject[] satellites, String[] tags, double mapRough,
			int mapSize, int mapDetail, double targetDensity,
			double hazardDensity) {
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
	}

}
