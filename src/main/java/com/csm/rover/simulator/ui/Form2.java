package com.csm.rover.simulator.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JDesktopPane;

import java.awt.Color;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JRadioButtonMenuItem;

import com.csm.rover.simulator.ui.desktop.EnbeddedDesktop;
import com.csm.rover.simulator.ui.frame.EmbeddedFrame;

public class Form2 extends JFrame {

	public Form2() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/SPEARS icon.png")));
		setUndecorated(true);
		setResizable(false);
		setTitle("Form 2");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 843, 476);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open Simulation");
		mntmOpen.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/open_folder.png"))));
		mnFile.add(mntmOpen);
		
		JMenuItem mntmNewSimulation = new JMenuItem("New Simulation");
		mntmNewSimulation.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/add.png"))));
		mnFile.add(mntmNewSimulation);
		
		mnFile.addSeparator();
		
		JMenuItem mntmViewLogFile = new JMenuItem("View Log File");
		mntmViewLogFile.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/file.png"))));
		mnFile.add(mntmViewLogFile);
		
		JMenu mnViewPlatformLog = new JMenu("View Platform Log");
		mnFile.add(mnViewPlatformLog);
		
		mnFile.addSeparator();
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/power.png"))));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JMenu mnNew = new JMenu("New Window");
		mnNew.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/user_id.png"))));
		mnView.add(mnNew);
		
		JMenuItem mntmObserver = new JMenuItem("Observer");
		mnNew.add(mntmObserver);
		
		JMenuItem mntmPuppyPicture = new JMenuItem("Puppy Picture");
		mnNew.add(mntmPuppyPicture);
		
		mnView.addSeparator();
		
		JMenu mnArrangeGrid = new JMenu("Arrange Grid");
		mnArrangeGrid.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/grid.png"))));
		mnView.add(mnArrangeGrid);
		
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
		
		JMenu mnShow = new JMenu("Show");
		mnShow.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/present.png"))));
		mnView.add(mnShow);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenu mnTools = new JMenu("Tools");
		mnTools.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/tools.png"))));
		mnOptions.add(mnTools);
		
		JMenu mnVolume = new JMenu("Volume");
		mnTools.add(mnVolume);
		
		JMenuItem mntmHigh = new JMenuItem("High");
		mntmHigh.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/speaker_loud.png"))));
		mnVolume.add(mntmHigh);
		
		JMenuItem mntmLow = new JMenuItem("Low");
		mntmLow.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/speaker_quiet.png"))));
		mnVolume.add(mntmLow);
		
		JMenuItem mntmMute = new JMenuItem("Mute");
		mntmMute.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/speaker_mute.png"))));
		mnVolume.add(mntmMute);
		
		mnOptions.addSeparator();
		
		JMenuItem mntmSettings = new JMenuItem("Settings");
		mntmSettings.setIcon(resizeMenuIcon(new ImageIcon(getClass().getResource("/gui/gear_thick.png"))));
		mnOptions.add(mntmSettings);
			
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
	
	protected ImageIcon resizeMenuIcon(Icon image){
		return resize(image, 24, 24);
	}
	
	protected ImageIcon resize(Icon image, int width, int height) {
		try {
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		    Graphics2D g = bi.createGraphics();
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		            RenderingHints.VALUE_ANTIALIAS_ON);
	        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
	        g.setComposite(comp);
	        g.drawImage(((ImageIcon) (image)).getImage(), 0, 0, width, height, null);
		    g.dispose();
		    return new ImageIcon(bi);
		}
		catch (Exception i) {
			return (ImageIcon) image;
		}
	}
}
