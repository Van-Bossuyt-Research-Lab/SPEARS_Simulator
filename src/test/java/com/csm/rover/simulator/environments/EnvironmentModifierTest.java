package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.test.objects.maps.LandMap;
import com.csm.rover.simulator.test.objects.maps.UnknownCaveMap;
import com.csm.rover.simulator.test.objects.modifiers.UnknownTunnelMod;
import com.csm.rover.simulator.test.objects.modifiers.UnlabeledWindMod;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EnvironmentModifierTest {

    @Test
    public void checkMapType_Good(){
        EnvironmentModifier mod = spy(new UnknownTunnelMod());
        EnvironmentMap map = new UnknownCaveMap();
        Map<String, Double> params = Collections.emptyMap();
        mod.modify(map, params);
        verify(mod, atLeastOnce()).doModify(map, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkMapType_Bad(){
        EnvironmentModifier mod = new UnknownTunnelMod();
        EnvironmentMap map = new LandMap();
        Map<String, Double> params = Collections.emptyMap();
        mod.modify(map, params);
    }

    @Test
    public void checkMapType_Gen(){
        EnvironmentModifier mod = spy(new UnlabeledWindMod());
        Map<String, Double> params = Collections.emptyMap();
        mod.modify(null, params);
        verify(mod, atLeastOnce()).doModify(null, params);
    }

}
