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

package com.spears.environments.sub;

import com.spears.environments.EnvironmentPopulator;
import com.spears.environments.PlatformEnvironment;
import com.spears.environments.annotations.Environment;
import com.spears.objects.util.DecimalPoint3D;
import com.spears.platforms.sub.SubObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * PlatformEnvironment implementation for the Sub Platform.
 */
@Environment(type="Sub")
public class AquaticEnvironment extends PlatformEnvironment<SubObject, AquaticMap> {

    /**
     * Creates a new empty environment.
     */
    public AquaticEnvironment(){
        super("Sub");
    }

    /**
     * Creates a new environment based on the provided map and populators.
     *
     * @param type must be "Sub"
     * @param map Map to use
     * @param pops Populator map
     */
    @JsonCreator
    public AquaticEnvironment(@JsonProperty("type") String type, @JsonProperty("map") AquaticMap map, @JsonProperty("populators") Map<String, EnvironmentPopulator> pops){
        super(type, map, pops);
        if (!type.equals("Sub")){
            throw new IllegalArgumentException("AquaticEnvironment type should be \'Sub\'");
        }
    }

    /**
     * Returns the current gravity acting in the environment.
     *
     * @return Gravitational acceleration in m/s^2
     */
    @JsonIgnore
    public double getGravity(){
        //TODO handle this better
        return 9.81;
    }

    /**
     * Returns the value of the populator at he given point.
     *
     * See: {@link PlatformEnvironment#getPopulatorValue(String, double...)}.
     *
     * @param pop Name of the populator to check
     * @param point Point to check at
     *
     * @return The populator value
     */
    public double getPopulatorValue(String pop, DecimalPoint3D point){
        return super.getPopulatorValue(pop, point.getX(), point.getY(), point.getZ());
    }

    /**
     * Returns whether the populator is defined at the requested point.
     *
     * See: {@link PlatformEnvironment#isPopulatorAt(String, double...)}.
     *
     * @param pop The name of the populator to check
     * @param point The point to check
     *
     * @return Populator value greater than 0
     */
    public boolean isPopulatorAt(String pop, DecimalPoint3D point){
        return super.isPopulatorAt(pop, point.getX(), point.getY(), point.getZ());
    }

    /**
     * Returns the size of the environment's map.
     *
     * @return {@link AquaticMap#getSize()}
     */
    @JsonIgnore
    public int getSize(){
        return map.getSize();
    }

    /**
     * Returns the detail of the environment's map.
     *
     * @return {@link AquaticMap#getDetail()}
     */
    @JsonIgnore
    public int getDetail(){
        return map.getDetail();
    }

    /**
     * Returns the density at the requested point.
     *
     * @param point Point to query
     *
     * @return {@link AquaticMap#getDensityAt(DecimalPoint3D)} density in kg/m^3
     */
    @JsonIgnore
    public double getDensityAt(DecimalPoint3D point){
        return map.getDensityAt(point);
    }

    /**
     * Returns the maximum density value in the map
     *
     * @return {@link AquaticMap#getMaxValue()}
     */
    @JsonIgnore
    public double getMaxDensity(){
        return map.getMaxValue();
    }

}