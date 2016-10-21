package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.objects.io.PlatformConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Platform {
    private static final Logger LOG = LogManager.getLogger(Platform.class);

    protected final String platform_type;

    protected PlatformEnvironment environment;

    protected PlatformAutonomousCodeModel autonomousCodeModel;
    protected PlatformPhysicsModel physicsModel;

    protected String name;
    protected String ID;

    protected Platform(String type){
        this.platform_type = type;
    }

    public final String getType(){
        return platform_type;
    }

    public void setEnvironment(PlatformEnvironment enviro){
        this.environment = enviro;
        if (autonomousCodeModel != null){
            autonomousCodeModel.setEnvironment(enviro);
        }
        else {
            LOG.log(Level.WARN, "Platform has no autonomousCodeModel: environment will not be set");
        }
        if (physicsModel != null){
            physicsModel.setEnvironment(enviro);
        }
        else {
            LOG.log(Level.WARN, "Platform has no physicsModel: environment will not be set");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Platform> T buildFromConfiguration(PlatformConfig cfg){
        Class<? extends Platform> type = PlatformRegistry.getPlatform(cfg.getType());
        try {
            Platform platform = type.newInstance();
            platform.name = cfg.getScreenName();
            platform.ID = cfg.getID();
            platform.autonomousCodeModel = PlatformRegistry.getAutonomousCodeModel(cfg.getType(), cfg.getAutonomousModelName()).newInstance();
            platform.autonomousCodeModel.constructParameters(cfg.getAutonomousModelParameters());
            platform.physicsModel = PlatformRegistry.getPhysicsModel(cfg.getType(), cfg.getPhysicsModelName()).newInstance();
            platform.physicsModel.constructParameters(cfg.getPhysicsModelParameters());
            platform.physicsModel.initializeState(PlatformRegistry.getPlatformState(cfg.getType()).newInstance().overrideValues(cfg.getStateParameters()));
            platform.physicsModel.setPlatformName(cfg.getScreenName());
            return (T)platform;
        }
        catch (InstantiationException | IllegalAccessException e) {
            LOG.log(Level.ERROR, "One or more platform classes failed to initialize on construct", e);
        }
        catch (ClassCastException e){
            LOG.log(Level.ERROR, "Incorrect type request, the final cast failed", e);
        }
        return null;
    }

    public abstract void start();

    public String getName(){
        return name;
    }

    public final PlatformState getState(){
        return physicsModel.getState();
    }

}
