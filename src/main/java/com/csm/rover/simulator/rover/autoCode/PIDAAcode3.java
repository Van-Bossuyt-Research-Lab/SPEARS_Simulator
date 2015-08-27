package com.csm.rover.simulator.rover.autoCode;

import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.wrapper.Access;

public class PIDAAcode3 extends RoverAutonomusCode {

	public PIDAAcode3(PIDAAcode3 in) {
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
		int substate3;
		
		//Target location 
		DecimalPoint Trgt=new DecimalPoint(0,200);
		int TrgtN=1;
		
		//PIDAA Specific
		boolean hazard;
		
		//Hazard Rate stuff
		double hzrate;
		double hzatt=0.25; //This is the acceptable hazard rate level before intervention 
		

		private long lastActionTime = 0;
		private int action = 0;
		private int seconds = 1;
		
		public PIDAAcode3(){
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
		hzrate=HZRT(location);
		//Battery hazard rate
		writeToLog(milliTime + "\t" + formatDouble(location.getX()) + "\t" + formatDouble(location.getY()) + "\t" + formatDouble(Access.getMapHeightatPoint(location)) + 
				"\t" + direction + "\t" + state + "\t" + substate3 + "\t" + tphi + "\t" + Access.getHazardValue(location) +"\t"+HZRT(location));
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
			if(hzatt<hzrate){
				//take mitigating action if hazard rate is too low
				state=3;
			} else{
			
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
					}
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
			//save current location
			LastPos=location;
			if (hzrate<=0.3){
				//Look to see if you can run through it (about 4 squares ahead clear)
					//run through it
				//If running through it looks like a bad idea skirt around it
				state=4;
					//optimized path that has hzrate at target<hzatt
			} else if (0.3 < hzrate && hzrate <= 0.55){
				//look to see if you can run through it in 3 squares
				//if not edge around 
				state=4;
			} else if (0.55 < hzrate && hzrate <= 0.8){
				// can we make it through in 2 squares?
				//skirt around
				state=4;
			} else if (0.8 < hzrate){
				//reverse and then skirt around 
				//skirt around it
				state=4;
			}
			substate3=1;
			rnTimeb=milliTime;
		}
		
//State 4 Navigate around local hazard 
		else if (state==4){
			//If currently in a hazard reverse until no longer in a hazard
			if (substate3==1){
				if (HZRT(location)>hzatt){
					return "backward";
				} else {
					//if safe stop at current location
					substate3=2;
					return "stop";
				}
				//Check to the right at left of the rover. Select safe direction and travel that direction for 5s
				//Check right and left
			} else if (substate3==2){
				//Compare hazard at the right to hazard at the left 
				//Set safe direction as tphi
				if (HZRT(location.offset(Math.cos(direction-Math.PI/4), Math.sin(direction-Math.PI/4)))<=HZRT(location.offset(Math.cos(direction+Math.PI/4), Math.sin(direction+Math.PI/4)))){
					tphi=direction-Math.PI/4;
				} else {
					tphi=direction+Math.PI/4;
				}
				//Check if tphi within appropriate range
				//adjust tphi if necessary
				if (tphi<0){
					tphi=tphi+2*Math.PI;
				} else if (tphi>2*Math.PI){
					tphi=tphi-2*Math.PI;
				}
				substate3=3;
			} else if (substate3==3){
				if (direction>=(tphi+phiBound)){
					return "spin_cw";
				} else if (direction<=(tphi-phiBound)){
					return "spin_ccw";
				} else {
					substate3=4;
					rnTimeb=milliTime;
					return "stop";
				}
			} else if (substate3==4){
				if (HZRT(location)<hzatt+0.05){
				//if (HZRT(location)<hzatt){
					//move
					if (milliTime-rnTimeb<1500){
						return "move";
					} else {
						state=0;
						substate3=1;
						return "stop";
					}
				} else {
					//return to beginning of mitigating action
					substate3=1;
				}
			}
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
//Look at hazard and try to skirt around it
	private double skirt(DecimalPoint loc,DecimalPoint trgt,double Hatt){
		double dir;
		double d1;
		double d2;
		double d3;
		double d4;
		double d5;
		double d6;
		double d7;
		double d8;
		
		d1=pythagorean(loc.offset(2,0),trgt);
		d2=pythagorean(loc.offset(2*Math.cos(Math.PI/8),2*Math.sin(Math.PI/8)),trgt);
		d3=pythagorean(loc.offset(2*Math.cos(2*Math.PI/8),2*Math.sin(2*Math.PI/8)),trgt);
		d4=pythagorean(loc.offset(2*Math.cos(3*Math.PI/8),2*Math.sin(3*Math.PI/8)),trgt);
		d5=pythagorean(loc.offset(0,2),trgt);
		d6=pythagorean(loc.offset(-2*Math.cos(3*Math.PI/8),2*Math.sin(3*Math.PI/8)),trgt);
		d7=pythagorean(loc.offset(-2*Math.cos(2*Math.PI/8),2*Math.sin(2*Math.PI/8)),trgt);
		d8=pythagorean(loc.offset(-2*Math.cos(Math.PI/8),2*Math.sin(Math.PI/8)),trgt);
		
		if (HZRT(loc.offset(2,0))>Hatt){
			d1=10000;
		}
		if (HZRT(loc.offset(2*Math.cos(Math.PI/8),2*Math.sin(Math.PI/8)))>Hatt){
			d2=10000;
		}
		if (HZRT(loc.offset(2*Math.cos(2*Math.PI/8),2*Math.sin(2*Math.PI/8)))>Hatt){
			d3=10000;
		}
		if (HZRT(loc.offset(2*Math.cos(3*Math.PI/8),2*Math.sin(3*Math.PI/8)))>Hatt){
			d4=10000;
		}
		if (HZRT(loc.offset(0,2))>Hatt){
			d5=10000;
		}
		if (HZRT(loc.offset(-2*Math.cos(3*Math.PI/8),2*Math.sin(3*Math.PI/8)))>Hatt){
			d6=10000;
		}
		if (HZRT(loc.offset(-2*Math.cos(2*Math.PI/8),2*Math.sin(2*Math.PI/8)))>Hatt){
			d7=10000;
		}
		if (HZRT(loc.offset(-2*Math.cos(Math.PI/8),2*Math.sin(Math.PI/8)))>Hatt){
			d8=10000;
		}
		
		dir=mindist(d1,d2,d3,d4,d5,d6,d7,d8);
		return dir;
	}
//Method for selection of minimum option
	private double mindist(double o1,double o2,double o3,double o4,double o5,double o6,double o7,double o8) {
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
	public PIDAAcode3 clone(){
		return new PIDAAcode3(this);
	}
}