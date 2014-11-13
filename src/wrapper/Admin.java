package wrapper;

import java.util.Random;
import objects.DecimalPoint;
import rover.RoverObj;
import rover.RoverParametersList;
import visual.Form;

public class Admin {

	static Form GUI = new Form();
	
	Random rnd = new Random();
	
	public static void align(){
		GUI = Form.frame;
	}
	
	public void beginSimulation(){
		GUI.RoverHubPnl.setRovers(new RoverObj[] { 
			new RoverObj("Rover 1", "r1", new RoverParametersList(), null, new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0), 
			new RoverObj("Rover 2", "r2", new RoverParametersList(), null, new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0)
		});
	}
	
}
