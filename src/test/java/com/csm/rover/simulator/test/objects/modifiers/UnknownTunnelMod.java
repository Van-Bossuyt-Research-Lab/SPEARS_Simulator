package com.csm.rover.simulator.test.objects.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.test.objects.maps.UnknownCaveMap;

import java.util.Map;

@Modifier(type = "Mole", name = "Tunnel Mod", parameters = {})
public class UnknownTunnelMod extends EnvironmentModifier<UnknownCaveMap> {

    public UnknownTunnelMod() {
        super("Mole");
    }

    @Override
    protected UnknownCaveMap doModify(UnknownCaveMap map, Map<String, Double> params) {
        return null;
    }

}
