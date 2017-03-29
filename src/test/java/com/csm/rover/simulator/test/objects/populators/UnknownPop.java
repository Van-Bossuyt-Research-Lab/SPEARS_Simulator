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
        super("Mole", "Crystal Pop", -1);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        RecursiveGridList<Double> out = RecursiveGridList.newGridList(Double.class, 2);
        out.put(0., 0, 0);
        out.put(0.5, 0, 1);
        out.put(0.5, 1, 0);
        out.put(1., 1, 1);
        //out.put(skipping for test, 0, 2);
        out.put(1.6, 1, 2);
        out.put(1.3, 2, 0);
        out.put(1.6, 2, 1);
        out.put(2., 2, 2);
        return out;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return null;
    }

}
