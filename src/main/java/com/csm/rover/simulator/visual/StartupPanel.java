package com.csm.rover.simulator.visual;

import com.csm.rover.simulator.control.PopUp;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.FreeThread;
import com.csm.rover.simulator.objects.MapFileFilter;
import com.csm.rover.simulator.objects.RunConfiguration;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.rover.autoCode.RoverAutonomousCode;
import com.csm.rover.simulator.rover.phsicsModels.RoverPhysicsModel;
import com.csm.rover.simulator.satellite.SatelliteAutonomusCode;
import com.csm.rover.simulator.satellite.SatelliteObject;
import com.csm.rover.simulator.satellite.SatelliteParametersList;
import com.csm.rover.simulator.wrapper.Admin;
import com.csm.rover.simulator.wrapper.Globals;
import com.csm.rover.simulator.wrapper.NamesAndTags;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static java.util.Arrays.asList;

public class StartupPanel extends Panel {
    private static final Logger LOG = LogManager.getLogger(StartupPanel.class);

    Random rnd = new Random();

    private Globals GLOBAL;

    private Map<String, RoverPhysicsModel> roverParameters = new TreeMap<String, RoverPhysicsModel>();
    private Map<String, RoverAutonomousCode> roverLogics = new TreeMap<String, RoverAutonomousCode>();
    private Map<String, SatelliteParametersList> satelliteParameters = new TreeMap<String, SatelliteParametersList>();
    private Map<String, SatelliteAutonomusCode> satelliteLogics = new TreeMap<String, SatelliteAutonomusCode>();
    private Map<String, RoverObject> roversToAdd = new TreeMap<String, RoverObject>();
    private Map<String, SatelliteObject> satsToAdd = new TreeMap<String, SatelliteObject>();

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

    public StartupPanel(Dimension size){
        super(size, "Start Up");
        GLOBAL = Globals.getInstance();
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

        RovDriveModelList = new ZList();
        RovDriveModelList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        RovDriveModelList.setBounds(10, 63, 200, 250);
        this.add(RovDriveModelList);

        RovAutonomusCodeLbl = new JLabel("Autonomous Logic");
        RovAutonomusCodeLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        RovAutonomusCodeLbl.setBounds(220, 43, 142, 21);
        this.add(RovAutonomusCodeLbl);

        RovAutonomusCodeList = new ZList();
        RovAutonomusCodeList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        RovAutonomusCodeList.setBounds(220, 63, 200, 250);
        this.add(RovAutonomusCodeList);

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

        RoverList = new ZList();
        RoverList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        RoverList.setBounds(510, 63, 200, 250);
        this.add(RoverList);

        SatAddTtl = new JLabel("Adding Satellites");
        SatAddTtl.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        SatAddTtl.setBounds(10, 460, 142, 21);
        this.add(SatAddTtl);

        SatDriveModelLbl = new JLabel("Physics Model");
        SatDriveModelLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        SatDriveModelLbl.setBounds(10, 492, 118, 21);
        this.add(SatDriveModelLbl);

        SatDriveModelList = new ZList();
        SatDriveModelList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        SatDriveModelList.setBounds(10, 512, 200, 250);
        this.add(SatDriveModelList);

        SatAutonomusCodeList = new ZList();
        SatAutonomusCodeList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        SatAutonomusCodeList.setBounds(220, 512, 200, 250);
        this.add(SatAutonomusCodeList);

        SatAutonomusCodeLbl = new JLabel("Autonomous Logic");
        SatAutonomusCodeLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        SatAutonomusCodeLbl.setBounds(220, 492, 142, 21);
        this.add(SatAutonomusCodeLbl);

        SatelliteList = new ZList();
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

        MapConfigTtl = new JLabel("Congiure Map");
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

    }

    private void align(){
        int spacing = 10;
        Rectangle panelSize = super.getWorkingBounds();
        int listWidth = (int) (((panelSize.getWidth() / 2) - spacing*4 - RovAddBtn.getWidth()) / 3 - 50);
        int listHeight = (int) ((panelSize.getHeight() - spacing*6 - RovAddTtl.getHeight() - RovDriveModelLbl.getHeight() - SatAddTtl.getHeight() - SatDriveModelLbl.getHeight()) / 2);
        this.RovAddTtl.setLocation(spacing, super.getTopOfPage()+spacing);
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

        this.StartBtn.setLocation((int)panelSize.getWidth()-StartBtn.getWidth()-spacing, (int)this.getHeight()-StartBtn.getHeight()-spacing);
        this.SaveConfigLbl.setLocation((int)panelSize.getWidth()-SaveConfigLbl.getWidth()-spacing, StartBtn.getY()-spacing-SaveConfigLbl.getHeight());
    }

    public void saveCurrentConfiguration(){
        new FreeThread(0, new Runnable(){
            public void run(){
                File config = new File("default.cfg");
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
        ArrayList<String> roverNames = new ArrayList<String>();
        ArrayList<String> roverTags = new ArrayList<String>();
        ArrayList<RoverObject> rovers = new ArrayList<RoverObject>(roversToAdd.size());
        ArrayList<String> satelliteNames = new ArrayList<String>();
        ArrayList<String> satelliteTags = new ArrayList<String>();
        ArrayList<SatelliteObject> satellites = new ArrayList<SatelliteObject>(satsToAdd.size());
        int x = 0;
        while (x < SatelliteList.getItems().size()){
            String key = (String)SatelliteList.getItemAt(x);
            satellites.add(x, satsToAdd.get(key));
            satelliteNames.add(key);
            satelliteTags.add(satellites.get(x).getIDCode());
            x++;
        }
        x = 0;
        while (x < RoverList.getItems().size()){
            String key = (String)RoverList.getItemAt(x);
            rovers.add(roversToAdd.get(key));
            roverNames.add(key);
            roverTags.add(rovers.get(x).getIDTag());
            x++;
        }
        NamesAndTags namesAndTags = new NamesAndTags(roverNames, roverTags, satelliteNames, satelliteTags);
        if (TypeSelector.getSelectedIndex() == 1){
            File mapFile = new File(FileLocTxt.getText());
            return new RunConfiguration(namesAndTags, rovers, satellites, mapFile, AccelChk.isSelected(),
                    (Integer)RuntimeSpnr.getValue());
        }
        else {
            double mapRough = MapRoughSlider.getValue()/50000.0;
            int mapSize = (Integer) MapSizeSpnr.getValue();
            int mapDetail = (Integer) MapDetailSpnr.getValue();
            double targetDensity = (Double) TargetDensitySpnr.getValue()/1000.;
            double hazardDensity = (Double) HazardDensitySpnr.getValue()/1000.;
            boolean monoTargets = !ValuedTargetsChk.isSelected(); //cause the for says use and the computer reads not using
            boolean monoHazards = !ValuedHazardsChk.isSelected();
            return new RunConfiguration(namesAndTags, rovers, satellites, mapRough,
                    mapSize, mapDetail, targetDensity, hazardDensity, monoTargets,
                    monoHazards, AccelChk.isSelected(),
                    (Integer)RuntimeSpnr.getValue());
        }
    }

    public void addRoverToList(){
        if (RovAutonomusCodeList.getSelectedIndex() != -1 && RovDriveModelList.getSelectedIndex() != -1){
            int numb = 1;
            String namebase = (String)RovAutonomusCodeList.getSelectedItem();
            if (RovAutonomusCodeList.getSelectedItem().equals("[null]")){
                namebase = "Rover";
            }
            String newName = namebase + " " + numb;
            while (asList(RoverList.getItems()).contains(newName)){
                numb++;
                newName = namebase + " " + numb;
            }
            //TODO change temp to map temp
            RoverList.addValue(newName);
            //if you're getting errors with rovers 'sharing' data it's the pass reference value here
            RoverAutonomousCode autoCode = roverLogics.get((String)RovAutonomusCodeList.getSelectedItem()).clone();
            autoCode.setRoverName(newName);
            RoverPhysicsModel params = roverParameters.get((String)RovDriveModelList.getSelectedItem()).clone();
            // for randomized start position roversToAdd.add(newName, new RoverObject(newName, "r"+GUI.RoverList.getItems().length, params, autoCode, new DecimalPoint(340*rnd.nextDouble()-170, 340*rnd.nextDouble()-170), 360*rnd.nextDouble(), 0));
            DecimalPoint location = new DecimalPoint(0, 0);
            roversToAdd.put(newName, new RoverObject(newName, "r"+RoverList.getItems().size(), params, autoCode, location, Math.PI/2, -30));
        }
    }

    public void removeRoverFromList(){
        try {
            roversToAdd.remove(RoverList.getSelectedItem().toString());
            RoverList.removeValue(RoverList.getSelectedIndex());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    public void addSatelliteToList(){
        try {
            if (SatAutonomusCodeList.getSelectedIndex() != -1 && SatDriveModelList.getSelectedIndex() != -1){
                int numb = 1;
                //TODO change to code name
                String newName = "Satellite " + numb;
                //newName = (String)GUI.SatAutonomusCodeList.getSelectedItem() + " " + numb;
                while (asList(SatelliteList.getItems()).contains(newName)){
                    numb++;
                    newName = "Satellite " + numb;
                    //newName = (String)GUI.SatAutonomusCodeList.getSelectedItem() + " " + numb;
                }
                SatelliteList.addValue(newName);
                this.satsToAdd.put(newName, new SatelliteObject(newName, "s"+SatelliteList.getItems().size(), null, null, rnd.nextDouble()*100000+10000000, rnd.nextDouble()*90, rnd.nextDouble()*360));
            }
        }
        catch (Exception e) {
            LOG.log(Level.ERROR, "Failed to add Satellite to list", e);
        }
    }

    public void removeSatelliteFromList(){
        try {
            satsToAdd.remove(SatelliteList.getSelectedItem().toString());
            SatelliteList.removeValue(SatelliteList.getSelectedIndex());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addItemToSelectionList(String name, RoverPhysicsModel item){
        roverParameters.put(name, item);
        RovDriveModelList.addValue(name);
    }

    public void addItemToSelectionList(String name, RoverAutonomousCode item){
        roverLogics.put(name, item);
        RovAutonomusCodeList.addValue(name);
    }

    public void addItemToSelectionList(String name, SatelliteParametersList item){
        satelliteParameters.put(name, item);
        SatDriveModelList.addValue(name);
    }

    public void addItemToSelectionList(String name, SatelliteAutonomusCode item){
        satelliteLogics.put(name, item);
        SatAutonomusCodeList.addValue(name);
    }

}
