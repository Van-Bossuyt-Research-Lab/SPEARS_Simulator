package com.csm.rover.simulator.environments.sub;

import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.sub.SubObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@Environment(type="Sub")
public class AquaticEnvironment extends PlatformEnvironment<SubObject, AquaticMap> {

    public AquaticEnvironment(){
        super("Sub");
    }

    @JsonCreator
    public AquaticEnvironment(@JsonProperty("type") String type, @JsonProperty("map") AquaticMap map, @JsonProperty("populators") Map<String, EnvironmentPopulator> pops){
        super(type, map, pops);
        if (!type.equals("Sub")){
            throw new IllegalArgumentException("TerrainEnvironment type should be \'Sub\'");
        }
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

}