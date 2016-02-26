package com.csm.rover.simulator.platforms.rover;

import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.PlatformState;

public class RoverState extends PlatformState {

    public DecimalPoint location;
    public double direction;

    public double temperature;

    public RoverState(){
        super("Rover");
    }

    public static RoverState defaultState(){
        RoverState state = new RoverState();
        state.location = new DecimalPoint(0, 0);
        state.direction = Math.PI / 2;
        state.temperature = -30;
        return state;
    }

}
