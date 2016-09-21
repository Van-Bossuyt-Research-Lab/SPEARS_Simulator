package com.csm.rover.simulator.environments.rover.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.environments.rover.modifiers.PlasmaFractalGen;
<<<<<<< HEAD
import com.csm.rover.simulator.environments.rover.modifiers.TruncateModifier;
import com.csm.rover.simulator.objects.util.ArrayGrid;
=======
import com.csm.rover.simulator.environments.rover.modifiers.ScalingModifier;
import com.csm.rover.simulator.environments.rover.modifiers.TruncateModifier;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;
>>>>>>> refs/remotes/origin/master
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.objects.util.RecursiveGridList;

import java.util.Map;

<<<<<<< HEAD
@Populator(type = "Rover", name = "Hazard", coordinates = {"x", "y"}, parameters = {})
public class TerrainHazardsPop extends EnvironmentPopulator {

    public TerrainHazardsPop(){
        super("Rover", "Hazard", 10);
=======
@Populator(type = "Rover", name = "Hazards", coordinates = {"x", "y"}, parameters = {"mono", "rough"})
public class TerrainHazardsPop extends EnvironmentPopulator {

    public TerrainHazardsPop(){
        super("Rover", "Hazards", 10);
>>>>>>> refs/remotes/origin/master
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
<<<<<<< HEAD
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
=======
        boolean mono = params.get("mono") == 1;
        int size = ((TerrainMap)map).getSize();
        double rough = params.get("rough");
        ArrayGrid<Float> plasma1 = getLayer(size, rough, 3);
        ArrayGrid<Float> plasma2 = getLayer(size, rough*20, 8);
        ArrayGrid<Float> added = new FloatArrayArrayGrid();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                added.put(x, y, (plasma1.get(x, y) + plasma2.get(x, y)));
            }
        }
        added = new TruncateModifier().modify(
                new ScalingModifier().modify(
                        new TerrainMap(((TerrainMap) map).getSize(), 1, added),
                        ParamMap.newParamMap().addParameter("range", 10).build()),
                ParamMap.newParamMap().addParameter("places", 0).build()
        ).rawValues();
        RecursiveGridList<Double> hzrds = RecursiveGridList.newGridList(Double.class, 2);
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                double value = (double)added.get(x, y);
                hzrds.put(mono ? (value >=7 ? 10 : 0) : value, x, y);
            }
        }
        return hzrds;
    }

    private ArrayGrid<Float> getLayer(int size, double rough, int range){
        return new ScalingModifier().modify(
                new PlasmaFractalGen().modify(
                        null,
                        ParamMap.newParamMap()
                                .addParameter("size", size)
                                .addParameter("detail", 1)
                                .addParameter("rough", rough).build()),
                    ParamMap.newParamMap().addParameter("range", range).build()).rawValues();
>>>>>>> refs/remotes/origin/master
    }

}
