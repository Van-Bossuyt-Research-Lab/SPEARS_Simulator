package com.csm.rover.simulator.platforms.rover.autoCode;

import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;
import com.csm.rover.simulator.wrapper.Globals;

import java.util.ArrayList;
import java.util.Map;

@AutonomousCodeModel(name="GORADRO", type="Rover", parameters={ "sampleDirections", "sampleRadius", "averagingRadius", "averagingAngle", "averagingPStep","averagingRStep", "mentality1", "mentality2", "mentality3", "mentality4", "mentality5" })
public class GORAROcode1 extends RoverAutonomousCode {
	
	private static final long serialVersionUID = -7214933982888683962L;
	
	private static final double ANGLE_ERROR = Math.PI/16.0;
	private static final double RECALC_TIME = 2000; //ms
	
	private int score = 0;
	private int state = 1;
	
	private int histories = 3;
	private int sampleDirections = 16;
	private double sampleRadius = 2;
	
	double[][] potentials;
	
	private double averagingRadius = 25;
	private double averagingAngle = Math.PI / 6.0; //rad
	private double averagingPStep = Math.PI / 45.0;
	private double averagingRStep = 0.1;
	
	private double targetDirection = 0;
	private long lastOptTime = 0;
	
	private DecimalPoint lastLoc = new DecimalPoint(0, 0);
	private long timeAtPoint = 0;
	private boolean begun = false;
	private static final double STALL_RADIUS = 1.5;
	private static final int STALL_TIME = 3000;
	private static final int RUN_TIME = 4000;
	
	private ArrayList<DecimalPoint> visitedScience = new ArrayList<>();
	
	private double[] mentality = new double[] { 10000, 3000, 1200, 500, 50 };

	public GORAROcode1(){
		super("GORARO Simple", "GORARO");
		potentials = new double[histories][sampleDirections];
	}

    @Override
    public void constructParameters(Map<String, Double> params) {
        this.sampleDirections = (int)params.get("sampleDirections").doubleValue();
        this.sampleRadius = params.get("sampleRadius");
        this.averagingRadius = params.get("averagingRadius");
        this.averagingAngle = params.get("averagingAngle");
        this.averagingPStep = params.get("averagingPStep");
        this.averagingRStep = params.get("averagingRStep");
        this.mentality = new double[] { params.get("mentality1"), params.get("mentality2"), params.get("mentality3"), params.get("mentality4"), params.get("mentality5") };
    }

    @Override
	public String doNextCommand(long milliTime, DecimalPoint location, double direction, Map<String, Double> params)
	{
		super.writeToLog(milliTime + "\t" + location.getX() + "\t" + location.getY() + "\t" + environment.getHeightAt(location) + "\t" + score + "\t" + state);
		direction = (direction + 2*Math.PI) % (2*Math.PI);
		if (hasUnvisitedScience(location)){
			score += environment.<Integer>getPopulatorValue("Targets", location)/10;
			visitedScience.add(location.clone());
			for (int x = 0; x < histories; x++){
				for (int y = 0; y < sampleDirections; y++){
					potentials[x][y] = 0;
				}
			}
		}
		int[] sciences = new int[sampleDirections];
		int[] hazards = new int[sampleDirections];
		switch (state){
		case 1:
			for (int i = 1; i < histories; i++){
				for (int j = 0; j < sampleDirections; j++){
					potentials[i-1][j] = potentials[i][j];
				}
			}
			if (Math.sqrt(Math.pow(location.getX()-lastLoc.getX(), 2) + Math.pow(location.getY()-lastLoc.getY(), 2)) < STALL_RADIUS){
				if (milliTime-timeAtPoint > STALL_TIME){
					if (begun){
						state = 4;
						lastOptTime = milliTime;
						return "move";
					}
					else {
						timeAtPoint = milliTime;
						begun = true;
					}
				}
			}
			else {
				lastLoc = location.clone();
				timeAtPoint = milliTime;
			}
			double maxPotential = Double.MIN_VALUE;
			double maxDirection = 0;
			for (int i = 0; i < sampleDirections; i++){
				double theta = 2*Math.PI*i/(double)sampleDirections;
				double deltaX = sampleRadius * Math.cos(theta);
				double deltaY = sampleRadius * Math.sin(theta);
				DecimalPoint examine = location.offset(deltaX, deltaY);
				
				//if there is a scientific value at the point raise priority
				int science = 0;
				for (int radius = 0; radius < sampleRadius; radius++){
					if (hasUnvisitedScience(location.offset(radius*Math.cos(theta), radius*Math.sin(theta)))){
						science = 1;
						break;
					}
				}
				
				//if there is a hazard at the point get less excited
				int hazard = 0;
				if (environment.<Integer>getPopulatorValue("Hazards", examine) > 0){
					hazard = 1;
				}
				
				//Calculate the density of science targets in a wedge away from the rover 
				double scienceArea = 0;
				for (double radius = sampleRadius; radius <= averagingRadius; radius += averagingRStep){
					for (double phi = -averagingAngle/2.0; phi <= averagingAngle/2.0; phi += averagingPStep){
						if (hasUnvisitedScience(location.offset(radius*Math.cos(theta+phi), radius*Math.sin(theta+phi)))){
							scienceArea += sampleRadius/radius;
							sciences[i] += 1;
						}
					}
				}
				scienceArea /= averagingRadius*averagingAngle;
				
				//Calculate the density of hazards in a wedge away from the rover 
				double hazardArea = 0;
				for (double radius = sampleRadius; radius <= averagingRadius; radius += averagingRStep){
					for (double phi = -averagingAngle/2.0; phi <= averagingAngle/2.0; phi += averagingPStep){
						if (environment.<Integer>getPopulatorValue("Hazard", location.offset(radius * Math.cos(theta + phi), radius * Math.sin(theta + phi))) > 0){
							hazardArea += sampleRadius/radius;
							hazards[i] += 1;
						}
					}
				}
				hazardArea /= averagingRadius*averagingAngle;
				
				//work required to move the same translational distance increases proportional to the tangent of the slope
				double energyCost = Math.tan((environment.getHeightAt(examine)- environment.getHeightAt(location)) / sampleRadius);
				
				//calculate the potential of the point
				potentials[histories-1][i] = mentality[0]*science - mentality[1]*hazard + mentality[2]*scienceArea - mentality[3]*hazardArea - mentality[4]*energyCost;
				double average = 0;
				for (int j = 0; j < histories; j++){
					average += potentials[j][i];
				}
				average /= (double)histories;
				if (average > maxPotential){
					maxPotential = average;
					maxDirection = theta;
				}
			}
			lastOptTime = milliTime;
		
			targetDirection = maxDirection;
			state++;
			if (Globals.getInstance().subtractAngles(direction, targetDirection) < 0){
				return "spin_cw";
			}
			else {
				return "spin_ccw";
			}
		case 2:
			if (Math.abs(direction-targetDirection) < ANGLE_ERROR){
				state++;
				return "move"; 
			}
			else {
				if (Globals.getInstance().subtractAngles(direction, targetDirection) < 0){
					return "spin_cw";
				}
				else {
					return "spin_ccw";
				}
			}
		case 3:
			if (milliTime-lastOptTime > RECALC_TIME){
				state = 1;
			}
			return "";
		case 4:
			if (milliTime-lastOptTime > RUN_TIME){
				lastOptTime = (long)(milliTime - RECALC_TIME);
				state = 1;
			}
			return "";
		default:
			state = 1;
			return "";
		}
	}

	private boolean hasUnvisitedScience(DecimalPoint loc){
		if (environment.<Integer>getPopulatorValue("Targets", loc) > 0){
			for (DecimalPoint past : visitedScience){
				if (Math.sqrt(Math.pow(loc.getX()-past.getX(), 2) + Math.pow(loc.getY()-past.getY(), 2)) < 3){
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
