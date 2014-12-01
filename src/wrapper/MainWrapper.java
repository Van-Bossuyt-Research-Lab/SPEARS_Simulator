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
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;

import rover.RoverObj;
import satellite.SatelliteObject;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWrapper extends Panel {

	JTabbedPane tabbedPane;
	JPanel CreateNewPnl;
		private JLabel RovAddLbl;
		private JLabel RovDriveModelLbl;
		ZList RovDriveModelList;
		private JLabel RovAutonomusCodeLbl;
		ZList RovAutonomusCodeList;
		private JButton RovAddBtn;
		private JLabel RoversListLbl;
		ZList RoverList;
		JSlider MapRoughSlider;
		private JLabel SatAddLbl;
		private JLabel SatDriveModelLbl;
		ZList SatDriveModelList;
		private JLabel SatAutonomusCodeLbl;
		ZList SatAutonomusCodeList;
		private JButton SatAddBtn;
		private JLabel SatelliteListLbl;
		ZList SatelliteList;
		private JLabel MapRoughnessLbl;
		private JLabel MapSmoothLbl;
		private JLabel MapRoughLbl;
		private JLabel MapTypeLbl;
		JComboBox<String> MapTypeCombo;
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
	private JLabel lblDfsjfjkdhaFhdsjkflhdsDshfjksdh;
	private JLabel lblNewLabel_1;
		
	
	public MainWrapper(Dimension size) {
		super(new Dimension(1920,1080), "Wrapper HUD");
		
		initalize();
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
		
		RovAddBtn = new JButton("<HTML>=><br>Add</HTML>");
		RovAddBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		RovAddBtn.setBounds(430, 152, 64, 64);
		CreateNewPnl.add(RovAddBtn);
		
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
		
		SatAddBtn = new JButton("<HTML>=><br>Add</HTML>");
		SatAddBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		SatAddBtn.setBounds(430, 607, 64, 64);
		CreateNewPnl.add(SatAddBtn);
		
		SatelliteListLbl = new JLabel("Satellites");
		SatelliteListLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		SatelliteListLbl.setBounds(510, 492, 142, 21);
		CreateNewPnl.add(SatelliteListLbl);
		
		MapRoughSlider = new JSlider();
		MapRoughSlider.setMajorTickSpacing(10);
		MapRoughSlider.setPaintTicks(true);
		MapRoughSlider.setValue(70);
		MapRoughSlider.setBounds(1404, 131, 400, 30);
		CreateNewPnl.add(MapRoughSlider);
		
		MapTypeCombo = new JComboBox<String>();
		MapTypeCombo.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		MapTypeCombo.setModel(new DefaultComboBoxModel(new String[] {"Plasma Fractal"}));
		MapTypeCombo.setBounds(1546, 41, 175, 25);
		CreateNewPnl.add(MapTypeCombo);
		
		StartBtn = new JButton("Start Simulation");
		StartBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Access.CODE.beginSimulation();
			}
		});
		StartBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
		StartBtn.setBounds(1665, 874, 220, 55);
		CreateNewPnl.add(StartBtn);
		
		MapRoughnessLbl = new JLabel("Map Roughness");
		MapRoughnessLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		MapRoughnessLbl.setBounds(1404, 99, 142, 21);
		CreateNewPnl.add(MapRoughnessLbl);
		
		MapSmoothLbl = new JLabel("Smooth");
		MapSmoothLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		MapSmoothLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		MapSmoothLbl.setBounds(1333, 131, 61, 21);
		CreateNewPnl.add(MapSmoothLbl);
		
		MapRoughLbl = new JLabel("Rough");
		MapRoughLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		MapRoughLbl.setBounds(1814, 131, 61, 21);
		CreateNewPnl.add(MapRoughLbl);
		
		MapTypeLbl = new JLabel("Map Type:");
		MapTypeLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		MapTypeLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		MapTypeLbl.setBounds(1447, 43, 89, 21);
		CreateNewPnl.add(MapTypeLbl);
		
		RuntimePnl = new JPanel();
		tabbedPane.addTab("Running Simulation", null, RuntimePnl, null);
		RuntimePnl.setLayout(null);
		
		SerialDisplayScroll = new JScrollPane();
		SerialDisplayScroll.setBorder(null);
		SerialDisplayScroll.setBounds(10, 43, 569, 123);
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
		this.RoverList.setBounds(RovAddBtn.getX()+RovAddBtn.getWidth()+spacing, this.RovAutonomusCodeList.getY(), listWidth, listHeight);
		this.RoversListLbl.setLocation(RoverList.getX(), this.RovAutonomusCodeLbl.getY());
		
		this.SatAddLbl.setLocation(RovAddLbl.getX(), RovDriveModelList.getY()+RovDriveModelList.getHeight()+spacing*2);
		this.SatDriveModelLbl.setLocation(SatAddLbl.getX(), SatAddLbl.getY()+SatAddLbl.getHeight()+spacing);
		this.SatDriveModelList.setBounds(SatDriveModelLbl.getX(), SatDriveModelLbl.getY()+SatDriveModelLbl.getHeight(), listWidth, listHeight);
		this.SatAutonomusCodeList.setBounds(SatDriveModelList.getX()+SatDriveModelList.getWidth()+spacing, SatDriveModelList.getY(), listWidth, listHeight);
		this.SatAutonomusCodeLbl.setLocation(SatAutonomusCodeList.getX(), SatAutonomusCodeList.getY()-SatAutonomusCodeLbl.getHeight());
		this.SatAddBtn.setLocation(SatAutonomusCodeList.getX()+SatAutonomusCodeList.getWidth()+spacing, SatAutonomusCodeList.getY());
		this.SatelliteList.setBounds(SatAddBtn.getX()+SatAddBtn.getWidth()+spacing, this.SatAutonomusCodeList.getY(), listWidth, listHeight);
		this.SatelliteListLbl.setLocation(SatelliteList.getX(), this.SatAutonomusCodeLbl.getY());
		
		this.SerialDisplayTitle.setLocation(spacing, spacing);
		this.SerialDisplayScroll.setBounds(SerialDisplayTitle.getX(), SerialDisplayTitle.getY()+SerialDisplayTitle.getHeight()+spacing, (this.tabbedPane.getWidth()-spacing*4)/3, this.tabbedPane.getHeight()-spacing*3-SerialDisplayTitle.getHeight()-40);
		this.SerialAvialableLbl.setLocation(SerialDisplayScroll.getX()+SerialDisplayScroll.getWidth()-SerialAvialableLbl.getWidth(), SerialDisplayTitle.getY());
	}
	
	public void genorateSerialDisplays(RoverObj[] rovs, SatelliteObject[] sats){
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
