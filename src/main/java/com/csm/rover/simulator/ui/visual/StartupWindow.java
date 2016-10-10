package com.csm.rover.simulator.ui.visual;

import java.util.List;

public interface StartupWindow {

    void registerPlatform(String platform);

    void registerAutonomousCodeModel(String platform, String name, List<String> parameters);

    void registerPhysicsModel(String platform, String name, List<String> parameters);

    void registerEnvironmentModifier(String platform, String name, List<String> parameters);

    void registerEnvironmentPopulator(String platform, String name, List<String> parameters);

    void display();

    void setStartUpAction(StartupListener e);

}
