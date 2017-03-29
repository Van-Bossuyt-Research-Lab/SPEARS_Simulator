/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
