package com.csm.rover.simulator.test.objects.AutoModels;

import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type = "Sub", name = "Generic", parameters = {})
public abstract class AbstractSubCode extends PlatformAutonomousCodeModel {

    protected AbstractSubCode() {
        super("Sub");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
    public String nextCommand(long milliTime, PlatformState state) {
        return null;
    }
}
