package com.csm.rover.simulator.test.objects.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.objects.util.RecursiveGridList;

import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import java.util.Map;

@Populator(type = "Mole", name = "Crystal Pop", parameters = {"size"}, coordinates = {"x", "y", "z"})
public class UnknownPop extends EnvironmentPopulator {

    public UnknownPop() {
        super("Mole", "Crystal Pop", 0);
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
