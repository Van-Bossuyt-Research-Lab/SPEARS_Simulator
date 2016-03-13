package com.csm.rover.simulator.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.csm.rover.simulator.ui.events.EmbeddedFrameEvent;
import com.csm.rover.simulator.ui.events.EmbeddedFrameListener;
import com.csm.rover.simulator.ui.events.InternalEventHandler;

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
		fileMenu.add(mntmOpen);
		
		JMenuItem mntmNewSimulation = new JMenuItem("New Simulation");
		mntmNewSimulation.setIcon(getMenuIcon("/gui/add.png"));
		fileMenu.add(mntmNewSimulation);
		
		fileMenu.addSeparator();
		
		JMenuItem mntmViewLogFile = new JMenuItem("View Log File");
		mntmViewLogFile.setIcon(getMenuIcon("/gui/file.png"));
		fileMenu.add(mntmViewLogFile);
		
		JMenu mnViewPlatformLog = new JMenu("View Platform Log");
		fileMenu.add(mnViewPlatformLog);
		
		fileMenu.addSeparator();
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setIcon(getMenuIcon("/gui/power.png"));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
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
		leftDivisionSelection.add(radioButtonMenuItem_5);
		
		JRadioButtonMenuItem radioButtonMenuItem_4 = new JRadioButtonMenuItem("1");
		radioButtonMenuItem_4.setSelected(true);
		mnLeftSide.add(radioButtonMenuItem_4);
		leftDivisionSelection.add(radioButtonMenuItem_4);
		
		JRadioButtonMenuItem radioButtonMenuItem_6 = new JRadioButtonMenuItem("2");
		mnLeftSide.add(radioButtonMenuItem_6);
		leftDivisionSelection.add(radioButtonMenuItem_6);
		
		JRadioButtonMenuItem radioButtonMenuItem_7 = new JRadioButtonMenuItem("3");
		mnLeftSide.add(radioButtonMenuItem_7);
		leftDivisionSelection.add(radioButtonMenuItem_7);
		
		JMenu mnRightSide = new JMenu("Right Side");
		mnDivisions.add(mnRightSide);
		
		ButtonGroup rightDivisionSelection = new ButtonGroup();
		
		JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem("0");
		mnRightSide.add(radioButtonMenuItem);
		rightDivisionSelection.add(radioButtonMenuItem);
		
		JRadioButtonMenuItem radioButtonMenuItem_1 = new JRadioButtonMenuItem("1");
		radioButtonMenuItem_1.setSelected(true);
		mnRightSide.add(radioButtonMenuItem_1);
		rightDivisionSelection.add(radioButtonMenuItem_1);
		
		JRadioButtonMenuItem radioButtonMenuItem_2 = new JRadioButtonMenuItem("2");
		mnRightSide.add(radioButtonMenuItem_2);
		rightDivisionSelection.add(radioButtonMenuItem_2);
		
		JRadioButtonMenuItem radioButtonMenuItem_3 = new JRadioButtonMenuItem("3");
		mnRightSide.add(radioButtonMenuItem_3);
		rightDivisionSelection.add(radioButtonMenuItem_3);
		
		JMenuItem mntmCetnerLine = new JMenuItem("Cetner Line");
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
					for (java.awt.Component menuitem : showMenu.getComponents()){
						try {
							FrameShowMenu menu = (FrameShowMenu)menuitem;
							if (menu.getFrame() == e.getComponent()){
								showMenu.remove(menu);
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
		
		JMenuItem mntmHigh = new JMenuItem("High");
		mntmHigh.setIcon(getMenuIcon("/gui/speaker_loud.png"));
		mnVolume.add(mntmHigh);
		
		JMenuItem mntmLow = new JMenuItem("Low");
		mntmLow.setIcon(getMenuIcon("/gui/speaker_quiet.png"));
		mnVolume.add(mntmLow);
		
		JMenuItem mntmMute = new JMenuItem("Mute");
		mntmMute.setIcon(getMenuIcon("/gui/speaker_mute.png"));
		mnVolume.add(mntmMute);
		
		optionsMenu.addSeparator();
		
		JMenuItem mntmSettings = new JMenuItem("Settings");
		mntmSettings.setIcon(getMenuIcon("/gui/gear_thick.png"));
		optionsMenu.add(mntmSettings);
	}

}
