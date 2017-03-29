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

package com.spears.test.objects.environments;

import com.spears.environments.EnvironmentPopulator;
import com.spears.environments.PlatformEnvironment;
import com.spears.environments.annotations.Environment;
import com.spears.test.objects.maps.LandMap;
import com.spears.test.objects.platforms.RoverPlatform;

import java.util.Map;

@Environment(type = "Rover")
public class LandEnv extends PlatformEnvironment<RoverPlatform, LandMap> {

    public LandEnv() {
        super("Rover");
    }

    public LandEnv(Map<String, EnvironmentPopulator> pops){
        super("Rover", new LandMap(), pops);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
