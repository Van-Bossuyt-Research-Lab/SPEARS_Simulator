package wrapper;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

import visual.Panel;
import visual.ZList;

import javax.swing.JTabbedPane;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;

import rover.RoverObject;
import satellite.SatelliteObject;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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

import objects.MapFileFilter;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class MainWrapper extends Panel {

	static final int MAX_MAP_LAYERS = 10;
	
	JTabbedPane tabbedPane;
	JPanel CreateNewPnl;
		private JLabel RovAddLbl;
			private JLabel RovDriveModelLbl;
			ZList RovDriveModelList;
			private JLabel RovAutonomusCodeLbl;
			ZList RovAutonomusCodeList;
			private JButton RovAddBtn;
			private JButton RovRemoveBtn;
			private JLabel RoversListLbl;
			ZList RoverList;
		private JLabel SatAddLbl;
			private JLabel SatDriveModelLbl;
			ZList SatDriveModelList;
			private JLabel SatAutonomusCodeLbl;
			ZList SatAutonomusCodeList;
			private JButton SatAddBtn;
			private JButton SatRemoveBtn;
			private JLabel SatelliteListLbl;
			ZList SatelliteList;
		private JLabel MapConfigLbl;
			JSlider MapRoughSlider;
			private JLabel MapRoughnessLbl;
			private JLabel MapSmoothLbl;
			private JLabel MapRoughLbl;
			private JLabel MapTypeLbl;
			JComboBox<String> MapTypeCombo;
			private JLabel FileLocLbl;
			JTextField FileLocTxt;
			private JButton FileLocBtn;
		JButton StartBtn;
	JPanel RuntimePnl;
		private JLabel SerialDisplayTitle;
		private JLabel SerialAvialableLbl;
		private JScrollPane SerialDisplayScroll;
		private JPanel SerialDisplayPnl;
		JLabel SerialGroundLbl;
		JLabel SerialGroundAvailableLbl;
		JLabel[] SerialRoverLbls;
		JLabel[] SerialRoverAvailableLbls;
		JLabel[] SerialSatelliteLbls;
		JLabel[] SerialSatelliteAvailableLbls;
		JSlider SerialHistorySlider;
		private JSpinner MapDetailSpnr;
	
	public MainWrapper(Dimension size) {
		super(new Dimension(1920, 1080), "Wrapper Display");
		
		initalize();
		MapTypeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileLocLbl.setVisible(MapTypeCombo.getSelectedIndex() == 1);
				FileLocTxt.setVisible(MapTypeCombo.getSelectedIndex() == 1);
				FileLocBtn.setVisible(MapTypeCombo.getSelectedIndex() == 1);
			}
		});
		align();
	}
	
	private void initalize(){
		tabbedPane = new JTabbedPane();
		tabbedPane.setEnabled(false);
		tabbedPane.setTabPlacement(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Trebuchet MS", Font.BOLD, 21));
		tabbedPane.setBounds(10, 56, 1900, 979);
		add(tabbedPane);
		
		CreateNewPnl = new JPanel();
		tabbedPane.addTab("New Simulation", null, CreateNewPnl, null);
		CreateNewPnl.setLayout(null);
		
		RovAddLbl = new JLabel("Adding Rovers");
		RovAddLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		RovAddLbl.setBounds(10, 11, 118, 21);
		CreateNewPnl.add(RovAddLbl);
		
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
		
		SatAddLbl = new JLabel("Adding Satellites");
		SatAddLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		SatAddLbl.setBounds(10, 460, 142, 21);
		CreateNewPnl.add(SatAddLbl);
		
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
		
		MapConfigLbl = new JLabel("Congiure Map");
		MapConfigLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		MapConfigLbl.setBounds(912, 10, 118, 21);
		CreateNewPnl.add(MapConfigLbl);
		
		MapRoughSlider = new JSlider();
		MapRoughSlider.setEnabled(false);
		MapRoughSlider.setMajorTickSpacing(10);
		MapRoughSlider.setPaintTicks(true);
		MapRoughSlider.setValue(70);
		MapRoughSlider.setBounds(1042, 179, 400, 30);
		CreateNewPnl.add(MapRoughSlider);
		
		MapTypeCombo = new JComboBox<String>();
		MapTypeCombo.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		MapTypeCombo.setModel(new DefaultComboBoxModel<String>(new String[] {"Plasma Fractal", "From File"}));
		MapTypeCombo.setSelectedIndex(0);
		MapTypeCombo.setBounds(1042, 41, 195, 25);
		CreateNewPnl.add(MapTypeCombo);
		
		StartBtn = new JButton("Start Simulation");
		StartBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Access.CODE.beginSimulation();
				tabbedPane.setSelectedIndex(1);
			}
		});
		StartBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
		StartBtn.setBounds(1665, 874, 220, 55);
		CreateNewPnl.add(StartBtn);
		
		MapRoughnessLbl = new JLabel("Map Roughness:");
		MapRoughnessLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		MapRoughnessLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		MapRoughnessLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		MapRoughnessLbl.setBounds(890, 147, 142, 21);
		CreateNewPnl.add(MapRoughnessLbl);
		
		MapSmoothLbl = new JLabel("Smooth");
		MapSmoothLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		MapSmoothLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		MapSmoothLbl.setBounds(971, 179, 61, 21);
		CreateNewPnl.add(MapSmoothLbl);
		
		MapRoughLbl = new JLabel("Rough");
		MapRoughLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		MapRoughLbl.setBounds(1452, 179, 43, 21);
		CreateNewPnl.add(MapRoughLbl);
		
		MapTypeLbl = new JLabel("Map Type:");
		MapTypeLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		MapTypeLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		MapTypeLbl.setBounds(890, 43, 142, 21);
		CreateNewPnl.add(MapTypeLbl);
		
		FileLocLbl = new JLabel("File Location:");
		FileLocLbl.setVisible(false);
		FileLocLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		FileLocLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		FileLocLbl.setBounds(890, 97, 142, 21);
		CreateNewPnl.add(FileLocLbl);
		
		FileLocTxt = new JTextField();
		FileLocTxt.setVisible(false);
		FileLocTxt.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		FileLocTxt.setBounds(1042, 95, 338, 25);
		CreateNewPnl.add(FileLocTxt);
		FileLocTxt.setColumns(10);
		
		FileLocBtn = new JButton("Browse...");
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
		FileLocBtn.setVisible(false);
		FileLocBtn.setBounds(1390, 97, 105, 25);
		CreateNewPnl.add(FileLocBtn);
		
		JSpinner MapSizeSpnr = new JSpinner();
		MapSizeSpnr.setModel(new SpinnerNumberModel(7, 4, 9, 1));
		MapSizeSpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		MapSizeSpnr.setBounds(1042, 241, 80, 25);
		CreateNewPnl.add(MapSizeSpnr);
		
		MapDetailSpnr = new JSpinner();
		MapDetailSpnr.setModel(new SpinnerNumberModel(3, 1, 9, 1));
		MapDetailSpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		MapDetailSpnr.setBounds(1342, 241, 80, 25);
		CreateNewPnl.add(MapDetailSpnr);
		
		JLabel MapSizeLbl = new JLabel("Map Size:");
		MapSizeLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		MapSizeLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		MapSizeLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		MapSizeLbl.setBounds(890, 243, 142, 21);
		CreateNewPnl.add(MapSizeLbl);
		
		JLabel MapDetailLbl = new JLabel("Map Detail:");
		MapDetailLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		MapDetailLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		MapDetailLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		MapDetailLbl.setBounds(1190, 243, 142, 21);
		CreateNewPnl.add(MapDetailLbl);
		
		JSpinner TargetDensitySpnr = new JSpinner();
		TargetDensitySpnr.setModel(new SpinnerNumberModel(0.1, 0.0, 100.0, 0.01));
		TargetDensitySpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		TargetDensitySpnr.setBounds(1042, 290, 80, 25);
		CreateNewPnl.add(TargetDensitySpnr);
		
		JLabel TargetDensityLbl = new JLabel("Target Density:");
		TargetDensityLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		TargetDensityLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		TargetDensityLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		TargetDensityLbl.setBounds(890, 292, 142, 21);
		CreateNewPnl.add(TargetDensityLbl);
		
		JLabel HazardDensityLbl = new JLabel("Hazard Density:");
		HazardDensityLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		HazardDensityLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		HazardDensityLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		HazardDensityLbl.setBounds(1190, 292, 142, 21);
		CreateNewPnl.add(HazardDensityLbl);
		
		JSpinner HazardDensitySpnr = new JSpinner();
		HazardDensitySpnr.setModel(new SpinnerNumberModel(0.02, 0.0, 100.0, 0.001));
		HazardDensitySpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		HazardDensitySpnr.setBounds(1342, 290, 80, 25);
		CreateNewPnl.add(HazardDensitySpnr);
		
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
		int listWidth = ((this.getWidth() / 2) - spacing*4 - RovAddBtn.getWidth()) / 3 - 50;
		int listHeight = (this.getWorkingHeight() - 60 - spacing*6 - RovAddLbl.getHeight() - RovDriveModelLbl.getHeight() - SatAddLbl.getHeight() - SatDriveModelLbl.getHeight()) / 2;
		this.RovAddLbl.setLocation(10, 10);
		this.RovDriveModelLbl.setLocation(RovAddLbl.getX(), RovAddLbl.getY() + RovAddLbl.getHeight() + spacing);
		this.RovDriveModelList.setBounds(RovDriveModelLbl.getX(), RovDriveModelLbl.getY()+RovDriveModelLbl.getHeight(), listWidth, listHeight);
		this.RovAutonomusCodeList.setBounds(RovDriveModelList.getX()+RovDriveModelList.getWidth()+spacing, RovDriveModelList.getY(), listWidth, listHeight);
		this.RovAutonomusCodeLbl.setLocation(RovAutonomusCodeList.getX(), RovAutonomusCodeList.getY()-RovAutonomusCodeLbl.getHeight());
		this.RovAddBtn.setLocation(RovAutonomusCodeList.getX()+RovAutonomusCodeList.getWidth()+spacing, RovAutonomusCodeList.getY());
		this.RovRemoveBtn.setLocation(RovAddBtn.getX(), RovAddBtn.getY()+RovAddBtn.getHeight()+spacing);
		this.RoverList.setBounds(RovAddBtn.getX()+RovAddBtn.getWidth()+spacing, this.RovAutonomusCodeList.getY(), listWidth, listHeight);
		this.RoversListLbl.setLocation(RoverList.getX(), this.RovAutonomusCodeLbl.getY());
		
		this.SatAddLbl.setLocation(RovAddLbl.getX(), RovDriveModelList.getY()+RovDriveModelList.getHeight()+spacing*2);
		this.SatDriveModelLbl.setLocation(SatAddLbl.getX(), SatAddLbl.getY()+SatAddLbl.getHeight()+spacing);
		this.SatDriveModelList.setBounds(SatDriveModelLbl.getX(), SatDriveModelLbl.getY()+SatDriveModelLbl.getHeight(), listWidth, listHeight);
		this.SatAutonomusCodeList.setBounds(SatDriveModelList.getX()+SatDriveModelList.getWidth()+spacing, SatDriveModelList.getY(), listWidth, listHeight);
		this.SatAutonomusCodeLbl.setLocation(SatAutonomusCodeList.getX(), SatAutonomusCodeList.getY()-SatAutonomusCodeLbl.getHeight());
		this.SatAddBtn.setLocation(SatAutonomusCodeList.getX()+SatAutonomusCodeList.getWidth()+spacing, SatAutonomusCodeList.getY());
		this.SatRemoveBtn.setLocation(SatAddBtn.getX(), SatAddBtn.getY()+SatAddBtn.getHeight()+spacing);
		this.SatelliteList.setBounds(SatAddBtn.getX()+SatAddBtn.getWidth()+spacing, this.SatAutonomusCodeList.getY(), listWidth, listHeight);
		this.SatelliteListLbl.setLocation(SatelliteList.getX(), this.SatAutonomusCodeLbl.getY());
		
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
				ColumnSpec.decode("center:default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("right:default"),
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
