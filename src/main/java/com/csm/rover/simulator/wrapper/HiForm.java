package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.control.InterfaceAccess;
import com.csm.rover.simulator.control.InterfacePanel;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.rover.TerrainEnvironment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.objects.util.FreeThread;
import com.csm.rover.simulator.platforms.PlatformRegistry;
import com.csm.rover.simulator.platforms.rover.RoverHub;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.platforms.satellite.SatelliteHub;
import com.csm.rover.simulator.platforms.satellite.SatelliteObject;
import com.csm.rover.simulator.visual.AccelPopUp;
import com.csm.rover.simulator.visual.Form;
import com.csm.rover.simulator.visual.Panel;
import com.csm.rover.simulator.visual.StartupPanel;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

public class HiForm implements HumanInterfaceAbstraction {

    private boolean init;

    private Form GUI;
    private StartupPanel startupPnl;

    private MainWrapper wrapperPnl;
    private Panel orbitalPnl;
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
    public void initialize(NamesAndTags namesAndTags, SerialBuffers buffers, ArrayList<RoverObject> rovers, ArrayList<SatelliteObject> satellites, PlatformEnvironment map) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        wrapperPnl = new MainWrapper(screenSize, buffers, namesAndTags);
        orbitalPnl = new Panel(screenSize, "Orbital View");
        roverHubPnl = new RoverHub(screenSize, buffers, rovers, (TerrainEnvironment)map);
        interfacePnl = new InterfacePanel(screenSize, buffers);
        satelliteHubPnl = new SatelliteHub(screenSize, satellites);
        GUI.setRunTimePanels(wrapperPnl, orbitalPnl, new Panel(screenSize, "Terrain View"), interfacePnl, roverHubPnl, satelliteHubPnl);
        init = true;
        InterfaceAccess.CODE.setCallTags(namesAndTags);
        roverHubPnl.setIdentifiers(namesAndTags.getTags("Rover"), namesAndTags.getTags("Satellite"));
    }

    private void setUpSelectionLists(){
        for (String name : PlatformRegistry.listAutonomousCodeModels("Rover")){
            startupPnl.addItemToRoverAutoList(name);
        }
        for (String name : PlatformRegistry.listPhysicsModels("Rover")){
            startupPnl.addItemToRoverPhysicsList(name);
        }
        for (String name : PlatformRegistry.listAutonomousCodeModels("Satellite")){
            startupPnl.addItemToSatelliteAutoList(name);
        }
        for (String name : PlatformRegistry.listPhysicsModels("Satellite")){
            startupPnl.addItemToSatellitePhysicsList(name);
        }
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
