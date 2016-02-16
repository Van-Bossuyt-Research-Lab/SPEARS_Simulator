package com.csm.rover.simulator.rover.autoCode;


import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.wrapper.Globals;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GORADROGuided extends RoverAutonomousCode {
    private static final Logger LOG = LogManager.getLogger(GORADROGuided.class);

    private static final long serialVersionUID = -8434875463993541766L;

    private static final double ANGLE_ERROR = Math.PI/13.0;
    private static final double WAYPOINT_ERROR = 1.5;
    private static final double RECALC_TIME = 2000; //ms
    enum States {
        CALCULATING, TURNING, TRAVELING, COLLECTING, STUCK, ESCAPING, DONE
    }

    private static final double STALL_RADIUS = 5;
    private static final int STALL_TIME = 90000;
    private static final int RUN_TIME = 25000;

    private static final int HISTORIES = 3;
    private static final int SAMPLE_DIRECTIONS = 16;
    private static final double SAMPLE_RADIUS = 2;

    private static final double AVERAGING_RADIUS = 25;
    private static final double AVERAGING_ANGLE = Math.PI / 6.0; //rad
    private static final double AVERAGING_P_STEP = Math.PI / 45.0;
    private static final double AVERAGING_R_STEP = 0.1;

    private boolean started = false;

    private States state = States.CALCULATING;
    private int score = 0;

    double[][] potentials;

    private double travelDirection = 0;

    private Set<Point> visitedScience = new HashSet<Point>();

    private long lastCalcTime = 0;
    private DecimalPoint lastLoc = new DecimalPoint(0, 0);
    private long timeAtPoint = 0;

    private Point[] waypoints;
    private int current_waypoint = 0;
    private static final double[] attitudes = { 1000, 25, 23.333, 0.666, 200 };
    private double dev_tolerance;

    private int stuck_turn = 0;

    public GORADROGuided(Point[] waypoints, double dev_tolerance) {
        super("GORADRO-G", "GORADRO-G");
        this.waypoints = waypoints;
        this.dev_tolerance = dev_tolerance;
        potentials = new double[HISTORIES][SAMPLE_DIRECTIONS];
    }

    public GORADROGuided(GORADROGuided orig){
        super(orig);
        //TODO copy variables here
        this.started = orig.started;
        this.state = orig.state;
        this.travelDirection = orig.travelDirection;
        this.lastCalcTime = orig.lastCalcTime;
        this.score = orig.score;
        this.waypoints = orig.waypoints.clone();
        this.current_waypoint = orig.current_waypoint;
        this.dev_tolerance = orig.dev_tolerance;
        this.lastLoc = orig.lastLoc;
        this.timeAtPoint = orig.timeAtPoint;
        this.potentials = orig.potentials.clone();
        this.visitedScience = orig.visitedScience;
    }

    @Override
    public String nextCommand(long milliTime, DecimalPoint location, double direction, Map<String, Double> parameters) {
        if (!started){
            writeToLog("time\tX\tY\tZ\tscore\tcharge\tstate\thazard\twaypoints");
            started = true;
        }
        writeToLog(milliTime + "\t" + formatDouble(location.getX()) + "\t" + formatDouble(location.getY()) + "\t" + formatDouble(MAP.getHeightAt(location)) + "\t" + score + "\t" + formatDouble(parameters.get("battery_charge")) + "\t" + state + "\t" + MAP.getHazardValueAt(location) + "\t" + current_waypoint);
        direction = (direction + 2*Math.PI) % (2*Math.PI);
        switch (state){
            case CALCULATING:
                calculateBasedDirection(location, direction);
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
                if (hasUnvisitedScience(location)){
                    state = States.COLLECTING;
                    return "stop";
                }
                else if (distanceBetween(location, waypoints[current_waypoint]) < WAYPOINT_ERROR){
                    current_waypoint++;
                    if (current_waypoint == waypoints.length){
                        state = States.DONE;
                    }
                }
                else if (milliTime-timeAtPoint > STALL_TIME){
                    if (distanceBetween(location, lastLoc) > STALL_RADIUS){
                        travelDirection = Math.atan2(waypoints[current_waypoint].getY()-location.getY(),
                                waypoints[current_waypoint].getX()-location.getX());
                        state = States.STUCK;
                    }
                    else {
                        lastLoc = location.clone();
                        timeAtPoint = milliTime;
                    }
                }
                else if (milliTime-lastCalcTime > RECALC_TIME) {
                    state = States.CALCULATING;
                }
                return "";

            case COLLECTING:
                score += MAP.getTargetValueAt(location);
                Point mapLoc = MAP.getMapSquare(location);
                visitedScience.add(new Point(mapLoc.x/3, mapLoc.y/3));
                state = States.CALCULATING;
//                LOG.log(Level.INFO, "Reached a target at {}.  Score at "+score, location.toString());
                return "";

            case STUCK:
                int stuck_this = Globals.getInstance().subtractAngles(direction, travelDirection) < 0 ? -1 : 1;
                if (stuck_turn == 0){
                    stuck_turn = stuck_this;
                    if (stuck_turn == -1){
                        return "spin_cw";
                    }
                    else {
                        return "spin_ccw";
                    }
                }
                else if (Math.abs(direction-travelDirection) < ANGLE_ERROR || stuck_turn != stuck_this){
                    stuck_turn = 0;
                    state = States.ESCAPING;
                    lastCalcTime = milliTime;
                    return "move";
                }
                else {
                    return "";
                }

            case ESCAPING:
                if (milliTime-lastCalcTime > RUN_TIME){
                    lastLoc = location;
                    timeAtPoint = milliTime;
                    state = States.CALCULATING;
                }
                else if (hasUnvisitedScience(location)){
                    state = States.COLLECTING;
                    return "stop";
                }
                else if (distanceBetween(location, waypoints[current_waypoint]) < WAYPOINT_ERROR){
                    current_waypoint++;
                    if (current_waypoint == waypoints.length){
                        state = States.DONE;
                    }
                }
                return "move";

            case DONE:
                return "stop";

            default:
                state = States.CALCULATING;
                return "stop";
        }
    }

    private void calculateBasedDirection(DecimalPoint location, double direction) {
        for (int i = 1; i < HISTORIES; i++){
            for (int j = 0; j < SAMPLE_DIRECTIONS; j++){
                potentials[i-1][j] = potentials[i][j];
            }
        }

        double maxPotential = Integer.MIN_VALUE;
        double maxDirection = 0;
        for (int i = 0; i < SAMPLE_DIRECTIONS; i++){
            double theta = 2*Math.PI*i/(double) SAMPLE_DIRECTIONS;
            double deltaX = SAMPLE_RADIUS * Math.cos(theta);
            double deltaY = SAMPLE_RADIUS * Math.sin(theta);
            DecimalPoint examine = location.offset(deltaX, deltaY);

            //if there is a scientific value at the point raise priority
            int science = 0;
            for (int radius = 0; radius < SAMPLE_RADIUS; radius++){
                if (hasUnvisitedScience(location.offset(radius*Math.cos(theta), radius*Math.sin(theta)))){
                    science = MAP.getTargetValueAt(location.offset(radius * Math.cos(theta), radius * Math.sin(theta)));
                    break;
                }
            }

            //if there is a hazard at the point get less excited
            int hazard = MAP.isPointInHazard(examine) ?  MAP.getHazardValueAt(examine) : 0;

            //Calculate the density of science targets in a wedge away from the rover
            double scienceArea = 0;
            for (double radius = SAMPLE_RADIUS; radius <= AVERAGING_RADIUS; radius += AVERAGING_R_STEP){
                for (double phi = -AVERAGING_ANGLE /2.0; phi <= AVERAGING_ANGLE /2.0; phi += AVERAGING_P_STEP){
                    if (hasUnvisitedScience(location.offset(radius*Math.cos(theta+phi), radius*Math.sin(theta+phi)))){
                        scienceArea += SAMPLE_RADIUS /radius;
                    }
                }
            }
            scienceArea /= AVERAGING_RADIUS * AVERAGING_ANGLE;

            //Calculate the density of hazards in a wedge away from the rover
            double hazardArea = 0;
            for (double radius = SAMPLE_RADIUS; radius <= AVERAGING_RADIUS; radius += AVERAGING_R_STEP){
                for (double phi = -AVERAGING_ANGLE /2.0; phi <= AVERAGING_ANGLE /2.0; phi += AVERAGING_P_STEP){
                    if (MAP.isPointInHazard(location.offset(radius * Math.cos(theta + phi), radius * Math.sin(theta + phi)))){
                        hazardArea += SAMPLE_RADIUS /radius;
                    }
                }
            }
            hazardArea /= AVERAGING_RADIUS * AVERAGING_ANGLE;

            //calculate the potential of the point
            potentials[HISTORIES -1][i] =
                    attitudes[0]*science
                    - attitudes[1]*hazard
                    + attitudes[2]*scienceArea
                    - attitudes[3]*hazardArea
                    - attitudes[4]*getDirectionPenalty(location,theta);
            double average = 0;
            for (int j = 0; j < HISTORIES; j++){
                average += potentials[j][i];
            }
            average /= (double) HISTORIES;
            if (average > maxPotential){
                maxPotential = average;
                maxDirection = theta;
            }
        }
        travelDirection = maxDirection;
    }

    private boolean hasUnvisitedScience(DecimalPoint loc){
        if (MAP.isPointAtTarget(loc)){
            Point mapLoc = MAP.getMapSquare(loc);
            return !visitedScience.contains(new Point(mapLoc.x/3, mapLoc.y/3));
        }
        return false;
    }

    private double getDirectionPenalty(DecimalPoint location, double direction){
        double waypoint_direction = getWaypointDirection(location);
        waypoint_direction = (waypoint_direction + 2*Math.PI) % (2*Math.PI);
        return 1-Math.exp(-(Math.pow(direction-waypoint_direction, 2))/(2*Math.pow(dev_tolerance, 2)));
    }

    private double getWaypointDirection(DecimalPoint loc){
        return  (Math.atan2(waypoints[current_waypoint].getY()-loc.getY(),
                waypoints[current_waypoint].getX()-loc.getX())+2*Math.PI)%(2*Math.PI);
    }

    private double distanceBetween(DecimalPoint a, Point b){
        return distanceBetween(a, new DecimalPoint(b.getX(), b.getY()));
    }

    private double distanceBetween(DecimalPoint a, DecimalPoint b){
        return Math.sqrt(Math.pow(a.getX()-b.getX(),2) + Math.pow(a.getY()-b.getY(), 2));
    }

    private String formatDouble(double in){
        String out = "";
        if (Math.abs(in) < Integer.MAX_VALUE/1000){
            if (in < 0){
                in *= -1;
                out = "-";
            }
            int whole = (int)in;
            out += whole;
            int part = (int)((in * 1000) - whole*1000);
            if (part == 0){
                out += ".000";
            }
            else if (part < 10){
                out += "." + part + "00";
            }
            else if (part < 100){
                out += "." + part + "0";
            }
            else {
                out += "." + part;
            }
        }
        else {
            out = (int)in + "";
        }
        return out;
    }

    @Override
    public RoverAutonomousCode clone() {
        return new GORADROGuided(this);
    }
}
