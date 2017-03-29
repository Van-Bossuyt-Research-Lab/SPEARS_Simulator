package com.csm.rover.simulator.platforms.rover;

import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.wrapper.Globals;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of the Rover Platform.
 */
@com.csm.rover.simulator.platforms.annotations.Platform(type="Rover")
public class RoverObject extends Platform {
	private static final Logger LOG = LogManager.getLogger(RoverObject.class);

	private Globals globals;

	private long nextOperationTime;

	/**
	 * Creates a new platform.
	 */
	public RoverObject(){
		super("Rover");
		globals = Globals.getInstance();
	}

	/**
	 * Starts executing the Platform.  Calls {@link PlatformPhysicsModel#start()} and begins a loop to process
	 * process the {@link com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel AutonomousCodeModel}.
	 */
	@Override
	public void start(){
		nextOperationTime = globals.timeMillis() + 10000;
		new SynchronousThread(100, this::executeCode, SynchronousThread.FOREVER, name+"-code");
		physicsModel.start();
	}
	
	private void executeCode(){
		try {
			if (globals.timeMillis() >= nextOperationTime) {
				String cmd = autonomousCodeModel.nextCommand(
						Globals.getInstance().timeMillis(),
						physicsModel.getState()
				);
				switch (cmd) {
					case "":
						break;
					case "delay":
						nextOperationTime = globals.timeMillis() + 1000;
						break;
					default:
						physicsModel.sendDriveCommand(cmd);
						break;
				}
			}
		}
		catch (Exception e) {
			LOG.log(Level.ERROR, name + ": Error in run code", e);
		}
	}

}
