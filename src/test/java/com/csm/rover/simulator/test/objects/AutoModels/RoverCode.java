package com.csm.rover.simulator.test.objects.AutoModels;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@AutonomousCodeModel(type = "Rover", name = "Test Rover", parameters = {"param1", "param2"})
public class RoverCode extends AbstractRoverCode {

    public Map<String, Double> paramMap = new TreeMap<>();

    public RoverCode(){

    }

    @Override
    public void setEnvironment(PlatformEnvironment enviro) {

    }

    @Override
    public void constructParameters(Map<String, Double> params) {
        paramMap = Collections.unmodifiableMap(params);
    }

    @Override
    public String nextCommand(long milliTime, PlatformState state) {
        return null;
    }

}
