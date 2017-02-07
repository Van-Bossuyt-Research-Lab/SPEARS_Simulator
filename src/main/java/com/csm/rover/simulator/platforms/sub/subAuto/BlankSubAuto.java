package com.csm.rover.simulator.platforms.sub.subAuto;

import com.csm.rover.simulator.objects.util.DecimalPoint3D;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type="Sub", name="NULL", parameters={})
public class BlankSubAuto extends SubAutonomousCode {

    private static final long serialVersionUID = 4270320886718139449L;

    public BlankSubAuto() {
        super("NULL", "NULL");
    }


    @Override
    public void constructParameters(Map<String, Double> params) {

    }

    @Override
    public String doNextCommand(long milliTime, DecimalPoint3D location,
                                double[] orientation, Map<String, Double> params) {
        // TODO Auto-generated method stub
        return "";
    }

}