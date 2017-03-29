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

package com.spears.environments;

import com.spears.environments.annotations.Modifier;

import java.util.Map;

/**
 * An extendable utility object for modifying/generating environment maps.  Should be labeled as a
 * {@link Modifier modifier} for use.  A generator should produce a
 * unique map, a modifier should manipulate the provided map.
 *
 * @param <T> Map type that extends {@link EnvironmentMap EnvironmentMap}
 */
public abstract class EnvironmentModifier<T extends EnvironmentMap> {

    protected final String platform_type;
    protected final boolean generator;

    /**
     * Creates a new modifier of with the given type.  Type will be checked against provided maps.
     *
     * @param type Type name
     */
    protected EnvironmentModifier(String type){
        this(type, false);
    }

    /**
     * Creates a new modifier or generator based on the generator parameter.
     *
     * @param type Type name
     * @param generator If true creates a generator
     */
    protected EnvironmentModifier(String type, boolean generator){
        platform_type = type;
        this.generator = generator;
    }

    /**
     * Returns the type name.
     *
     * @return type name
     */
    public String getType(){
        return platform_type;
    }

    /**
     * Returns true if the modifier is a designated generator.
     *
     * @return is a generator
     */
    public boolean isGenerator(){
        return generator;
    }

    /**
     * Public facing modify method.  Performs modification on the provided map and returns a new map instance.  Cannot
     * be extended by developers see {@link #doModify(EnvironmentMap, Map)}.
     *
     * @param map A {@link EnvironmentMap EnvironmentMap} of the appropriate type
     * @param params Parameter map to be used by the modification
     *
     * @return A new {@link EnvironmentMap EnvironmentMap} instance
     *
     * @throws IllegalArgumentException if the provided map is of the wrong type
     */
    public final T modify(T map, Map<String, Double> params){
        if (!generator && !map.getType().equals(platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, map.getType()));
        }
        return doModify(map, params);
    }

    /**
     * Developer method: this method should be extended to implement new modification functions.
     *
     * @param map (final) Incoming map, gaunted to be of correct type
     * @param params Parameter map
     *
     * @return Should return a new instance
     */
    abstract protected T doModify(final T map, final Map<String, Double> params);

}
