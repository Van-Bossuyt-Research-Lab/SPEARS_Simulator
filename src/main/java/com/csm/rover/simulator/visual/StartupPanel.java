package com.csm.rover.simulator.visual;

import com.csm.rover.simulator.control.PopUp;
import com.csm.rover.simulator.environments.EnvironmentIO;
import com.csm.rover.simulator.environments.rover.TerrainEnvironment;
import com.csm.rover.simulator.environments.rover.modifiers.PlasmaFractalGen;
import com.csm.rover.simulator.environments.rover.modifiers.SmoothingModifier;
import com.csm.rover.simulator.environments.rover.modifiers.TruncateModifier;
import com.csm.rover.simulator.environments.rover.populators.TerrainHazardsPop;
import com.csm.rover.simulator.environments.rover.populators.TerrainTargetsPop;
import com.csm.rover.simulator.objects.io.MapFileFilter;
import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.objects.io.RunConfiguration;
import com.csm.rover.simulator.objects.util.FreeThread;
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.platforms.PlatformRegistry;
import com.csm.rover.simulator.wrapper.Admin;
import com.csm.rover.simulator.wrapper.Globals;
import com.csm.rover.simulator.wrapper.NamesAndTags;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class StartupPanel extends Panel {
    private static final Logger LOG = LogManager.getLogger(StartupPanel.class);

    private Map<String, PlatformConfig> roversToAdd = new TreeMap<String, PlatformConfig>();
    private Map<String, PlatformConfig> satsToAdd = new TreeMap<String, PlatformConfig>();

    private JLabel RovAddTtl;
    private JLabel RovDriveModelLbl;
    ZList<String> RovPhysicsModelList;
    private JLabel RovAutonomousCodeLbj;
    ZList<String> RovAutonomousCodeList;
    private JButton RovAddBtn;
    private JButton RovRemoveBtn;
    private JLabel RoversListLbl;
    ZList<String> RoverList;
    private JLabel SatAddTtl;
    private JLabel SatDriveModelLbl;
    ZList<String> SatPhysicsModelList;
    private JLabel SatAutonomousCodeLbl;
    ZList<String> SatAutonomousCodeList;
    private JButton SatAddBtn;
    private JButton SatRemoveBtn;
    private JLabel SatelliteListLbl;
    ZList<String> SatelliteList;
    private JLabel MapConfigTtl;
    JTabbedPane TypeSelector;
    JSlider MapRoughSlider;
    JSpinner MapDetailSpnr;
    JSpinner MapSizeSpnr;
    JSpinner HazardDensitySpnr;
    JSpinner TargetDensitySpnr;
    JCheckBox ValuedTargetsChk;
    JCheckBox ValuedHazardsChk;
    JTextField FileLocTxt;
    private JLabel TimingOptionsTtl;
    JCheckBox AccelChk;
    private JLabel RuntimeLbl;
    JSpinner RuntimeSpnr;
    private JLabel RunUnitsLbl;
    JLabel RunAltLbl;
    private JLabel SaveConfigLbl;
    JButton StartBtn;
    private JScrollPane roverPhysicsParamScrl;
    private JTable roverPhysicsParamTbl;
    private JScrollPane roverCodeParamScrl;
    private JTable roverCodeParamTbl;
    private JScrollPane satPhysicsParamScrl;
    private JTable satPhysicsParamTbl;
    private JScrollPane satCodeParamScrl;
    private JTable satCodeParamTbl;

    public StartupPanel(Dimension size){
        super(size, "Start Up");
        initialize();
        align();
    }
    

    public StartupPanel(){
        super(new Dimension(1920, 1080), "Start Up");
        initialize();
        align();
    }

    private void initialize(){
        this.setLayout(null);

        RovAddTtl = new JLabel("Adding Rovers");
        RovAddTtl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        RovAddTtl.setBounds(10, 11, 118, 21);
        this.add(RovAddTtl);

        RovDriveModelLbl = new JLabel("Physics Model");
        RovDriveModelLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        RovDriveModelLbl.setBounds(10, 43, 118, 21);
        this.add(RovDriveModelLbl);

        RovPhysicsModelList = new ZList<String>();
        RovPhysicsModelList.addListSelectionListener(new ListSelectionListener() {
        	@Override
        	public void valueChanged(ListSelectionEvent e) {
        		if (RovAutonomousCodeList.getSelectedIndex() != -1){
					roverPhysicsParamTbl.setModel(generateTableModel(
						PlatformRegistry.getParametersForPhysicsModel("Rover", RovPhysicsModelList.getSelectedItem())));
				}
        	}
        });
        RovPhysicsModelList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        RovPhysicsModelList.setBounds(10, 63, 200, 174);
        this.add(RovPhysicsModelList);

        RovAutonomousCodeLbj = new JLabel("Autonomous Logic");
        RovAutonomousCodeLbj.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        RovAutonomousCodeLbj.setBounds(220, 43, 142, 21);
        this.add(RovAutonomousCodeLbj);

        RovAutonomousCodeList = new ZList<String>();
        RovAutonomousCodeList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        RovAutonomousCodeList.setBounds(220, 63, 200, 174);
        RovAutonomousCodeList.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (RovAutonomousCodeList.getSelectedIndex() != -1){
					roverCodeParamTbl.setModel(generateTableModel(
						PlatformRegistry.getParametersForAutonomousCodeModel("Rover", RovAutonomousCodeList.getSelectedItem())));
				}
			}	
        });
        this.add(RovAutonomousCodeList);

        RovAddBtn = new JButton("<HTML><center>=><br>Add</center></HTML>");
        RovAddBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                addRoverToList();
            }
        });
        RovAddBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
        RovAddBtn.setBounds(430, 152, 80, 64);
        this.add(RovAddBtn);

        RovRemoveBtn = new JButton("<HTML><center>&#60=<br>Remove</center></HTML>");
        RovRemoveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                removeRoverFromList();
            }
        });
        RovRemoveBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
        RovRemoveBtn.setBounds(430, 152, 80, 64);
        this.add(RovRemoveBtn);

        RoversListLbl = new JLabel("Rovers");
        RoversListLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        RoversListLbl.setBounds(510, 43, 142, 21);
        this.add(RoversListLbl);

        RoverList = new ZList<String>();
        RoverList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        RoverList.setBounds(510, 63, 200, 174);
        this.add(RoverList);

        SatAddTtl = new JLabel("Adding Satellites");
        SatAddTtl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        SatAddTtl.setBounds(10, 457, 142, 21);
        this.add(SatAddTtl);

        SatDriveModelLbl = new JLabel("Physics Model");
        SatDriveModelLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        SatDriveModelLbl.setBounds(10, 488, 118, 21);
        this.add(SatDriveModelLbl);

        SatPhysicsModelList = new ZList<String>();
        SatPhysicsModelList.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		if (SatPhysicsModelList.getSelectedIndex() != -1){
					satPhysicsParamTbl.setModel(generateTableModel(
						PlatformRegistry.getParametersForPhysicsModel("Satellite", SatPhysicsModelList.getSelectedItem())));
				}
        	}
        });
        SatPhysicsModelList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        SatPhysicsModelList.setBounds(10, 512, 200, 250);
        this.add(SatPhysicsModelList);

        SatAutonomousCodeList = new ZList<String>();
        SatAutonomousCodeList.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		if (SatAutonomousCodeList.getSelectedIndex() != -1){
					satCodeParamTbl.setModel(generateTableModel(
						PlatformRegistry.getParametersForAutonomousCodeModel("Satellite", SatAutonomousCodeList.getSelectedItem())));
				}
        	}
        });
        SatAutonomousCodeList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        SatAutonomousCodeList.setBounds(220, 512, 200, 250);
        this.add(SatAutonomousCodeList);

        SatAutonomousCodeLbl = new JLabel("Autonomous Logic");
        SatAutonomousCodeLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        SatAutonomousCodeLbl.setBounds(220, 492, 142, 21);
        this.add(SatAutonomousCodeLbl);

        SatelliteList = new ZList<String>();
        SatelliteList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        SatelliteList.setBounds(510, 512, 200, 250);
        this.add(SatelliteList);

        SatAddBtn = new JButton("<HTML><center>=><br>Add</center></HTML>");
        SatAddBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addSatelliteToList();
            }
        });
        SatAddBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
        SatAddBtn.setBounds(430, 607, 80, 64);
        this.add(SatAddBtn);

        SatRemoveBtn = new JButton("<HTML><center>&#60=<br>Remove</center></HTML>");
        SatRemoveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSatelliteFromList();
            }
        });
        SatRemoveBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        SatRemoveBtn.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
        SatRemoveBtn.setBounds(430, 607, 80, 64);
        this.add(SatRemoveBtn);

        SatelliteListLbl = new JLabel("Satellites");
        SatelliteListLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        SatelliteListLbl.setBounds(510, 492, 142, 21);
        this.add(SatelliteListLbl);

        MapConfigTtl = new JLabel("Configure Map");
        MapConfigTtl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        MapConfigTtl.setBounds(912, 10, 118, 21);
        this.add(MapConfigTtl);

        StartBtn = new JButton("Start Simulation");
        StartBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Admin.getInstance().beginSimulation(getConfigurationFromForm());
            }
        });
        StartBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
        StartBtn.setBounds(1665, 874, 220, 55);
        this.add(StartBtn);

        TypeSelector = new JTabbedPane(JTabbedPane.TOP);
        TypeSelector.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        TypeSelector.setBounds(740, 41, 636, 267);
        this.add(TypeSelector);

        JPanel selectionPlasmaPnl = new JPanel();
        TypeSelector.addTab("Plasma Fractal", null, selectionPlasmaPnl, null);
        selectionPlasmaPnl.setLayout(null);

        MapRoughSlider = new JSlider();
        MapRoughSlider.setOpaque(false);
        MapRoughSlider.setBounds(162, 43, 400, 30);
        selectionPlasmaPnl.add(MapRoughSlider);
        MapRoughSlider.setMaximum(700);
        MapRoughSlider.setMajorTickSpacing(100);
        MapRoughSlider.setPaintTicks(true);
        MapRoughSlider.setValue(200);

        JLabel mapSmoothLbl = new JLabel("Smooth");
        mapSmoothLbl.setBounds(91, 43, 61, 21);
        selectionPlasmaPnl.add(mapSmoothLbl);
        mapSmoothLbl.setHorizontalAlignment(SwingConstants.TRAILING);
        mapSmoothLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

        JLabel mapRoughnessLbl = new JLabel("Map Roughness:");
        mapRoughnessLbl.setBounds(10, 11, 142, 21);
        selectionPlasmaPnl.add(mapRoughnessLbl);
        mapRoughnessLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        mapRoughnessLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        mapRoughnessLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));

        JLabel mapRoughLbl = new JLabel("Rough");
        mapRoughLbl.setBounds(572, 43, 43, 21);
        selectionPlasmaPnl.add(mapRoughLbl);
        mapRoughLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

        MapDetailSpnr = new JSpinner();
        MapDetailSpnr.setBounds(462, 105, 80, 25);
        selectionPlasmaPnl.add(MapDetailSpnr);
        MapDetailSpnr.setModel(new SpinnerNumberModel(3, 1, 5, 1));
        MapDetailSpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));

        JLabel mapDetailLbl = new JLabel("Map Detail:");
        mapDetailLbl.setBounds(310, 107, 142, 21);
        selectionPlasmaPnl.add(mapDetailLbl);
        mapDetailLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        mapDetailLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        mapDetailLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));

        JLabel hazardDensityLbl = new JLabel("Hazard Density:");
        hazardDensityLbl.setBounds(310, 156, 142, 21);
        selectionPlasmaPnl.add(hazardDensityLbl);
        hazardDensityLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        hazardDensityLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        hazardDensityLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));

        HazardDensitySpnr = new JSpinner();
        HazardDensitySpnr.setBounds(462, 154, 80, 25);
        selectionPlasmaPnl.add(HazardDensitySpnr);
        HazardDensitySpnr.setModel(new SpinnerNumberModel(0.4, 0.0, 100.0, 0.01));
        HazardDensitySpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));

        TargetDensitySpnr = new JSpinner();
        TargetDensitySpnr.setBounds(162, 154, 80, 25);
        selectionPlasmaPnl.add(TargetDensitySpnr);
        TargetDensitySpnr.setModel(new SpinnerNumberModel(4., 0.0, 100.0, 0.1));
        TargetDensitySpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));

        JLabel targetDensityLbl = new JLabel("Target Density:");
        targetDensityLbl.setBounds(10, 156, 142, 21);
        selectionPlasmaPnl.add(targetDensityLbl);
        targetDensityLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        targetDensityLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        targetDensityLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));

        JLabel mapSizeLbl = new JLabel("Map Size:");
        mapSizeLbl.setBounds(10, 107, 142, 21);
        selectionPlasmaPnl.add(mapSizeLbl);
        mapSizeLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        mapSizeLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        mapSizeLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));

        MapSizeSpnr = new JSpinner();
        MapSizeSpnr.setBounds(162, 105, 80, 25);
        selectionPlasmaPnl.add(MapSizeSpnr);
        MapSizeSpnr.setModel(new SpinnerNumberModel(170, 50, 800, 10));
        MapSizeSpnr.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));

        ValuedTargetsChk = new JCheckBox("Use Valued Targets");
        ValuedTargetsChk.setOpaque(false);
        ValuedTargetsChk.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        ValuedTargetsChk.setBounds(73, 196, 169, 23);
        selectionPlasmaPnl.add(ValuedTargetsChk);

        ValuedHazardsChk = new JCheckBox("Use Valued Hazards");
        ValuedHazardsChk.setOpaque(false);
        ValuedHazardsChk.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        ValuedHazardsChk.setBounds(373, 196, 169, 23);
        selectionPlasmaPnl.add(ValuedHazardsChk);

        JPanel selectionFilePnl = new JPanel();
        TypeSelector.addTab("From File", null, selectionFilePnl, null);
        selectionFilePnl.setLayout(null);

        JLabel fileLocLbl = new JLabel("File Location:");
        fileLocLbl.setBounds(10, 13, 142, 21);
        selectionFilePnl.add(fileLocLbl);
        fileLocLbl.setHorizontalAlignment(SwingConstants.TRAILING);
        fileLocLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));

        FileLocTxt = new JTextField();
        FileLocTxt.setBounds(162, 11, 338, 25);
        selectionFilePnl.add(FileLocTxt);
        FileLocTxt.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        FileLocTxt.setColumns(10);

        JButton fileLocBtn = new JButton("Browse...");
        fileLocBtn.setBounds(510, 13, 105, 25);
        selectionFilePnl.add(fileLocBtn);
        fileLocBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser finder = new JFileChooser();
                finder.setFileFilter(new MapFileFilter());
                finder.setApproveButtonText("Choose");
                int option = finder.showOpenDialog(getParent());
                if (option == JFileChooser.APPROVE_OPTION) {
                    FileLocTxt.setText(finder.getSelectedFile().getAbsolutePath());
                }
            }
        });

        SaveConfigLbl = new JLabel("<HTML><U>Save Run Configuration</U></HTML>");
        SaveConfigLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                saveCurrentConfiguration();
            }
        });
        SaveConfigLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        SaveConfigLbl.setForeground(UIManager.getColor("TabbedPane.darkShadow"));
        SaveConfigLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        SaveConfigLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        SaveConfigLbl.setBounds(1729, 842, 156, 21);
        this.add(SaveConfigLbl);

        TimingOptionsTtl = new JLabel("Timing Options");
        TimingOptionsTtl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        TimingOptionsTtl.setBounds(740, 369, 125, 21);
        this.add(TimingOptionsTtl);

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
        AccelChk.setOpaque(false);
        AccelChk.setBounds(750, 397, 181, 23);
        this.add(AccelChk);

        RuntimeLbl = new JLabel("Run Time:");
        RuntimeLbl.setEnabled(false);
        RuntimeLbl.setHorizontalAlignment(SwingConstants.TRAILING);
        RuntimeLbl.setHorizontalTextPosition(SwingConstants.LEADING);
        RuntimeLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        RuntimeLbl.setBounds(740, 427, 90, 21);
        this.add(RuntimeLbl);

        RuntimeSpnr = new JSpinner();
        RuntimeSpnr.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                RunAltLbl.setText("(" + (int)((Integer)RuntimeSpnr.getValue()*60/ Globals.getInstance().getTimeAccelerant()*3) + " min)");
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
        this.add(RuntimeSpnr);

        RunUnitsLbl = new JLabel("Hours");
        RunUnitsLbl.setEnabled(false);
        RunUnitsLbl.setHorizontalTextPosition(SwingConstants.LEADING);
        RunUnitsLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
        RunUnitsLbl.setBounds(937, 427, 44, 21);
        this.add(RunUnitsLbl);

        RunAltLbl = new JLabel("( min)");
        RunAltLbl.setEnabled(false);
        RunAltLbl.setHorizontalTextPosition(SwingConstants.LEADING);
        RunAltLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        RunAltLbl.setBounds(991, 427, 149, 21);
        this.add(RunAltLbl);
        RuntimeSpnr.setValue((Integer)RuntimeSpnr.getValue()+1);
        RuntimeSpnr.setValue((Integer)RuntimeSpnr.getValue()-1);
        
        roverPhysicsParamScrl = new JScrollPane();
        roverPhysicsParamScrl.setBounds(10, 248, 200, 107);
        add(roverPhysicsParamScrl);
        
        roverPhysicsParamTbl = new JTable();
        roverPhysicsParamTbl.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"Parameter", "Value"
        	}
        ) {
        	Class[] columnTypes = new Class[] {
        		String.class, Double.class
        	};
        	public Class getColumnClass(int columnIndex) {
        		return columnTypes[columnIndex];
        	}
        });
        roverPhysicsParamTbl.getColumnModel().getColumn(0).setResizable(false);
        roverPhysicsParamTbl.getColumnModel().getColumn(1).setResizable(false);
        roverPhysicsParamTbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
        roverPhysicsParamScrl.setViewportView(roverPhysicsParamTbl);
        
        roverCodeParamScrl = new JScrollPane();
        roverCodeParamScrl.setBounds(220, 248, 200, 107);
        add(roverCodeParamScrl);
        
        roverCodeParamTbl = new JTable();
        roverCodeParamTbl.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"Paramter", "Value"
        	}
        ) {
        	Class[] columnTypes = new Class[] {
        		String.class, Double.class
        	};
        	public Class getColumnClass(int columnIndex) {
        		return columnTypes[columnIndex];
        	}
        	boolean[] columnEditables = new boolean[] {
        		false, true
        	};
        	public boolean isCellEditable(int row, int column) {
        		return columnEditables[column];
        	}
        });
        roverCodeParamTbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
        roverCodeParamScrl.setViewportView(roverCodeParamTbl);
        
        satPhysicsParamScrl = new JScrollPane();
        satPhysicsParamScrl.setBounds(10, 773, 200, 107);
        add(satPhysicsParamScrl);
        
        satPhysicsParamTbl = new JTable();
        satPhysicsParamTbl.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"Parameter", "Value"
        	}
        ) {
        	Class[] columnTypes = new Class[] {
        		String.class, Double.class
        	};
        	public Class getColumnClass(int columnIndex) {
        		return columnTypes[columnIndex];
        	}
        });
        satPhysicsParamTbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
        satPhysicsParamScrl.setViewportView(satPhysicsParamTbl);
        
        satCodeParamScrl = new JScrollPane();
        satCodeParamScrl.setBounds(220, 773, 200, 107);
        add(satCodeParamScrl);
        
        satCodeParamTbl = new JTable();
        satCodeParamTbl.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"Parameter", "Value"
        	}
        ) {
        	Class[] columnTypes = new Class[] {
        		String.class, Double.class
        	};
        	public Class getColumnClass(int columnIndex) {
        		return columnTypes[columnIndex];
        	}
        	boolean[] columnEditables = new boolean[] {
        		false, true
        	};
        	public boolean isCellEditable(int row, int column) {
        		return columnEditables[column];
        	}
        });
        satCodeParamTbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
        satCodeParamScrl.setViewportView(satCodeParamTbl);

    }

    private void align(){
        int spacing = 10;
        Rectangle panelSize = super.getWorkingBounds();
        int listWidth = (int) (((panelSize.getWidth() / 2) - spacing*4 - RovAddBtn.getWidth()) / 3 - 50);
        int listHeight = (int) ((panelSize.getHeight() - spacing*6 - RovAddTtl.getHeight() - RovDriveModelLbl.getHeight() - SatAddTtl.getHeight() - SatDriveModelLbl.getHeight()) / 2);
        this.RovAddTtl.setLocation(spacing, super.getTopOfPage()+spacing);
        this.RovDriveModelLbl.setLocation(RovAddTtl.getX(), RovAddTtl.getY() + RovAddTtl.getHeight() + spacing);
        this.RovPhysicsModelList.setBounds(RovDriveModelLbl.getX(), RovDriveModelLbl.getY()+RovDriveModelLbl.getHeight(), listWidth, listHeight/2);
        this.roverPhysicsParamScrl.setBounds(RovDriveModelLbl.getX(), RovPhysicsModelList.getY()+RovPhysicsModelList.getHeight()+5, listWidth, RovPhysicsModelList.getY()+listHeight-(RovPhysicsModelList.getY()+RovPhysicsModelList.getHeight()+5));
        this.RovAutonomousCodeList.setBounds(RovPhysicsModelList.getX()+ RovPhysicsModelList.getWidth()+spacing, RovPhysicsModelList.getY(), listWidth, this.RovPhysicsModelList.getHeight());
        this.roverCodeParamScrl.setBounds(RovAutonomousCodeList.getX(), roverPhysicsParamScrl.getY(), roverPhysicsParamScrl.getWidth(), roverPhysicsParamScrl.getHeight());
        this.RovAutonomousCodeLbj.setLocation(RovAutonomousCodeList.getX(), RovAutonomousCodeList.getY() - RovAutonomousCodeLbj.getHeight());
        this.RovAddBtn.setLocation(RovAutonomousCodeList.getX() + RovAutonomousCodeList.getWidth() + spacing, RovAutonomousCodeList.getY());
        this.RovRemoveBtn.setLocation(RovAddBtn.getX(), RovAddBtn.getY()+RovAddBtn.getHeight()+spacing);
        this.RoverList.setBounds(RovAddBtn.getX()+RovAddBtn.getWidth()+spacing, this.RovAutonomousCodeList.getY(), listWidth, listHeight);
        this.RoversListLbl.setLocation(RoverList.getX(), this.RovAutonomousCodeLbj.getY());

        this.SatAddTtl.setLocation(RovAddTtl.getX(), roverPhysicsParamScrl.getY()+ roverPhysicsParamScrl.getHeight()+spacing*2);
        this.SatDriveModelLbl.setLocation(SatAddTtl.getX(), SatAddTtl.getY()+SatAddTtl.getHeight()+spacing);
        this.SatPhysicsModelList.setBounds(SatDriveModelLbl.getX(), SatDriveModelLbl.getY()+SatDriveModelLbl.getHeight(), listWidth, RovPhysicsModelList.getHeight());
        this.satPhysicsParamScrl.setBounds(SatPhysicsModelList.getX(), SatPhysicsModelList.getY()+SatPhysicsModelList.getHeight()+5, roverPhysicsParamScrl.getWidth(), roverPhysicsParamScrl.getHeight());
        this.SatAutonomousCodeList.setBounds(SatPhysicsModelList.getX()+ SatPhysicsModelList.getWidth()+spacing, SatPhysicsModelList.getY(), listWidth, RovPhysicsModelList.getHeight());
        this.satCodeParamScrl.setBounds(SatAutonomousCodeList.getX(), satPhysicsParamScrl.getY(), satPhysicsParamScrl.getWidth(), satPhysicsParamScrl.getHeight());
        this.SatAutonomousCodeLbl.setLocation(SatAutonomousCodeList.getX(), SatAutonomousCodeList.getY() - SatAutonomousCodeLbl.getHeight());
        this.SatAddBtn.setLocation(SatAutonomousCodeList.getX() + SatAutonomousCodeList.getWidth() + spacing, SatAutonomousCodeList.getY());
        this.SatRemoveBtn.setLocation(SatAddBtn.getX(), SatAddBtn.getY()+SatAddBtn.getHeight()+spacing);
        this.SatelliteList.setBounds(SatAddBtn.getX()+SatAddBtn.getWidth()+spacing, this.SatAutonomousCodeList.getY(), listWidth, listHeight);
        this.SatelliteListLbl.setLocation(SatelliteList.getX(), this.SatAutonomousCodeLbl.getY());

        this.MapConfigTtl.setLocation(RoverList.getX()+RoverList.getWidth()+spacing*3, this.RovAddTtl.getY());
        this.TypeSelector.setLocation(MapConfigTtl.getX(), MapConfigTtl.getY()+MapConfigTtl.getHeight()+spacing);

        this.TimingOptionsTtl.setLocation(MapConfigTtl.getX(), TypeSelector.getY()+TypeSelector.getHeight()+spacing*4);
        this.AccelChk.setLocation(TimingOptionsTtl.getX(), TimingOptionsTtl.getY()+TimingOptionsTtl.getHeight()+spacing);
        this.RuntimeLbl.setLocation(AccelChk.getX(), AccelChk.getY()+AccelChk.getHeight()+spacing);
        this.RuntimeSpnr.setLocation(RuntimeLbl.getX()+RuntimeLbl.getWidth()+spacing, RuntimeLbl.getY()-2);
        this.RunUnitsLbl.setLocation(RuntimeSpnr.getX()+RuntimeSpnr.getWidth()+spacing, RuntimeLbl.getY());
        this.RunAltLbl.setLocation(RunUnitsLbl.getX()+RunUnitsLbl.getWidth()+spacing, RuntimeLbl.getY());

        this.StartBtn.setLocation((int)panelSize.getWidth()-StartBtn.getWidth()-spacing, (int)this.getHeight()-StartBtn.getHeight()-spacing);
        this.SaveConfigLbl.setLocation((int)panelSize.getWidth()-SaveConfigLbl.getWidth()-spacing, StartBtn.getY()-spacing-SaveConfigLbl.getHeight());
    }
    
    private TableModel generateTableModel(List<String> options){
    	Object[][] entries = new Object[options.size()][2];
    	for (int i = 0; i < options.size(); i++){
    		entries[i][0] = options.get(i);
    		entries[i][1] = null;
    	}
    	return new DefaultTableModel(
            	entries,
            	new String[] {
            		"Parameter", "Value"
            	}
            ) {
            	Class[] columnTypes = new Class[] {
            		String.class, Double.class
            	};
            	public Class getColumnClass(int columnIndex) {
            		return columnTypes[columnIndex];
            	}
            	boolean[] columnEditables = new boolean[] {
            		false, true
            	};
            	public boolean isCellEditable(int row, int column) {
            		return columnEditables[column];
            	}
            };
    }
    
    private Map<String, Double> getParametersFromTableModel(TableModel model) throws NoSuchElementException {
    	Map<String, Double> params = new TreeMap<String, Double>();
    	for (int i = 0; i < model.getRowCount(); i++){
    		if (model.getValueAt(i, 1) == null){
    			throw new NoSuchElementException();
    		}
    		else {
    			 try {
    				 params.put((String)model.getValueAt(i,  0), (Double)model.getValueAt(i, 1));
    			 }
    			 catch (ClassCastException e){
    				 throw new NoSuchElementException(e.getMessage());
    			 }
    		}
    	}
    	return params;
    }

    public void saveCurrentConfiguration(){
        new FreeThread(0, new Runnable(){
            public void run(){
                File config = new File("config.json");
                if (config.exists()){
                    if ((new PopUp()).showConfirmDialog("There is already a quick run file saved would you like to overwrite it?", "Save Configuration", PopUp.YES_NO_OPTIONS) == PopUp.YES_OPTION){
                        try {
                            getConfigurationFromForm().Save(config);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            (new PopUp()).showConfirmDialog("Something went wrong and the operation was aborted.", "Save Configuration", PopUp.OK_OPTION);
                        }
                    }
                }
                else {
                    try {
                        getConfigurationFromForm().Save(config);
                        (new PopUp()).showConfirmDialog("Startup configuration was successfully saved.", "Save Configuration", PopUp.OK_OPTION);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        (new PopUp()).showConfirmDialog("Something went wrong and the operation was aborted.", "Save Configuration", PopUp.OK_OPTION);
                    }
                }
            }
        }, 1, "config-save");
    }

    public RunConfiguration getConfigurationFromForm(){
        ArrayList<PlatformConfig> platforms = new ArrayList<PlatformConfig>(roversToAdd.size()+satsToAdd.size());
        platforms.addAll(roversToAdd.values());
        platforms.addAll(satsToAdd.values());
        NamesAndTags namesAndTags = NamesAndTags.newFromPlatforms(platforms);
        if (TypeSelector.getSelectedIndex() == 1){
            File mapFile = new File(FileLocTxt.getText());
            return new RunConfiguration(namesAndTags, platforms, mapFile, AccelChk.isSelected(),
                    (Integer)RuntimeSpnr.getValue());
        }
        else {
            double mapRough = MapRoughSlider.getValue()/50000.0;
            int mapSize = (Integer) MapSizeSpnr.getValue();
            int mapDetail = (Integer) MapDetailSpnr.getValue();
            double targetDensity = (Double) TargetDensitySpnr.getValue()/1000.;
            double hazardDensity = (Double) HazardDensitySpnr.getValue()/1000.;
            boolean monoTargets = !ValuedTargetsChk.isSelected(); //cause the form says use and the computer reads not using
            boolean monoHazards = !ValuedHazardsChk.isSelected();
            return new RunConfiguration(namesAndTags, platforms, generateTempMap(mapRough,
                    mapSize, mapDetail, targetDensity, hazardDensity, monoTargets,
                    monoHazards), AccelChk.isSelected(),
                    (Integer)RuntimeSpnr.getValue());
        }
    }

    private File generateTempMap(double mapRough,
                                 int mapSize,
                                 int mapDetail,
                                 double targetDensity,
                                 double hazardDensity,
                                 boolean monoTargets,
                                 boolean monoHazards){
        TerrainEnvironment terrainEnvironment = new TerrainEnvironment();
        terrainEnvironment.generateNewMap(new PlasmaFractalGen(), ParamMap.newParamMap()
                        .addParameter("size", mapSize)
                        .addParameter("detail", mapDetail)
                        .addParameter("rough", 0.003)
                        .addParameter("range", 10)
                        .build()
                )
                .addMapModifier(new SmoothingModifier(), ParamMap.emptyParamMap())
                .addMapModifier(new TruncateModifier(), ParamMap.newParamMap().addParameter("places", 4).build())
                .addPopulator("Targets", new TerrainTargetsPop(), ParamMap.newParamMap()
                        .addParameter("trgt_density", 0.02)
                        .addParameter("mono", 1)
                        .build())
                .addPopulator("Hazards", new TerrainHazardsPop(), ParamMap.emptyParamMap())
                .generate();
        Random rnd = new Random();
        File tempFile;
        do {
            tempFile = new File(String.format("Temp/%d.map", (int) (rnd.nextDouble() * 10000)));
        } while (tempFile.exists());
        EnvironmentIO.saveEnvironment(terrainEnvironment, tempFile);
        return tempFile;
    }

    public void addRoverToList(){
        if (RovAutonomousCodeList.getSelectedIndex() != -1 && RovPhysicsModelList.getSelectedIndex() != -1){
            int numb = 1;
            String namebase = RovAutonomousCodeList.getSelectedItem();
            if (RovAutonomousCodeList.getSelectedItem().equals("[null]")){
                namebase = "Rover";
            }
            String newName = namebase + " " + numb;
            while (RoverList.getItems().contains(newName)){
                numb++;
                newName = namebase + " " + numb;
            }
            try {
	            PlatformConfig cfg = PlatformConfig.builder()
	                    .setType("Rover")
	                    .setScreenName(newName)
	                    .setID("r" + (RoverList.getItems().size()+1))
	                    .setAutonomousModel(RovAutonomousCodeList.getSelectedItem(), getParametersFromTableModel(roverCodeParamTbl.getModel()))
	                    .setPhysicsModel(RovPhysicsModelList.getSelectedItem(), getParametersFromTableModel(roverPhysicsParamTbl.getModel()))
	                    .build();
	            roversToAdd.put(newName, cfg);
	            RoverList.addValue(newName);
            }
            catch (NoSuchElementException e){
            	//The parameter table was incomplete
            }
        }
    }

    public void removeRoverFromList(){
        try {
            roversToAdd.remove(RoverList.getSelectedItem());
            RoverList.removeValue(RoverList.getSelectedIndex());
        }
        catch (NullPointerException e){
            //Nothing selected: don't worry about it
        }
        catch (Exception e){
            LOG.log(Level.WARN, "trouble removing", e);
        }
    }

    public void addSatelliteToList(){
        try {
            if (SatAutonomousCodeList.getSelectedIndex() != -1 && SatPhysicsModelList.getSelectedIndex() != -1){
                int numb = 1;
                String newName = SatAutonomousCodeList.getSelectedItem() + " " + numb;
                while (SatelliteList.getItems().contains(newName)){
                    numb++;
                    newName = SatAutonomousCodeList.getSelectedItem() + " " + numb;
                }
                try {
		            PlatformConfig cfg = PlatformConfig.builder()
		                    .setType("Satellite")
		                    .setScreenName(newName)
		                    .setID("s"+(SatelliteList.getItems().size()+1))
		                    .setAutonomousModel(SatAutonomousCodeList.getSelectedItem(), getParametersFromTableModel(satCodeParamTbl.getModel()))
		                    .setPhysicsModel(SatPhysicsModelList.getSelectedItem(), getParametersFromTableModel(satPhysicsParamTbl.getModel()))
		                    .build();
		            satsToAdd.put(newName, cfg);
		            SatelliteList.addValue(newName);
                }
                catch (NoSuchElementException e){
                	//The parameter table was incomplete
                }
            }
        }
        catch (Exception e) {
            LOG.log(Level.ERROR, "Failed to add Satellite to list", e);
        }
    }

    public void removeSatelliteFromList(){
        try {
            satsToAdd.remove(SatelliteList.getSelectedItem());
            SatelliteList.removeValue(SatelliteList.getSelectedIndex());
        }
        catch (NullPointerException e){
            //Nothing selected: don't worry about it
        }
        catch (Exception e){
            LOG.log(Level.WARN, "trouble removing", e);
        }
    }

    public void addItemToRoverPhysicsList(String name){
        RovPhysicsModelList.addValue(name);
    }

    public void addItemToRoverAutoList(String name){
        RovAutonomousCodeList.addValue(name);
    }

    public void addItemToSatellitePhysicsList(String name){
        SatPhysicsModelList.addValue(name);
    }

    public void addItemToSatelliteAutoList(String name){
        SatAutonomousCodeList.addValue(name);
    }
}
