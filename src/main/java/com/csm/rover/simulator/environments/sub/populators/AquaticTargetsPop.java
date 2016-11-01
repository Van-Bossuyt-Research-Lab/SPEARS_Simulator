package com.csm.rover.simulator.environments.sub.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.objects.util.DecimalPoint3D;
import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import javafx.geometry.Point3D;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Populator(type="Sub", name="Targets", coordinates={"x", "y","z"}, parameters={"trgt_density", "mono"})
public class AquaticTargetsPop extends EnvironmentPopulator {

    public AquaticTargetsPop() {
        super("Sub", "Targets", 0);
    }

    @Override
    protected RecursiveGridList doBuild(EnvironmentMap map, Map<String, Double> params) {
        int size = ((TerrainMap)map).getSize();
        int targetCount = (int)(Math.pow(size, 3) / 100.0 * params.get("trgt_density"));
        Random rnd = new Random();
        Set<DecimalPoint3D> points = new HashSet<>();
        while (points.size() < targetCount){
            points.add(new DecimalPoint3D(rnd.nextInt(size), rnd.nextInt(size), rnd.nextInt(size)));
        }
        boolean mono = params.get("mono") == 1;
        RecursiveGridList<Double> out = RecursiveGridList.newGridList(Double.class, 3);
        for (DecimalPoint3D pnt : points){
            double value;
            if (mono){
                value = 10;
            }
            else {
                double rand = rnd.nextDouble();
                if (rand < 0.3){
                    value = 1;
                }
                else if (rand < 0.45){
                    value = 2;
                }
                else if (rand < 0.6){
                    value = 3;
                }
                else if (rand < 0.7){
                    value = 4;
                }
                else if (rand < 0.8){
                    value = 5;
                }
                else if (rand < 0.86){
                    value = 6;
                }
                else if (rand < 0.91){
                    value = 7;
                }
                else if (rand < 0.95){
                    value = 8;
                }
                else if (rand < 0.98){
                    value = 9;
                }
                else {
                    value = 10;
                }
            }
            out.put(value, pnt.roundX(), pnt.roundY(), pnt.roundZ());
        }
        return out;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return null;
    }

}