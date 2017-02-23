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
        super("Rover", "Rock Pop", 0);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        return null;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return (d) -> Color.GREEN;
    }
}
