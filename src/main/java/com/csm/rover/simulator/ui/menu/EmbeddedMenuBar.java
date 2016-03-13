package com.csm.rover.simulator.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class EmbeddedMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1996909612935260422L;

	public EmbeddedMenuBar() {
		JMenu mnFile = new JMenu("File");
		add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnView = new JMenu("View");
		add(mnView);
		
		JMenu mnNew = new JMenu("New");
		mnView.add(mnNew);
		
		JMenuItem mntmObserver = new JMenuItem("Observer");
		mnNew.add(mntmObserver);
		
		JMenuItem mntmPuppyPicture = new JMenuItem("Puppy Picture");
		mnNew.add(mntmPuppyPicture);
		
		JMenu mnOptions = new JMenu("Options");
		add(mnOptions);
		
		JCheckBoxMenuItem chckbxmntmThing = new JCheckBoxMenuItem("Thing 1");
		mnOptions.add(chckbxmntmThing);
		
		JCheckBoxMenuItem chckbxmntmThing_1 = new JCheckBoxMenuItem("Thing 2");
		mnOptions.add(chckbxmntmThing_1);
	}

}
