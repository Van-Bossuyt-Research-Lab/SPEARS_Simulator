package wrapper;

import java.io.File;
import java.util.Random;

import control.InterfaceCode;
import objects.DecimalPoint;
import objects.List;
import objects.Map;
import objects.Queue;
import objects.RunConfiguration;
import rover.RoverAutonomusCode;
import rover.RoverObject;
import rover.RoverParametersList;
import rover.autoCode.*;
import satellite.SatelliteAutonomusCode;
import satellite.SatelliteObject;
import satellite.SatelliteParametersList;
import visual.Form;

public class Admin {

	public static Form GUI = new Form();
	
	Random rnd = new Random();
	private int queue_key = "qwert".hashCode();
	
	private Map<String, RoverParametersList> roverParameters = new Map<String, RoverParametersList>();
	private Map<String, RoverAutonomusCode> roverLogics = new Map<String, RoverAutonomusCode>();
	private Map<String, SatelliteParametersList> satelliteParameters = new Map<String, SatelliteParametersList>();
	private Map<String, SatelliteAutonomusCode> satelliteLogics = new Map<String, SatelliteAutonomusCode>();
	
	private Map<String, RoverObject> roversToAdd = new Map<String, RoverObject>();
	private Map<String, SatelliteObject> satsToAdd = new Map<String, SatelliteObject>();
	
	private List<List<String>> serialHistory = new List<List<String>>();
	
	public void wakeUp(){};
	public static void align(){
		GUI = Form.frame;
	}
	
	//TODO Add items for rover and satellite option here using addItemToSelectionList
	public Admin(){
		//addItemToSelectionList(	name_on_list ,	object_to_add	);
		addItemToSelectionList(		"Default", 		new RoverParametersList());
		addItemToSelectionList(		"Generic4", 	new GenericRover("Generic4", 4));
		addItemToSelectionList(		"RAIR", 		new RAIRcode());
		addItemToSelectionList(		"RAIR Control", new RAIRcodeControl());
		addItemToSelectionList(		"RAIR Risk Averse", new RAIRcodeRA());
		addItemToSelectionList(		"RAIR Risk Seeking", new RAIRcodeRS());
		addItemToSelectionList(		"RAIR Risk Temper", new RAIRcodeRT());	
		addItemToSelectionList(		"PIDAA",		new PIDAAcode());
		//addItemToSelectionList(		"PIDAA 2",		new PIDAAcode2());
		addItemToSelectionList(		"[null]", 		(SatelliteAutonomusCode)null);
		addItemToSelectionList(		"[null]", 		(SatelliteParametersList)null);
	}
	
	public void beginSimulation(RunConfiguration config){
	
		if (config.mapFromFile){
			try {
				if (!config.mapFile.exists()){
					int a = 1/0;
				}
				GUI.TerrainPnl.HeightMap.loadMap(config.mapFile);
				Globals.writeToLogFile("Start Up", "Using Map File: " + config.mapFile.getName());
			}
			catch (Exception e){
				System.out.println("Invalid Map File");
				return;
			}
		}
		else {
			GUI.TerrainPnl.HeightMap.genorateLandscape(config.mapSize, config.mapDetail, config.mapRough);
			GUI.TerrainPnl.HeightMap.genorateTargets(config.targetDensity);
			GUI.TerrainPnl.HeightMap.genorateHazards(config.hazardDensity);
			Globals.writeToLogFile("Start Up", "Using Random Map");
		}
		
		serialHistory = new List<List<String>>();
		serialHistory.add(new List<String>());
		serialHistory.get(0).add("");
		int x = 0;
		while (x < GUI.WrapperPnl.SatelliteList.getItems().length){
			String key = (String)GUI.WrapperPnl.SatelliteList.getItemAt(x);
			serialHistory.add(new List<String>());
			serialHistory.get(x+1).add("");
			x++;
		}
		x = 0;
		while (x < GUI.WrapperPnl.RoverList.getItems().length){
			String key = (String)GUI.WrapperPnl.RoverList.getItemAt(x);
			serialHistory.add(new List<String>());
			serialHistory.get(x+1+satsToAdd.size()).add("");
			x++;
		}
		GUI.WrapperPnl.SerialHistorySlider.setValue(0);
		GUI.WrapperPnl.SerialHistorySlider.setMaximum(0);
		Globals.initalizeLists(config.tags);
		
		GUI.WrapperPnl.genorateSerialDisplays(config.rovers, config.satellites);
		GUI.RoverHubPnl.setRovers(config.rovers);
		GUI.SatelliteHubPnl.setSatellites(config.satellites);
		Access.INTERFACE.setCallTags(config.roverNames, config.satelliteNames);
		InterfaceCode.start();
		GUI.RoverHubPnl.start();
		GUI.SatelliteHubPnl.start();
		Globals.startTime();
		
		updateSerialDisplays();
		
		GUI.WrapperPnl.tabbedPane.setEnabled(true);
	}
	
	public RunConfiguration getConfigurationFromForm(){
		Map<String, String> roverNames = new Map<String, String>();
		RoverObject[] rovers = new RoverObject[roversToAdd.size()];
		Map<String, String> satelliteNames = new Map<String, String>();
		SatelliteObject[] satellites = new SatelliteObject[satsToAdd.size()];
		String[] tags = new String[roversToAdd.size()+satsToAdd.size()+1];
		tags[0] = "g";
		int x = 0;
		while (x < GUI.WrapperPnl.SatelliteList.getItems().length){
			String key = (String)GUI.WrapperPnl.SatelliteList.getItemAt(x);
			satellites[x] = satsToAdd.get(key);
			satelliteNames.add(key, satellites[x].getIDCode());
			tags[x+1] = satellites[x].getIDCode();
			x++;
		}
		x = 0;
		while (x < GUI.WrapperPnl.RoverList.getItems().length){
			String key = (String)GUI.WrapperPnl.RoverList.getItemAt(x);
			rovers[x] = roversToAdd.get(key);
			roverNames.add(key, rovers[x].getIDTag());
			tags[x+1+satsToAdd.size()] = rovers[x].getIDTag();
			x++;
		}
		if (GUI.WrapperPnl.TypeSelector.getSelectedIndex() == 1){
			File mapFile = new File(GUI.WrapperPnl.FileLocTxt.getText());
			return new RunConfiguration(roverNames,	rovers, satelliteNames,
					satellites, tags, mapFile);
		}
		else {
			double mapRough = GUI.WrapperPnl.MapRoughSlider.getValue()/1000.0;
			int mapSize = (int) GUI.WrapperPnl.MapSizeSpnr.getValue();
			int mapDetail = (int) GUI.WrapperPnl.MapDetailSpnr.getValue();
			double targetDensity = (double) GUI.WrapperPnl.TargetDensitySpnr.getValue();
			double hazardDensity = (double) GUI.WrapperPnl.HazardDensitySpnr.getValue();
			return new RunConfiguration(roverNames, rovers, satelliteNames,	satellites, tags, mapRough,
					mapSize, mapDetail, targetDensity, hazardDensity);
		}
	}
	
	public void updateSerialDisplays(){
		Queue<Byte>[] buffers = Globals.getSerialQueues(queue_key);
		String[] stored = new String[buffers.length];
		int x = 0;
		while (x < buffers.length){
			stored[x] = "";
			while (!buffers[x].isEmpty()){
				stored[x] += (char) buffers[x].pop().byteValue();
			}
			x++;
		}
		x = 0;
		while (x < stored.length){
			serialHistory.get(x).add(stored[x]);
			x++;
		}
		GUI.WrapperPnl.SerialHistorySlider.setMaximum(GUI.WrapperPnl.SerialHistorySlider.getMaximum()+1);
		if (GUI.WrapperPnl.SerialHistorySlider.getValue() == GUI.WrapperPnl.SerialHistorySlider.getMaximum()-1){
			GUI.WrapperPnl.SerialHistorySlider.setValue(GUI.WrapperPnl.SerialHistorySlider.getMaximum());
		}
		drawSerialBuffers(GUI.WrapperPnl.SerialHistorySlider.getValue());
	}
	
	public void drawSerialBuffers(int hist){
		GUI.WrapperPnl.SerialGroundLbl.setText(serialHistory.get(0).get(hist));
		GUI.WrapperPnl.SerialGroundAvailableLbl.setText(serialHistory.get(0).get(hist).length()+"");
		int x = 1;
		int i = 0;
		while (i < GUI.WrapperPnl.SerialSatelliteLbls.length){
			GUI.WrapperPnl.SerialSatelliteLbls[i].setText(serialHistory.get(x).get(hist));
			GUI.WrapperPnl.SerialSatelliteAvailableLbls[i].setText(serialHistory.get(x).get(hist).length()+"");
			i++;
			x++;
		}
		i = 0;
		while (i < GUI.WrapperPnl.SerialRoverLbls.length){
			GUI.WrapperPnl.SerialRoverLbls[i].setText(serialHistory.get(x).get(hist));
			GUI.WrapperPnl.SerialRoverAvailableLbls[i].setText(serialHistory.get(x).get(hist).length()+"");
			i++;
			x++;
		}
	}
	
	public void addRoverToList(){
		if (GUI.WrapperPnl.RovAutonomusCodeList.getSelectedIndex() != -1 && GUI.WrapperPnl.RovDriveModelList.getSelectedIndex() != -1){
			int numb = 1;
			String newName = (String)GUI.WrapperPnl.RovAutonomusCodeList.getSelectedItem() + " " + numb;
			while (contains(GUI.WrapperPnl.RoverList.getItems(), newName)){
				numb++;
				newName = (String)GUI.WrapperPnl.RovAutonomusCodeList.getSelectedItem() + " " + numb;
			}
			//TODO change temp to map temp
			GUI.WrapperPnl.RoverList.addValue(newName);
			//if you're getting errors with rovers 'sharing' data it's the pass reference value here
			RoverAutonomusCode autoCode = roverLogics.get((String)GUI.WrapperPnl.RovAutonomusCodeList.getSelectedItem()); 
			RoverParametersList params = roverParameters.get((String)GUI.WrapperPnl.RovDriveModelList.getSelectedItem());
			// for randomized start position roversToAdd.add(newName, new RoverObject(newName, "r"+GUI.WrapperPnl.RoverList.getItems().length, params, autoCode, new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0));
			DecimalPoint location = new DecimalPoint(-170, -170);
			roversToAdd.add(newName, new RoverObject(newName, "r"+GUI.WrapperPnl.RoverList.getItems().length, params, autoCode, location, Math.PI/2, GUI.TerrainPnl.getTemperature(location)));		
		}
	}
	
	public void removeRoverFromList(){
		try {
			roversToAdd.remove(GUI.WrapperPnl.RoverList.getSelectedItem().toString());
			GUI.WrapperPnl.RoverList.removeValue(GUI.WrapperPnl.RoverList.getSelectedIndex());
		} 
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void addSatelliteToList(){
		try {
			if (GUI.WrapperPnl.SatAutonomusCodeList.getSelectedIndex() != -1 && GUI.WrapperPnl.SatDriveModelList.getSelectedIndex() != -1){
				int numb = 1;
				//TODO change to code name
				String newName = "Satellite " + numb;
				//newName = (String)GUI.WrapperPnl.SatAutonomusCodeList.getSelectedItem() + " " + numb;
				while (contains(GUI.WrapperPnl.SatelliteList.getItems(), newName)){
					numb++;
					newName = "Satellite " + numb;
					//newName = (String)GUI.WrapperPnl.SatAutonomusCodeList.getSelectedItem() + " " + numb;
				}
				GUI.WrapperPnl.SatelliteList.addValue(newName);
				this.satsToAdd.add(newName, new SatelliteObject(newName, "s"+GUI.WrapperPnl.SatelliteList.getItems().length, null, null, rnd.nextDouble()*100000+10000000, rnd.nextDouble()*90, rnd.nextDouble()*360));
			}
		}
		catch (Exception e){
			Globals.reportError("Admin", "addSatellitetoList", e);
		}
	}
	
	public void removeSatelliteFromList(){
		try {
			satsToAdd.remove(GUI.WrapperPnl.SatelliteList.getSelectedItem().toString());
			GUI.WrapperPnl.SatelliteList.removeValue(GUI.WrapperPnl.SatelliteList.getSelectedIndex());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void addItemToSelectionList(String name, RoverParametersList item){
		roverParameters.add(name, item);
		GUI.WrapperPnl.RovDriveModelList.addValue(name);
	}
	
	private void addItemToSelectionList(String name, RoverAutonomusCode item){
		roverLogics.add(name, (RoverAutonomusCode)item);
		GUI.WrapperPnl.RovAutonomusCodeList.addValue(name);
	}
	
	private void addItemToSelectionList(String name, SatelliteParametersList item){
		satelliteParameters.add(name, item);
		GUI.WrapperPnl.SatDriveModelList.addValue(name);
	}
	
	private void addItemToSelectionList(String name, SatelliteAutonomusCode item){
		satelliteLogics.add(name, item);
		GUI.WrapperPnl.SatAutonomusCodeList.addValue(name);
	}
	
	private boolean contains(Object[] array, String val){
		int x = 0;
		while (x < array.length){
			if (array[x].equals(val)){
				return true;
			}
			x++;
		}
		return false;
	}
	
	private String[] add(Object[] one, Object[] two){
		String[] out = new String[one.length + two.length];
		int x = 0;
		while (x < one.length){
			out[x] = one[x].toString();
			x++;
		}
		while (x < out.length){
			out[x] = two[x-one.length].toString();
			x++;
		}
		return out;
	}
}
