package wrapper;

import java.util.Random;

import control.InterfaceCode;
import objects.DecimalPoint;
import objects.Map;
import objects.Queue;
import rover.RoverAutonomusCode;
import rover.RoverObject;
import rover.RoverParametersList;
import rover.autoCode.GenericRover;
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
	
	public void wakeUp(){};
	public static void align(){
		GUI = Form.frame;
	}
	
	public Admin(){
		roverParameters.add("Default", new RoverParametersList());
		GUI.WrapperPnl.RovDriveModelList.addValue("Default");
		roverLogics.add("Generic4", new GenericRover("Generic4", 4));
		GUI.WrapperPnl.RovAutonomusCodeList.addValue("Generic4");
		GUI.WrapperPnl.SatAutonomusCodeList.addValue("[null]");
		GUI.WrapperPnl.SatDriveModelList.addValue("[null]");
	}
	
	public void beginSimulation(){
		
		Globals.startTime();
		Map<String, String> roverNames = new Map<String, String>();
		RoverObject[] rovers = new RoverObject[roversToAdd.size()];
		Map<String, String> satelliteNames = new Map<String, String>();
		SatelliteObject[] satellites = new SatelliteObject[satsToAdd.size()];
		String[] tags = new String[roversToAdd.size()+satsToAdd.size()+1];
		tags[0] = "g";
		int x = 0;
		while (x < GUI.WrapperPnl.RoverList.getItems().length){
			String key = (String)GUI.WrapperPnl.RoverList.getItemAt(x);
			rovers[x] = roversToAdd.get(key);
			roverNames.add(key, rovers[x].getIDTag());
			tags[x+1] = rovers[x].getIDTag();
			x++;
		}
		x = 0;
		while (x < GUI.WrapperPnl.SatelliteList.getItems().length){
			String key = (String)GUI.WrapperPnl.SatelliteList.getItemAt(x);
			satellites[x] = satsToAdd.get(key);
			satelliteNames.add(key, satellites[x].getIDCode());
			tags[x+1+roversToAdd.size()] = satellites[x].getIDCode();
			x++;
		}
		Globals.initalizeLists(tags);
		GUI.WrapperPnl.genorateSerialDisplays(rovers, satellites);
		GUI.RoverHubPnl.setRovers(rovers);
		GUI.SatelliteHubPnl.setSatellites(satellites);
		Access.INTERFACE.setCallTags(roverNames, satelliteNames);
		InterfaceCode.start();
		GUI.RoverHubPnl.start();
		GUI.SatelliteHubPnl.start();
		
		updateSerialDisplays();
		
		GUI.WrapperPnl.tabbedPane.setEnabled(true);
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
		GUI.WrapperPnl.SerialGroundLbl.setText(stored[0]);
		GUI.WrapperPnl.SerialGroundAvailableLbl.setText(stored[0].length()+"");
		x = 1;
		int i = 0;
		while (i < GUI.WrapperPnl.SerialSatelliteLbls.length){
			GUI.WrapperPnl.SerialSatelliteLbls[i].setText(stored[x]);
			GUI.WrapperPnl.SerialSatelliteAvailableLbls[i].setText(stored[x].length()+"");
			i++;
			x++;
		}
		i = 0;
		while (i < GUI.WrapperPnl.SerialRoverLbls.length){
			GUI.WrapperPnl.SerialRoverLbls[i].setText(stored[x]);
			GUI.WrapperPnl.SerialRoverAvailableLbls[i].setText(stored[x].length()+"");
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
			roversToAdd.add(newName, new RoverObject(newName, "r"+GUI.WrapperPnl.RoverList.getItems().length, roverParameters.get((String)GUI.WrapperPnl.RovDriveModelList.getSelectedItem()), this.roverLogics.get((String)GUI.WrapperPnl.RovAutonomusCodeList.getSelectedItem()), new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0));
		}
	}
	
	public void addSatelliteToList(){
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
