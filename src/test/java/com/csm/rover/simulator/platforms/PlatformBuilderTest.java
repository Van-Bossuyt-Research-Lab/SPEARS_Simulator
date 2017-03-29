package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.objects.util.ParamMap;
import com.csm.rover.simulator.test.objects.AutoModels.RoverCode;
import com.csm.rover.simulator.test.objects.PhysicsModels.RoverPhysics;
import com.csm.rover.simulator.test.objects.platforms.RoverPlatform;
import com.csm.rover.simulator.test.objects.platforms.UAVPlatform;
import com.csm.rover.simulator.test.objects.states.RoverState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PlatformBuilderTest {

    private static final double TOLERANCE = 0.0000001;

    private Platform platform;

    @Before
    public void setup(){
        mockRegistry();
        PlatformConfig config = PlatformConfig.builder()
                .setType("Rover")
                .setScreenName("Rover 1")
                .setID("r1")
                .setAutonomousModel("Test Rover", ParamMap.newParamMap()
                        .addParameter("param1", 1).addParameter("param2", 2).build())
                .setPhysicsModel("Test Rover", ParamMap.newParamMap()
                        .addParameter("paramA", 'A').addParameter("paramB", 'B').build())
                .addStateVariable("state", 0.)
                .build();
        platform = Platform.buildFromConfiguration(config);
    }

    private void mockRegistry(){
        PlatformRegistry reg = new PlatformRegistry();
        Inject.field("platforms").of(reg).with(newMap("Rover", RoverPlatform.class.getName()));
        Inject.field("platformStates").of(reg).with(newMap("Rover", RoverState.class.getName()));
        Inject.field("autoModels").of(reg).with(newLayeredMap("Rover", "Test Rover", RoverCode.class.getName()));
        Inject.field("autoModelParameters").of(reg).with(newParamMap("Rover", "Test Rover", "param1", "param2"));
        Inject.field("physicsModels").of(reg).with(newLayeredMap("Rover", "Test Rover", RoverPhysics.class.getName()));
        Inject.field("physicsModelParameters").of(reg).with(newParamMap("Rover", "Test Rover", "paramA", "paramB"));
    }

    @Test
    public void testType(){
        assertEquals("Rover", platform.getType());
    }

    @Test
    public void testName(){
        assertEquals("Rover 1", platform.getName());
    }

    @Test
    public void testAutoCode(){
        assert platform.autonomousCodeModel instanceof RoverCode;
    }

    @Test
    public void testAutoParams(){
        Map<String, Double> params = ((RoverCode) platform.autonomousCodeModel).paramMap;
        assert params.containsKey("param1");
        assertEquals(1, params.get("param1"), TOLERANCE);
        assert params.containsKey("param2");
        assertEquals(2, params.get("param2"), TOLERANCE);
    }

    @Test
    public void testPhysicsParams(){
        Map<String, Double> params = ((RoverPhysics) platform.physicsModel).paramMap;
        assert params.containsKey("paramA");
        assertEquals('A', params.get("paramA"), TOLERANCE);
        assert params.containsKey("paramB");
        assertEquals('B', params.get("paramB"), TOLERANCE);
    }

    @Test
    public void testPhysics(){
        assert platform.physicsModel instanceof RoverPhysics;
    }

    @Test
    public void testStateInit(){
        assertEquals(0., platform.getState().get("state"), TOLERANCE);
    }

    @Test
    public void testNullOnRegFail(){
        assertNull(Platform.buildFromConfiguration(PlatformConfig.builder()
                .setType("UAV")
                .setScreenName("UAV 1")
                .setID("u1")
                .setAutonomousModel("Test UAV", ParamMap.newParamMap()
                        .addParameter("param1", 1).addParameter("param2", 2).build())
                .setPhysicsModel("Test UAV", ParamMap.newParamMap()
                        .addParameter("paramA", 'A').addParameter("paramB", 'B').build())
                .addStateVariable("state", 0)
                .build()));
    }

    @After
    public void reset(){
        PlatformRegistry reg = new PlatformRegistry();
        Inject.field("platforms").of(reg).with(new TreeMap<>());
        Inject.field("platformStates").of(reg).with(new TreeMap<>());
        Inject.field("autoModels").of(reg).with(new TreeMap<>());
        Inject.field("autoModelParameters").of(reg).with(new TreeMap<>());
        Inject.field("physicsModels").of(reg).with(new TreeMap<>());
        Inject.field("physicsModelParameters").of(reg).with(new TreeMap<>());
    }

    private Map<String, String> newMap(String key, String clazz){
        Map<String, String> out = new TreeMap<>();
        out.put(key, clazz);
        return out;
    }

    private Map<String, Map<String, String>> newLayeredMap(String key, String name, String clazz){
        Map<String, Map<String, String>> out = new TreeMap<>();
        Map<String, String> layer = new TreeMap<>();
        layer.put(name, clazz);
        out.put(key, layer);
        return out;
    }

    private Map<String, Map<String, String[]>> newParamMap(String key, String name, String... params){
        Map<String, Map<String, String[]>> out = new TreeMap<>();
        Map<String, String[]> layer = new TreeMap<>();
        layer.put(name, params);
        out.put(key, layer);
        return out;
    }

}
