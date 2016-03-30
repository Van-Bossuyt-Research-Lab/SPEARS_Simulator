package com.csm.rover.simulator.ui.menu;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.csm.rover.simulator.ui.events.InternalEventHandler;
import com.csm.rover.simulator.ui.events.MenuCommandEvent;
import com.csm.rover.simulator.ui.frame.FrameRegistry;

public class NewFrameMenu extends JMenu {

	private static final long serialVersionUID = -6654005507690093494L;

	public NewFrameMenu(){
		initialize();
	}

	private void initialize() {
		for (String framename : FrameRegistry.getFrameClasses()){
			JMenuItem item = new JMenuItem();
			item.setText(framename);
			item.addActionListener((ActionEvent e) -> fireEvent(framename));
			this.add(item);
		}
	}
	
	private void fireEvent(String name){
		InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.NEW_FRAME).setValue(FrameRegistry.getFrameClass(name)).build());
	}
	
}
