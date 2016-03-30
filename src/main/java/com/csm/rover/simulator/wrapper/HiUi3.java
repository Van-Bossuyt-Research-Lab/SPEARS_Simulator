package com.csm.rover.simulator.wrapper;

import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.UIManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.satellite.SatelliteObject;
import com.csm.rover.simulator.ui.Form2;
import com.csm.rover.simulator.ui.events.InternalEventCheckGate;
import com.csm.rover.simulator.ui.events.InternalEventHandler;
import com.csm.rover.simulator.ui.events.MenuCommandEvent;

public class HiUi3 implements HumanInterfaceAbstraction {
	private static Logger LOG = LogManager.getLogger(HiUi3.class);

	private Form2 form;
	
	public HiUi3(){
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (Exception e) {
	    	LOG.log(Level.WARN, "UI Look and Feel failed to be set to: " + UIManager.getSystemLookAndFeelClassName(), e);
	    }
		form = new Form2();
		form.setLocation(0, 0);
		form.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		form.setVisible(true);
		InternalEventHandler.registerInternalListener(InternalEventCheckGate.responseWith(() -> exit()).forAction(MenuCommandEvent.Action.EXIT));
	}
	
	@Override
	public void initialize(NamesAndTags namesAndTags, SerialBuffers buffers,
			ArrayList<RoverObject> rovers,
			ArrayList<SatelliteObject> satellites, TerrainMap map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateRovers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSatellites() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateRover(String name, DecimalPoint location, double direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSatellite(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSerialBuffers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void viewAccelerated(int runtime, double accelerant) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		LOG.log(Level.INFO, "Exiting SPEARS");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		System.exit(0);
	}

}
