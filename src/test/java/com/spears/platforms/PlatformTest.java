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

package com.spears.platforms;

import com.spears.environments.PlatformEnvironment;
import com.spears.test.objects.AutoModels.RoverCode;
import com.spears.test.objects.PhysicsModels.RoverPhysics;
import com.spears.test.objects.environments.LandEnv;
import com.spears.test.objects.platforms.RoverPlatform;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PlatformTest {

    @Test
    public void setEnviroAutoCode(){
        Platform plat = new RoverPlatform();
        PlatformAutonomousCodeModel code = spy(new RoverCode());
        Inject.field("autonomousCodeModel").of(plat).with(code);
        PlatformEnvironment environment = new LandEnv();
        plat.setEnvironment(environment);

        verify(code).setEnvironment(environment);
    }

    @Test
    public void setEnviroPhysics(){
        Platform plat = new RoverPlatform();
        PlatformPhysicsModel model = spy(new RoverPhysics());
        Inject.field("physicsModel").of(plat).with(model);
        PlatformEnvironment environment = new LandEnv();
        plat.setEnvironment(environment);

        verify(model).setEnvironment(environment);
    }

}
