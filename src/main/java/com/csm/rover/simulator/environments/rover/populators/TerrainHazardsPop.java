package com.csm.rover.simulator.environments.rover.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.environments.rover.modifiers.PlasmaFractalGen;
import com.csm.rover.simulator.environments.rover.modifiers.ScalingModifier;
import com.csm.rover.simulator.environments.rover.modifiers.TruncateModifier;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.objects.util.RecursiveGridList;

import java.util.Map;

@Populator(type = "Rover", name = "Hazards", coordinates = {"x", "y"}, parameters = {"mono", "rough"})
public class TerrainHazardsPop extends EnvironmentPopulator {

    public TerrainHazardsPop(){
        super("Rover", "Hazards", 10);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        boolean mono = params.get("mono") == 1;
        int size = ((TerrainMap)map).getSize();
        double rough = params.get("rough");
        ArrayGrid<Float> plasma1 = getLayer(size, rough, mono);
        ArrayGrid<Float> plasma2 = getLayer(size, rough, mono);
        RecursiveGridList<Double> hzrds = RecursiveGridList.newGridList(Double.class, 2);
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                hzrds.put((double)(plasma1.get(x, y) + plasma2.get(x, y)), x, y);
            }
        }
        return hzrds;
    }

    private ArrayGrid<Float> getLayer(int size, double rough, boolean mono){
        return new TruncateModifier().modify(
                new ScalingModifier().modify(
                    new PlasmaFractalGen().modify(
                            null,
                            ParamMap.newParamMap()
                                    .addParameter("size", size)
                                    .addParameter("detail", 1)

                                    .addParameter("rough", rough).build()),
                        ParamMap.newParamMap().addParameter("range", 5).build()),
                ParamMap.newParamMap().addParameter("places", mono ? -1 : 0).build()).rawValues();
    }

}
