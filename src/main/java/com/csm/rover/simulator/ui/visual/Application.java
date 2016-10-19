package com.csm.rover.simulator.ui.visual;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.Platform;

import java.util.List;
import java.util.Map;

public interface Application {

    void show();

    void hide();

    void start(Map<String, PlatformEnvironment> enviros, Map<String, List<Platform>> platforms);

}
