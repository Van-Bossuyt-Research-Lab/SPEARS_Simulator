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

package com.csm.rover.simulator.environments.sub;

import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.objects.util.ArrayGrid3D;
import com.csm.rover.simulator.objects.util.DecimalPoint3D;
import com.csm.rover.simulator.objects.util.FloatArrayArrayArrayGrid;
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.test.objects.populators.UnlabeledKelpPop;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AquaticEnvironmentTest {

    private static final double TOLERANCE = 0.0000001;

    @Test(expected = IllegalArgumentException.class)
    public void testJsonTypeError(){
        new AquaticEnvironment("Rover", mock(AquaticMap.class), Collections.emptyMap());
    }

    @Test
    public void testGetGravity(){
        assertEquals(9.81, getAnEnv().getGravity(), TOLERANCE);
    }

    @Test
    public void testGetPop(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.loadFromArray(new Float[][][] {
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.5f, 2f, 1.5f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {0.75f, 0.5f, 0.75f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
        });
        AquaticMap map = new AquaticMap(2, 2, grid);
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        EnvironmentPopulator pop = new UnlabeledKelpPop();
        pop.build(map, ParamMap.emptyParamMap());
        pops.put("Kelp Pop", pop);
        AquaticEnvironment env = new AquaticEnvironment("Sub", map, pops);

        assertEquals(5, env.getPopulatorValue("Kelp Pop", new DecimalPoint3D(2, 6, -1)), TOLERANCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUnknownPop(){
        getAnEnv().getPopulatorValue("no pop", new DecimalPoint3D());
    }

    @Test
    public void testIsPop(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.loadFromArray(new Float[][][] {
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.5f, 2f, 1.5f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {0.75f, 0.5f, 0.75f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
        });
        AquaticMap map = new AquaticMap(2, 2, grid);
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        EnvironmentPopulator pop = new UnlabeledKelpPop();
        pop.build(map, ParamMap.emptyParamMap());
        pops.put("Kelp Pop", pop);
        AquaticEnvironment env = new AquaticEnvironment("Sub", map, pops);

        assert env.isPopulatorAt("Kelp Pop", new DecimalPoint3D(2.1, 6.2, -0.9));
    }

    @Test
    public void testIsNoPop(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.loadFromArray(new Float[][][] {
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.5f, 2f, 1.5f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {0.75f, 0.5f, 0.75f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
        });
        AquaticMap map = new AquaticMap(2, 2, grid);
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        EnvironmentPopulator pop = new UnlabeledKelpPop();
        pop.build(map, ParamMap.emptyParamMap());
        pops.put("Kelp Pop", pop);
        AquaticEnvironment env = new AquaticEnvironment("Sub", map, pops);

        assert !env.isPopulatorAt("Kelp Pop", new DecimalPoint3D(0, 0, 0));
    }

    @Test
    public void testGetDensity(){
        assertEquals(1.2875, getAnEnv().getDensityAt(new DecimalPoint3D(-0.75, -0.75, 0.75)), TOLERANCE);
    }

    @Test
    public void testGetSize(){
        assertEquals(2, getAnEnv().getSize());
    }

    @Test
    public void testGetDetail(){
        assertEquals(2, getAnEnv().getDetail());
    }

    @Test
    public void testGetMaxDensity(){
        assertEquals(2, getAnEnv().getMaxDensity(), TOLERANCE);
    }

    private AquaticEnvironment getAnEnv(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.loadFromArray(new Float[][][] {
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.5f, 2f, 1.5f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {0.75f, 0.5f, 0.75f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
        });
        AquaticMap map = new AquaticMap(2, 2, grid);
        return new AquaticEnvironment("Sub", map, Collections.emptyMap());
    }

}
