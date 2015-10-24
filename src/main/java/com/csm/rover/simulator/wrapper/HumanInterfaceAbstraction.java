package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.satellite.SatelliteObject;

import java.util.ArrayList;

public interface HumanInterfaceAbstraction {

    void initialize(NamesAndTags namesAndTags,
                    SerialBuffers buffers,
                    ArrayList<RoverObject> rovers,
                    ArrayList<SatelliteObject> satellites,
                    TerrainMap map);

    void start();

    void updateRovers();

    void updateSatellites();

    void updateRover(String name, DecimalPoint location, double direction);

    void updateSatellite(String name);

    void updateSerialBuffers();

    void viewAccelerated(int runtime);

    void exit();

}
