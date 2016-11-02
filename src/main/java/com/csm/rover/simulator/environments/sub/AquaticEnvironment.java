package com.csm.rover.simulator.environments.sub;

import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.objects.util.DecimalPoint3D;
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
            throw new IllegalArgumentException("TerrainEnvironment type should be \'Rover\'");
        }
    }

    @JsonIgnore
    public double getGravity(){
        //TODO handle this better
        return 9.81;
    }

    public double getPopulatorValue(String pop, DecimalPoint3D point){
        return super.getPopulatorValue(pop, point.getX(), point.getY(), point.getZ());
    }

    public boolean isPopulatorAt(String pop, DecimalPoint point){
        return super.isPopulatorAt(pop, point.getX(), point.getY());
    }

    @JsonIgnore
    public int getSize(){
        return map.getSize();
    }

    @JsonIgnore
    public double getDensityAt(DecimalPoint3D point){
        return map.getValueAt(point);
    }

    @JsonIgnore
    public int getDetail(){
        return map.getDetail();
    }

    @JsonIgnore
    public double getMaxDensity(){
        return map.getMaxValue();
    }

}