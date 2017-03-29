package com.csm.rover.simulator.test.objects.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.test.objects.maps.LandMap;

import java.util.Map;

@Modifier(type = "Rover", name = "Land Gen", parameters = {}, generator = true)
public class LandGen extends EnvironmentModifier<LandMap> {

    private LandMap theMap;

    public LandGen() {
        super("Rover", true);
        theMap = new LandMap();
    }

    @Override
    protected LandMap doModify(LandMap map, Map<String, Double> params) {
        return theMap;
    }

    public LandMap getTheMap(){
        return theMap;
    }

}
