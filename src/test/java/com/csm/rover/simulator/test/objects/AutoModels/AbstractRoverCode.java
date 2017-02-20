package com.csm.rover.simulator.test.objects.autoModels;

import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type = "Rover", name = "Generic", parameters = {})
public abstract class AbstractRoverCode extends PlatformAutonomousCodeModel {

    protected AbstractRoverCode() {
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
