package rover.autoCode;

import objects.DecimalPoint;
import rover.RoverAutonomusCode;
import wrapper.Access;

public class PIDAAcontrol extends RoverAutonomusCode {

	public PIDAAcontrol(PIDAAcontrol in) {
		super(in);
		this.phiBound = in.phiBound;
		this.tphi = in.tphi;
		LastPos = in.LastPos;
		Travelled = in.Travelled;
		this.rnTime = in.rnTime;
		this.rnTimeb = in.rnTimeb;
		this.checkTime = in.checkTime;
		this.state = in.state;
		this.TrgtN = in.TrgtN;
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
		DecimalPoint LastPos;
		
		double Travelled;
		long rnTime=0;
		long rnTimeb;
		long checkTime=0;
		
		//State set
		int state=0;
		int substate3=0;
		
		//Target location 
		DecimalPoint Trgt=new DecimalPoint(0,200);
		int TrgtN=1;
		
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
		//Hazard Rate stuff
		double hzrate;
		double hzatt=0.25; //This is the acceptable hazard rate level before intervention 
		

		private long lastActionTime = 0;
		private int action = 0;
		private int seconds = 1;
		
		public PIDAAcontrol(){
			super("PIDAA Control", "PIDAA Control");

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
		hzrate=HZRT(location);
		elevation=Access.getMapHeightatPoint(location);
		//Battery hazard rate
		writeToLog(milliTime + "\t" + formatDouble(location.getX()) + "\t" + formatDouble(location.getY()) + "\t" + formatDouble(Access.getMapHeightatPoint(location)) + 
				"\t" + direction + "\t" + state + "\t" + TrgtN + "\t" + Access.getHazardValue(location) +"\t"+HZRT(location));
//State 0 set heading
		if(state==0){
			//Set direction 
			tphi=hddg(location,Trgt);
			if (direction>=(tphi+phiBound)){
				return "spin_cw";
			} else if (direction<=(tphi-phiBound)){
				return "spin_ccw";
			} else {
				state=1;
				rnTimeb=milliTime;
			}
		}
//State 1 drive until reach goal or hazard
		else if(state==1){
			//Check hazard rate vs allowable hazard rate
			
			//Hazard Mitigation
			//if(hzatt<hzrate){
			//	//take mitigating action if hazard rate is too low
			//	state=3;
			//} else{
			
				//check if you have reached target 
				if(pythagorean(Trgt,location)<=5){
					//Move to next target point
					state=2;
				} else{
					//Check how much time has passed and decide if you should keep driving or try to adjust path
					if ((milliTime-rnTimeb)>=2500){
						state=0;
					}else {
						return "move";
			//		}
				}
			}
		}
//State 2 switches to new target points
		else if(state==2){
			//Step forward target
			if(TrgtN==4){
				TrgtN=1;
			
			} else{
				TrgtN=TrgtN+1;
			}
			//Select new point
			Trgt=AimTrgt(TrgtN);
			//Return to state 0
			state=0;
		}
//State 3 Damage Mitigation
		else if (state==3){
			state=0;
		}
			return "delay100";
		}

//Sub methods
//Select Proper target point
	private DecimalPoint AimTrgt(int N){
		DecimalPoint newTrgt = null;
		if(N==1){
			newTrgt=new DecimalPoint(0,200);
		} else if (N==2){
			newTrgt=new DecimalPoint(200,200);
		} else if (N==3){
			newTrgt=new DecimalPoint(200,0);
		} else if (N==4){
			newTrgt=new DecimalPoint(0,0);
		}
		
		return newTrgt;
	}
//Determine proximity to target point
	private double pythagorean(DecimalPoint A,DecimalPoint B){
		double C;
		
		double delX=A.getX()-B.getX();
		double delY=A.getY()-B.getY();
		
		C=Math.sqrt(Math.pow(delX, 2)+Math.pow(delY,2));
		
		return C;
	}
//Determine Direction to travel
	private double hddg(DecimalPoint loc,DecimalPoint trgt){
		double theta;
		//Determine delta X, Y
		double dlX=trgt.getX()-loc.getX();
		double dlY=trgt.getY()-loc.getY();
		
		//Determine target angle
		if (dlY>0){
			theta=Math.acos(dlX/pythagorean(loc,trgt));
		} else {
			theta=2*Math.PI-Math.acos(dlX/pythagorean(loc,trgt));
		}
		
		//Report angle
		return theta;
	}
//Method for calculating hazard rate instantaneously 
	private double HZRT(DecimalPoint loc){
		
		double hzrd;
		double hzrate;
		
		hzrd=Access.getHazardValue(loc);
		
		hzrate=-0.00009*Math.pow(hzrd,5)+0.0036*Math.pow(hzrd,4)-0.05*Math.pow(hzrd,3)+0.2937*Math.pow(hzrd,2)-0.5274*hzrd+0.3;
				
		return hzrate;
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
	public PIDAAcontrol clone(){
		return new PIDAAcontrol(this);
	}
}