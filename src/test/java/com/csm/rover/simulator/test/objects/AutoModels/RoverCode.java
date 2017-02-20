package com.csm.rover.simulator.test.objects.autoModels;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type = "Rover", name = "Test Rover", parameters = {"param1", "param2"})
public class RoverCode extends AbstractRoverCode {

    public RoverCode(){

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
