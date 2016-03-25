package com.csm.rover.simulator.ui;

import java.awt.Toolkit;

import javax.swing.JFrame;

import java.awt.Color;

import com.csm.rover.simulator.ui.desktop.EnbeddedDesktop;
import com.csm.rover.simulator.ui.events.InternalEventCheckGate;
import com.csm.rover.simulator.ui.events.InternalEventHandler;
import com.csm.rover.simulator.ui.events.MenuCommandEvent;
import com.csm.rover.simulator.ui.menu.EmbeddedMenuBar;
import com.csm.rover.simulator.ui.sound.SoundPlayer;
import com.csm.rover.simulator.ui.sound.SpearsSound;

public class Form2 extends JFrame {

	private static final long serialVersionUID = -9008675264187922065L;

	public Form2() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/SPEARS icon.png")));
		setUndecorated(true);
		setResizable(false);
		setTitle("Form 2");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 843, 476);
		
		EmbeddedMenuBar menuBar = new EmbeddedMenuBar();
		setJMenuBar(menuBar);
			
		EnbeddedDesktop desktopPane = new EnbeddedDesktop();
		desktopPane.setBackground(new Color(250, 255, 255));
		setContentPane(desktopPane);
		
		InternalEventHandler.registerInternalListener(InternalEventCheckGate.responseWith(() -> SoundPlayer.playSound(SpearsSound.ERROR)).forAction(MenuCommandEvent.Action.SETTINGS));
		
	}
	
}
