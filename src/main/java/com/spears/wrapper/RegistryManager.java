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

package com.spears.wrapper;

import com.spears.environments.EnvironmentRegistry;
import com.spears.objects.CoverageIgnore;
import com.spears.platforms.PlatformRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * A class that initiates both the {@link PlatformRegistry Platform} and
 * {@link EnvironmentRegistry Environment} registries to load and checks them
 * for consistency.  Reports findings to log.
 */
public class RegistryManager {
    private static final Logger LOG = LogManager.getLogger(RegistryManager.class);

    /**
     * Initiates both registries to load and prints comparison to log.
     */
    static void checkRegistries(){
        PlatformRegistry.fillRegistry();
        EnvironmentRegistry.fillRegistry();
        compareRegistries();
    }

    @CoverageIgnore //just prints things
    private static void compareRegistries() {
        List<String> platformTypes = PlatformRegistry.getTypes();
        List<String> environmentTypes = EnvironmentRegistry.getTypes();
        LOG.log(Level.INFO, "Initializing Registry Comparison");
        for (String type : platformTypes){
            if (!environmentTypes.contains(type)){
                LOG.log(Level.WARN, "No Environment found corresponding to Platform type {}", type);
            }
            else {
                LOG.log(Level.INFO, "Linked Platform {} to Environment {}", PlatformRegistry.getPlatform(type), EnvironmentRegistry.getEnvironment(type));
            }
        }
        LOG.log(Level.INFO, "Registry Comparison Complete");
    }

}
