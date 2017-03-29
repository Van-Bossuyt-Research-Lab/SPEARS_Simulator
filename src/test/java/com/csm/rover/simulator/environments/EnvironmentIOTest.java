package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.environments.rover.TerrainEnvironment;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.environments.rover.modifiers.PlasmaFractalGen;
import com.csm.rover.simulator.environments.sub.AquaticEnvironment;
import com.csm.rover.simulator.environments.sub.AquaticMap;
import com.csm.rover.simulator.environments.sub.modifiers.Gradient;
import com.csm.rover.simulator.objects.util.ArrayGrid3D;
import com.csm.rover.simulator.objects.util.FloatArrayArrayArrayGrid;
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.test.objects.environments.UnlabeledSeaEnv;
import com.csm.rover.simulator.test.objects.populators.RockPop;
import com.csm.rover.simulator.test.objects.populators.UnlabeledKelpPop;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EnvironmentIOTest {

    private static List<File> files;

    @BeforeClass
    public static void makeFile(){
        files = new ArrayList<>();
    }

    @Test
    public void testAppendOnGood(){
        File test = newFile("test.temp.rover.env");
        Assert.assertEquals(test.getAbsolutePath(), EnvironmentIO.appendSuffix("Rover", test).getAbsolutePath());
    }

    @Test
    public void testAppendOnBad(){
        File test = newFile("test.sub.env");
        Assert.assertEquals(test.getAbsolutePath()+".rover.env", EnvironmentIO.appendSuffix("Rover", test).getAbsolutePath());
    }

    @Test
    public void testCache() throws IOException {
        File test = newFile("temp.sub.env");
        UnlabeledSeaEnv env = new UnlabeledSeaEnv();
        EnvironmentIO.saveEnvironment(env, test);
        test.delete();
        Assert.assertEquals(env, EnvironmentIO.loadEnvironment(test, UnlabeledSeaEnv.class));
    }

    @Test
    public void testReadWriteRover() throws IOException {
        File test = newFile("roverRW.rover.env");
        Map<String, EnvironmentPopulator> pops = new TreeMap<>();
        EnvironmentPopulator pop = new RockPop();
        pop.build(new TerrainMap(2, 1, new Float[][]{
                new Float[] {1f, 1f, -0.5f},
                new Float[] {-0.5f, 0f, 0.5f},
                new Float[] {-1f, -1f, -0.5f}
        }), ParamMap.emptyParamMap());
        pops.put("Rock Pop", pop);
        TerrainEnvironment env = new TerrainEnvironment("Rover", new TerrainMap(2, 1, new Float[][]{
                new Float[] {1f, 1f, -0.5f},
                new Float[] {-0.5f, 0f, 0.5f},
                new Float[] {-1f, -1f, -0.5f}
        }), pops);
        env.generateNewMap(new PlasmaFractalGen(),
                ParamMap.newParamMap().addParameter("size", 10).addParameter("detail", 1).addParameter("rough", 0.5).build())
                .generate();
        EnvironmentIO.saveEnvironment(env, test);
        clearCache();
        Assert.assertEquals(env, EnvironmentIO.loadEnvironment(test, TerrainEnvironment.class));
    }

    @Test
    public void testReadWriteSub() throws IOException {
        File test = newFile("subRW.sub.env");
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
        env.generateNewMap(new Gradient(), ParamMap.newParamMap().addParameter("size", 10).addParameter("detail", 2).build())
                .generate();
        EnvironmentIO.saveEnvironment(env, test);
        clearCache();
        Assert.assertEquals(env, EnvironmentIO.loadEnvironment(test, AquaticEnvironment.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRefusesFileType(){
        File test = newFile("testing.rover.env");
        EnvironmentIO.saveEnvironment(new UnlabeledSeaEnv(), test);
    }

    @Test(expected = IOException.class)
    public void testFailsToRead() throws IOException {
        EnvironmentIO.loadEnvironment(new File("not.a.file.rover.env"), TerrainEnvironment.class);
    }

    private void clearCache(){
        Inject.field("environmentHolder").of(new EnvironmentIO()).with(new TreeMap<>());
    }

    private File newFile(String path){
        File out = new File(path);
        files.add(out);
        return out;
    }

    @AfterClass
    public static void cleanUpFiles(){
        for (File file : files){
            file.delete();
        }
    }

}
