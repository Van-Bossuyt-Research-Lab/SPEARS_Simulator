package com.csm.rover.simulator.ui;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JFrame;

import java.awt.Color;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JRadioButtonMenuItem;

import com.csm.rover.simulator.ui.desktop.EnbeddedDesktop;
import com.csm.rover.simulator.ui.frame.EmbeddedFrame;
import com.csm.rover.simulator.ui.menu.EmbeddedMenuBar;

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
		
		EmbeddedFrame internalFrame = new EmbeddedFrame();
		internalFrame.setTitle("New EmbeddedFrame");
		internalFrame.setBounds(72, 42, 276, 331);
		desktopPane.add(internalFrame);
		
		EmbeddedFrame internalFrame_1 = new EmbeddedFrame();
		internalFrame_1.setTitle("New EmbeddedFrame");
		internalFrame_1.setBounds(461, 67, 276, 331);
		desktopPane.add(internalFrame_1);
		internalFrame_1.setVisible(true);
		internalFrame.setVisible(true);
	}
	
}
