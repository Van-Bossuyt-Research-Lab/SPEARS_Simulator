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

package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.objects.CoverageIgnore;
import com.csm.rover.simulator.objects.io.PlatformConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Parent class for the construction to Platforms.
 */
public abstract class Platform {
    @CoverageIgnore
    private static final Logger LOG = LogManager.getLogger(Platform.class);

    /**
     * The type name
     */
    protected final String platform_type;

    /**
     * Environment the platform is operating in
     */
    protected PlatformEnvironment environment;

    /**
     * The autonomous code model for this platform instance
     */
    protected PlatformAutonomousCodeModel autonomousCodeModel;
    /**
     * Physics model for this platform instance
     */
    protected PlatformPhysicsModel physicsModel;

    /**
     * The platform's name
     */
    protected String name;
    /**
     * The communication ID for the platform
     * @deprecated
     */
    protected String ID;

    /**
     * Constructor requires a type.  See {@link #buildFromConfiguration(PlatformConfig)} to create a new Platform.
     *
     * @param type Platform type name
     */
    protected Platform(String type){
        this.platform_type = type;
    }

    public final String getType(){
        return platform_type;
    }

    /**
     * Set the active environment for this platform as well as its code and physics models.
     *
     * @param enviro The environment, must be of the same type
     */
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

    /**
     * Creates a new platform from a configuration object.  Instantiates the code and physics models using the
     * {@link PlatformRegistry} and runs their {@link PlatformAutonomousCodeModel#constructParameters(Map)} and
     * {@link PlatformAutonomousCodeModel#constructParameters(Map)} methods respectively.  Return null if instantiation
     * fails.
     *
     * @param cfg Configuration to use
     * @param <T> Return class of Platform, does not catch bad cast
     * @return Complete Platform instance
     */
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
        catch (NullPointerException | InstantiationException | IllegalAccessException e) {
            LOG.log(Level.ERROR, "One or more platform classes failed to initialize on construct", e);
        }
        return null;
    }

    /**
     * Starts the platform executing the simulation.  This is implemented by the child object, which should
     * start the code and physics models.
     */
    public abstract void start();

    public String getName(){
        return name;
    }

    /**
     * Returns the current state of the platform taken from the physics model.
     *
     * @return {@link PlatformPhysicsModel#getState()}
     */
    public final PlatformState getState(){
        return physicsModel.getState();
    }

}
