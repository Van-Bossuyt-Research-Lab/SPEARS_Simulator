package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.environments.EnvironmentRegistry;
import com.csm.rover.simulator.platforms.PlatformRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class RegistryManager {
    private static final Logger LOG = LogManager.getLogger(RegistryManager.class);

    public static void checkRegistries(){
        PlatformRegistry.fillRegistry();
        compareRegistries();
    }

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
