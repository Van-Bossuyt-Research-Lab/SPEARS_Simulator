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

package com.csm.rover.simulator.environments.rover;

import com.csm.rover.simulator.environments.EnvironmentPopulator;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.wrapper.Globals;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * The PlatformEnvironment implementation for the Rover Platform.
 */
@Environment(type="Rover")
public class TerrainEnvironment extends PlatformEnvironment<RoverObject, TerrainMap> {
    private static final Logger LOG = LogManager.getLogger(TerrainEnvironment.class);

    /**
     * Creates and empty environment.
     */
    public TerrainEnvironment(){
        super("Rover");
    }

    /**
     * Better constructor for the TerrainEnvironment.
     *
     * @param type Must be Rover
     * @param map Environment map to base the environment on
     * @param pops Map of Populators to use in the environment
     *
     * @throws IllegalArgumentException if type != "Rover"
     */
    @JsonCreator
    public TerrainEnvironment(@JsonProperty("type") String type, @JsonProperty("map") TerrainMap map, @JsonProperty("populators") Map<String, EnvironmentPopulator> pops){
        super(type, map, pops);
        if (!type.equals("Rover")){
            throw new IllegalArgumentException("TerrainEnvironment type should be \'Rover\'");
        }
    }

    /**
     * Returns the map height at the requested point.  See {@link TerrainMap#getHeightAt(DecimalPoint)}.
     * If the point is out of bounds, attempts to terminate the current thread.
     *
     * @param point Point to query
     * @return Interpolated height map value
     */
    public double getHeightAt(DecimalPoint point){
        try {
            return map.getHeightAt(point);
        }
        catch (ArrayIndexOutOfBoundsException e){
            String current = Thread.currentThread().getName();
            String name = current.substring(0, current.indexOf('-'));
            if (name.equalsIgnoreCase("awt")){
                throw e;
            }
            else {
                LOG.log(Level.WARN, String.format("Rover \'%s\' has reached the end of the map - TERMINATING", name));
                Globals.getInstance().killThread(current);
            }
            return 0;
        }
    }

    /**
     * Returns the instantaneous slope at the requested point in the requested direction.
     *
     * @param point Point to query
     * @param direction Direction to examine
     * @return Slope in radians
     */
    public double getSlopeAt(DecimalPoint point, double direction){
        double radius = 0.1;
        double h0 = getHeightAt(point);
        DecimalPoint loc2 = point.offset(radius*Math.cos(direction), radius*Math.sin(direction));
        double hnew = getHeightAt(loc2);
        return Math.atan((hnew - h0) / radius);
    }

    /**
     * Returns the instantaneous slope at the requested point 90deg cw from the requested direction.
     *
     * @param point Point to query
     * @param direction Base direction
     * @return Slope in radians
     */
    public double getCrossSlopeAt(DecimalPoint point, double direction){
        return getSlopeAt(point, (direction - Math.PI / 2.0 + Math.PI * 2) % (2 * Math.PI));
    }

    /**
     * Returns the base gravity value for the environment
     *
     * @return Gravitational acceleration in m/s^2
     */
    @JsonIgnore
    public double getGravity(){
        //TODO handle this better
        return 9.81;
    }

    /**
     * Gets the value of a populator in the environment.
     *
     * See: {@link PlatformEnvironment#getPopulatorValue(String, double...)}
     *
     * @param pop Populator name
     * @param point Point to query
     *
     * @return The value of the populator
     */
    public double getPopulatorValue(String pop, DecimalPoint point){
        return super.getPopulatorValue(pop, point.getX(), point.getY());
    }

    /**
     * Returns whether the populator is defined at a point.
     *
     * See: {@link PlatformEnvironment#isPopulatorAt(String, double...)}
     *
     * @param pop Populator name
     * @param point Point to query
     *
     * @return Populator value == 0
     */
    public boolean isPopulatorAt(String pop, DecimalPoint point){
        return super.isPopulatorAt(pop, point.getX(), point.getY());
    }

    /**
     * Returns the size of the Map.
     *
     * @return {@link TerrainMap#getSize()}
     */
    @JsonIgnore
    public int getSize(){
        return map.getSize();
    }

    /**
     * Returns the detail of the Map.
     *
     * @return {@link TerrainMap#getDetail()}
     */
    @JsonIgnore
    public int getDetail(){
        return map.getDetail();
    }

    /**
     * Returns the maximum height of the Map.
     *
     * @return {@link TerrainMap#getMaxValue()}
     */
    @JsonIgnore
    public double getMaxHeight(){
        return map.getMaxValue();
    }

}
