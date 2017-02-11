package com.csm.rover.simulator.test.objects.autoModels;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type = "Sub", name = "Test Sub", parameters = {"param1"})
public class PrivateSubCode extends PlatformAutonomousCodeModel {

    protected PrivateSubCode() {
        super("Sub");
    }

    @Override
    public void setEnvironment(PlatformEnvironment enviro) {

    }

    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
    public String nextCommand(long milliTime, PlatformState state) {
        return null;
    }

}
