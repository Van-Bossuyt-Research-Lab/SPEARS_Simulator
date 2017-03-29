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

import com.csm.rover.simulator.environments.sub.AquaticMap;
import com.csm.rover.simulator.objects.util.ArrayGrid3D;
import com.csm.rover.simulator.objects.util.FloatArrayArrayArrayGrid;
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.test.objects.maps.LandMap;
import com.csm.rover.simulator.test.objects.maps.UnknownCaveMap;
import com.csm.rover.simulator.test.objects.maps.UnlabeledSkyMap;
import com.csm.rover.simulator.test.objects.populators.RockPop;
import com.csm.rover.simulator.test.objects.populators.UnknownPop;
import com.csm.rover.simulator.test.objects.populators.UnlabeledKelpPop;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EnvironmentPopulatorTest {

    private double TOLERANCE = 0.000001;

    private EnvironmentPopulator pop;

    @Before
    public void setup(){
        pop = new UnknownPop();
        pop.build(new UnknownCaveMap(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsBadType(){;
        pop.build(new UnlabeledSkyMap(), ParamMap.emptyParamMap());
    }

    @Test
    public void testCallsDoBuild(){
        pop = spy(new UnknownPop());
        EnvironmentMap map = new UnknownCaveMap();
        Map<String, Double> params = ParamMap.newParamMap().addParameter("hi", -1).build();
        pop.build(map, params);
        verify(pop).doBuild(map, params);
    }

    @Test
    public void testGetValue(){
        assertEquals(1, pop.getValue(1, 1), TOLERANCE);
    }

    @Test
    public void testUsesFloor(){
        assertEquals(1, pop.getValue(1.7, 1.2), TOLERANCE);
    }

    @Test
    public void testDefaultOnOutOfBounds(){
        assertEquals(-1, pop.getValue(10, 10), TOLERANCE);

        assertEquals(-1, pop.getValue(-10, -10), TOLERANCE);
    }

    @Test
    public void testDefaultOnUnfilled(){
        assertEquals(-1, pop.getValue(0, 2), TOLERANCE);
    }

    @Test
    public void testDefaultOnWrongCoordinates(){
        assertEquals(-1, pop.getValue(1), TOLERANCE);

        assertEquals(-1, pop.getValue(1, 4, 6), TOLERANCE);
    }

    @Test
    public void testDefaultOnNotBuilt(){
        EnvironmentPopulator pop = new UnknownPop();
        assertEquals(-1, pop.getValue(1, 1), TOLERANCE);
    }

    @Test
    public void testEquals(){
        EnvironmentPopulator pop1 = new RockPop();
        pop1.build(new LandMap(), ParamMap.emptyParamMap());
        EnvironmentPopulator pop2 = new RockPop();
        pop2.build(new LandMap(), ParamMap.emptyParamMap());
        assertEquals(pop1, pop2);
    }

    @Test
    public void testNotEquals_Type(){
        EnvironmentPopulator pop1 = new RockPop();
        pop1.build(new LandMap(), ParamMap.emptyParamMap());
        EnvironmentPopulator pop2 = new UnlabeledKelpPop();
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.fillToSize(7, 7, 7);
        pop2.build(new AquaticMap(3, 2, grid), ParamMap.emptyParamMap());
        assertNotEquals(pop1, pop2);
    }

    @Test
    public void testNotEquals_Values(){
        EnvironmentPopulator pop1 = new RockPop();
        pop1.build(new LandMap(), ParamMap.emptyParamMap());
        EnvironmentPopulator pop2 = new RockPop();
        pop2.build(new LandMap(), ParamMap.newParamMap().addParameter("use", -1.).build());
        assertNotEquals(pop1, pop2);
    }

    @Test
    public void testReallyNotEquals(){
        EnvironmentPopulator pop1 = new RockPop();
        pop1.build(new LandMap(), ParamMap.emptyParamMap());
        assertNotEquals(pop1, "string");
    }

}
