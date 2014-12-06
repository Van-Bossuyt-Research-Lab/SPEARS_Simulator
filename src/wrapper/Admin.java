package wrapper;

import java.util.Random;

import control.InterfaceCode;
import objects.DecimalPoint;
import objects.Map;
import objects.Queue;
import rover.RoverObj;
import rover.RoverParametersList;
import rover.autoCode.GenericRover;
import satellite.SatelliteObject;
import visual.Form;

public class Admin {

	public static Form GUI = new Form();
	
	Random rnd = new Random();
	private int queue_key = "qwert".hashCode();
	
	public static void align(){
		GUI = Form.frame;
	}
	
	public void beginSimulation(){
		
		Globals.startTime();
		Globals.initalizeLists(new String[] { "g", "s", "r1", "r2" });
		RoverObj[] rovers = new RoverObj[] { 
				new RoverObj("Rover 1", "r1", new RoverParametersList(), new GenericRover("Rover 1", 2), new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0), 
				new RoverObj("Rover 2", "r2", new RoverParametersList(), new GenericRover("Rover 2", 3), new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0)
			};
		GUI.WrapperPnl.genorateSerialDisplays(rovers, new SatelliteObject[] { new SatelliteObject("Satellite 1", "s") });
		GUI.RoverHubPnl.setRovers(rovers);
		Map<String, String> roverNames = new Map<String, String>();
		roverNames.add("Rover 1", "r1");
		roverNames.add("Rover 2", "r2");
		Map<String, String> satelliteNames = new Map<String, String>();
		satelliteNames.add("Satellite 1", "s");
		Access.INTERFACE.setCallTags(roverNames, satelliteNames);
		InterfaceCode.start();
		GUI.RoverHubPnl.start();
		
		updateSerialDisplays();
		
		GUI.WrapperPnl.tabbedPane.setEnabled(true);
		/*
		Globals.startTime();
		Map<String, String> roverNames = new Map<String, String>();
		roverNames.add("Rover 1", "r1");
		roverNames.add("Rover 2", "r2");
		Map<String, String> satelliteNames = new Map<String, String>();
		satelliteNames.add("Satellite 1", "s");
		GUI.RoverHubPnl.setRovers(new RoverObj[] { 
			new RoverObj("Rover 1", "r1", new RoverParametersList(), null, new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0), 
			new RoverObj("Rover 2", "r2", new RoverParametersList(), null, new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0)
		});
		Globals.initalizeLists(add(new String[] { "g" }, add(roverNames.getValues(), satelliteNames.getValues())));
		Access.INTERFACE.setCallTags(roverNames, satelliteNames);
		InterfaceCode.start();
		GUI.RoverHubPnl.start();
		*/
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
