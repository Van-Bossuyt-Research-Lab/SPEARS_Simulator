package com.csm.rover.simulator.rover.autoCode;

import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.wrapper.Globals;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class GORADRO2code extends RoverAutonomousCode {
    private static final Logger LOG = LogManager.getLogger(GORADRO2code.class);

    private static final long serialVersionUID = -8434875463993541766L;

    private static final double ANGLE_ERROR = Math.PI/16.0;
    private static final double RECALC_TIME = 2000; //ms
    enum States {
        CALCULATING, TURNING, TRAVELING, COLLECTING, STUCK, ESCAPING;
    };

    private static final double STALL_RADIUS = 5;
    private static final int STALL_TIME = 60000;
    private static final double RUN_ROTATION = 5*Math.PI/16.0;
    private static final int RUN_TIME = 15000;

    private boolean started = false;

    private States state = States.CALCULATING;
    private int score = 0;

    private double travelDirection = 0;

    private long lastCalcTime = 0;

    public GORADRO2code() {
        super("GORADRO2", "GORADRO");
    }

    public GORADRO2code(GORADRO2code orig) {
        super(orig);
        //TODO copy variables here
        this.started = orig.started;
        this.state = orig.state;
        this.travelDirection = orig.travelDirection;
        this.lastCalcTime = orig.lastCalcTime;
        this.score = orig.score;

    }

    @Override
    public String nextCommand(long milliTime, DecimalPoint location, double direction, Map<String, Double> parameters) {
        if (!started){

            started = true;
        }
        switch (state){
            case CALCULATING:
                calculateBasedDirection();
                lastCalcTime = milliTime;
                state = States.TURNING;

            case TURNING:
                if (Math.abs(direction-travelDirection) < ANGLE_ERROR){
                    state = States.TRAVELING;
                    return "move";
                }
                else {
                    if (Globals.getInstance().subtractAngles(direction, travelDirection) < 0){
                        return "spin_cw";
                    }
                    else {
                        return "spin_ccw";
                    }
                }

            case TRAVELING:
                if (MAP.isPointAtTarget(location)){
                    state = States.COLLECTING;
                    return "stop";
                }
                else if (milliTime-lastCalcTime > RECALC_TIME) {
                    state = States.CALCULATING;
                }
                return "";

            case COLLECTING:
                score += MAP.getTargetValueAt(location);
                state = States.CALCULATING;
                LOG.log(Level.INFO, "Reached a target.  Score at "+score);
                return "";

            case STUCK:

                return "";

            case ESCAPING:

                return "";

            default:
                state = States.CALCULATING;
                return "stop";
        }
    }

    private void calculateBasedDirection() {

    }

    @Override
    public RoverAutonomousCode clone() {
        return new GORADRO2code(this);
    }
}
