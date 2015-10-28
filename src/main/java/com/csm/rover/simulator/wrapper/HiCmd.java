package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.satellite.SatelliteObject;

import java.util.ArrayList;

public class HiCmd implements HumanInterfaceAbstraction {
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
    public void viewAccelerated(int runtime) {

    }

    @Override
    public void exit() {

    }
}
