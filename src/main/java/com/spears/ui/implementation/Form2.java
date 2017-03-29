/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.ui.implementation;

import com.spears.environments.PlatformEnvironment;
import com.spears.platforms.Platform;
import com.spears.ui.visual.Application;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.List;
import java.util.Map;
//import SoundPlayer;
//import SpearsSound;

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
