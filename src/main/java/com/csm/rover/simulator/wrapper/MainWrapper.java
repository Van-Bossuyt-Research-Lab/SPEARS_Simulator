package com.csm.rover.simulator.wrapper;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.csm.rover.simulator.visual.Panel;
import com.csm.rover.simulator.visual.ZList;

import javax.swing.JTabbedPane;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;

import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.satellite.SatelliteObject;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;

import com.csm.rover.simulator.objects.MapFileFilter;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.UIManager;
import javax.swing.JCheckBox;

public class MainWrapper extends Panel {

	static final int MAX_MAP_LAYERS = 10;
	
	JTabbedPane tabbedPane;
	JPanel CreateNewPnl;
		private JLabel RovAddTtl;
			private JLabel RovDriveModelLbl;
			ZList RovDriveModelList;
			private JLabel RovAutonomusCodeLbl;
			ZList RovAutonomusCodeList;
			private JButton RovAddBtn;
			private JButton RovRemoveBtn;
			private JLabel RoversListLbl;
			ZList RoverList;
		private JLabel SatAddTtl;
			private JLabel SatDriveModelLbl;
			ZList SatDriveModelList;
			private JLabel SatAutonomusCodeLbl;
			ZList SatAutonomusCodeList;
			private JButton SatAddBtn;
			private JButton SatRemoveBtn;
			private JLabel SatelliteListLbl;
			ZList SatelliteList;
		private JLabel MapConfigTtl;
			JTabbedPane TypeSelector;
			private JPanel SelectionPlasmaPnl;
				JSlider MapRoughSlider;
				private JLabel MapRoughnessLbl;
				private JLabel MapSmoothLbl;
				private JLabel MapRoughLbl;
				private JLabel MapDetailLbl;
				private JLabel HazardDensityLbl;
				private JLabel TargetDensityLbl;
				private JLabel MapSizeLbl;
				JSpinner MapDetailSpnr;
				JSpinner MapSizeSpnr;
				JSpinner HazardDensitySpnr;
				JSpinner TargetDensitySpnr;
				JCheckBox ValuedTargetsChk;
				JCheckBox ValuedHazardsChk;
			private JPanel SelectionFilePnl;
				private JLabel FileLocLbl;
				JTextField FileLocTxt;
				private JButton FileLocBtn;
		private JLabel TimingOptionsTtl;
			JCheckBox AccelChk;
			private JLabel RuntimeLbl;
			JSpinner RuntimeSpnr;
			private JLabel RunUnitsLbl;
			JLabel RunAltLbl;
		private JLabel SaveConfigLbl;
		JButton StartBtn;
	JPanel RuntimePnl;
		private JLabel SerialDisplayTitle;
		private JLabel SerialAvialableLbl;
		private JScrollPane SerialDisplayScroll;
		private JPanel SerialDisplayPnl;
		JSlider SerialHistorySlider;
		JLabel SerialGroundLbl;
		JLabel SerialGroundAvailableLbl;
		JLabel[] SerialRoverLbls;
		JLabel[] SerialRoverAvailableLbls;
		JLabel[] SerialSatelliteLbls;
		JLabel[] SerialSatelliteAvailableLbls;
	
	public MainWrapper(Dimension size) {
		super(size /*new Dimension(1024, 800)*/, "Wrapper Display");
		initalize();
		align();
	}
	
	private void initalize(){
		tabbedPane = new JTabbedPane();
		tabbedPane.setEnabled(false);
		tabbedPane.setTabPlacement(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Trebuchet MS", Font.BOLD, 21));
		tabbedPane.setBounds(10, super.getTopOfPage()+5, this.getWidth()-20, super.getWorkingHeight()-10);
		add(tabbedPane);
		
		CreateNewPnl = new JPanel();
		tabbedPane.addTab("New Simulation", null, CreateNewPnl, null);
		CreateNewPnl.setLayout(null);
		
		RovAddTtl = new JLabel("Adding Rovers");
		RovAddTtl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		RovAddTtl.setBounds(10, 11, 118, 21);
		CreateNewPnl.add(RovAddTtl);
		
		RovDriveModelLbl = new JLabel("Physics Model");
		RovDriveModelLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		RovDriveModelLbl.setBounds(10, 43, 118, 21);
		CreateNewPnl.add(RovDriveModelLbl);
		
		RovDriveModelList = new ZList();
		RovDriveModelList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		RovDriveModelList.setBounds(10, 63, 200, 250);
		CreateNewPnl.add(RovDriveModelList);
		
		RovAutonomusCodeLbl = new JLabel("Autonomous Logic");
		RovAutonomusCodeLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		RovAutonomusCodeLbl.setBounds(220, 43, 142, 21);
		CreateNewPnl.add(RovAutonomusCodeLbl);
		
		RovAutonomusCodeList = new ZList();
		RovAutonomusCodeList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		RovAutonomusCodeList.setBounds(220, 63, 200, 250);
		CreateNewPnl.add(RovAutonomusCodeList);
		
		RovAddBtn = new JButton("<HTML><center>=><br>Add</center></HTML>");
		RovAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Access.CODE.addRoverToList();
			}
		});
		RovAddBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		RovAddBtn.setBounds(430, 152, 80, 64);
		CreateNewPnl.add(RovAddBtn);
		
		RovRemoveBtn = new JButton("<HTML><center>&#60=<br>Remove</center></HTML>");
		RovRemoveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Access.CODE.removeRoverFromList();
			}
		});
		RovRemoveBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		RovRemoveBtn.setBounds(430, 152, 80, 64);
		CreateNewPnl.add(RovRemoveBtn);
		
		RoversListLbl = new JLabel("Rovers");
		RoversListLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		RoversListLbl.setBounds(510, 43, 142, 21);
		CreateNewPnl.add(RoversListLbl);
		
		RoverList = new ZList();
		RoverList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		RoverList.setBounds(510, 63, 200, 250);
		CreateNewPnl.add(RoverList);
		
		SatAddTtl = new JLabel("Adding Satellites");
		SatAddTtl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		SatAddTtl.setBounds(10, 460, 142, 21);
		CreateNewPnl.add(SatAddTtl);
		
		SatDriveModelLbl = new JLabel("Physics Model");
		SatDriveModelLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		SatDriveModelLbl.setBounds(10, 492, 118, 21);
		CreateNewPnl.add(SatDriveModelLbl);
		
		SatDriveModelList = new ZList();
		SatDriveModelList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		SatDriveModelList.setBounds(10, 512, 200, 250);
		CreateNewPnl.add(SatDriveModelList);
		
		SatAutonomusCodeList = new ZList();
		SatAutonomusCodeList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		SatAutonomusCodeList.setBounds(220, 512, 200, 250);
		CreateNewPnl.add(SatAutonomusCodeList);
		
		SatAutonomusCodeLbl = new JLabel("Autonomous Logic");
		SatAutonomusCodeLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		SatAutonomusCodeLbl.setBounds(220, 492, 142, 21);
		CreateNewPnl.add(SatAutonomusCodeLbl);
		
		SatelliteList = new ZList();
		SatelliteList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		SatelliteList.setBounds(510, 512, 200, 250);
		CreateNewPnl.add(SatelliteList);
		
		SatAddBtn = new JButton("<HTML><center>=><br>Add</center></HTML>");
		SatAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Access.CODE.addSatelliteToList();
			}
		});
		SatAddBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		SatAddBtn.setBounds(430, 607, 80, 64);
		CreateNewPnl.add(SatAddBtn);
		
		SatRemoveBtn = new JButton("<HTML><center>&#60=<br>Remove</center></HTML>");
		SatRemoveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Access.CODE.removeSatelliteFromList();
			}
		});
		SatRemoveBtn.setHorizontalTextPosition(SwingConstants.CENTER);
		SatRemoveBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		SatRemoveBtn.setBounds(430, 607, 80, 64);
		CreateNewPnl.add(SatRemoveBtn);
		
		SatelliteListLbl = new JLabel("Satellites");
		SatelliteListLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		SatelliteListLbl.setBounds(510, 492, 142, 21);
		CreateNewPnl.add(SatelliteListLbl);
		
		MapConfigTtl = new JLabel("Congiure Map");
		MapConfigTtl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		MapConfigTtl.setBounds(912, 10, 118, 21);
		CreateNewPnl.add(MapConfigTtl);
		
		StartBtn = new JButton("Start Simulation");
		StartBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Access.CODE.beginSimulation(Access.CODE.getConfigurationFromForm());
			}
		});
		StartBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
		StartBtn.setBounds(1665, 874, 220, 55);
		CreateNewPnl.add(StartBtn);
		
		TypeSelector = new JTabbedPane(JTabbedPane.TOP);
		TypeSelector.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		TypeSelector.setBounds(740, 41, 636, 267);
		CreateNewPnl.add(TypeSelector);
		
		SelectionPlasmaPnl = new JPanel();
		TypeSelector.addTab("Plasma Fractal", null, SelectionPlasmaPnl, null);
		SelectionPlasmaPnl.setLayout(null);
		
		MapRoughSlider = new JSlider();
		MapRoughSlider.setBounds(162, 43, 400, 30);
		SelectionPlasmaPnl.add(MapRoughSlider);
		MapRoughSlider.setMaximum(700);
		MapRoughSlider.setMajorTickSpacing(100);
		MapRoughSlider.setPaintTicks(true);
		MapRoughSlider.setValue(200);
		
		MapSmoothLbl = new JLabel("Smooth");
		MapSmoothLbl.setBounds(91, 43, 61, 21);
		SelectionPlasmaPnl.add(MapSmoothLbl);
		MapSmoothLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		MapSmoothLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		
		MapRoughnessLbl = new JLabel("Map Roughness:");
		MapRoughnessLbl.setBounds(10, 11, 142, 21);
		SelectionPlasmaPnl.add(MapRoughnessLbl);
		MapRoughnessLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		MapRoughnessLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		MapRoughnessLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		
		MapRoughLbl = new JLabel("Rough");
		MapRoughLbl.setBounds(572, 43, 43, 21);
		SelectionPlasmaPnl.add(MapRoughLbl);
		MapRoughLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		
		MapDetailSpnr = new JSpinner();
		MapDetailSpnr.setBounds(462, 105, 80, 25);
		SelectionPlasmaPnl.add(MapDetailSpnr);
		MapDetailSpnr.setModel(new SpinnerNumberModel(3, 1, 9, 1));
		MapDetailSpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		
		MapDetailLbl = new JLabel("Map Detail:");
		MapDetailLbl.setBounds(310, 107, 142, 21);
		SelectionPlasmaPnl.add(MapDetailLbl);
		MapDetailLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		MapDetailLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		MapDetailLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		
		HazardDensityLbl = new JLabel("Hazard Density:");
		HazardDensityLbl.setBounds(310, 156, 142, 21);
		SelectionPlasmaPnl.add(HazardDensityLbl);
		HazardDensityLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		HazardDensityLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		HazardDensityLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		
		HazardDensitySpnr = new JSpinner();
		HazardDensitySpnr.setBounds(462, 154, 80, 25);
		SelectionPlasmaPnl.add(HazardDensitySpnr);
		HazardDensitySpnr.setModel(new SpinnerNumberModel(0.4, 0.0, 100.0, 0.01));
		HazardDensitySpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		
		TargetDensitySpnr = new JSpinner();
		TargetDensitySpnr.setBounds(162, 154, 80, 25);
		SelectionPlasmaPnl.add(TargetDensitySpnr);
		TargetDensitySpnr.setModel(new SpinnerNumberModel(4., 0.0, 100.0, 0.1));
		TargetDensitySpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		
		TargetDensityLbl = new JLabel("Target Density:");
		TargetDensityLbl.setBounds(10, 156, 142, 21);
		SelectionPlasmaPnl.add(TargetDensityLbl);
		TargetDensityLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		TargetDensityLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		TargetDensityLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		
		MapSizeLbl = new JLabel("Map Size:");
		MapSizeLbl.setBounds(10, 107, 142, 21);
		SelectionPlasmaPnl.add(MapSizeLbl);
		MapSizeLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		MapSizeLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		MapSizeLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		
		MapSizeSpnr = new JSpinner();
		MapSizeSpnr.setBounds(162, 105, 80, 25);
		SelectionPlasmaPnl.add(MapSizeSpnr);
		MapSizeSpnr.setModel(new SpinnerNumberModel(10, 7, 13, 1));
		MapSizeSpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		
		ValuedTargetsChk = new JCheckBox("Use Valued Targets");
		ValuedTargetsChk.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		ValuedTargetsChk.setBounds(73, 196, 169, 23);
		SelectionPlasmaPnl.add(ValuedTargetsChk);
		
		ValuedHazardsChk = new JCheckBox("Use Valued Hazards");
		ValuedHazardsChk.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		ValuedHazardsChk.setBounds(373, 196, 169, 23);
		SelectionPlasmaPnl.add(ValuedHazardsChk);
		
		SelectionFilePnl = new JPanel();
		TypeSelector.addTab("From File", null, SelectionFilePnl, null);
		SelectionFilePnl.setLayout(null);
		
		FileLocLbl = new JLabel("File Location:");
		FileLocLbl.setBounds(10, 13, 142, 21);
		SelectionFilePnl.add(FileLocLbl);
		FileLocLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		FileLocLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		
		FileLocTxt = new JTextField();
		FileLocTxt.setBounds(162, 11, 338, 25);
		SelectionFilePnl.add(FileLocTxt);
		FileLocTxt.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		FileLocTxt.setColumns(10);
		
		FileLocBtn = new JButton("Browse...");
		FileLocBtn.setBounds(510, 13, 105, 25);
		SelectionFilePnl.add(FileLocBtn);
		FileLocBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser finder = new JFileChooser();
				finder.setFileFilter(new MapFileFilter());
				finder.setApproveButtonText("Choose");
				int option = finder.showOpenDialog(getParent());
				if (option == JFileChooser.APPROVE_OPTION){
					FileLocTxt.setText(finder.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		SaveConfigLbl = new JLabel("<HTML><U>Save Run Configuration</U></HTML>");
		SaveConfigLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Access.CODE.saveCurrentconfiguration();
			}
		});
		SaveConfigLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		SaveConfigLbl.setForeground(UIManager.getColor("TabbedPane.darkShadow"));
		SaveConfigLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		SaveConfigLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		SaveConfigLbl.setBounds(1729, 842, 156, 21);
		CreateNewPnl.add(SaveConfigLbl);
		
		TimingOptionsTtl = new JLabel("Timing Options");
		TimingOptionsTtl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		TimingOptionsTtl.setBounds(740, 369, 125, 21);
		CreateNewPnl.add(TimingOptionsTtl);
		
		AccelChk = new JCheckBox("Accelerate Simulation");
		AccelChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RuntimeLbl.setEnabled(AccelChk.isSelected());
				RuntimeSpnr.setEnabled(AccelChk.isSelected());
				RunUnitsLbl.setEnabled(AccelChk.isSelected());
				RunAltLbl.setEnabled(AccelChk.isSelected());
			}
		});
		AccelChk.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		AccelChk.setBounds(750, 397, 181, 23);
		CreateNewPnl.add(AccelChk);
		
		RuntimeLbl = new JLabel("Run Time:");
		RuntimeLbl.setEnabled(false);
		RuntimeLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		RuntimeLbl.setHorizontalTextPosition(SwingConstants.LEADING);
		RuntimeLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		RuntimeLbl.setBounds(740, 427, 90, 21);
		CreateNewPnl.add(RuntimeLbl);
		
		RuntimeSpnr = new JSpinner();
		RuntimeSpnr.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				RunAltLbl.setText("(" + (int)((Integer)RuntimeSpnr.getValue()*60/Globals.getTimeAccelerant()*3) + " min)");
			}
		});
		RuntimeSpnr.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					SaveConfigLbl.requestFocus();
				}
			}
		});
		RuntimeSpnr.setEnabled(false);
		RuntimeSpnr.setModel(new SpinnerNumberModel(24, 1, 555, 8));
		RuntimeSpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		RuntimeSpnr.setBounds(840, 425, 91, 25);
		CreateNewPnl.add(RuntimeSpnr);
		
		RunUnitsLbl = new JLabel("Hours");
		RunUnitsLbl.setEnabled(false);
		RunUnitsLbl.setHorizontalTextPosition(SwingConstants.LEADING);
		RunUnitsLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		RunUnitsLbl.setBounds(937, 427, 44, 21);
		CreateNewPnl.add(RunUnitsLbl);
		
		RunAltLbl = new JLabel("( min)");
		RunAltLbl.setEnabled(false);
		RunAltLbl.setHorizontalTextPosition(SwingConstants.LEADING);
		RunAltLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		RunAltLbl.setBounds(991, 427, 149, 21);
		CreateNewPnl.add(RunAltLbl);
		RuntimeSpnr.setValue((Integer)RuntimeSpnr.getValue()+1);
		RuntimeSpnr.setValue((Integer)RuntimeSpnr.getValue()-1);
		
		RuntimePnl = new JPanel();
		tabbedPane.addTab("Running Simulation", null, RuntimePnl, null);
		RuntimePnl.setLayout(null);
		
		SerialDisplayScroll = new JScrollPane();
		SerialDisplayScroll.setBorder(null);
		SerialDisplayScroll.setBounds(10, 41, 620, 444);
		RuntimePnl.add(SerialDisplayScroll);
		
		SerialDisplayPnl = new JPanel();
		SerialDisplayPnl.setBorder(null);
		SerialDisplayScroll.setViewportView(SerialDisplayPnl);
		
		SerialDisplayTitle = new JLabel("Serial Buffers");
		SerialDisplayTitle.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		SerialDisplayTitle.setBounds(10, 11, 118, 21);
		RuntimePnl.add(SerialDisplayTitle);
		
		SerialAvialableLbl = new JLabel("Available");
		SerialAvialableLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		SerialAvialableLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		SerialAvialableLbl.setBounds(461, 12, 118, 21);
		RuntimePnl.add(SerialAvialableLbl);
		
		SerialHistorySlider = new JSlider();
		SerialHistorySlider.setMinorTickSpacing(1);
		SerialHistorySlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				Access.CODE.drawSerialBuffers(SerialHistorySlider.getValue());
			}
		});
		SerialHistorySlider.setMajorTickSpacing(10);
		SerialHistorySlider.setSnapToTicks(true);
		SerialHistorySlider.setValue(0);
		SerialHistorySlider.setMaximum(10);
		SerialHistorySlider.setPaintTicks(true);
		SerialHistorySlider.setBounds(10, 491, 620, 25);
		RuntimePnl.add(SerialHistorySlider);
	}
	
	private void align(){
		int spacing = 10;
		Dimension panelSize = new Dimension(tabbedPane.getWidth()-6, tabbedPane.getHeight()-40);
		int listWidth = (int) (((panelSize.getWidth() / 2) - spacing*4 - RovAddBtn.getWidth()) / 3 - 50);
		int listHeight = (int) ((panelSize.getHeight() - spacing*6 - RovAddTtl.getHeight() - RovDriveModelLbl.getHeight() - SatAddTtl.getHeight() - SatDriveModelLbl.getHeight()) / 2);
		this.RovAddTtl.setLocation(10, 10);
		this.RovDriveModelLbl.setLocation(RovAddTtl.getX(), RovAddTtl.getY() + RovAddTtl.getHeight() + spacing);
		this.RovDriveModelList.setBounds(RovDriveModelLbl.getX(), RovDriveModelLbl.getY()+RovDriveModelLbl.getHeight(), listWidth, listHeight);
		this.RovAutonomusCodeList.setBounds(RovDriveModelList.getX()+RovDriveModelList.getWidth()+spacing, RovDriveModelList.getY(), listWidth, listHeight);
		this.RovAutonomusCodeLbl.setLocation(RovAutonomusCodeList.getX(), RovAutonomusCodeList.getY()-RovAutonomusCodeLbl.getHeight());
		this.RovAddBtn.setLocation(RovAutonomusCodeList.getX()+RovAutonomusCodeList.getWidth()+spacing, RovAutonomusCodeList.getY());
		this.RovRemoveBtn.setLocation(RovAddBtn.getX(), RovAddBtn.getY()+RovAddBtn.getHeight()+spacing);
		this.RoverList.setBounds(RovAddBtn.getX()+RovAddBtn.getWidth()+spacing, this.RovAutonomusCodeList.getY(), listWidth, listHeight);
		this.RoversListLbl.setLocation(RoverList.getX(), this.RovAutonomusCodeLbl.getY());
		
		this.SatAddTtl.setLocation(RovAddTtl.getX(), RovDriveModelList.getY()+RovDriveModelList.getHeight()+spacing*2);
		this.SatDriveModelLbl.setLocation(SatAddTtl.getX(), SatAddTtl.getY()+SatAddTtl.getHeight()+spacing);
		this.SatDriveModelList.setBounds(SatDriveModelLbl.getX(), SatDriveModelLbl.getY()+SatDriveModelLbl.getHeight(), listWidth, listHeight);
		this.SatAutonomusCodeList.setBounds(SatDriveModelList.getX()+SatDriveModelList.getWidth()+spacing, SatDriveModelList.getY(), listWidth, listHeight);
		this.SatAutonomusCodeLbl.setLocation(SatAutonomusCodeList.getX(), SatAutonomusCodeList.getY()-SatAutonomusCodeLbl.getHeight());
		this.SatAddBtn.setLocation(SatAutonomusCodeList.getX()+SatAutonomusCodeList.getWidth()+spacing, SatAutonomusCodeList.getY());
		this.SatRemoveBtn.setLocation(SatAddBtn.getX(), SatAddBtn.getY()+SatAddBtn.getHeight()+spacing);
		this.SatelliteList.setBounds(SatAddBtn.getX()+SatAddBtn.getWidth()+spacing, this.SatAutonomusCodeList.getY(), listWidth, listHeight);
		this.SatelliteListLbl.setLocation(SatelliteList.getX(), this.SatAutonomusCodeLbl.getY());
		
		this.MapConfigTtl.setLocation(RoverList.getX()+RoverList.getWidth()+spacing*3, this.RovAddTtl.getY());
		this.TypeSelector.setLocation(MapConfigTtl.getX(), MapConfigTtl.getY()+MapConfigTtl.getHeight()+spacing);
		
		this.TimingOptionsTtl.setLocation(MapConfigTtl.getX(), TypeSelector.getY()+TypeSelector.getHeight()+spacing*4);
		this.AccelChk.setLocation(TimingOptionsTtl.getX(), TimingOptionsTtl.getY()+TimingOptionsTtl.getHeight()+spacing);
		this.RuntimeLbl.setLocation(AccelChk.getX(), AccelChk.getY()+AccelChk.getHeight()+spacing);
		this.RuntimeSpnr.setLocation(RuntimeLbl.getX()+RuntimeLbl.getWidth()+spacing, RuntimeLbl.getY()-2);
		this.RunUnitsLbl.setLocation(RuntimeSpnr.getX()+RuntimeSpnr.getWidth()+spacing, RuntimeLbl.getY());
		this.RunAltLbl.setLocation(RunUnitsLbl.getX()+RunUnitsLbl.getWidth()+spacing, RuntimeLbl.getY());
		
		this.StartBtn.setLocation((int)panelSize.getWidth()-StartBtn.getWidth()-spacing, (int)panelSize.getHeight()-StartBtn.getHeight()-spacing);
		this.SaveConfigLbl.setLocation((int)panelSize.getWidth()-SaveConfigLbl.getWidth()-spacing, StartBtn.getY()-spacing-SaveConfigLbl.getHeight());
		
		this.SerialDisplayTitle.setLocation(spacing, spacing);
		this.SerialDisplayScroll.setBounds(SerialDisplayTitle.getX(), SerialDisplayTitle.getY()+SerialDisplayTitle.getHeight()+spacing, (this.tabbedPane.getWidth()-spacing*4)/3, (this.tabbedPane.getHeight()-spacing*3-SerialDisplayTitle.getHeight()-40)/2);
		this.SerialAvialableLbl.setLocation(SerialDisplayScroll.getX()+SerialDisplayScroll.getWidth()-SerialAvialableLbl.getWidth(), SerialDisplayTitle.getY());
	}
	
	public void genorateSerialDisplays(RoverObject[] rovs, SatelliteObject[] sats){
		RowSpec[] rows = new RowSpec[rovs.length*2+sats.length*2+2+1];
		rows[0] = FormFactory.RELATED_GAP_ROWSPEC;
		int x = 1;
		while (x < rows.length){
			rows[x] = FormFactory.DEFAULT_ROWSPEC;
			rows[x+1] = FormFactory.RELATED_GAP_ROWSPEC;
			x += 2;
		}
		SerialDisplayPnl.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("center:default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("left:default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("right:default"),
				FormFactory.RELATED_GAP_COLSPEC,},
			rows ));
		JLabel[] titles = new JLabel[rovs.length+sats.length+1];
		SerialRoverLbls = new JLabel[rovs.length];
		SerialRoverAvailableLbls = new JLabel[rovs.length];
		SerialSatelliteLbls = new JLabel[sats.length];
		SerialSatelliteAvailableLbls = new JLabel[sats.length];
		titles[0] = new JLabel("Ground");
		titles[0].setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		SerialDisplayPnl.add(titles[0], "2, 2, left, default");
		SerialGroundLbl = new JLabel();
		SerialGroundLbl.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		SerialDisplayPnl.add(SerialGroundLbl, "4, 2, left, default");
		SerialGroundAvailableLbl = new JLabel("0");
		SerialGroundAvailableLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		SerialDisplayPnl.add(SerialGroundAvailableLbl, "6, 2, right, default");
		x = 1;
		int i = 0;
		while (i < sats.length){
			titles[x] = new JLabel(sats[i].getName());
			titles[x].setFont(new Font("Trebuchet MS", Font.BOLD, 13));
			SerialDisplayPnl.add(titles[x], "2, " + (x*2+2) + ", left, default");
			SerialSatelliteLbls[i] = new JLabel();
			SerialSatelliteLbls[i].setFont(new Font("Lucida Console", Font.PLAIN, 12));
			SerialDisplayPnl.add(SerialSatelliteLbls[i], "4, " + (x*2+2) + ", left, default");
			SerialSatelliteAvailableLbls[i] = new JLabel("0");
			SerialSatelliteAvailableLbls[i].setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
			SerialDisplayPnl.add(SerialSatelliteAvailableLbls[i], "6, " + (x*2+2) + ", right, default");
			i++;
			x++;
		}
		i = 0;
		while (i < rovs.length){
			titles[x] = new JLabel(rovs[i].getName());
			titles[x].setFont(new Font("Trebuchet MS", Font.BOLD, 13));
			SerialDisplayPnl.add(titles[x], "2, " + (x*2+2) + ", left, default");
			SerialRoverLbls[i] = new JLabel();
			SerialRoverLbls[i].setFont(new Font("Lucida Console", Font.PLAIN, 12));
			SerialDisplayPnl.add(SerialRoverLbls[i], "4, " + (x*2+2) + ", left, default");
			SerialRoverAvailableLbls[i] = new JLabel("0");
			SerialRoverAvailableLbls[i].setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
			SerialDisplayPnl.add(SerialRoverAvailableLbls[i], "6, " + (x*2+2) + ", right, default");
			i++;
			x++;
		}
	}
}
