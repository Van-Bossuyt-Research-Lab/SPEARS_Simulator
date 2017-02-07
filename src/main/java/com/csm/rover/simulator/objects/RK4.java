package com.csm.rover.simulator.objects;

public abstract class RK4 {

    public static double advance(RK4Function fn, double time_step, double current_time, double current_y, double... others){
        double k1 = fn.eval(current_time, current_y, others),
                k2 = fn.eval(current_time+time_step/2., current_y+time_step/2.*k1, others),
                k3 = fn.eval(current_time+time_step/2., current_y+time_step/2.*k2, others),
                k4 = fn.eval(current_time+time_step, current_y+time_step*k3, others);
        return current_y + time_step/6. * (k1 + k2 + k3 + k4);
    }

    public interface RK4Function {

        double eval(double time, double y, double... others);

    }

}
