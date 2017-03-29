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

import com.spears.objects.CoverageIgnore;
import com.spears.objects.io.jsonserial.EnvironmentMapDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EnvironmentMapDeserializer.class)
public abstract class EnvironmentMap {

    @JsonIgnore
    protected final String platform_type;

    protected EnvironmentMap(String type){
        platform_type = type;
    }

    public String getType(){
        return platform_type;
    }

    @CoverageIgnore
    @Override
    public boolean equals(Object o){
        return o instanceof EnvironmentMap && isEqual((EnvironmentMap)o);
    }

    protected abstract boolean isEqual(EnvironmentMap other);

}
