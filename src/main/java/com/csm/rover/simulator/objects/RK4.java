/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csm.rover.simulator.objects;

/**
 * Static function class to implement an Runge-Kutta 4 scheme.
 */
public abstract class RK4 {

    /**
     * Uses RK4 to predict the advance of the provided function
     *
     * @param fn Function to advance
     * @param time_step Time step to advance
     * @param current_time Current param 1
     * @param current_y Current param 2
     * @param others Other parameters unaffected by time
     *
     * @return Guess for y evaluated at current_time + time_step
     */
    public static double advance(RK4Function fn, double time_step, double current_time, double current_y, double... others){
        double k1 = fn.eval(current_time, current_y, others),
                k2 = fn.eval(current_time+time_step/2., current_y+time_step/2.*k1, others),
                k3 = fn.eval(current_time+time_step/2., current_y+time_step/2.*k2, others),
                k4 = fn.eval(current_time+time_step, current_y+time_step*k3, others);
        return current_y + time_step/6. * (k1 + k2 + k3 + k4);
    }

    /**
     * Functional interface
     */
    public interface RK4Function {

        /**
         * Evaluates the function.
         *
         * @param time Current param 1
         * @param y Current param 2
         * @param others Other function values unaffected by time
         *
         * @return Mathematical evaluation of the function
         */
        double eval(double time, double y, double... others);

    }

}
