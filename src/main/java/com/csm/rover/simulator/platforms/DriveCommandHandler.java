package com.csm.rover.simulator.platforms;

/**
 * Mapping interface for issuing command to the physics model's drive engine.
 */
public interface DriveCommandHandler {

    /**
     * Executes a user defined command with the provided parameters.
     *
     * @param params Command parameters
     */
    void processCommand(double[] params);

}
