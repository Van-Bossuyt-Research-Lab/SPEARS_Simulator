package rover.autoCode;

import objects.DecimalPoint;
import rover.RoverAutonomusCode;
import wrapper.Access;

public class PIDAAcode extends RoverAutonomusCode {

	//Declare any variables you want here
		//Angle bounds
		double phiBound=Math.PI/5;
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
		
		//State set
		int state=0;
		
		//Target location 
		DecimalPoint Trgt=new DecimalPoint(170,170);

		private long lastActionTime = 0;
		private int action = 0;
		private int seconds = 1;
		
		public PIDAAcode(){
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
			double battery_temp
	) {

		//Write processing code here

		if (state==0){
			return "spin_cw";
			
			
		}
		
		return "delay1";
		


		
		//return "";		
	}
		
	// writeToLog(String message);
}