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

package com.spears.platforms.rover;

import com.spears.objects.SynchronousThread;
import com.spears.platforms.PlatformPhysicsModel;
import com.spears.wrapper.Globals;
import com.spears.platforms.PlatformAutonomousCodeModel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of the Rover Platform.
 */
@com.spears.platforms.annotations.Platform(type="Rover")
public class RoverObject extends com.spears.platforms.Platform {
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
	 * process the {@link PlatformAutonomousCodeModel AutonomousCodeModel}.
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
