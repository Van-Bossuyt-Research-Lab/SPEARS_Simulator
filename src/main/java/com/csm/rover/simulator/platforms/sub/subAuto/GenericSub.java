package com.csm.rover.simulator.platforms.sub.subAuto;

import com.csm.rover.simulator.objects.util.DecimalPoint3D;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;

import java.util.Map;

@AutonomousCodeModel(type="Sub", name="Generic", parameters={"sec"})
public class GenericSub extends SubAutonomousCode {

    private static final long serialVersionUID = -5883548370057346938L;

    private long lastActionTime = 0;
    private int action = 0;
    private int seconds = 1;
    private int power = 250;

    public GenericSub(){
        super("Generic", "Generic");
    }

    @Override
    public void constructParameters(Map<String, Double> params) {
        seconds = (int)params.get("sec").doubleValue();
    }

    @Override
    public String doNextCommand(
            long milliTime,
            DecimalPoint3D location,
            double[] orientation,
            Map<String, Double> params
    ){
        if (milliTime-lastActionTime > 1000*seconds){
            lastActionTime = milliTime;
            action++;
            if (action%11 == 0){
                power -= 50;
                return "chngmtr0" + power;
            }
            if (action%5 < 2){
                return "move";
            }
            else if (action%5 < 5){
                return "turnFR";
            }
            else {
                return "";
            }
        }
        else {
            return "";
        }
    }

}