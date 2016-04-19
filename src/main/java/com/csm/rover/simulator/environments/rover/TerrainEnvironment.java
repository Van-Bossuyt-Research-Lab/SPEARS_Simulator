package com.csm.rover.simulator.environments.rover;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.rover.RoverObject;

import java.util.ArrayList;

@Environment(type="Rover")
public class TerrainEnvironment extends PlatformEnvironment {

    private ArrayList<RoverObject> rovers;

    private TerrainMap map;

    public TerrainEnvironment(){
        super("Rover");
        rovers = new ArrayList<>();
    }

    @Override
    protected void doPlacePlatform(Platform platform) {
        rovers.add((RoverObject)platform);
    }

    @Override
    protected void buildActions(){
        this.map = (TerrainMap) super.map;
    }

    public double getHeightAt(DecimalPoint point){
        return map.getHeightAt(point);
    }

    public double getSlopeAt(DecimalPoint point, double direction){
        double radius = 0.1;
        double h0 = getHeightAt(point);
        DecimalPoint loc2 = point.offset(radius*Math.cos(direction), radius*Math.sin(direction));
        double hnew = getHeightAt(loc2);
        return Math.atan((hnew - h0) / radius);
    }

    public double getCrossSlopeAt(DecimalPoint point, double direction){
        return getSlopeAt(point, (direction - Math.PI / 2.0 + Math.PI * 2) % (2 * Math.PI));
    }

    public <T> T getPopulatorValue(String pop, DecimalPoint point){
        if (populators.containsKey(pop)){
            Object ret = populators.get(pop).getValue(point.getX(), point.getY());
            try {
                return (T)ret;
            }
            catch (ClassCastException e){
                throw new RuntimeException("The populator "+pop+" does not return the requested type", e);
            }
        }
        else {
            throw new NullPointerException("The requested populator "+pop+" is not defined");
        }
    }

}
