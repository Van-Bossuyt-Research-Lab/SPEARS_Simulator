package com.csm.rover.simulator.rover.autoCode;

import com.csm.rover.simulator.objects.DecimalPoint;

import java.util.Map;

public class GORADRO2code extends RoverAutonomousCode {

    enum States {
        CALCULATING("calculating");

        private String string;
        States(String name){
            this.string = name;
        }
    };

    private boolean started = false;

    private States state = States.CALCULATING;

    public GORADRO2code() {
        super("GORADRO2", "GORADRO");
    }

    public GORADRO2code(GORADRO2code orig) {
        super(orig);
        //TODO cpy variables here
        this.started = orig.started;
        this.state = orig.state;

    }

    @Override
    public String nextCommand(long milliTime, DecimalPoint location, double direction, Map<String, Double> parameters) {
        if (!started){

            started = true;
        }
        switch (state){
            case (CALCULATING):

                return "";
            default:

                return "";
        }
    }

    @Override
    public RoverAutonomousCode clone() {
        return new GORADRO2code(this);
    }
}
