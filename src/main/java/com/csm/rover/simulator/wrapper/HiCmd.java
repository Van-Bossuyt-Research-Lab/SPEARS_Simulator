package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.satellite.SatelliteObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class HiCmd implements HumanInterfaceAbstraction {
    private final static Logger LOG = LogManager.getLogger(HiCmd.class);

    @Override
    public void initialize(NamesAndTags namesAndTags, SerialBuffers buffers, ArrayList<RoverObject> rovers, ArrayList<SatelliteObject> satellites, TerrainMap map) {

    }

    @Override
    public void start() {

    }

    @Override
    public void updateRovers() {

    }

    @Override
    public void updateSatellites() {

    }

    @Override
    public void updateRover(String name, DecimalPoint location, double direction) {

    }

    @Override
    public void updateSatellite(String name) {

    }

    @Override
    public void updateSerialBuffers() {

    }

    @Override
    public void viewAccelerated(int runtime, double accelerant) {

    }

    @Override
    public void exit() {
        //exit procedures
        LOG.log(Level.INFO, "Exiting SPEARS");
        System.exit(0);
    }
}
