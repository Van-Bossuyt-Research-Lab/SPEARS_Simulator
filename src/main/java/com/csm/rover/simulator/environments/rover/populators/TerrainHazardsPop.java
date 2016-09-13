package com.csm.rover.simulator.environments.rover.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.environments.rover.modifiers.PlasmaFractalGen;
import com.csm.rover.simulator.environments.rover.modifiers.TruncateModifier;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.objects.util.RecursiveGridList;

import java.util.Map;

@Populator(type = "Rover", name = "Hazard", coordinates = {"x", "y"}, parameters = {})
public class TerrainHazardsPop extends EnvironmentPopulator {

    public TerrainHazardsPop(){
        super("Rover", "Hazard", 10);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        ArrayGrid<Float> plasma = new TruncateModifier().modify(
                new PlasmaFractalGen().modify(
                        null,
                        ParamMap.newParamMap()
                                .addParameter("size", ((TerrainMap)map).getSize())
                                .addParameter("detail", 1)
                                .addParameter("range", 5)
                                .addParameter("rough", 0.05).build()),
                ParamMap.newParamMap().addParameter("places", 0).build()).rawValues();
        //TODO generate increased patches
        //TODO copy into RGL and apply patches
        RecursiveGridList<Double> hzrds = RecursiveGridList.newGridList(Double.class, 2);
        return hzrds;
    }

}
