package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.control.InterfaceAccess;
import com.csm.rover.simulator.control.InterfacePanel;
import com.csm.rover.simulator.map.PlanetParametersList;
import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.map.display.LandMapPanel;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.FreeThread;
import com.csm.rover.simulator.rover.RoverHub;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.rover.autoCode.*;
import com.csm.rover.simulator.rover.phsicsModels.FailOnHazard;
import com.csm.rover.simulator.rover.phsicsModels.RiskOnHazard;
import com.csm.rover.simulator.rover.phsicsModels.RoverPhysicsModel;
import com.csm.rover.simulator.satellite.SatelliteAutonomusCode;
import com.csm.rover.simulator.satellite.SatelliteHub;
import com.csm.rover.simulator.satellite.SatelliteObject;
import com.csm.rover.simulator.satellite.SatelliteParametersList;
import com.csm.rover.simulator.visual.AccelPopUp;
import com.csm.rover.simulator.visual.Form;
import com.csm.rover.simulator.visual.Panel;
import com.csm.rover.simulator.visual.StartupPanel;

import java.awt.*;
import java.util.ArrayList;

public class HiForm implements HumanInterfaceAbstraction {

    private boolean init;

    private Form GUI;
    private StartupPanel startupPnl;

    private MainWrapper wrapperPnl;
    private Panel orbitalPnl;
    private LandMapPanel terrainPnl;
    private InterfacePanel interfacePnl;
    private RoverHub roverHubPnl;
    private SatelliteHub satelliteHubPnl;

    private AccelPopUp informer;

    public HiForm(){
        init = false;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        startupPnl = new StartupPanel(screenSize);
        GUI = new Form(screenSize, startupPnl);
        setUpSelectionLists();
        GUI.setVisible(true);
    }

    @Override
    public void initialize(NamesAndTags namesAndTags, SerialBuffers buffers, ArrayList<RoverObject> rovers, ArrayList<SatelliteObject> satellites, TerrainMap map) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        wrapperPnl = new MainWrapper(screenSize, buffers, namesAndTags);
        orbitalPnl = new Panel(screenSize, "Orbital View");
        roverHubPnl = new RoverHub(screenSize, buffers, rovers, map);
        terrainPnl = new LandMapPanel(screenSize, new PlanetParametersList(), roverHubPnl, rovers, map);
        interfacePnl = new InterfacePanel(screenSize, buffers);
        satelliteHubPnl = new SatelliteHub(screenSize, satellites);
        GUI.setRunTimePanels(wrapperPnl, orbitalPnl, terrainPnl, interfacePnl, roverHubPnl, satelliteHubPnl);
        init = true;
        InterfaceAccess.CODE.setCallTags(namesAndTags);
        roverHubPnl.setIdentifiers(namesAndTags.getRoverTags(), namesAndTags.getSatelliteTags());
    }

    private void setUpSelectionLists(){
        //addItemToSelectionList(	name_on_list ,	object_to_add	);
        startupPnl.addItemToSelectionList("Default", new RoverPhysicsModel());
        startupPnl.addItemToSelectionList("Fail On Hazard", new FailOnHazard());
        startupPnl.addItemToSelectionList("Risk On Hazard", new RiskOnHazard());
        startupPnl.addItemToSelectionList("[null]", new BlankRoverAuto());
        startupPnl.addItemToSelectionList("Generic4", new GenericRover("Generic4", 4));
        startupPnl.addItemToSelectionList("RAIR", new RAIRcode());
        startupPnl.addItemToSelectionList("RAIR Control", new RAIRcodeControl());
        startupPnl.addItemToSelectionList("RAIR Risk Averse", new RAIRcodeRA());
        startupPnl.addItemToSelectionList("RAIR Risk Seeking", new RAIRcodeRS());
        startupPnl.addItemToSelectionList("RAIR Risk Temper", new RAIRcodeRT());
        startupPnl.addItemToSelectionList("PIDAA", new PIDAAcode3());
        startupPnl.addItemToSelectionList("PIDAA CONTROL", new PIDAAcontrol());
        startupPnl.addItemToSelectionList("GORARO Simp", new GORAROcode1());
        startupPnl.addItemToSelectionList("GORARO Adv A", new GORAROAdvanceCode(new double[]{
                10000, 84.2892, 567.513, 7.37412, 0
        }));
        /*wrapperPnl.addItemToSelectionList("GORARO Adv B", new GORAROAdvanceCode(new double[]{
                10000, 80.44, 972.8, 25.84, 0
        }));
        wrapperPnl.addItemToSelectionList("GORARO Adv C", new GORAROAdvanceCode(new double[]{
                10000, 100, 500, 2, 0
        }));
        wrapperPnl.addItemToSelectionList("GORARO Adv D", new GORAROAdvanceCode(new double[]{
                10000, 40, 600, 2, 0
        }));
        wrapperPnl.addItemToSelectionList("GORARO Adv E", new GORAROAdvanceCode(new double[]{
                10000, 70, 600, 2, 0
        }));
        wrapperPnl.addItemToSelectionList("GORARO Adv F", new GORAROAdvanceCode(new double[]{
                10000, 100, 600, 2, 0
        }));
        wrapperPnl.addItemToSelectionList("GORARO Adv G", new GORAROAdvanceCode(new double[]{
                10000, 40, 700, 2, 0
        }));
        wrapperPnl.addItemToSelectionList("GORARO Adv H", new GORAROAdvanceCode(new double[]{
                10000, 70, 700, 2, 0
        }));*/
        startupPnl.addItemToSelectionList("MER", new MER(new DecimalPoint[]{
                new DecimalPoint(-9.5, -9.5),
                new DecimalPoint(-12.5, -5.5),
                new DecimalPoint(-17.5, -9.5),
                new DecimalPoint(-10.5, -16.5),
                new DecimalPoint(-2.5, -38.5),
                new DecimalPoint(8.5, -37.5),
                new DecimalPoint(15.5, -36.5),
                new DecimalPoint(21.5, -49.5),
                new DecimalPoint(22.5, -60.5),
                new DecimalPoint(23.5, -70.5),
                new DecimalPoint(14.5, -65.5),
                new DecimalPoint(13.5, -81.5),
                new DecimalPoint(6.5, -81.5),
                new DecimalPoint(2.5, -77.5),
                new DecimalPoint(3.5, -83.5),
                new DecimalPoint(0.5, -89.5),
                new DecimalPoint(-30.5, -109.5),
                new DecimalPoint(-26.5, -117.5),
                new DecimalPoint(-43.5, -127.5),
                new DecimalPoint(-54.5, -146.5),
                new DecimalPoint(-54.5, -149.5),
                new DecimalPoint(-57.5, -147.5),
                new DecimalPoint(-56.5, -159.5),
                new DecimalPoint(-59.5, -160.5),
                new DecimalPoint(-64.5, -165.5),
                new DecimalPoint(-61.5, -174.5),
                new DecimalPoint(-55.5, -182.5),
                new DecimalPoint(-57.5, -184.5),
                new DecimalPoint(-59.5, -189.5),
                new DecimalPoint(-43.5, -193.5),
                new DecimalPoint(-39.5, -190.5),
                new DecimalPoint(-38.5, -200.5),
                new DecimalPoint(-24.5, -198.5),
                new DecimalPoint(-25.5, -193.5),
                new DecimalPoint(-38.5, -212.5),
                new DecimalPoint(-21.5, -219.5),
                new DecimalPoint(-8.5, -223.5),
                new DecimalPoint(-4.5, -224.5),
                new DecimalPoint(-5.5, -227.5)
        }));
        //wrapperPnl.addItemToSelectionList(		"PIDAA 2",		new PIDAAcode2());
        startupPnl.addItemToSelectionList("[null]", (SatelliteAutonomusCode) null);
        startupPnl.addItemToSelectionList("[null]", (SatelliteParametersList) null);
        startupPnl.addItemToSelectionList("GORADRO-G", new GORADROGuided(new Point[]{
                new Point(20, 20),
                new Point(10, -15),
                new Point(0, 0)
        }, -2));
    }

    @Override
    public void start(){
        interfacePnl.CODE.start();
        roverHubPnl.start();
        satelliteHubPnl.start();
    }

    @Override
    public void updateRovers() {
        if (init){
            roverHubPnl.updateDisplays();
        }
    }

    @Override
    public void updateSatellites() {
        if (init){
            satelliteHubPnl.updateDisplays();
        }
    }

    @Override
    public void updateRover(String name, DecimalPoint location, double direction) {
        if (init){
            terrainPnl.updateRover(name, location, direction);
        }
    }

    @Override
    public void updateSatellite(String name) {

    }

    @Override
    public void updateSerialBuffers() {
        if (init){
            wrapperPnl.updateSerialDisplays();
        }
    }

    @Override
    public void viewAccelerated(int runtime, double accelerant) {
        if (init){
            GUI.setVisible(false);
            informer = new AccelPopUp(runtime, (int) (runtime/accelerant/60000));
            new FreeThread(1000, new Runnable(){
                public void run(){
                    informer.update((int) Globals.getInstance().timeMillis);
                }
            }, FreeThread.FOREVER, "accel-pop-up");
        }
    }

    @Override
    public void exit(){
        GUI.exit();
    }
}
