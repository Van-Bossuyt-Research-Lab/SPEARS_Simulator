package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.test.objects.environments.LandEnv;
import com.csm.rover.simulator.test.objects.maps.LandMap;
import com.csm.rover.simulator.test.objects.modifiers.HillMod;
import com.csm.rover.simulator.test.objects.modifiers.LandGen;
import com.csm.rover.simulator.test.objects.modifiers.UnknownTunnelMod;
import com.csm.rover.simulator.test.objects.modifiers.WaterGen;
import com.csm.rover.simulator.test.objects.populators.RockPop;
import com.csm.rover.simulator.test.objects.populators.UnlabeledKelpPop;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PlatformEnvironmentBuilderTest {

    private static PlatformEnvironment enviro;

    @BeforeClass
    public static void setupEnviro(){
        enviro = new LandEnv();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectGen(){
        enviro.generateNewMap(new WaterGen(), Collections.emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAGen(){
        enviro.generateNewMap(new HillMod(), Collections.emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectMod(){
        enviro.generateNewMap(new LandGen(), Collections.emptyMap())
                .addMapModifier(new UnknownTunnelMod(), Collections.emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectModIsGen(){
        enviro.generateNewMap(new LandGen(), Collections.emptyMap())
                .addMapModifier(new LandGen(), Collections.emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectPop(){
        enviro.generateNewMap(new LandGen(), Collections.emptyMap())
                .addPopulator(new UnlabeledKelpPop(), Collections.emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectRepeatPop(){
        enviro.generateNewMap(new LandGen(), Collections.emptyMap())
                .addPopulator(new RockPop(), Collections.emptyMap())
                .addPopulator(new RockPop(), Collections.emptyMap());
    }

    @Test
    public void usesMods(){
        LandGen gen = spy(new LandGen());
        LandMap map = gen.getTheMap();
        HillMod mod1 = spy(new HillMod());
        HillMod mod2 = spy(new HillMod());
        enviro.generateNewMap(gen, Collections.emptyMap())
                .addMapModifier(mod1, Collections.emptyMap())
                .addMapModifier(mod2, Collections.emptyMap())
                .generate();

        verify(gen).modify(null, Collections.emptyMap());
        verify(mod1).modify(map, Collections.emptyMap());
        verify(mod2).modify(map, Collections.emptyMap());
    }

    @Test
    public void buildsPops(){
        LandGen gen = new LandGen();
        LandMap map = gen.getTheMap();
        RockPop pop1 = spy(new RockPop());
        enviro.generateNewMap(gen, Collections.emptyMap())
                .addPopulator(pop1, Collections.emptyMap())
                .generate();

        verify(pop1).build(map, Collections.emptyMap());
    }

    @Test
    public void addsPops(){
        RockPop pop1 = new RockPop();
        enviro.generateNewMap(new LandGen(), Collections.emptyMap())
                .addPopulator(pop1, Collections.emptyMap())
                .generate();

        Assert.assertEquals(Arrays.asList("Rock Pop"), enviro.getPopulators());
    }

    private int calls = 0;
    @Test
    public void callsBuildAction(){
        enviro.setBuildActions(() -> calls++);
        enviro.generateNewMap(new LandGen(), ParamMap.newParamMap().addParameter("test", 0).build()).generate();
        assert calls > 0;
    }

}
