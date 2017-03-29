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

package com.spears.objects.io;

import com.spears.objects.CoverageIgnore;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * A data structure object used for holding all data on a particular platform type.
 */
public class TypeConfig implements Cloneable {

    /**
     * The platform type name
     */
    public final String type;

    /**
     * The absolute file to the saved map.
     */
    @JsonIgnore
    public final File mapFile;

    /**
     * {@link Collections#unmodifiableList(List) Unmoddifiable list} containing the platforms
     */
    @JsonProperty("platforms")
    public final List<PlatformConfig> platformConfigs;

    /**
     * Basic constructor used for JSON.  Recommend using {@link #TypeConfig(String, File, List)}.
     *
     * @param type The type name
     * @param mapFile The filepath to the saved map file
     * @param platforms List of platforms of the type {@link #type}
     */
    @JsonCreator
    public TypeConfig(
            @JsonProperty("type") String type,
            @JsonProperty("map") String mapFile,
            @JsonProperty("platforms") List<PlatformConfig> platforms){
        this.type = type;
        this.mapFile = new File(new File(mapFile).getAbsolutePath());
        this.platformConfigs = Collections.unmodifiableList(platforms);
    }

    /**
     * Recommended constructor.
     *
     * @param type The type name
     * @param mapFile Absolute file of the saved map
     * @param platformConfigs List of platfroms of the type {@link #type}
     */
    public TypeConfig(String type, File mapFile, List<PlatformConfig> platformConfigs){
        this(type, mapFile.getAbsolutePath(), platformConfigs);
    }

    private TypeConfig(TypeConfig orig){
        this.type = orig.type;
        this.mapFile = new File(orig.mapFile.getAbsolutePath());
        this.platformConfigs = Collections.unmodifiableList(orig.platformConfigs);
    }

    /**
     * Returns the absolute filepath of {@link #mapFile}.
     *
     * @return absolute filepath
     */
    @CoverageIgnore
    @JsonProperty("map")
    public String getMapPath(){
        return mapFile.getAbsolutePath();
    }

    @Override
    public TypeConfig clone(){
        return new TypeConfig(this);
    }

    @CoverageIgnore
    @Override
    public boolean equals(Object o){
        if (o instanceof TypeConfig){
            TypeConfig other = (TypeConfig)o;
            return other.mapFile.equals(this.mapFile) && this.type.equals(other.type)
                    && this.platformConfigs.equals(other.platformConfigs);
        }
        else {
            return false;
        }
    }

}
