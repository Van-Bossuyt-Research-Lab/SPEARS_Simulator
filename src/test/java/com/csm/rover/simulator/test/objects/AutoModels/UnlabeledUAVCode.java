package com.csm.rover.simulator.test.objects.AutoModels;

import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformState;

import java.util.Map;

public class UnlabeledUAVCode extends PlatformAutonomousCodeModel {

    public UnlabeledUAVCode() {
        super("UAV");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
    public String nextCommand(long milliTime, PlatformState state) {
        return null;
    }

}
