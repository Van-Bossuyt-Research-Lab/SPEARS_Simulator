package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.control.InterfaceCode;
import com.csm.rover.simulator.control.PopUp;
import com.csm.rover.simulator.map.io.TerrainMapReader;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.FreeThread;
import com.csm.rover.simulator.objects.RunConfiguration;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.rover.autoCode.*;
import com.csm.rover.simulator.rover.phsicsModels.FailOnHazard;
import com.csm.rover.simulator.rover.phsicsModels.RiskOnHazard;
import com.csm.rover.simulator.rover.phsicsModels.RoverPhysicsModel;
import com.csm.rover.simulator.satellite.SatelliteAutonomusCode;
import com.csm.rover.simulator.satellite.SatelliteObject;
import com.csm.rover.simulator.satellite.SatelliteParametersList;
import com.csm.rover.simulator.visual.Form;

import java.io.File;
import java.util.*;

import static java.util.Arrays.asList;

//TODO make into a signularity thingy
public class Admin {

	public static Form GUI = new Form();
	
	Random rnd = new Random();
	private int queue_key = "qwert".hashCode();
	
	private Map<String, RoverPhysicsModel> roverParameters = new TreeMap<String, RoverPhysicsModel>();
	private Map<String, RoverAutonomusCode> roverLogics = new TreeMap<String, RoverAutonomusCode>();
	private Map<String, SatelliteParametersList> satelliteParameters = new TreeMap<String, SatelliteParametersList>();
	private Map<String, SatelliteAutonomusCode> satelliteLogics = new TreeMap<String, SatelliteAutonomusCode>();
	
	private Map<String, RoverObject> roversToAdd = new TreeMap<String, RoverObject>();
	private Map<String, SatelliteObject> satsToAdd = new TreeMap<String, SatelliteObject>();
	
	private ArrayList<ArrayList<String>> serialHistory = new ArrayList<ArrayList<String>>();
	
	public void wakeUp(){}
	public static void align(){
		GUI = Form.frame;
	}
	
	//TODO Add items for rover and satellite option here using addItemToSelectionList
	//TODO clean up this interface for OCP
	public Admin(){
		//addItemToSelectionList(	name_on_list ,	object_to_add	);
		addItemToSelectionList(		"Default", 		new RoverPhysicsModel());
		addItemToSelectionList(		"Fail On Hazard", new FailOnHazard());
		addItemToSelectionList(		"Risk On Hazard", new RiskOnHazard());
		addItemToSelectionList(		"[null]", 		new BlankRoverAuto());
		addItemToSelectionList(		"Generic4", 	new GenericRover("Generic4", 4));
		addItemToSelectionList(		"RAIR", 		new RAIRcode());
		addItemToSelectionList(		"RAIR Control", new RAIRcodeControl());
		addItemToSelectionList(		"RAIR Risk Averse", new RAIRcodeRA());
		addItemToSelectionList(		"RAIR Risk Seeking", new RAIRcodeRS());
		addItemToSelectionList("RAIR Risk Temper", new RAIRcodeRT());
		addItemToSelectionList("PIDAA", new PIDAAcode3());
		addItemToSelectionList("PIDAA CONTROL", new PIDAAcontrol());
		addItemToSelectionList("GORARO Simp", new GORAROcode1());
		addItemToSelectionList("GORARO Adv A", new GORAROAdvanceCode(new double[]{
				10000, 84.2892, 567.513, 7.37412, 0
		}));
		addItemToSelectionList("GORARO Adv B", new GORAROAdvanceCode(new double[]{
				10000, 80.44, 972.8, 25.84, 0
		}));
		addItemToSelectionList("GORARO Adv C", new GORAROAdvanceCode(new double[]{
				10000, 100, 500, 2, 0
		}));
		addItemToSelectionList("GORARO Adv D", new GORAROAdvanceCode(new double[]{
				10000, 40, 600, 2, 0
		}));
		addItemToSelectionList("GORARO Adv E", new GORAROAdvanceCode(new double[]{
				10000, 70, 600, 2, 0
		}));
		addItemToSelectionList("GORARO Adv F", new GORAROAdvanceCode(new double[]{
				10000, 100, 600, 2, 0
		}));
		addItemToSelectionList("GORARO Adv G", new GORAROAdvanceCode(new double[]{
				10000, 40, 700, 2, 0
		}));
		addItemToSelectionList("GORARO Adv H", new GORAROAdvanceCode(new double[]{
				10000, 70, 700, 2, 0
		}));
		addItemToSelectionList("MER", new MER(new DecimalPoint[]{
				new DecimalPoint(-9.5, -9.5),
				new DecimalPoint(-12.5, -5.5),
				new DecimalPoint(-17.5, -9.5),
				new DecimalPoint(-10.5, -16.5),
				new DecimalPoint(-2.5, -38.5),
				new DecimalPoint(8.5, -37.5),
				new DecimalPoint(15.5, -36.5),
				new DecimalPoint(21.5, -49.5),
				new DecimalPoint(22.5, -60.5),
				new DecimalPoint(23.5, -70.5),
				new DecimalPoint(14.5, -65.5),
				new DecimalPoint(13.5, -81.5),
				new DecimalPoint(6.5, -81.5),
				new DecimalPoint(2.5, -77.5),
				new DecimalPoint(3.5, -83.5),
				new DecimalPoint(0.5, -89.5),
				new DecimalPoint(-30.5, -109.5),
				new DecimalPoint(-26.5, -117.5),
				new DecimalPoint(-43.5, -127.5),
				new DecimalPoint(-54.5, -146.5),
				new DecimalPoint(-54.5, -149.5),
				new DecimalPoint(-57.5, -147.5),
				new DecimalPoint(-56.5, -159.5),
				new DecimalPoint(-59.5, -160.5),
				new DecimalPoint(-64.5, -165.5),
				new DecimalPoint(-61.5, -174.5),
				new DecimalPoint(-55.5, -182.5),
				new DecimalPoint(-57.5, -184.5),
				new DecimalPoint(-59.5, -189.5),
				new DecimalPoint(-43.5, -193.5),
				new DecimalPoint(-39.5, -190.5),
				new DecimalPoint(-38.5, -200.5),
				new DecimalPoint(-24.5, -198.5),
				new DecimalPoint(-25.5, -193.5),
				new DecimalPoint(-38.5, -212.5),
				new DecimalPoint(-21.5, -219.5),
				new DecimalPoint(-8.5, -223.5),
				new DecimalPoint(-4.5, -224.5),
				new DecimalPoint(-5.5, -227.5)
		}));
		//addItemToSelectionList(		"PIDAA 2",		new PIDAAcode2());
		addItemToSelectionList("[null]", (SatelliteAutonomusCode) null);
		addItemToSelectionList("[null]", (SatelliteParametersList) null);
	}
	
	public void beginSimulation(RunConfiguration config){
		//TODO add option to toggle time shifted executions
		if (config.rovers.size() == 0 ||config.satellites.size() == 0){
			System.err.println("Invalid Configuration.  Requires at least 1 rover and 1 satellite.");
			return;
		}
		
		if (config.mapFromFile){
			try {
				if (!config.mapFile.exists()){
					throw new Exception();
				}
				GUI.TerrainPnl.heightMap = TerrainMapReader.loadMap(config.mapFile);
				Globals.writeToLogFile("Start Up", "Using Map File: " + config.mapFile.getName());
			}
			catch (Exception e){
				System.err.println("Invalid Map File");
				e.printStackTrace();
				return;
			}
		}
		else {
			GUI.TerrainPnl.heightMap.generateLandscape(config.mapSize, config.mapDetail);
			GUI.TerrainPnl.heightMap.generateTargets(config.monoTargets, config.targetDensity);
			GUI.TerrainPnl.heightMap.generateHazards(config.monoHazards, config.hazardDensity);
			GUI.TerrainPnl.mapPanel.setSize(0, 0); //forces the map panel to auto resize
			Globals.writeToLogFile("Start Up", "Using Random Map");
		}
		
		if (config.accelerated){
			Globals.writeToLogFile("Start Up", "Accelerating Simulation");
			GUI.setVisible(false);
			Globals.setUpAcceleratedRun(3600000*config.runtime);
		}
		
		serialHistory = new ArrayList<ArrayList<String>>();
		int x = 0;
		while (x < 1+config.satellites.size()+config.rovers.size()){
			serialHistory.add(new ArrayList<String>());
			serialHistory.get(x).add("");
			x++;
		}
		GUI.WrapperPnl.SerialHistorySlider.setValue(0);
		GUI.WrapperPnl.SerialHistorySlider.setMaximum(0);
		Globals.initializeLists(config.tags);
		
		GUI.WrapperPnl.genorateSerialDisplays(config.rovers, config.satellites);
		GUI.RoverHubPnl.setRovers(config.rovers);
		GUI.SatelliteHubPnl.setSatellites(config.satellites);
		Access.INTERFACE.setCallTags(config.roverNames, config.satelliteNames);
		GUI.RoverHubPnl.setIdentifiers(config.roverNames.values(), config.satelliteNames.values());
		InterfaceCode.start();
		GUI.RoverHubPnl.start();
		GUI.SatelliteHubPnl.start();
		Globals.startTime(config.accelerated);
		
		updateSerialDisplays();
		
		GUI.WrapperPnl.tabbedPane.setEnabled(true);
		GUI.WrapperPnl.tabbedPane.setSelectedIndex(1);
	}
	
	public void saveCurrentconfiguration(){
		new FreeThread(0, new Runnable(){
			public void run(){
				File config = new File("default.cfg");
				if (config.exists()){
					if ((new PopUp()).showConfirmDialog("There is already a quick run file saved would you like to overwrite it?", "Save Configuration", PopUp.YES_NO_OPTIONS) == PopUp.YES_OPTION){
						try {
							getConfigurationFromForm().Save(config);
						}
						catch (Exception e){
							e.printStackTrace();
							(new PopUp()).showConfirmDialog("Something went wrong and the operation was aborted.", "Save Configuration", PopUp.OK_OPTION);
						}
					}
				}
				else {
					try {
						getConfigurationFromForm().Save(config);
						(new PopUp()).showConfirmDialog("Startup configuration was successfully saved.", "Save Configuration", PopUp.OK_OPTION);
					}
					catch (Exception e){
						e.printStackTrace();
						(new PopUp()).showConfirmDialog("Something went wrong and the operation was aborted.", "Save Configuration", PopUp.OK_OPTION);
					}
				}
			}
		}, 1, "config-save");
	}
	
	public RunConfiguration getConfigurationFromForm(){
		Map<String, String> roverNames = new TreeMap<String, String>();
		ArrayList<RoverObject> rovers = new ArrayList<RoverObject>(roversToAdd.size());
		Map<String, String> satelliteNames = new TreeMap<String, String>();
		ArrayList<SatelliteObject> satellites = new ArrayList<SatelliteObject>(satsToAdd.size());
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("g");
		int x = 0;
		while (x < GUI.WrapperPnl.SatelliteList.getItems().size()){
			String key = (String)GUI.WrapperPnl.SatelliteList.getItemAt(x);
			satellites.add(x, satsToAdd.get(key));
			satelliteNames.put(key, satellites.get(x).getIDCode());
			tags.add(satellites.get(x).getIDCode());
			x++;
		}
		x = 0;
		while (x < GUI.WrapperPnl.RoverList.getItems().size()){
			String key = (String)GUI.WrapperPnl.RoverList.getItemAt(x);
			rovers.add(roversToAdd.get(key));
			roverNames.put(key, rovers.get(x).getIDTag());
			tags.add(rovers.get(x).getIDTag());
			x++;
		}
		if (GUI.WrapperPnl.TypeSelector.getSelectedIndex() == 1){
			File mapFile = new File(GUI.WrapperPnl.FileLocTxt.getText());
			return new RunConfiguration(roverNames,	rovers, satelliteNames,
					satellites, tags, mapFile, GUI.WrapperPnl.AccelChk.isSelected(), 
					(Integer)GUI.WrapperPnl.RuntimeSpnr.getValue());
		}
		else {
			double mapRough = GUI.WrapperPnl.MapRoughSlider.getValue()/10000.0;
			int mapSize = (Integer) GUI.WrapperPnl.MapSizeSpnr.getValue();
			int mapDetail = (Integer) GUI.WrapperPnl.MapDetailSpnr.getValue();
			double targetDensity = (Double) GUI.WrapperPnl.TargetDensitySpnr.getValue()/1000.;
			double hazardDensity = (Double) GUI.WrapperPnl.HazardDensitySpnr.getValue()/1000.;
			boolean monoTargets = !GUI.WrapperPnl.ValuedTargetsChk.isSelected(); //cause the for says use and the computer reads not using
			boolean monoHazards = !GUI.WrapperPnl.ValuedHazardsChk.isSelected();
			return new RunConfiguration(roverNames, rovers, satelliteNames,	satellites, tags, mapRough,
					mapSize, mapDetail, targetDensity, hazardDensity, monoTargets, 
					monoHazards, GUI.WrapperPnl.AccelChk.isSelected(), 
					(Integer)GUI.WrapperPnl.RuntimeSpnr.getValue());
		}
	}
	
	public void updateSerialDisplays(){
		ArrayList<Queue<Byte>> buffers = Globals.getSerialQueues();
		String[] stored = new String[buffers.size()];
		int x = 0;
		while (x < buffers.size()){
			stored[x] = "";
			while (!buffers.get(x).isEmpty()){
				stored[x] += (char) buffers.get(x).poll().byteValue();
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
			String namebase = (String)GUI.WrapperPnl.RovAutonomusCodeList.getSelectedItem(); 
			if (GUI.WrapperPnl.RovAutonomusCodeList.getSelectedItem().equals("[null]")){
				namebase = "Rover";
			}
			String newName = namebase + " " + numb;
			while (asList(GUI.WrapperPnl.RoverList.getItems()).contains(newName)){
				numb++;
				newName = namebase + " " + numb;
			}
			//TODO change temp to map temp
			GUI.WrapperPnl.RoverList.addValue(newName);
			//if you're getting errors with rovers 'sharing' data it's the pass reference value here
			RoverAutonomusCode autoCode = roverLogics.get((String)GUI.WrapperPnl.RovAutonomusCodeList.getSelectedItem()).clone();
			autoCode.setRoverName(newName);
			RoverPhysicsModel params = roverParameters.get((String)GUI.WrapperPnl.RovDriveModelList.getSelectedItem()).clone();
			// for randomized start position roversToAdd.add(newName, new RoverObject(newName, "r"+GUI.WrapperPnl.RoverList.getItems().length, params, autoCode, new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0));
			DecimalPoint location = new DecimalPoint(0, 0);
			roversToAdd.put(newName, new RoverObject(newName, "r"+GUI.WrapperPnl.RoverList.getItems().size(), params, autoCode, location, Math.PI/2, GUI.TerrainPnl.getTemperature(location)));
		}
	}
	
	public void removeRoverFromList(){
		try {
			roversToAdd.remove(GUI.WrapperPnl.RoverList.getSelectedItem().toString());
			GUI.WrapperPnl.RoverList.removeValue(GUI.WrapperPnl.RoverList.getSelectedIndex());
		} 
		catch (Exception e){ e.printStackTrace(); }
	}
	
	public void addSatelliteToList(){
		try {
			if (GUI.WrapperPnl.SatAutonomusCodeList.getSelectedIndex() != -1 && GUI.WrapperPnl.SatDriveModelList.getSelectedIndex() != -1){
				int numb = 1;
				//TODO change to code name
				String newName = "Satellite " + numb;
				//newName = (String)GUI.WrapperPnl.SatAutonomusCodeList.getSelectedItem() + " " + numb;
				while (asList(GUI.WrapperPnl.SatelliteList.getItems()).contains(newName)){
					numb++;
					newName = "Satellite " + numb;
					//newName = (String)GUI.WrapperPnl.SatAutonomusCodeList.getSelectedItem() + " " + numb;
				}
				GUI.WrapperPnl.SatelliteList.addValue(newName);
				this.satsToAdd.put(newName, new SatelliteObject(newName, "s"+GUI.WrapperPnl.SatelliteList.getItems().size(), null, null, rnd.nextDouble()*100000+10000000, rnd.nextDouble()*90, rnd.nextDouble()*360));
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
	
	private void addItemToSelectionList(String name, RoverPhysicsModel item){
		roverParameters.put(name, item);
		GUI.WrapperPnl.RovDriveModelList.addValue(name);
	}
	
	private void addItemToSelectionList(String name, RoverAutonomusCode item){
		roverLogics.put(name, item);
		GUI.WrapperPnl.RovAutonomusCodeList.addValue(name);
	}
	
	private void addItemToSelectionList(String name, SatelliteParametersList item){
		satelliteParameters.put(name, item);
		GUI.WrapperPnl.SatDriveModelList.addValue(name);
	}
	
	private void addItemToSelectionList(String name, SatelliteAutonomusCode item){
		satelliteLogics.put(name, item);
		GUI.WrapperPnl.SatAutonomusCodeList.addValue(name);
	}
}
