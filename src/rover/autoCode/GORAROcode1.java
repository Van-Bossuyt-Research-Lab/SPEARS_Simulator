package rover.autoCode;

import objects.DecimalPoint;
import rover.RoverAutonomusCode;
import wrapper.Access;

public class GORAROcode1 extends RoverAutonomusCode {
	
	private static final double ANGLE_ERROR = Math.PI/16.0;
	
	private int score = 0;
	private int state = 1;
	
	private int sampleDirections = 8;
	private double sampleRadius = 2;
	private double averagingRadius = 15;
	private double averagingAngle = 30; //deg
	
	private double targetDirection = 0;
	private long lastOptTime = 0;
	
	private double[] mentality = new double[] { 5000, 3000, 1, 2, 500 };

	public GORAROcode1(){
		super("GORARO Simple", "GORARO");
	}	
	
	public GORAROcode1(int sampleDirections, double sampleRadius, double averagingRadius,
			double averagingAngle, double mentality1, double mentality2, double mentality3, 
			double mentality4, double mentality5) {
		super("GORARO Simple", "GORARO");
		this.sampleDirections = sampleDirections;
		this.sampleRadius = sampleRadius;
		this.averagingRadius = averagingRadius;
		this.averagingAngle = averagingAngle;
		this.mentality = new double[] { mentality1, mentality2, mentality3, mentality4, mentality5 };
	}

	public GORAROcode1(GORAROcode1 org){
		super(org);
		score = org.score;
		state = org.state;
		this.sampleDirections = org.sampleDirections;
		this.sampleRadius = org.sampleRadius;
		this.averagingRadius = org.averagingRadius;
		this.averagingAngle = org.averagingAngle;
		this.mentality = org.mentality;
		this.targetDirection = org.targetDirection;
		this.lastOptTime = org.lastOptTime;
	}	

	@Override
	public String nextCommand(long milliTime, DecimalPoint location,
			double direction, double acceleration, double angular_acceleration,
			double wheel_speed_FL, double wheel_speed_FR,
			double wheel_speed_BL, double wheel_speed_BR,
			double motor_current_FL, double motor_current_FR,
			double motor_current_BL, double motor_current_BR,
			double motor_temp_FL, double motor_temp_FR, double motor_temp_BL,
			double motor_temp_BR, double battery_voltage,
			double battery_current, double battery_temp, double battery_charge) 
	{
		if (Access.isAtTarget(location)){
			score += Access.getTargetValue(location);
		}
		switch (state){
		case 1:
			double[] potentials = new double[sampleDirections];
			double maxPotential = Double.MIN_VALUE;
			double maxDirection = 0;
			for (int i = 0; i < sampleDirections; i++){
				double theta = 2*Math.PI*i/(double)sampleDirections;
				double deltaX = sampleRadius * Math.cos(theta);
				double deltaY = sampleRadius * Math.sin(theta);
				DecimalPoint examine = location.offset(deltaX, deltaY);
				
				//if there is a scientific value at the point raise priority
				int science = 0;
				if (Access.isAtTarget(examine)){
					science = 1;
				}
				
				//if there is a hazard at the point get less excited
				int hazard = 0;
				if (Access.isInHazard(examine)){
					hazard = 1;
				}
				
				double scienceArea = 0;
				//TODO average over area
				
				double hazardArea = 0;
				//TODO average over area
				
				//work required to move the same translational distance increases proportional to the tangent of the slope
				double energyCost = Math.tan((Access.getMapHeightatPoint(examine)-Access.getMapHeightatPoint(location)) / sampleRadius);
				
				//calculate the potential of the point
				potentials[i] = mentality[0]*science - mentality[1]*hazard + mentality[2]*scienceArea - mentality[3]*hazardArea - mentality[4]*energyCost;
				if (potentials[i] > maxPotential){
					maxPotential = potentials[i];
					maxDirection = theta;
				}
			}
			lastOptTime = milliTime;
			targetDirection = maxDirection;
			state++;
			return "spin_ccw";
		case 2:
			System.out.println(direction + " - " + targetDirection);
			if (Math.abs(direction-targetDirection) < ANGLE_ERROR){
				state++;
				return "move"; 
			}
			else {
				return "";
			}
		default:
			if (milliTime-lastOptTime > 2000){
				state = 1;
			}
			return "";
		}
	}

	@Override
	public RoverAutonomusCode clone() {
		return new GORAROcode1(this);
	}

}
