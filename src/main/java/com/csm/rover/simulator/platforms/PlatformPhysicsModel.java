package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;

/**
 * Super class for developing physics models for platforms.
 */
public abstract class PlatformPhysicsModel {
    private static final Logger LOG = LogManager.getLogger(PlatformPhysicsModel.class);

    protected final String platform_type;

    private final Map<String, DriveCommandHandler> command_handlers;

    /**
     * Standard creator
     *
     * @param type The platform type of the model
     */
    protected PlatformPhysicsModel(String type){
        this.platform_type = type;
        command_handlers = new TreeMap<>();
    }

    public final String getType(){
        return platform_type;
    }

    /**
     * Initialization method called when the simulation is started.  Parameters should be specified in a
     * {@link com.csm.rover.simulator.platforms.annotations.PhysicsModel} tag.
     *
     * @param params Build parameters
     */
    public abstract void constructParameters(Map<String, Double> params);

    /**
     * Starts the physic models update routine.
     */
    public abstract void start();

    public abstract void setPlatformName(String name);

    /**
     * Sets the initial state of the platform.  Should only called before {@link #start()}.
     * @param state The starting state
     */
    public abstract void initializeState(PlatformState state);

    /**
     * Returns the state of the platform as tracked by the physics model.
     *
     * @return The current platform state
     */
    public abstract PlatformState getState();

    /**
     * Sets the environment the platform is operating in.  Should only be called once, before {@link #start()}.
     *
     * @param enviro The platform's environment
     */
    public abstract void setEnvironment(PlatformEnvironment enviro);

    /**
     * Adds mapping for text commands to make changes in the physics drive model.
     *
     * @param cmd The command
     * @param process Desired response
     */
    protected final void addCommandHandler(String cmd, DriveCommandHandler process){
        if (command_handlers.containsKey(cmd)){
            LOG.log(Level.WARN, "Previous handler for command \"{}\" is being overridden", cmd);
        }
        command_handlers.put(cmd, process);
    }

    /**
     * Fires the appropriate command based on existing mapping.  If the command is unknown, no effect.
     *
     * @param cmd Command to execute
     * @param params Command parameters
     */
    public boolean sendDriveCommand(String cmd, double... params){
        if (command_handlers.containsKey(cmd)) {
            command_handlers.get(cmd).processCommand(params);
            return true;
        }
        else {
            LOG.log(Level.ERROR, "This PhysicsModel has no definition for command \"{}\"", cmd);
            return false;
        }
    }

}
