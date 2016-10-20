package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.Platform;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

class NewFrameMenu extends JMenu {
	private static final Logger LOG = LogManager.getLogger(NewFrameMenu.class);

	private static final long serialVersionUID = -6654005507690093494L;

	NewFrameMenu(){
		initialize();
	}

	private void initialize() {
		for (String framename : FrameRegistry.getFrameClasses()){
			JMenuItem item = new JMenuItem();
			item.setText(framename);
			item.addActionListener((ActionEvent e) -> createMiscFrame(framename));
			this.add(item);
		}
	}
	
	private void createMiscFrame(String name){
		try {
			UiFactory.getDesktop().add(FrameRegistry.getFrameClass(name).newInstance());
		}
		catch (InstantiationException | IllegalAccessException e) {
			LOG.log(Level.ERROR, "Failed to add class to desktop", e);
		}
	}

	void initPlatformViewControls(Map<String, PlatformEnvironment> enviros, Map<String, List<Platform>> platforms){
		for (String type : enviros.keySet()){
			JMenu menu = new JMenu(type);
			this.add(menu);

			JMenuItem enviroItem = new JMenuItem("Environment vViewer");
			enviroItem.addActionListener((a) -> {
				try {
					EnvironmentDisplay display = FrameRegistry.getEnvironmentDisplay(type).newInstance();
					display.setEnvironment(enviros.get(type));
					UiFactory.getDesktop().add(display);
				}
				catch (InstantiationException | IllegalAccessException e) {
					LOG.log(Level.ERROR, "Could not create a new EnvironmentDisplay", e);
				}
			});
			menu.add(enviroItem);

			JMenu platformMenu = new JMenu("Platforms");
			menu.add(platformMenu);

			for (Platform platform : platforms.get(type)){
				JMenuItem item = new JMenuItem(platform.getName());
				item.addActionListener((a) -> {
					try {
						PlatformDisplay display = FrameRegistry.getPlatformDisplay(type).newInstance();
						display.setPlatform(platform);
						UiFactory.getDesktop().add(display);
					}
					catch (InstantiationException | IllegalAccessException e) {
						LOG.log(Level.ERROR, "Could not create new PlatformDisplay", e);
					}
				});
				platformMenu.add(item);
			}
		}
	}
	
}
