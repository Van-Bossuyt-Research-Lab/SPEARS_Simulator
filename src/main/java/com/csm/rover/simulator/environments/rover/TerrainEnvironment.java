package com.csm.rover.simulator.environments.rover;

import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.wrapper.Globals;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Environment(type="Rover")
public class TerrainEnvironment extends PlatformEnvironment<RoverObject, TerrainMap> {
    private static final Logger LOG = LogManager.getLogger(TerrainEnvironment.class);

    public TerrainEnvironment(){
        super("Rover");
    }

    @JsonCreator
    public TerrainEnvironment(@JsonProperty("type") String type, @JsonProperty("map") TerrainMap map, @JsonProperty("populators") Map<String, EnvironmentPopulator> pops){
        super(type, map, pops);
        if (!type.equals("Rover")){
            throw new IllegalArgumentException("TerrainEnvironment type should be \'Rover\'");
        }
    }

    public double getHeightAt(DecimalPoint point){
        try {
            return map.getHeightAt(point);
        }
        catch (ArrayIndexOutOfBoundsException e){
            String current = Thread.currentThread().getName();
            String name = current.substring(0, current.indexOf('-'));
            if (name.equalsIgnoreCase("awt")){
                throw e;
            }
            else {
                LOG.log(Level.WARN, String.format("Rover \'%s\' has reached the end of the map - TERMINATING", name));
                Globals.getInstance().killThread(current);
            }
            return 0;
        }
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

    @JsonIgnore
    public double getGravity(){
        //TODO handle this better
        return 9.81;
    }

    public double getPopulatorValue(String pop, DecimalPoint point){
        return super.getPopulatorValue(pop, point.getX(), point.getY());
    }

    public boolean isPopulatorAt(String pop, DecimalPoint point){
        return super.isPopulatorAt(pop, point.getX(), point.getY());
    }

    @JsonIgnore
    public int getSize(){
        return map.getSize();
    }

    @JsonIgnore
    public int getDetail(){
        return map.getDetail();
    }

    @JsonIgnore
    public double getMaxHeight(){
        return map.getMaxValue();
    }

}
