package com.csm.rover.simulator.test.objects.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.test.objects.maps.LandMap;

import java.util.Map;

@Modifier(type = "Rover", name = "Hill Mod", parameters = {"param1", "param2"})
public class HillMod extends EnvironmentModifier<LandMap> {

    public HillMod() {
        super("Rover");
    }

    @Override
    protected LandMap doModify(LandMap map, Map params) {
        return null;
    }

}
