package com.csm.rover.simulator.environments.rover.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;

import java.util.Map;

@Populator(type="Rover", name="Targets", parameters={"trgt_density", "mono"})
public class TerrainTargetsPop extends EnvironmentPopulator {

    public TerrainTargetsPop() {
        super("Rover");
    }

    @Override
    protected void doBuild(EnvironmentMap map, Map<String, Double> params) {

    }

}
