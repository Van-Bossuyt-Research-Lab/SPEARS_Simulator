package rover.autoCode;

import objects.DecimalPoint;
import rover.RoverAutonomusCode;
import wrapper.Access;

public class RAIRcode extends RoverAutonomusCode {

	//Declare any variables you want here
		//Angle bounds
		double phiBound=Math.PI/16;
		double tphi;
	
		//The distance that the rover looks to travel in a single direction before turning in meters
		double path_dist=1;
		double path_angd=Math.sqrt(2)/2;
		DecimalPoint D1=new DecimalPoint(1,0);
		DecimalPoint D2=new DecimalPoint(path_angd,path_angd);
		DecimalPoint D3=new DecimalPoint(0,1);
		DecimalPoint D4=new DecimalPoint(-1*path_angd,path_angd);
		DecimalPoint D5=new DecimalPoint(-1,0);
		DecimalPoint D6=new DecimalPoint(-1*path_angd,-1*path_angd);
		DecimalPoint D7=new DecimalPoint(0,-1);
		DecimalPoint D8=new DecimalPoint(path_angd,-1*path_angd);
		double S1;
		double S2;
		double S3;
		double S4;
		double S5;
		double S6;
		double S7;
		double S8;
		DecimalPoint LastPos;
		
		double Travelled;
		long rnTime=0;
		long rnTimeb;
		
		//State set
		int state=0;
		
		//Target location 
		DecimalPoint Trgt=new DecimalPoint(-170,-175);

		private long lastActionTime = 0;
		private int action = 0;
		private int seconds = 1;
		
		public RAIRcode(){
			super("RAIR", "RAIR");

		}
	
//	@Override
	public String nextCommand(
			long milliTime,
			DecimalPoint location,
			double direction,
			double acceleration,
			double angular_acceleration,
			double wheel_speed_FL,
			double wheel_speed_FR,
			double wheel_speed_BL,
			double wheel_speed_BR,
			double motor_current_FL,
			double motor_current_FR,
			double motor_current_BL,
			double motor_current_BR,
			double motor_temp_FL,
			double motor_temp_FR,
			double motor_temp_BL,
			double motor_temp_BR,
			double battery_voltage,
			double battery_current,
			double battery_temp
	) {

		//Write processing code here
				
			//determine variables: arc angle range theta, number of angle increments n, distance checked d, 
				
			//Begin loop
				
			//determine delta x,y,z

		//double check this is the right way to call these out with Zach
		double delta_X=Trgt.getX()-location.getX();
		double delta_Y=Trgt.getY()-location.getY();
		double delta_L=Math.sqrt(Math.pow(delta_X, 2)+Math.pow(delta_Y, 2));
		double delta_Z=Access.getMapHeightatPoint(Trgt)-Access.getMapHeightatPoint(location);
		
			//determine angle between A and B 
			//	arc tangent of opposite/adjacent set as phi
		double phi=Math.atan(delta_X/delta_Y);
			//optimal climb angle
		double theta=Math.atan(delta_Z/delta_L);

if (milliTime-rnTime>=500){
//State 0 determines is the route selection state
		if (state==0) {
		//Look at 8 near points
		//P1
		DecimalPoint loc=location;
		DecimalPoint P1;
		P1=location.offset(D1);
		S1=RouteChoice(P1,loc);
		
		//P2
		DecimalPoint P2;
		P2=location.offset(D2);
		S2=RouteChoice(P2,loc);
		
		//P3
		DecimalPoint P3;
		P3=location.offset(D3);
		S3=RouteChoice(P3,loc);
		
		//P4
		DecimalPoint P4;
		P4=location.offset(D4);
		S4=RouteChoice(P4,loc);
		
		//P5
		DecimalPoint P5;
		P5=location.offset(D5);
		S5=RouteChoice(P5,loc);
		
		//P6
		DecimalPoint P6;
		P6=location.offset(D6);
		S6=RouteChoice(P6,loc);
		
		//P7
		DecimalPoint P7;
		P7=location.offset(D7);
		S7=RouteChoice(P7,loc);
		
		//P8
		DecimalPoint P8;
		P8=location.offset(D8);
		S8=RouteChoice(P8,loc);

		//set appropriate next state
		state=1;
		tphi=minscore(S1,S2,S3,S4,S5,S6,S7,S8);
//State 1 is the rover turning to line up with the desired angle 
		} else if (state==1) {
			//Log current position 
			//DecimalPoint LastPos=location;
			
			//Determine if current angle is outside of desired range 
			if (direction>=(tphi+phiBound)){
				return "spin_cw";
			} else if (direction<=(tphi-phiBound)){
				return "spin_ccw";
			} else {
				state=2;
				rnTimeb=milliTime;
			}
//State 2 is the driving state 
		} else if (state==2){

			//double tX=LastPos.getX()-location.getX();
			//double tY=LastPos.getY()-location.getY();
			//double traveled=Math.sqrt(Math.pow(tX, 2)+Math.pow(tY, 2));
			
			if ((milliTime-rnTimeb)<=10000){
			return "move";
			} else {
				state=0;
				return "stop";
			}
			
		}
				
}
		return "delay100";		
	}
		

	// writeToLog(String message);


//Method for selection of minimum option
	private double minscore(double o1,double o2,double o3,double o4,double o5,double o6,double o7,double o8) {
		double tested;
		int Option;
		double Oang;
		if (o1<=o2){
			Option=1;
			tested=o1;
			Oang=0;
		}else{
			Option=2;
			tested=o2;
			Oang=Math.PI/4;
		}
		if (tested<=o3){
		}else{
			Option=3;
			tested=o3;
			Oang=2*Math.PI/4;
		}
		if (tested<=o4){
		}else{
			Option=4;
			tested=o4;
			Oang=3*Math.PI/4;
		}
		if (tested<=o5){
		}else{
			Option=5;
			tested=o5;
			Oang=4*Math.PI/4;
		}
		if (tested<=o6){
		}else{
			Option=6;
			tested=o6;
			Oang=5*Math.PI/4;
		}
		if (tested<=o7){
		}else{
			Option=7;
			tested=o7;
			Oang=6*Math.PI/4;
		}
		if (tested<=o8){
		}else{
			Option=8;
			tested=o8;
			Oang=7*Math.PI/4;
		}
		return Oang;
	}
//Method for determining Point Optimization Score
	private double RouteChoice(DecimalPoint pnt,DecimalPoint loc){
		double score;

		double delta_X=Trgt.getX()-loc.getX();
		double delta_Y=Trgt.getY()-loc.getY();
		double delta_L=Math.sqrt(Math.pow(delta_X, 2)+Math.pow(delta_Y, 2));
		double delta_Z=Access.getMapHeightatPoint(Trgt)-Access.getMapHeightatPoint(loc);
		
		//target drive direction
		double phi=Math.atan(delta_Y/delta_X);
		//target climb angle
		double theta=Math.atan(delta_Z/delta_L);
		
		double Pd_X=pnt.getX()-loc.getX();
		double Pd_Y=pnt.getY()-loc.getY();
		double Pd_L=Math.sqrt(Math.pow(Pd_X, 2)+Math.pow(Pd_Y, 2));
		double Pd_Z=Access.getMapHeightatPoint(Trgt)-Access.getMapHeightatPoint(pnt);
		double phiP=Math.atan(Pd_Y/Pd_X);
		double thetaP=Math.atan(Pd_Z/Pd_L);
		
		
		//score point for optimality
		score=Math.abs(phiP-phi);
		
		return score;
	}
}