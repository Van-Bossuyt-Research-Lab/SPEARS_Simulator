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

package com.spears.test.objects.modifiers;

import com.spears.environments.EnvironmentModifier;
import com.spears.environments.annotations.Modifier;
import com.spears.test.objects.maps.LandMap;

import java.util.Map;

@Modifier(type = "Rover", name = "Land Gen", parameters = {}, generator = true)
public class LandGen extends EnvironmentModifier<LandMap> {

    private LandMap theMap;

    public LandGen() {
        super("Rover", true);
        theMap = new LandMap();
    }

    @Override
    protected LandMap doModify(LandMap map, Map<String, Double> params) {
        return theMap;
    }

    public LandMap getTheMap(){
        return theMap;
    }

}
