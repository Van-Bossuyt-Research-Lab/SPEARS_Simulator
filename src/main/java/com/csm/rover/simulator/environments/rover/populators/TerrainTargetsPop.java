package com.csm.rover.simulator.environments.rover.populators;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.objects.util.RecursiveGridList;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;

import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Populator(type="Rover", name="Targets", coordinates={"x", "y"}, parameters={"trgt_density", "mono"})
public class TerrainTargetsPop extends EnvironmentPopulator {

    public TerrainTargetsPop() {
        super("Rover", "Targets", 0);
    }

    @Override
    protected RecursiveGridList<Double> doBuild(EnvironmentMap map, Map<String, Double> params) {
        int size = ((TerrainMap)map).getSize();
        int targetCount = (int)(Math.pow(size, 2) * params.get("trgt_density"));
        Random rnd = new Random();
        Set<Point> points = new HashSet<>();
        while (points.size() < targetCount){
            points.add(new Point(rnd.nextInt(size)-size/2, rnd.nextInt(size)-size/2));
        }
        boolean mono = params.get("mono") == 1;
        RecursiveGridList<Double> out = RecursiveGridList.newGridList(Double.class, 2);
        for (Point pnt : points){
            int value;
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
            out.put((double)value, pnt.x, pnt.y);
        }
        return out;
    }

    @Override
    public PopulatorDisplayFunction getDisplayFunction() {
        return (value) -> new Color(
                (int)(Color.MAGENTA.getRed()*(value+5)/15.),
                (int)(Color.MAGENTA.getGreen()*(value+5)/15.),
                (int)(Color.MAGENTA.getBlue()*(value+5)/15.)
        );
    }

}
