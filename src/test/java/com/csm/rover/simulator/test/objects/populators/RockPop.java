package com.csm.rover.simulator.test.objects.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.objects.util.RecursiveGridList;

import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;

import java.awt.*;
import java.util.Map;

@Populator(type = "Rover", name = "Rock Pop", parameters = { "paramA", "paramB" }, coordinates = { "x", "y" })
public class RockPop  extends EnvironmentPopulator {

    public RockPop() {
        super("Rover", "Rock Pop", 10);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        RecursiveGridList<Double> out = RecursiveGridList.newGridList(Double.class, 2);
        out.put(3.14, 3, 4);
        out.put(params.containsKey("use") ? params.get("use") : 0., 0, 0);
        return out;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return (d) -> Color.GREEN;
    }
}
