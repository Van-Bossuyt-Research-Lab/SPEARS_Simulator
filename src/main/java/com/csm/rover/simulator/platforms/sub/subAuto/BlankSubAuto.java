package com.csm.rover.simulator.platforms.sub.subAuto;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type="Sub", name="NULL", parameters={})
public class BlankSubAuto extends SubAutonomousCode {

    private static final long serialVersionUID = 4270320886718139449L;

    public BlankSubAuto() {
        super("NULL", "NULL");
    }

    @Override
    public void setEnvironment(PlatformEnvironment environment){
        //TODO
    }

    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
    public String doNextCommand(long milliTime, DecimalPoint location,
                                double direction, Map<String, Double> params) {
        // TODO Auto-generated method stub
        return "";
    }

}