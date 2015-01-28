package rover.autoCode;

import objects.DecimalPoint;
import rover.RoverAutonomusCode;
import wrapper.Access;

public class PIDAAcode2 extends RoverAutonomusCode {

	public PIDAAcode2(PIDAAcode2 in) {
		super(in);
		this.phiBound = in.phiBound;
		this.tphi = in.tphi;
		this.path_dist = in.path_dist;
		this.path_angd = in.path_angd;
		D1 = in.D1;
		D2 = in.D2;
		D3 = in.D3;
		D4 = in.D4;
		D5 = in.D5;
		D6 = in.D6;
		D7 = in.D7;
		D8 = in.D8;
		D9=in.D9;
		D10=in.D10;
		D11=in.D11;
		D12=in.D12;
		D13=in.D13;
		D14=in.D14;
		D15=in.D15;
		D16=in.D16;
		S1 = in.S1;
		S2 = in.S2;
		S3 = in.S3;
		S4 = in.S4;
		S5 = in.S5;
		S6 = in.S6;
		S7 = in.S7;
		S8 = in.S8;
		S9=in.S9;
		S10=in.S10;
		S11=in.S11;
		S12=in.S12;
		S13=in.S13;
		S14=in.S14;
		S15=in.S15;
		S16=in.S16;
		LastPos = in.LastPos;
		Travelled = in.Travelled;
		this.rnTime = in.rnTime;
		this.rnTimeb = in.rnTimeb;
		this.checkTime = in.checkTime;
		this.checkPoint = in.checkPoint;
		this.state = in.state;
		this.substate3 = in.substate3;
		Trgt = in.Trgt;
		this.lastActionTime = in.lastActionTime;
		this.action = in.action;
		this.seconds = seconds;
		hazard=in.hazard;
		elevation=in.elevation;
		HBatt=in.HBatt;
		HwFL=in.HwFL;
		HwFR=in.HwFR;
		HwBL=in.HwBL;
		HwBR=in.HwBR;
		Hhzrd=in.Hhzrd;
		Hinc=in.Hinc;
		HRate=in.HRate;
		FRate=in.FRate;
		MitAct=in.MitAct;
		dtime=in.dtime;
		
	}

		//Declare any variables you want here
		//Angle bounds
		double phiBound=Math.PI/16;
		double tphi;
	
		//The distance that the rover looks to travel in a single direction before turning in meters
		double path_dist=1;
		double path_angd=Math.sqrt(2)/2;
		DecimalPoint D1=new DecimalPoint(1,0);
		DecimalPoint D2=new DecimalPoint(Math.cos(Math.PI/8),Math.sin(Math.PI/8));
		DecimalPoint D3=new DecimalPoint(Math.cos(2*Math.PI/8),Math.sin(2*Math.PI/8));
		DecimalPoint D4=new DecimalPoint(Math.cos(3*Math.PI/8),Math.sin(3*Math.PI/8));
		DecimalPoint D5=new DecimalPoint(0,1);
		DecimalPoint D6=new DecimalPoint(-1*Math.cos(3*Math.PI/8),Math.sin(3*Math.PI/8));
		DecimalPoint D7=new DecimalPoint(-1*Math.cos(2*Math.PI/8),Math.sin(2*Math.PI/8));
		DecimalPoint D8=new DecimalPoint(-1*Math.cos(Math.PI/8),Math.sin(Math.PI/8));
		DecimalPoint D9=new DecimalPoint(-1,0);
		DecimalPoint D10=new DecimalPoint(-1*Math.cos(Math.PI/8),-1*Math.sin(Math.PI/8));
		DecimalPoint D11=new DecimalPoint(-1*Math.cos(2*Math.PI/8),-1*Math.sin(2*Math.PI/8));
		DecimalPoint D12=new DecimalPoint(-1*Math.cos(3*Math.PI/8),-1*Math.sin(3*Math.PI/8));
		DecimalPoint D13=new DecimalPoint(0,-1);
		DecimalPoint D14=new DecimalPoint(Math.cos(3*Math.PI/8),-1*Math.sin(3*Math.PI/8));
		DecimalPoint D15=new DecimalPoint(Math.cos(2*Math.PI/8),-1*Math.sin(2*Math.PI/8));
		DecimalPoint D16=new DecimalPoint(Math.cos(Math.PI/8),-1*Math.sin(Math.PI/8));
		double S1;
		double S2;
		double S3;
		double S4;
		double S5;
		double S6;
		double S7;
		double S8;
		double S9;
		double S10;
		double S11;
		double S12;
		double S13;
		double S14;
		double S15;
		double S16;
		DecimalPoint LastPos;
		
		double Travelled;
		long rnTime=0;
		long rnTimeb;
		long checkTime=0;
		DecimalPoint checkPoint=new DecimalPoint(-170,-170);
		
		//State set
		int state=0;
		int substate3=0;
		
		//Target location 
		DecimalPoint Trgt=new DecimalPoint(-100,-100);
		
		//PIDAA Specific
		boolean hazard;
		double elevation;
		double HBatt;
		double HwFL;
		double HwFR;
		double HwBL;
		double HwBR;
		double Hhzrd;
		double Hinc;
		double HRate;
		double FRate;
		int MitAct=0;
		int dtime;
		

		private long lastActionTime = 0;
		private int action = 0;
		private int seconds = 1;
		
		public PIDAAcode2(){
			super("PIDAA", "PIDAA");

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
			double battery_temp,
			double battery_charge
	) {

		//Write processing code here
		//log
		
//This is where you should calculate the status of the system
		hazard=Access.isInHazard(location);
		elevation=Access.getMapHeightatPoint(location);
		//Battery hazard rate
		HBatt=HazR(battery_temp,-30,5);
		//Wheels hazard rates
		HwFL=.12*HazR(motor_temp_FL,15,33);
		HwFR=.12*HazR(motor_temp_FR,15,33);
		HwBL=.12*HazR(motor_temp_BL,15,33);
		HwBR=.12*HazR(motor_temp_BR,15,33);
		//Hazard hazard rate
		Hhzrd=0;
		if (hazard){
			Hhzrd=.95;
		}
		//Incline hazard rate
		Hinc=HazR(Access.getMapInclineAtPoint(location, direction),0,5.1);
		//Total Hazard rate
		HRate=sumP(HBatt,HwFL,HwFR,HwBL,HwBR,Hhzrd,Hinc);
		//Total Failure Rate
		FRate=FailR(HRate,1);
		writeToLog("RA" +","+state+","+MitAct+","+ milliTime +","+ location+","+ elevation+","+FRate+","+HRate+","+Hinc+","+Hhzrd+","+
				motor_temp_FL+","+HwFL+","+motor_temp_FR+","+HwFR+","+motor_temp_BL+","+HwBL+","+motor_temp_BR+","+HwBR+","+battery_temp+","+HBatt);
				
//This is where you should set the Acceptable level of risk
//This is where you should determine if we have exceeded acceptable levels of risk
		if (FRate>=0.6){
			//determine what contributes most to the failure
			state=3;
			MitAct=MitChoice(Hinc,Hhzrd,HwFL,HwFR,HwBL,HwBR,HBatt);
		}
			
	//Goal Reached State 5 Break
			if (pythagorean(location,Trgt)<=1.5){
				state=5;
			}
		
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
			
			//P9
			DecimalPoint P9;
			P9=location.offset(D9);
			S9=RouteChoice(P9,loc);
			
			//P10
			DecimalPoint P10;
			P10=location.offset(D10);
			S10=RouteChoice(P10,loc);
			
			//P11
			DecimalPoint P11;
			P11=location.offset(D11);
			S11=RouteChoice(P11,loc);
			
			//P12
			DecimalPoint P12;
			P12=location.offset(D12);
			S12=RouteChoice(P12,loc);
			
			//P13
			DecimalPoint P13;
			P13=location.offset(D13);
			S13=RouteChoice(P13,loc);
			
			//P14
			DecimalPoint P14;
			P14=location.offset(D14);
			S14=RouteChoice(P14,loc);
			
			//P15
			DecimalPoint P15;
			P15=location.offset(D15);
			S15=RouteChoice(P15,loc);
			
			//P16
			DecimalPoint P16;
			P16=location.offset(D16);
			S16=RouteChoice(P16,loc);

		//set appropriate next state
		state=1;
		tphi=minscore(S1,S2,S3,S4,S5,S6,S7,S8,S9,S10,S11,S12,S13,S14,S15,S16);
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
			
			if ((milliTime-rnTimeb)<=2500){
			return "move";
			} else {
				state=0;
				return "stop";
			}
//State 3 is the Mitigation State
		} else if (state==3){
			state=4;
			//determine which system is causing the largest problem
			if (MitAct==1){
				//Incline is to large 
				if (Access.getMapInclineAtPoint(location, direction)>=0){
					dtime=5;
					return "backward";
				} else {
					dtime=5;
					return "backward";
				}
			}else if (MitAct==2){
				//Ground Hazard
				dtime=10;
				return "backward";
			}else if (MitAct==3){
				//determine if hot or cold
				if (motor_temp_FL>=15){
					dtime=10;
					return "stop";
				}else {
					dtime=2;
					return "spin_ccw";
				}
			}else if (MitAct==4){
				//determine if hot or cold
				if (motor_temp_FR>=15){
					dtime=10;
					return "stop";
				}else {
					dtime=2;
					return "spin_cw";
				}
			}else if (MitAct==5){
				//determine if hot or cold
				if (motor_temp_BL>=15){
					dtime=10;
					MitAct=0;
					return "stop";
				}else {
					dtime=2;
					return "spin_ccw";
				}
			}else if (MitAct==6){
				//determine if hot or cold
				if (motor_temp_BR>=15){
					dtime=10;
					return "stop";
				}else {
					dtime=2;
					return "spin_cw";
				}
			}else if (MitAct==7){
				//determine if hot or cold
				if (battery_temp>=5){
					dtime=10;
					return "stop";
				}else {
					dtime=2;
					return "spin_ccw";
				}
			}
				
//State 4 Waiting			
		} else if (state==4){
			state=0;
			if (dtime==1){
				return "delay1000";
			}else if (dtime==2){
				return "delay2000";
			}else if (dtime==5){
				return "delay5000";
			}else if (dtime==10){
				return "delay10000";
			}
			
//State 5 Goal Reached
		}else if (state==5){	
			checkTime=milliTime;
			return "stop";
		}
}

		return "";
		//end line of code
	}
		

//Method for selection of minimum option
	private double minscore(double o1,double o2,double o3,double o4,double o5,double o6,double o7,double o8,double o9,double o10,double o11,double o12,double o13, double o14, double o15,double o16) {
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
			Oang=Math.PI/8;
		}
		if (tested<=o3){
		}else{
			Option=3;
			tested=o3;
			Oang=2*Math.PI/8;
		}
		if (tested<=o4){
		}else{
			Option=4;
			tested=o4;
			Oang=3*Math.PI/8;
		}
		if (tested<=o5){
		}else{
			Option=5;
			tested=o5;
			Oang=4*Math.PI/8;
		}
		if (tested<=o6){
		}else{
			Option=6;
			tested=o6;
			Oang=5*Math.PI/8;
		}
		if (tested<=o7){
		}else{
			Option=7;
			tested=o7;
			Oang=6*Math.PI/8;
		}
		if (tested<=o8){
		}else{
			Option=8;
			tested=o8;
			Oang=7*Math.PI/8;
		}
		if (tested<=o9){
		}else{
			Option=9;
			tested=o9;
			Oang=8*Math.PI/8;
		}
		if (tested<=o10){
		}else{
			Option=10;
			tested=o10;
			Oang=9*Math.PI/8;
		}
		if (tested<=o11){
		}else{
			Option=11;
			tested=o11;
			Oang=10*Math.PI/8;
		}
		if (tested<=o12){
		}else{
			Option=12;
			tested=o12;
			Oang=11*Math.PI/8;
		}
		if (tested<=o13){
		}else{
			Option=13;
			tested=o13;
			Oang=12*Math.PI/8;
		}
		if (tested<=o14){
		}else{
			Option=14;
			tested=o14;
			Oang=13*Math.PI/8;
		}
		if (tested<=o15){
		}else{
			Option=15;
			tested=o15;
			Oang=14*Math.PI/8;
		}
		if (tested<=o16){
		}else{
			Option=16;
			tested=o16;
			Oang=15*Math.PI/8;
		}
		return Oang;
	}
	
//Method for determining which mitigation actions should be taken
		private int MitChoice(double o1,double o2,double o3,double o4,double o5,double o6,double o7) {
			double tested;
			int Option;
			if (o1>=o2){
				Option=1;
				tested=o1;
			}else{
				Option=2;
				tested=o2;
			}
			if (tested>=o3){
			}else{
				Option=3;
				tested=o3;
			}
			if (tested>=o4){
			}else{
				Option=4;
				tested=o4;
			}
			if (tested>=o5){
			}else{
				Option=5;
				tested=o5;
			}
			if (tested>=o6){
			}else{
				Option=6;
				tested=o6;
			}
			if (tested>=o7){
			}else{
				Option=7;
				tested=o7;
			}
			return Option;
		}
	
//Method for finding linear distance between two points
	private double pythagorean(DecimalPoint A,DecimalPoint B){
		double C;
		
		double delX=A.getX()-B.getX();
		double delY=A.getY()-B.getY();
		
		C=Math.sqrt(Math.pow(delX, 2)+Math.pow(delY,2));
		
		return C;
	}
	
//Hazard Rate (lambda)
	private double HazR(double x,double mu,double sig){
		double hr;
		//Taken from the PDF of the normal distribution
			//sig is sigma is sqrt(varience)
			//x is value of interest
		double r1=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(1/10)-mu,2)/(2*Math.pow(sig, 2))));
		double r2=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(2/10)-mu,2)/(2*Math.pow(sig, 2))));
		double r3=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(3/10)-mu,2)/(2*Math.pow(sig, 2))));
		double r4=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(4/10)-mu,2)/(2*Math.pow(sig, 2))));
		double r5=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(5/10)-mu,2)/(2*Math.pow(sig, 2))));
		double r6=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(6/10)-mu,2)/(2*Math.pow(sig, 2))));
		double r7=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(7/10)-mu,2)/(2*Math.pow(sig, 2))));
		double r8=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(8/10)-mu,2)/(2*Math.pow(sig, 2))));
		double r9=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(9/10)-mu,2)/(2*Math.pow(sig, 2))));
		double r10=Math.abs((x/10)*(1/(sig*Math.sqrt(2*Math.PI)))*Math.exp(-1*Math.pow(x*(10/10)-mu,2)/(2*Math.pow(sig, 2))));
		hr=2*(r1+r2+r3+r4+r5+r6+r7+r8+r9+r10);
		
		return hr;
	}
	
//Failure rate
	private double FailR(double lmb,double t){
		double fr;
		//This is the exponential failure distribution
		fr=1-Math.exp(-1*lmb*t);
		return fr;
	}
	
//Sum Probabilities 
	private double sumP(double p1,double p2, double p3, double p4, double p5, double p6,double p7){
		double pt;
		
		double A=p1+p2-p1*p2;
		double B=A+p3-A*p3;
		double C=B+p4-B*p4;
		double D=C+p5-C*p5;
		double E=D+p6-D*p6;
		pt=E+p7-E*p7;
		
		return pt;
	}
//Method for determining Point Optimization Score for navigation
	private double RouteChoice(DecimalPoint pnt,DecimalPoint loc){
		double score;

		double delta_X=Trgt.getX()-loc.getX();
		double delta_Y=Trgt.getY()-loc.getY();
		double delta_L=Math.sqrt(Math.pow(delta_X, 2)+Math.pow(delta_Y, 2));
		
		double Pd_X=Trgt.getX()-pnt.getX();
		double Pd_Y=Trgt.getY()-pnt.getY();
		double Pd_L=Math.sqrt(Math.pow(Pd_X, 2)+Math.pow(Pd_Y, 2));		
				
		//score point for optimality
		score=Math.abs((delta_L-Pd_L-1)/((delta_L-Pd_L+3)/2));
		return score;
	}
		
//Method for determining Point Optimization Score
	private double RouteMit(DecimalPoint pnt,DecimalPoint loc){
		double score;
		double hzrds;
		double slopehzrd;
		double intslopehzrd;
		double delta_X=Trgt.getX()-loc.getX();
		double delta_Y=Trgt.getY()-loc.getY();
		double delta_L=Math.sqrt(Math.pow(delta_X, 2)+Math.pow(delta_Y, 2));
		double delta_Z=Access.getMapHeightatPoint(Trgt)-Access.getMapHeightatPoint(loc);
				
		//target climb angle
		double theta=Math.atan(delta_Z/delta_L);
		
		double Pd_X=Trgt.getX()-pnt.getX();
		double Pd_Y=Trgt.getY()-pnt.getY();
		double Pt_X=pnt.getX()-loc.getX();
		double Pt_Y=pnt.getY()-loc.getY();
		double Pd_L=Math.sqrt(Math.pow(Pd_X, 2)+Math.pow(Pd_Y, 2));
		double Pt_L=Math.sqrt(Math.pow(Pt_X, 2)+Math.pow(Pt_Y, 2));
		double Pt_Z=Access.getMapHeightatPoint(pnt)-Access.getMapHeightatPoint(loc);
		double thetaP=Math.atan(Pt_Z/Pt_L);
			
		//determine if ground cover is safe
		boolean hzrd=Access.isInHazard(pnt);
		if (hzrd){
			hzrds=10;
		} else{
			hzrds=0;
		}
		
		//determine if incline is too great
		if (thetaP>=0.15){
			slopehzrd=100;
		} else{
			slopehzrd=0;
		}
			
		//intermediate point
			//average of pnt and loc
		DecimalPoint intpnt;
		intpnt=loc.offset(Pt_X/10,Pt_Y/10);
		double intPt_L=Math.sqrt(Math.pow(Pt_X/10, 2)+Math.pow(Pt_Y/10, 2));
		double intPt_Z=Access.getMapHeightatPoint(intpnt)-Access.getMapHeightatPoint(loc);
		double intTheta=Math.atan(intPt_Z/intPt_L);
		if (intTheta>=0.15){
			intslopehzrd=100;
		} else{
			intslopehzrd=0;
		}
		
		//score point for optimality
		score=Math.abs((theta-thetaP)/((theta+thetaP)/2))+5*Math.abs((delta_L-Pd_L-1)/((delta_L-Pd_L+3)/2))+hzrds+slopehzrd+intslopehzrd;
		return score;
	}
		public PIDAAcode2 clone(){
			return new PIDAAcode2(this);
		}

}