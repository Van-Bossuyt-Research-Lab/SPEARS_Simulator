package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.objects.io.PlatformConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Platform {
    private static final Logger LOG = LogManager.getLogger(Platform.class);

    protected final String platform_type;

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

    @SuppressWarnings("unchecked")
    public static <T extends Platform> T buildFromConfiguration(PlatformConfig cfg, PlatformState initState){
        Class<? extends Platform> type = PlatformRegistry.getPlatform(cfg.getType());
        try {
            Platform platform = type.newInstance();
            platform.name = cfg.getScreenName();
            platform.ID = cfg.getID();
            platform.autonomousCodeModel = PlatformRegistry.getAutonomousCodeModel(cfg.getType(), cfg.getAutonomousModelName()).newInstance();
            platform.autonomousCodeModel.constructParameters(cfg.getAutonomousModelParameters());
            platform.physicsModel = PlatformRegistry.getPhysicsModel(cfg.getType(), cfg.getPhysicsModelName()).newInstance();
            platform.physicsModel.constructParameters(cfg.getPhysicsModelParameters());
            platform.physicsModel.initializeState(initState);
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

}
