package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.ui.visual.Application;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.List;
import java.util.Map;
//import com.csm.rover.simulator.ui.sound.SoundPlayer;
//import com.csm.rover.simulator.ui.sound.SpearsSound;

class Form2 extends JFrame implements Application {

	private static final long serialVersionUID = -9008675264187922065L;

    final EmbeddedDesktop desktop;
	private EmbeddedMenuBar menuBar;

	Form2(EmbeddedMenuBar menuBar) {
//		SoundPlayer.playSound(SpearsSound.HELLO);  TODO add back later so volume controls work correctly
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/SPEARS icon.png")));
        setLocation(0, 0);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setUndecorated(true);
		setResizable(false);
		setTitle("SPEARS Simulator");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.menuBar = menuBar;
		setJMenuBar(this.menuBar);
			
		desktop = new EmbeddedDesktop();
		desktop.setBackground(new Color(250, 255, 255));
		setContentPane(desktop);
	}

	@Override
	public void start(Map<String, PlatformEnvironment> enviros, Map<String, List<Platform>> platforms) {
		menuBar.setPlatfroms(enviros, platforms);
	}

}
