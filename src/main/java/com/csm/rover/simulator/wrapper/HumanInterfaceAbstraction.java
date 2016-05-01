package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.map.SubMap;
import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.platforms.satellite.SatelliteObject;
import com.csm.rover.simulator.platforms.sub.SubObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface HumanInterfaceAbstraction {

    void initialize(NamesAndTags namesAndTags,
                    SerialBuffers buffers,
                    ArrayList<RoverObject> rovers,
                    ArrayList<SatelliteObject> satellites,
                    TerrainMap map);

    void initialize(NamesAndTags namesAndTags,
                    SerialBuffers buffers,
                    ArrayList<SubObject> subs,
                    SubMap map);

    void start();

    void updateRovers();

    void updateSatellites();

    void updateRover(String name, DecimalPoint location, double direction);

    void updateSatellite(String name);

    void updateSerialBuffers();

    void viewAccelerated(int runtime, double accelerant);

    void exit();

}
