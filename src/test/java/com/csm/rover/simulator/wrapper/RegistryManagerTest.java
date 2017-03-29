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

package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.environments.*;
import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.platforms.*;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import com.csm.rover.simulator.platforms.annotations.State;
import com.csm.rover.simulator.test.objects.AutoModels.RoverCode;
import com.csm.rover.simulator.test.objects.environments.LandEnv;
import com.csm.rover.simulator.test.objects.maps.LandMap;
import com.csm.rover.simulator.test.objects.modifiers.HillMod;
import com.csm.rover.simulator.test.objects.PhysicsModels.RoverPhysics;
import com.csm.rover.simulator.test.objects.platforms.RoverPlatform;
import com.csm.rover.simulator.test.objects.populators.RockPop;
import com.csm.rover.simulator.test.objects.states.RoverState;
import com.google.common.collect.Sets;
import org.junit.BeforeClass;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;
import org.reflections.Reflections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class RegistryManagerTest {

    private static Reflections reflectionsMock = mock(Reflections.class);

    @BeforeClass
    public static void setup(){
        Inject.field("reflect").of(new PlatformRegistry()).with(reflectionsMock);
        doReturn(Sets.newHashSet(RoverPlatform.class)).when(reflectionsMock).getSubTypesOf(Platform.class);
        doReturn(Sets.newHashSet(RoverPlatform.class)).when(reflectionsMock).getTypesAnnotatedWith(com.csm.rover.simulator.platforms.annotations.Platform.class);
        doReturn(Sets.newHashSet(RoverState.class)).when(reflectionsMock).getSubTypesOf(PlatformState.class);
        doReturn(Sets.newHashSet(RoverState.class)).when(reflectionsMock).getTypesAnnotatedWith(State.class);
        doReturn(Sets.newHashSet(RoverCode.class)).when(reflectionsMock).getSubTypesOf(PlatformAutonomousCodeModel.class);
        doReturn(Sets.newHashSet(RoverCode.class)).when(reflectionsMock).getTypesAnnotatedWith(AutonomousCodeModel.class);
        doReturn(Sets.newHashSet(RoverPhysics.class)).when(reflectionsMock).getSubTypesOf(PlatformPhysicsModel.class);
        doReturn(Sets.newHashSet(RoverPhysics.class)).when(reflectionsMock).getTypesAnnotatedWith(PhysicsModel.class);

        doReturn(Sets.newHashSet(LandEnv.class)).when(reflectionsMock).getSubTypesOf(PlatformEnvironment.class);
        doReturn(Sets.newHashSet(LandEnv.class)).when(reflectionsMock).getTypesAnnotatedWith(Environment.class);
        doReturn(Sets.newHashSet(LandMap.class)).when(reflectionsMock).getSubTypesOf(EnvironmentMap.class);
        doReturn(Sets.newHashSet(LandMap.class)).when(reflectionsMock).getTypesAnnotatedWith(com.csm.rover.simulator.environments.annotations.Map.class);
        doReturn(Sets.newHashSet(HillMod.class)).when(reflectionsMock).getSubTypesOf(EnvironmentModifier.class);
        doReturn(Sets.newHashSet(HillMod.class)).when(reflectionsMock).getTypesAnnotatedWith(Modifier.class);
        doReturn(Sets.newHashSet(RockPop.class)).when(reflectionsMock).getSubTypesOf(EnvironmentPopulator.class);
        doReturn(Sets.newHashSet(RockPop.class)).when(reflectionsMock).getTypesAnnotatedWith(Populator.class);

        RegistryManager.checkRegistries();
    }

    @Test
    public void didFillPlatforms(){
        assert PlatformRegistry.getTypes().contains("Rover");
    }

    @Test
    public void didFillEnvironments(){
        assert EnvironmentRegistry.getTypes().contains("Rover");
    }

}
