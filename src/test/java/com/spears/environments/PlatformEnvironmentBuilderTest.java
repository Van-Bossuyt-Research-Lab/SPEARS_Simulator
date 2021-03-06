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

package com.spears.environments;

import com.spears.objects.util.ParamMap;
import com.spears.test.objects.environments.LandEnv;
import com.spears.test.objects.maps.LandMap;
import com.spears.test.objects.modifiers.HillMod;
import com.spears.test.objects.modifiers.LandGen;
import com.spears.test.objects.modifiers.UnknownTunnelMod;
import com.spears.test.objects.modifiers.WaterGen;
import com.spears.test.objects.populators.RockPop;
import com.spears.test.objects.populators.UnlabeledKelpPop;
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
