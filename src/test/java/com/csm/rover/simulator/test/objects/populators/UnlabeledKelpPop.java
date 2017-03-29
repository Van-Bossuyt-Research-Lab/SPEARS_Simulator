package com.csm.rover.simulator.test.objects.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.objects.util.RecursiveGridList;

import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import java.util.Map;

public class UnlabeledKelpPop extends EnvironmentPopulator {

    public UnlabeledKelpPop() {
        super("Sub", "Kelp Pop", 0);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        RecursiveGridList<Double> out = RecursiveGridList.newGridList(Double.class, 3);
        out.put(5., 2, 6, -1);
        return out;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return null;
    }
}
