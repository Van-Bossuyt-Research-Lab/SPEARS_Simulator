package com.csm.rover.simulator.ui.menu;

import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.csm.rover.simulator.ui.events.EmbeddedFrameEvent;
import com.csm.rover.simulator.ui.events.EmbeddedFrameListener;
import com.csm.rover.simulator.ui.events.InternalEventHandler;
import com.csm.rover.simulator.ui.events.MenuCommandEvent;

import static com.csm.rover.simulator.ui.ImageFunctions.getMenuIcon;

public class EmbeddedMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1996909612935260422L;

	private JMenu fileMenu, viewMenu, optionsMenu;
	
	private JMenu showMenu;
	
	public EmbeddedMenuBar() {
		fileMenu = new JMenu("File");
		this.add(fileMenu);
		
		viewMenu = new JMenu("View");
		this.add(viewMenu);
		
		optionsMenu = new JMenu("Options");
		this.add(optionsMenu);
		
		initialize();
	}
	
	public void initialize(){
		
		JMenuItem mntmOpen = new JMenuItem("Open Simulation");
		mntmOpen.setIcon(getMenuIcon("/gui/open_folder.png"));
		mntmOpen.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.OPEN).setOrigin(e).build()));
		fileMenu.add(mntmOpen);
		
		JMenuItem mntmNewSimulation = new JMenuItem("New Simulation");
		mntmNewSimulation.setIcon(getMenuIcon("/gui/add.png"));
		mntmNewSimulation.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.NEW_SIM).setOrigin(e).build()));
		fileMenu.add(mntmNewSimulation);
		
		fileMenu.addSeparator();
		
		JMenuItem mntmViewLogFile = new JMenuItem("View Log File");
		mntmViewLogFile.setIcon(getMenuIcon("/gui/file.png"));
		mntmViewLogFile.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.VIEW_LOG).setValue("SPEARS").setOrigin(e).build()));
		fileMenu.add(mntmViewLogFile);
		
		JMenu mnViewPlatformLog = new JMenu("View Platform Log");
		fileMenu.add(mnViewPlatformLog);
		
		fileMenu.addSeparator();
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setIcon(getMenuIcon("/gui/power.png"));
		mntmExit.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.EXIT).setOrigin(e).build()));
		fileMenu.add(mntmExit);
		
		JMenu mnNew = new JMenu("New Window");
		mnNew.setIcon(getMenuIcon("/gui/user_id.png"));
		viewMenu.add(mnNew);
		
		JMenuItem mntmObserver = new JMenuItem("Observer");
		mnNew.add(mntmObserver);
		
		JMenuItem mntmPuppyPicture = new JMenuItem("Puppy Picture");
		mnNew.add(mntmPuppyPicture);
		
		viewMenu.addSeparator();
		
		JMenu mnArrangeGrid = new JMenu("Arrange Grid");
		mnArrangeGrid.setIcon(getMenuIcon("/gui/grid.png"));
		viewMenu.add(mnArrangeGrid);
		
		JMenu mnDivisions = new JMenu("Divisions");
		mnArrangeGrid.add(mnDivisions);
		
		JMenu mnLeftSide = new JMenu("Left Side");
		mnDivisions.add(mnLeftSide);
		
		ButtonGroup leftDivisionSelection = new ButtonGroup();
		
		JRadioButtonMenuItem radioButtonMenuItem_5 = new JRadioButtonMenuItem("0");
		mnLeftSide.add(radioButtonMenuItem_5);
		radioButtonMenuItem_5.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("L0").setOrigin(e).build()));
		leftDivisionSelection.add(radioButtonMenuItem_5);
		
		JRadioButtonMenuItem radioButtonMenuItem_4 = new JRadioButtonMenuItem("1");
		radioButtonMenuItem_4.setSelected(true);
		mnLeftSide.add(radioButtonMenuItem_4);
		radioButtonMenuItem_4.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("L1").setOrigin(e).build()));
		leftDivisionSelection.add(radioButtonMenuItem_4);
		
		JRadioButtonMenuItem radioButtonMenuItem_6 = new JRadioButtonMenuItem("2");
		mnLeftSide.add(radioButtonMenuItem_6);
		radioButtonMenuItem_6.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("L2").setOrigin(e).build()));
		leftDivisionSelection.add(radioButtonMenuItem_6);
		
		JRadioButtonMenuItem radioButtonMenuItem_7 = new JRadioButtonMenuItem("3");
		mnLeftSide.add(radioButtonMenuItem_7);
		radioButtonMenuItem_7.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("L3").setOrigin(e).build()));
		leftDivisionSelection.add(radioButtonMenuItem_7);
		
		JMenu mnRightSide = new JMenu("Right Side");
		mnDivisions.add(mnRightSide);
		
		ButtonGroup rightDivisionSelection = new ButtonGroup();
		
		JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem("0");
		mnRightSide.add(radioButtonMenuItem);

		radioButtonMenuItem.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("R0").setOrigin(e).build()));
		rightDivisionSelection.add(radioButtonMenuItem);
		
		JRadioButtonMenuItem radioButtonMenuItem_1 = new JRadioButtonMenuItem("1");
		radioButtonMenuItem_1.setSelected(true);
		mnRightSide.add(radioButtonMenuItem_1);

		radioButtonMenuItem_1.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("R1").setOrigin(e).build()));
		rightDivisionSelection.add(radioButtonMenuItem_1);
		
		JRadioButtonMenuItem radioButtonMenuItem_2 = new JRadioButtonMenuItem("2");
		radioButtonMenuItem_2.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("R2").setOrigin(e).build()));
		mnRightSide.add(radioButtonMenuItem_2);
		rightDivisionSelection.add(radioButtonMenuItem_2);
		
		JRadioButtonMenuItem radioButtonMenuItem_3 = new JRadioButtonMenuItem("3");
		mnRightSide.add(radioButtonMenuItem_3);
		radioButtonMenuItem_3.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("R3").setOrigin(e).build()));
		rightDivisionSelection.add(radioButtonMenuItem_3);
		
		JMenuItem mntmCetnerLine = new JMenuItem("Cetner Line");
		mntmCetnerLine.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.DIVIDER_CHANGE).setOrigin(e).build()));
		mnArrangeGrid.add(mntmCetnerLine);
		
		showMenu = new JMenu("Show");
		showMenu.setIcon(getMenuIcon("/gui/present.png"));
		viewMenu.add(showMenu);
		
		InternalEventHandler.registerInternalListener(new EmbeddedFrameListener(){
			@Override
			public void frameChanged(EmbeddedFrameEvent e) {
				if (e.getAction().equals(EmbeddedFrameEvent.Action.ADDED)){
					showMenu.add(new FrameShowMenu(e.getComponent()));
				}
				else if (e.getAction().equals(EmbeddedFrameEvent.Action.CLOSED)){
					for (java.awt.Component menuitem : showMenu.getMenuComponents()){
						try {
							FrameShowMenu menu = (FrameShowMenu)menuitem;
							if (menu.getFrame() == e.getComponent()){
								showMenu.remove(menuitem);
							}
						}
						catch (ClassCastException ex){
							continue;
						}
					}
				}
			}
		});
		
		JMenu mnTools = new JMenu("Tools");
		mnTools.setIcon(getMenuIcon("/gui/tools.png"));
		optionsMenu.add(mnTools);
		
		JMenu mnVolume = new JMenu("Volume");
		mnTools.add(mnVolume);
		
		ButtonGroup volumeControls = new ButtonGroup();
		
		JRadioButtonMenuItem mntmHigh = new JRadioButtonMenuItem("High");
		mntmHigh.setIcon(getMenuIcon("/gui/speaker_loud.png"));
		mntmHigh.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.VOLUME_CHANGE).setValue("HIGH").setOrigin(e).build()));
		mntmHigh.setSelected(true);
		mnVolume.add(mntmHigh);
		volumeControls.add(mntmHigh);
		
		JRadioButtonMenuItem mntmLow = new JRadioButtonMenuItem("Low");
		mntmLow.setIcon(getMenuIcon("/gui/speaker_quiet.png"));
		mntmLow.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.VOLUME_CHANGE).setValue("LOW").setOrigin(e).build()));
		mnVolume.add(mntmLow);
		volumeControls.add(mntmLow);
		
		JRadioButtonMenuItem mntmMute = new JRadioButtonMenuItem("Mute");
		mntmMute.setIcon(getMenuIcon("/gui/speaker_mute.png"));
		mntmMute.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.VOLUME_CHANGE).setValue("MUTE").setOrigin(e).build()));
		mnVolume.add(mntmMute);
		volumeControls.add(mntmMute);
		
		optionsMenu.addSeparator();
		
		JMenuItem mntmSettings = new JMenuItem("Settings");
		mntmSettings.setIcon(getMenuIcon("/gui/gear_thick.png"));
		mntmSettings.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.SETTINGS).setOrigin(e).build()));
		optionsMenu.add(mntmSettings);
	}

}
