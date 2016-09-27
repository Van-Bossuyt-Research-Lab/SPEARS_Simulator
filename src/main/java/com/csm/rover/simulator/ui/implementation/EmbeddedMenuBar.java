package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.ui.sound.SoundPlayer;
import com.csm.rover.simulator.ui.sound.SpearsSound;
import com.csm.rover.simulator.ui.sound.VolumeChangeListener;
import com.csm.rover.simulator.ui.visual.MainMenu;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

import static com.csm.rover.simulator.ui.implementation.ImageFunctions.getMenuIcon;

class EmbeddedMenuBar extends JMenuBar implements MainMenu {

	private static final long serialVersionUID = 1996909612935260422L;

	private JMenu fileMenu, viewMenu, optionsMenu;
	
	private JMenu newMenu;
	private JMenu showMenu;

    private Optional<Runnable> exitOp = Optional.empty();
    private Optional<VolumeChangeListener> volumeListener = Optional.empty();
	
	EmbeddedMenuBar() {
		fileMenu = new JMenu("File");
		this.add(fileMenu);
		
		viewMenu = new JMenu("View");
		this.add(viewMenu);
		
		optionsMenu = new JMenu("Options");
		this.add(optionsMenu);
		
		initialize();
		
		createInternalFeedback();
	}

	private void initialize(){
		
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
		mntmExit.addActionListener((ActionEvent e) -> {if (exitOp.isPresent()) exitOp.get().run(); });
		fileMenu.add(mntmExit);
		
		newMenu = new NewFrameMenu();
		newMenu.setText("New Window");
		newMenu.setIcon(getMenuIcon("/gui/user_id.png"));
		viewMenu.add(newMenu);
		
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

        switch (UiConfiguration.getDesktopDivsLeft()){
            case 0:
                radioButtonMenuItem_5.setSelected(true);
                break;
            case 1:
                radioButtonMenuItem_4.setSelected(true);
                break;
            case 2:
                radioButtonMenuItem_6.setSelected(true);
                break;
            case 3:
                radioButtonMenuItem_7.setSelected(true);
                break;
        }
		
		JMenu mnRightSide = new JMenu("Right Side");
		mnDivisions.add(mnRightSide);
		
		ButtonGroup rightDivisionSelection = new ButtonGroup();
		
		JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem("0");
		radioButtonMenuItem.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("R0").setOrigin(e).build()));
        mnRightSide.add(radioButtonMenuItem);
        rightDivisionSelection.add(radioButtonMenuItem);
		
		JRadioButtonMenuItem radioButtonMenuItem_1 = new JRadioButtonMenuItem("1");
		mnRightSide.add(radioButtonMenuItem_1);
		radioButtonMenuItem_1.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("R1").setOrigin(e).build()));
		rightDivisionSelection.add(radioButtonMenuItem_1);
		
		JRadioButtonMenuItem radioButtonMenuItem_2 = new JRadioButtonMenuItem("2");
		radioButtonMenuItem_2.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("R2").setOrigin(e).build()));
		mnRightSide.add(radioButtonMenuItem_2);
		rightDivisionSelection.add(radioButtonMenuItem_2);
		
		JRadioButtonMenuItem radioButtonMenuItem_3 = new JRadioButtonMenuItem("3");
		radioButtonMenuItem_3.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.GRID_CHANGE).setValue("R3").setOrigin(e).build()));
        mnRightSide.add(radioButtonMenuItem_3);
        rightDivisionSelection.add(radioButtonMenuItem_3);

        switch (UiConfiguration.getDesktopDivsRight()){
            case 0:
                radioButtonMenuItem.setSelected(true);
                break;
            case 1:
                radioButtonMenuItem_1.setSelected(true);
                break;
            case 2:
                radioButtonMenuItem_2.setSelected(true);
                break;
            case 3:
                radioButtonMenuItem_3.setSelected(true);
                break;
        }
		
		JMenuItem mntmCetnerLine = new JMenuItem("Center Line");
		mntmCetnerLine.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.DIVIDER_CHANGE).setOrigin(e).build()));
		mnArrangeGrid.add(mntmCetnerLine);
		
		showMenu = new JMenu("Show");
		showMenu.setIcon(getMenuIcon("/gui/present.png"));
		viewMenu.add(showMenu);
		
		InternalEventHandler.registerInternalListener((EmbeddedFrameEvent e) -> {
            if (e.getAction().equals(EmbeddedFrameEvent.Action.ADDED)){
                showMenu.add(new FrameShowMenu(e.getComponent()));
            }
            else if (e.getAction().equals(EmbeddedFrameEvent.Action.CLOSED)) {
                for (java.awt.Component menuitem : showMenu.getMenuComponents()) {
                    try {
                        FrameShowMenu menu = (FrameShowMenu) menuitem;
                        if (menu.getFrame() == e.getComponent()) {
                            showMenu.remove(menuitem);
                        }
                    }
                    catch (ClassCastException ex) {}
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
		mntmHigh.addActionListener((ActionEvent e) -> fireVolumeChange(SoundPlayer.Volume.HIGH));
		mnVolume.add(mntmHigh);
		volumeControls.add(mntmHigh);
		
		JRadioButtonMenuItem mntmLow = new JRadioButtonMenuItem("Low");
		mntmLow.setIcon(getMenuIcon("/gui/speaker_quiet.png"));
		mntmLow.addActionListener((ActionEvent e) -> fireVolumeChange(SoundPlayer.Volume.LOW));
		mnVolume.add(mntmLow);
		volumeControls.add(mntmLow);
		
		JRadioButtonMenuItem mntmMute = new JRadioButtonMenuItem("Mute");
		mntmMute.setIcon(getMenuIcon("/gui/speaker_mute.png"));
		mntmMute.addActionListener((ActionEvent e) -> fireVolumeChange(SoundPlayer.Volume.MUTE));
		mnVolume.add(mntmMute);
		volumeControls.add(mntmMute);

        switch (UiConfiguration.getVolume()){
            case HIGH:
                mntmHigh.setSelected(true);
                break;
            case LOW:
                mntmLow.setSelected(true);
                break;
            case MUTE:
                mntmMute.setSelected(true);
                break;
        }
		
		optionsMenu.addSeparator();
		
		JMenuItem mntmSettings = new JMenuItem("Settings");
		mntmSettings.setIcon(getMenuIcon("/gui/gear_thick.png"));
		mntmSettings.addActionListener((ActionEvent e) -> InternalEventHandler.fireInternalEvent(MenuCommandEvent.builder().setAction(MenuCommandEvent.Action.SETTINGS).setOrigin(e).build()));
		optionsMenu.add(mntmSettings);

        this.add(Box.createHorizontalGlue());
        this.add(Box.createHorizontalGlue());

        JMenu minimizeBtn = new JMenu("__");
        minimizeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SoundPlayer.playSound(SpearsSound.DROPPING);
                ((JFrame)UiFactory.getApplication()).setState(JFrame.ICONIFIED);
            }
        });
        this.add(minimizeBtn);

        JMenu closeBtn = new JMenu("\u2715");
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (exitOp.isPresent()){
                    SoundPlayer.playSound(SpearsSound.GOODBYE);
                    exitOp.get().run();
                }
            }
        });
        this.add(closeBtn);
	}

    @Override
    public void setCloseOperation(Runnable exit){
        exitOp = Optional.of(exit);
    }

    @Override
    public void setVolumeListener(VolumeChangeListener listen){
        volumeListener = Optional.of(listen);
        listen.changeVolume(UiConfiguration.getVolume());
    }

    private void fireVolumeChange(SoundPlayer.Volume level){
        UiConfiguration.setVolume(level);
        if (volumeListener.isPresent()){
            volumeListener.get().changeVolume(level);
        }
    }

	private void createInternalFeedback() {
		Component[] menus = new Component[this.getMenuCount()];
		for (int i = 0; i < menus.length; i++){
			menus[i] = this.getMenu(i);
		}
		addButtonClicks(menus);
	}
	
	private void addButtonClicks(Component[] menus){
		for (Component menu : menus){
			if (menu instanceof JMenu){
				addButtonClicks(((JMenu)menu).getMenuComponents());
			}
			else if (menu instanceof JMenuItem){
                JMenuItem jmenu = ((JMenuItem)menu);
				if (!jmenu.getText().equals("Exit")) {
                    jmenu.addActionListener((ActionEvent) -> SoundPlayer.playSound(SpearsSound.BEEP_LOW));
                }
                else {
                    jmenu.addActionListener((ActionEvent e) -> SoundPlayer.playSound(SpearsSound.GOODBYE));
                }
			}
		}
	}

}
