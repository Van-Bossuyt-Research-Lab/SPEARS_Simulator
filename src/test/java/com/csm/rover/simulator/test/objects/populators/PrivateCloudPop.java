package com.csm.rover.simulator.test.objects.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.objects.util.RecursiveGridList;

import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import java.util.Map;

@Populator(type = "UAV", name = "Cloud Pop", parameters = {}, coordinates = { "x", "y", "z" })
public class PrivateCloudPop extends EnvironmentPopulator {

    private PrivateCloudPop() {
        super("UAV", "Cloud Pop", 1);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        return null;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return null;
    }
}
