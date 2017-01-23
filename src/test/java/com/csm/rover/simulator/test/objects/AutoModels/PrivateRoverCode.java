package com.csm.rover.simulator.test.objects.AutoModels;

import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type = "Rover", name = "Test Rov", parameters = {"param1"})
public class PrivateRoverCode extends PlatformAutonomousCodeModel {

    protected PrivateRoverCode() {
        super("Rover");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
    public String nextCommand(long milliTime, PlatformState state) {
        return null;
    }

}
