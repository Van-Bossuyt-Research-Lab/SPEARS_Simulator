package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.objects.util.ParamMap;
import org.junit.Assert;
import org.junit.Test;

public class PlatformConfigBuilderTest {

    private double TOLERANCE = 0.0000001;

    @Test(expected = IllegalStateException.class)
    public void testErrorMissingType(){
        PlatformConfig.builder()
                .setScreenName("Rover 1")
                .setID("r1")
                .setAutonomousModel("AutoCode", ParamMap.newParamMap().addParameter("version", 5).build())
                .setPhysicsModel("PhysicsModel", ParamMap.newParamMap().addParameter("mass", 10).build())
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorMissingName(){
        PlatformConfig.builder()
                .setType("Rover")
                .setID("r1")
                .setAutonomousModel("AutoCode", ParamMap.newParamMap().addParameter("version", 5).build())
                .setPhysicsModel("PhysicsModel", ParamMap.newParamMap().addParameter("mass", 10).build())
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorMissingID(){
        PlatformConfig.builder()
                .setType("Rover")
                .setScreenName("Rover 1")
                .setAutonomousModel("AutoCode", ParamMap.newParamMap().addParameter("version", 5).build())
                .setPhysicsModel("PhysicsModel", ParamMap.newParamMap().addParameter("mass", 10).build())
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorMissingPhysics(){
        PlatformConfig.builder()
                .setType("Rover")
                .setScreenName("Rover 1")
                .setID("r1")
                .setAutonomousModel("AutoCode", ParamMap.newParamMap().addParameter("version", 5).build())
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorMissingCode(){
        PlatformConfig.builder()
                .setType("Rover")
                .setScreenName("Rover 1")
                .setID("r1")
                .setPhysicsModel("PhysicsModel", ParamMap.newParamMap().addParameter("mass", 10).build())
                .build();
    }

    private PlatformConfig createPlatform(){
        return PlatformConfig.builder()
                .setType("Rover")
                .setScreenName("Rover 1")
                .setID("r1")
                .setAutonomousModel("AutoCode", ParamMap.newParamMap().addParameter("version", 5).build())
                .setPhysicsModel("PhysicsModel", ParamMap.newParamMap().addParameter("mass", 10).build())
                .addStateVariable("weather", -5.)
                .build();
    }

    @Test
    public void testType(){
        PlatformConfig config = createPlatform();
        Assert.assertEquals("Rover", config.getType());
    }

    @Test
    public void testName(){
        PlatformConfig config = createPlatform();
        Assert.assertEquals("Rover 1", config.getScreenName());
    }

    @Test
    public void testID(){
        PlatformConfig config = createPlatform();
        Assert.assertEquals("r1", config.getID());
    }

    @Test
    public void testCode(){
        PlatformConfig config = createPlatform();
        Assert.assertEquals("AutoCode", config.getAutonomousModelName());
        assert config.getAutonomousModelParameters().containsKey("version");
        Assert.assertEquals(5, config.getAutonomousModelParameters().get("version"), TOLERANCE);
    }

    @Test
    public void testPhysics(){
        PlatformConfig config = createPlatform();
        Assert.assertEquals("PhysicsModel", config.getPhysicsModelName());
        assert config.getPhysicsModelParameters().containsKey("mass");
        Assert.assertEquals(10., config.getPhysicsModelParameters().get("mass"), TOLERANCE);
    }

    @Test
    public void testState(){
        PlatformConfig config = createPlatform();
        assert config.getStateParameters().containsKey("weather");
        Assert.assertEquals(-5., (Double)config.getStateParameters().get("weather"), TOLERANCE);
    }

}
