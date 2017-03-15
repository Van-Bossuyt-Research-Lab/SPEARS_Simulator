package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.test.objects.AutoModels.RoverCode;
import com.csm.rover.simulator.test.objects.PhysicsModels.RoverPhysics;
import com.csm.rover.simulator.test.objects.environments.LandEnv;
import com.csm.rover.simulator.test.objects.platforms.RoverPlatform;
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
