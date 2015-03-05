package rover.autoCode;

import objects.DecimalPoint;
import rover.RoverAutonomusCode;
import wrapper.Access;
import wrapper.Globals;

public class GORAROcode1 extends RoverAutonomusCode {
	
	private static final double ANGLE_ERROR = Math.PI/16.0;
	private static final double RECALC_TIME = 2000; //ms
	
	private int score = 0;
	private int state = 1;
	
	private int sampleDirections = 8;
	private double sampleRadius = 2;
	
	private double averagingRadius = 25;
	private double averagingAngle = Math.PI / 6.0; //rad
	private double averagingPStep = Math.PI / 45.0;
	private double averagingRStep = 1;
	
	private double targetDirection = 0;
	private long lastOptTime = 0;
	
	private double[] mentality = new double[] { 5000, 3000, 900, 250, 500 };
	int[] science = new int[8];
	int[] hazards = new int[8];

	public GORAROcode1(){
		super("GORARO Simple", "GORARO");
	}	
	
	public GORAROcode1(int sampleDirections, double sampleRadius, double averagingRadius,
			double averagingAngle, double averagingPStep, double averagingRStep, double mentality1, double mentality2, double mentality3, 
			double mentality4, double mentality5) {
		super("GORARO Simple", "GORARO");
		this.sampleDirections = sampleDirections;
		this.sampleRadius = sampleRadius;
		this.averagingRadius = averagingRadius;
		this.averagingAngle = averagingAngle;
		this.averagingPStep = averagingPStep;
		this.averagingRStep = averagingRStep;
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
		direction = (direction + 2*Math.PI) % (2*Math.PI);
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
				
				//Calculate the density of science targets in a wedge away from the rover 
				double scienceArea = 0;
				for (double radius = 0; radius <= averagingRadius; radius += averagingRStep){
					for (double phi = -averagingAngle/2.0; phi <= averagingAngle/2.0; phi += averagingPStep){
						if (Access.isAtTarget(location.offset(radius*Math.cos(theta+phi), radius*Math.sin(theta+phi)))){
							scienceArea++;
							this.science[i] += 1;
							System.err.println("SCIENCE!!!");
						}
					}
				}
				scienceArea /= averagingRadius*averagingAngle;
				
				//Calculate the density of hazards in a wedge away from the rover 
				double hazardArea = 0;
				for (double radius = 0; radius <= averagingRadius; radius += averagingRStep){
					for (double phi = -averagingAngle/2.0; phi <= averagingAngle/2.0; phi += averagingPStep){
						if (Access.isInHazard(location.offset(radius*Math.cos(theta+phi), radius*Math.sin(theta+phi)))){
							hazardArea++;
							this.hazards[i]++;
						}
					}
				}
				hazardArea /= averagingRadius*averagingAngle;
				
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
			//if (true){
			//	for (double pot : potentials){
			//		System.out.print(pot + "\t");
			//	}
			//	System.out.println();
			//}
			
			System.out.println("\n\n\n\n\n\n");
			System.out.println("\t\t" + formatDouble(potentials[2])+"("+this.science[2]+", "+this.hazards[2]+")");
			System.out.println("\t" + formatDouble(potentials[3])+"("+this.science[3]+", "+this.hazards[3]+")" + "\t\t" + formatDouble(potentials[1])+"("+this.science[1]+", "+this.hazards[1]+")");
			System.out.println(formatDouble(potentials[4])+"("+this.science[4]+", "+this.hazards[4]+")" + "\t\t\t\t" + formatDouble(potentials[0])+"("+this.science[0]+", "+this.hazards[0]+")");
			System.out.println("\t" + formatDouble(potentials[5])+"("+this.science[5]+", "+this.hazards[5]+")" + "\t\t" + formatDouble(potentials[7])+"("+this.science[7]+", "+this.hazards[7]+")");
			System.out.println("\t\t" + formatDouble(potentials[6])+"("+this.science[6]+", "+this.hazards[6]+")");
			
			targetDirection = maxDirection;
			state++;
			if (Globals.subtractAngles(direction, targetDirection) < 0){
				return "spin_cw";
			}
			else {
				return "spin_ccw";
			}
		case 2:
			//System.out.println(direction + " - " + targetDirection);
			if (Math.abs(direction-targetDirection) < ANGLE_ERROR){
				state++;
				return "move"; 
			}
			else {
				if (Globals.subtractAngles(direction, targetDirection) < 0){
					return "spin_cw";
				}
				else {
					return "spin_ccw";
				}
			}
		default:
			if (milliTime-lastOptTime > RECALC_TIME){
				state = 1;
			}
			return "";
		}
	}

	@Override
	public RoverAutonomusCode clone() {
		return new GORAROcode1(this);
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

}
