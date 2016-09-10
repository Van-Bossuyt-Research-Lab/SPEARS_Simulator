package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.platforms.satellite.SatelliteObject;

import java.util.ArrayList;

public interface HumanInterfaceAbstraction {

    void initialize(NamesAndTags namesAndTags,
                    SerialBuffers buffers,
                    ArrayList<RoverObject> rovers,
                    ArrayList<SatelliteObject> satellites,
                    PlatformEnvironment map);

    void start();

    void updateRovers();

    void updateSatellites();

    void updateSubs();

    void updateRover(String name, DecimalPoint location, double direction);

    void updateSatellite(String name);

    void updateSub(String name, double[] location, double[] direction);

    void updateSerialBuffers();

    void viewAccelerated(int runtime, double accelerant);

    void exit();

}
