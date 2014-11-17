package wrapper;

import java.util.Random;

import control.InterfaceCode;
import objects.DecimalPoint;
import objects.Map;
import rover.RoverObj;
import rover.RoverParametersList;
import visual.Form;

public class Admin {

	public static Form GUI = new Form();
	
	Random rnd = new Random();
	
	public static void align(){
		GUI = Form.frame;
	}
	
	public void beginSimulation(){
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
