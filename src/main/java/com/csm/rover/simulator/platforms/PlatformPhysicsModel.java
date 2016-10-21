package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;

public abstract class PlatformPhysicsModel {
    private static final Logger LOG = LogManager.getLogger(PlatformPhysicsModel.class);

    protected final String platform_type;

    protected PlatformPhysicsModel(String type){
        this.platform_type = type;
        command_handlers = new TreeMap<>();
    }

    public final String getType(){
        return platform_type;
    }

    public abstract void constructParameters(Map<String, Double> params);

    public abstract void start();
    public abstract void updatePhysics();

    public abstract void setPlatformName(String name);
    public abstract void initializeState(PlatformState state);
    public abstract PlatformState getState();

    private final Map<String, DriveCommandHandler> command_handlers;

    public abstract void setEnvironment(PlatformEnvironment enviro);

    protected final void addCommandHandler(String cmd, DriveCommandHandler process){
        if (command_handlers.containsKey(cmd)){
            LOG.log(Level.WARN, "Previous handler for command \"{}\" is being overridden", cmd);
        }
        command_handlers.put(cmd, process);
    }

    public final void sendDriveCommand(String cmd, double... params){
        if (command_handlers.containsKey(cmd)) {
            command_handlers.get(cmd).processCommand(params);
        }
        else {
            LOG.log(Level.ERROR, "This PhysicsModel has no definition for command \"{}\"", cmd);
        }
    }

}
