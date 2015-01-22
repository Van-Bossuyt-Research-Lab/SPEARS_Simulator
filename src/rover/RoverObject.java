package rover;

import java.io.InputStream;

import objects.DecimalPoint;
import objects.Map;
import objects.ThreadTimer;
import wrapper.Access;
import wrapper.Globals;

public class RoverObject {
	
	public static final int FORWARD = 1, BACKWARD = -1, RELEASE = 0; // possible motor states
	public static final int FL = 0, BL = 1, BR = 2, FR = 3; // identifiers on wheels

	private String name;
	private String IDcode;
	private RoverParametersList params;
	private RoverAutonomusCode autoCode;
	
	private double time_step = 0.01; // time step of physics, in seconds
	
	private boolean connected = false; // Can the ground station hear/talk to us
	private boolean mute = false; // Can we talk at all
	boolean moving = false; // are we moving
	private String motorState = ""; // how are we moving

	boolean hasInstructions = false; // has a list of instructions on file
	private String instructions = ""; // the list of instructions
	private int instructsComplete = 0; // how many items on the list have we done
	private long timeOfLastCmd = Globals.TimeMillis+3600000; // how long since the ground station talked last
	private boolean waiting = false; // are we waiting
	private long cmdWaitTime = 0; // how long to wait
	private boolean run_auto = false; // are we running autonomously
	private String temperatureData = ""; // "file" for recorded temperature data

	private float boardVoltage = 9; // The measured voltage of the battery powering the board
	private float motorVoltage = 12; // The measured voltage of the battery powering the motors
	private float armVoltage = 0; // measured voltage powering the arm
	private float motorCurrent = 0; // how much current the motors are pulling

	private boolean waitingForResponse = false; // are we waiting for a response
	private long responseWaitTime = 0; // how long to wait for response
	private long timeSinceMessage = 0; // how long ago was the message
	private char desiredResponse = '\0'; // what are we listening for
	private boolean responseRecieved = false; // did the ground respond
	
	private char tag = '\0'; // message identification tag
	private char[] data = new char[30]; // message body
	private int index = 0;
	private boolean go = true; // allowed to interpret message
	
	private double EOLbattery = -1;
	private double EOLmotor = Double.MAX_VALUE;
	
	private boolean atTarget = false; // did we find a target
	private int actionCounter = 0; 
	private long autoWaitUntil = 0; // how long to wait in autonomous mode
	private long lastLogWrite = 0; // the last time the log was written to
	
	private boolean checkCurrent = false; // are we checking current
	private float currentIntegral = 0; // the sum of all currents checked
	private long lastCurrentCheck = 0; // time of last current check
	private long startCurretIntegral = 0; // time of initial integral check
	private float averageCurrent = 0; // integral divided by time
		
	private DecimalPoint location; //m x m from center of map
	private double direction; //rad off of positive X
	private double speed = 0; //m/s
	private double angular_velocity = 0; //rad/s
	private double acceleration = 0; //m/s^2
	private double angular_acceleration = 0; //rad/s^2
	private double slip_acceleration = 0; //m/s^2
	private double slip_velocity = 0; //m/s

	private double fric_gr_all = 0;
	private double[] slip = { 0, 0, 0, 0 };		
	
	private int[] motor_power = new int[] {  250, 250, 250, 250 }; // assigned motor powers
	private int[] motor_states = new int[] { 0, 0, 0, 0 }; // assigned motor states
	private double[] wheel_speed = { 0, 0, 0, 0 }; //rad/s	
	private double[] motor_current = { 0, 0, 0, 0 }; //A
	
	private double battery_charge; //C
	private double battery_cp_charge = 0; //C
	private double battery_voltage = 12; //V
	private double battery_current = 0; //A
	private double SOC = 1;
	
	private boolean temp_initalized = false;
	private double battery_temperature = 30; //*c
	private double[] winding_temp = { 30, 30, 30, 30 }; //*c
	private double[] motor_temp = { 30, 30, 30, 30 }; //*c
	
	
	private String serialHistory = "";
	private Map<String, Boolean> LEDs = new Map<String, Boolean>();
	
	public RoverObject(String name, String ID, RoverParametersList param, RoverAutonomusCode code, DecimalPoint loc, double dir, double temp){
		this.name = name;
		IDcode = ID;
		params = param;
		battery_charge = params.getbattery_max_charge();
		autoCode = code;
		location = loc;
		direction = dir;
		battery_temperature = temp;
		for (int i = 0; i < 4; i++){
			winding_temp[i] = temp;
			motor_temp[i] = temp;
		}
		LEDs.add("Mute", false);
		LEDs.add("Instructions", false);
		LEDs.add("Autonomus", false);
	}
	
	public void start(){
		ThreadTimer codeThread = new ThreadTimer(100, new Runnable(){
			public void run(){
				excecuteCode();
			}
		},
		ThreadTimer.FOREVER, name+"-code");
		ThreadTimer physicsThread = new ThreadTimer((int) (time_step*1000), new Runnable(){
			public void run(){
				excecutePhysics();
			}
		},
		ThreadTimer.FOREVER, name+"-physics");
	}
	
	private void excecuteCode(){
		try {
			try {
				motorVoltage = (float) getBatteryVoltage(); // check battery voltage
				if (motorVoltage < 0.0001){
					Globals.writeToLogFile("Rover", "Rover out of Power:\t" + Globals.TimeMillis);
					System.exit(0);
				}
				motorCurrent = (float) getBatteryCurrent(); // measure current draw				
				if (checkCurrent){
					currentIntegral += motorCurrent * (Globals.TimeMillis - lastCurrentCheck); // predict life (really simply)
					lastCurrentCheck = Globals.TimeMillis;
					averageCurrent = currentIntegral / (Globals.TimeMillis - startCurretIntegral);
				}
			} catch (Exception e) {
				Globals.reportError("RoverCode", "runCode - voltage", e);
			}
			
			if (Globals.RFAvailable(IDcode) > 1) { // if there is a message
				delay(500);
				char[] id = strcat((char)Globals.ReadSerial(IDcode), (char)Globals.ReadSerial(IDcode));
				if (strcmp(id, IDcode) == 0 && go) { // if the message is for us and are we allowed to read it
															// go is set to false if the first read is not IDcode to prevent starting a message not intened for the rover from within the body of another message
					Globals.ReadSerial(IDcode); // white space
					tag = (char) Globals.ReadSerial(IDcode); // get type tag
					if (Globals.RFAvailable(IDcode) > 0) { // if there is more to the message
						run_auto = false; // stop running autonomously
						data[0] = tag; // tag is not actually import, just read the entire body of the message
						index++;
						while (Globals.RFAvailable(IDcode) > 0 && index < data.length-1) { //read in message body
							data[index] = (char) Globals.ReadSerial(IDcode);
							index++;
						}
						data[index] = '\0'; // end of string
						// switch through commands
						if (strcmp(data, "move") == 0) {
							sendSerial("s1 g %"); // confirm message reciet
							driveForward();
							moving = true;
						} 
						else if (strcmp(data, "stop") == 0) {
							sendSerial("s1 g %");
							driveStop();
							moving = true;
						} 
						else if (strcmp(data, "spin_ccw") == 0) {
							sendSerial("s1 g %");
							driveSpinCCW();
							moving = true;
						} 
						else if (strcmp(data, "spin_cw") == 0) {
							sendSerial("s1 g %");
							driveSpinCW();
							moving = true;
						} 
						else if (strcmp(data, "backward") == 0) {
							sendSerial("s1 g %");
							driveBackward();
							moving = true;
						}
						else if (strcmp(data, "turnFL") == 0){
							sendSerial("s1 g %");
							driveTurnFL();
							moving = true;
						}
						else if (strcmp(data, "turnFR") == 0){
							sendSerial("s1 g %");
							driveTurnFR();
							moving = true;
						}
						else if (strcmp(data, "turnBL") == 0){
							sendSerial("s1 g %");
							driveTurnBL();
							moving = true;
						}
						else if (strcmp(data, "turnBR") == 0){
							sendSerial("s1 g %");
							driveTurnBR();
							moving = true;
						}
						else if (strcmp(data, "getvolts") == 0) {
							sendSerial("s1 g %");
							delay(2000);
							sendSerial("s1 g Vrov=");
							sendSerial(boardVoltage);
							setWaitForResponse('#', 2000);
							delay(2500);
							sendSerial("s1 g Vmtr=");
							sendSerial(motorVoltage);
							setWaitForResponse('#', 2000);
							delay(2500);
							sendSerial("s1 g Varm=");
							sendSerial(armVoltage);
							setWaitForResponse('#', 2000);
						} 
						else if (strcmp(data, "photo") == 0) {
							sendSerial("s1 g %");
							delay(2000);
							takePicture();
						} 
						else if (strcmp(data, "instructions") == 0) { // if we are recieving instructions
							instructions = ""; // clear existing "file"
							while (Globals.RFAvailable(IDcode) == 0) { // wait for transmission to start
								delay(5);
							}
							delay(1000); // begin syncopation of read/write
							while (Globals.RFAvailable(IDcode) > 0) { 
								while (Globals.RFAvailable(IDcode) > 0) {
									instructions += (char) Globals.ReadSerial(IDcode); // read in character, add to list
								}
								delay(2000); // continue syncopation, we want to avoid the incoming message running out of room in the buffer
							}
							sendSerial("s1 g {"); // unmute the ground station
							delay(1500); // allow unmute to arrive
							sendSerial("s1 g n Instructions Transfered"); // confirm received of instructions
							hasInstructions = true; // we now have instructions
							instructsComplete = 0; // we haven't started them
							System.out.print(instructions); // show console what they are so I can figure out why they don't work
						}
						else if (strcmp(data, "!instruct") == 0){ // if we want to delete existing instructions / abort
							hasInstructions = false; // we don't have instructions
							instructions = ""; // delete "file"
							if (moving){
								driveStop(); // stop the rover
							}
							delay(1000);
							sendSerial("s1 g KillDone"); // confirm abort
						}
						else if (strcmp(data, "auto") == 0){ // force into autonomous mode
							run_auto = true;
							sendSerial("s1 g %");
						}
					} 
					// if there isn't more to the message interpret the tag
					else if (tag == '#') { // message confirmation from ground
						if (waitingForResponse && tag == desiredResponse){
							responseRecieved = true;
						}
					} 
					else if (tag == '^') { // the ground pinged us
						connected = true;
						sendSerial("s1 g ^");
						delay(2000);
						pingGround(); // test the connection
					} 
					else if (tag == '*'){ // our ping of the ground station returned
						if (waitingForResponse && tag == desiredResponse){
							responseRecieved = true;
						}
					}
					else if (tag == '}') { // we have been muted
						mute = true;
						LEDs.set("Mute", mute);
					} 
					else if (tag == '{') { // we have been unmuted
						mute = false;
						LEDs.set("Mute", mute);
					}
					data = new char[data.length]; // reset data array
					index = 0;
					tag = '\0';
					timeOfLastCmd = Globals.TimeMillis; // reset time since command
					if (moving){
				        cmdWaitTime += Globals.TimeMillis + 60000; // reset command wait
					}
				} 
				else { // the message wasn't for us
					go = false; // ignore it
					int waiting = Globals.RFAvailable(IDcode);
					while (waiting > 0) { // delete it
						Globals.ReadSerial(IDcode);
						waiting--;
					}
				}
			} 
			else { // there isn't a message waiting
				go = true; // allowed to read the next message
			}
			
			// Listening for response
			if (Globals.TimeMillis - timeSinceMessage > responseWaitTime && waitingForResponse){ // if waiting and appropriate time has passed
				waitingForResponse = false; // we're no longer waiting
				// switch possible responses
				if (desiredResponse == '*'){
					connected = responseRecieved; // are we connected = did they answer
				}
				else if (desiredResponse == '%'){ // message confirmation
					if (!responseRecieved){
						pingGround(); // if failed, make sure the ground station is still there
					}
				}
			}

			// Autonomous and Instruction handling
			if (Globals.TimeMillis - timeOfLastCmd > 60000){ // if it has been a minute since we heard from them
				if (hasInstructions && !mute) { // if we have instructions, can send things
					LEDs.set("Instructions", true);
					LEDs.set("Autonomus", false);
					run_auto = false; // don't run autonomously
					if (!waiting || (Globals.TimeMillis > cmdWaitTime)) { // if we're not waiting or have waiting long enough
						waiting = false;
						String cmd = ""; // the command
						int x = 0;
						int count = 0;
						while (count <= instructsComplete) { // sift through commands we have done
							cmd = "";
							while (instructions.charAt(x) != '\n') {
								cmd += instructions.charAt(x);
								x++;
							}
							x++;
							count++;
						}
						System.out.println(cmd); // TODO: more debugging
						if (strcmp(cmd.substring(0, 2), IDcode) == 0) { // if the instruction is for us
							String temp = cmd; //drop the first 2 characters cause they're not important
							cmd = "";
							x = 2;
							while (x < temp.length()){
								cmd += temp.charAt(x);
								x++;
							}
							System.out.println(cmd);
							// switch all known commands
							if (strcmp(cmd, "move") == 0) {
								driveForward();
								moving = true;
								motorState = cmd;
							} 
							else if (strcmp(cmd, "backward") == 0) {
								driveBackward();
								moving = true;
								motorState = cmd;
							} 
							else if (strcmp(cmd, "spin_cw") == 0) {
								driveSpinCW();
								moving = true;
								motorState = cmd;
							} 
							else if (strcmp(cmd, "spin_ccw") == 0) {
								driveSpinCCW();
								moving = true;
								motorState = cmd;
							} 
							else if (strcmp(cmd, "stop") == 0) {
								driveStop();
								moving = false;
							}
							else if (strcmp(cmd, "turnFL") == 0){
								driveTurnFL();
								moving = true;
								motorState = cmd;
							}
							else if (strcmp(cmd, "turnFR") == 0){
								driveTurnFR();
								moving = true;
								motorState = cmd;
							}
							else if (strcmp(cmd, "turnBL") == 0){
								driveTurnBL();
								moving = true;
								motorState = cmd;
							}
							else if (strcmp(cmd, "turnBR") == 0){
								driveTurnBR();
								moving = true;
								motorState = cmd;
							}
							else if (cmd.equals("photo")) {
								takePicture();
							}
							else if (strcmp(cmd, "recTemp") == 0){ // record temperature
								if (temperatureData.equals("")){ // if there is not an existing "file," create one
						    		temperatureData += "Graph Title,Temperatures," + "Vertical Units,*C," + "Horizontal Units,s," + "Label,\nSys Time,Temperature,";
						    	}
						    	temperatureData += Globals.TimeMillis + "," + getTemperature() + ",\n"; // get temperature data and add it to the file
						    }
						    else if (strcmp(cmd, "sendTemp") == 0){ // report temperature
						    	sendSerial("s1 g }"); // mute the ground so they can't interrupt
						    	delay(2000);
						    	sendSerial("s c CSV"); // tell the satellite a file is coming
						    	delay(2000);
						    	int i = 0;
						    	while (i < temperatureData.length()) { // send the file in 60 byte chunks
						    		index = 0;
						    		while (index < 60 && i < temperatureData.length()){
						    			sendSerial(temperatureData.charAt(i));
						    			index++;
						    			i++;
						    		}
						    		delay(2000); // synocpation
						    	}
						    	temperatureData = ""; // delete existing "file"
						    }
						    else if (strcmp(cmd, "delay1") == 0) { // wait a second
						    	waiting = true;
								cmdWaitTime = Globals.TimeMillis + 1000;
							}
							else if (strcmp(cmd, "report") == 0) { // report completion of instructions to ground
								if (sendSerial("s1 g n Rover Instructs Done")) { // if we're not muted
									hasInstructions = false; // we no longer have instructions
									instructsComplete = 0;
								}
								else { // if we are muted, try to report again later
									instructsComplete--;
								}
							}
						}
						instructsComplete++;
						delay(5);
					}
					/*else if (waiting){
					   if (moving){
					  	   if (strcmp(motorState, "move") == 0) {
								driveForward();
							} 
							else if (strcmp(motorState, "backward") == 0) {
								driveBackward();
							} 
							else if (strcmp(motorState, "spin_cw") == 0) {
								driveSpinCW();
							} 
							else if (strcmp(motorState, "spin_ccw") == 0) {
								driveSpinCCW();
							} 
							else if (strcmp(motorState, "stop") == 0) {
								driveStop();
							}
							else if (strcmp(motorState, "turnFL") == 0){
								driveTurnFL();
							}
							else if (strcmp(motorState, "turnFR") == 0){
								driveTurnFR();
							}
							else if (strcmp(motorState, "turnBL") == 0){
								driveTurnBL();
							}
							else if (strcmp(motorState, "turnBR") == 0){
								driveTurnBR();
							}
					    }
					}*/
				}
				else { 
					run_auto = true;
				}
			}
			else {
				LEDs.set("Autonomus", run_auto);
				LEDs.set("Instructions", false);
			}
			
			if (run_auto){//Follow Autonomous Thought
				LEDs.set("Autonomus", true);
				LEDs.set("Instructions", false);
				
				String cmd = autoCode.nextCommand(
						Globals.TimeMillis,
						location,
						direction,
						getAcceleration(),
						getAngularAcceleration(),
						getWheelSpeed(FL),
						getWheelSpeed(FR),
						getWheelSpeed(BL),
						getWheelSpeed(BR),
						getMotorCurrent(FL),
						getMotorCurrent(FR),
						getMotorCurrent(BL),
						getMotorCurrent(BR),
						getMotorTemp(FL),
						getMotorTemp(FR),
						getMotorTemp(BL),
						getMotorTemp(BR),
						getBatteryVoltage(),
						getBatteryCurrent(),
						getBatteryTemperature(),
						getBatteryCharge()
				);
				// switch all known commands
				if (strcmp(cmd, "") == 0){ /*Do Nothing*/ }
				else if (strcmp(cmd, "move") == 0) {
					driveForward();
					moving = true;
					motorState = cmd;
				} 
				else if (strcmp(cmd, "backward") == 0) {
					driveBackward();
					moving = true;
					motorState = cmd;
				} 
				else if (strcmp(cmd, "spin_cw") == 0) {
					driveSpinCW();
					moving = true;
					motorState = cmd;
				} 
				else if (strcmp(cmd, "spin_ccw") == 0) {
					driveSpinCCW();
					moving = true;
					motorState = cmd;
				} 
				else if (strcmp(cmd, "stop") == 0) {
					driveStop();
					moving = false;
				}
				else if (strcmp(cmd, "turnFL") == 0){
					driveTurnFL();
					moving = true;
					motorState = cmd;
				}
				else if (strcmp(cmd, "turnFR") == 0){
					driveTurnFR();
					moving = true;
					motorState = cmd;
				}
				else if (strcmp(cmd, "turnBL") == 0){
					driveTurnBL();
					moving = true;
					motorState = cmd;
				}
				else if (strcmp(cmd, "turnBR") == 0){
					driveTurnBR();
					moving = true;
					motorState = cmd;
				}
				else if (cmd.equals("photo")) {
					takePicture();
				}
				else if (strcmp(cmd, "recTemp") == 0){ // record temperature
					if (temperatureData.equals("")){ // if there is not an existing "file," create one
			    		temperatureData += "Graph Title,Temperatures," + "Vertical Units,*C," + "Horizontal Units,s," + "Label,\nSys Time,Temperature,";
			    	}
			    	temperatureData += Globals.TimeMillis + "," + getTemperature() + ",\n"; // get temperature data and add it to the file
			    }
			    else if (strcmp(cmd, "sendTemp") == 0){ // report temperature
			    	sendSerial("s1 g }"); // mute the ground so they can't interrupt
			    	delay(2000);
			    	sendSerial("s c CSV"); // tell the satellite a file is coming
			    	delay(2000);
			    	int i = 0;
			    	while (i < temperatureData.length()) { // send the file in 60 byte chunks
			    		index = 0;
			    		while (index < 60 && i < temperatureData.length()){
			    			sendSerial(temperatureData.charAt(i));
			    			index++;
			    			i++;
			    		}
			    		delay(2000); // synocpation
			    	}
			    	temperatureData = ""; // delete existing "file"
			    }
			    else if (strcmp(cmd.substring(0, 5), "delay") == 0) { // wait a second
			    	waiting = true;
					cmdWaitTime = Globals.TimeMillis + Integer.parseInt(cmd.substring(5, cmd.length()));
				}
				
				/*if (Globals.TimeMillis >= autoWaitUntil){
					driveSpinCCW();
					// EOL Calculation
					setEOL(resistanceP*capacitanceB*ln(motorVoltage/(motorCurrent*(resistanceP+resistanceCP+resistanceS))), "battery");
					Globals.writeToLogFile("PHM Calc", "Time Elapsed:\t" + (Globals.TimeMillis-this.startCurretIntegral) + "\tEOL:\t" + getEndOfLife("battery") + "\tMotor Voltage:\t" + motorVoltage + "\tMotor Current:\t" + motorCurrent);
					autoWaitUntil = Globals.TimeMillis + 600000;
				}
				else {
					//System.out.println(Globals.TimeMillis + " < " + autoWaitUntil);
				}
				
				if (Globals.TimeMillis - this.lastLogWrite > 10*60*1000){
					Globals.writeToLogFile("Autonomus", "Battery Voltage = " + getBatteryVoltage() + " V\t\tSOC = " + getSOC() + "%");
					lastLogWrite = Globals.TimeMillis;
				}
				
				if (Access.isAtTarget(location) && !atTarget){
					atTarget = true;
					driveStop();
					takePicture();
					timeOfLastCmd = Globals.TimeMillis;
					autoWaitUntil = Globals.TimeMillis + 1000;
					
				}
				else {
					atTarget = false;
				}
				if (Globals.TimeMillis >= autoWaitUntil){
					actionCounter++;
					int step = actionCounter % 2;
					if (step == 0){
						driveTurnFR();
						autoWaitUntil = Globals.TimeMillis + 2000;
					}
					else if (step == 1){
						driveForward();
						autoWaitUntil = Globals.TimeMillis + 4*1000;
					}							
				}*/
				
			}

			/*
			 * double volt1 = (analogRead(irPin1)); double distance1 =
			 * 8054.2 / ( pow ( volt1, .94 ) ); double volt2 =
			 * (analogRead(irPin2)); double distance2 = 8054.2 / ( pow
			 * (volt2, .94) );
			 * 
			 * if (distance1 < 50 || distance2 < 50) { tries = tries + 1; }
			 * else tries = 0;
			 * 
			 * if (tries > 1) { //sendSerial("s1 g n" distance1);
			 * motor1->run(RELEASE); motor2->run(RELEASE);
			 * motor3->run(RELEASE); motor4->run(RELEASE); delay(1000); }
			 */
		} catch (Exception e) {
			// something went wrong
			System.out.println("Error in Rover Run Code");
			e.printStackTrace();
			Globals.reportError("RoverCode", "runCode - code", e);
		}
	}
	
	private void takePicture() { // take a picture
		try {
			sendSerial("s1 g }"); // mute the ground so it can't interrupt
			InputStream data = RoverObject.class.getResourceAsStream("/Rover Sample.jpg"); // get the buffer from the "camera"
			delay(2000);
			sendSerial("s c [o]"); // tell the satellite a file is coming
			delay(2000);
			int hold = data.read();
			int index;
			while (hold != -1) { // send the file in 60 byte chunks
				index = 0;
				while (index < 60 && hold != -1) {
					Globals.writeToSerial((byte) hold, IDcode);
					hold = data.read();
					index++;
				}
				delay(2000);
			}
		}
		catch (Exception e){
			Globals.reportError("RoverCode", "takePicture", e);
		}
	}
	
	private void pingGround(){ // ping the ground station and listen for a response
		sendSerial("s1 g *");
		setWaitForResponse('*', 3000);
	}
	
	// how to move the motors for driving
	private void driveForward(){
		setMotorState(FL, FORWARD);
		setMotorState(BL, FORWARD);
		setMotorState(BR, FORWARD);
		setMotorState(FR, FORWARD);
	}
	
	private void driveBackward(){
		setMotorState(FL, BACKWARD);
		setMotorState(BL, BACKWARD);
		setMotorState(BR, BACKWARD);
		setMotorState(FR, BACKWARD);
	}
	
	private void driveSpinCW(){
		setMotorState(FL, FORWARD);
		setMotorState(BL, FORWARD);
		setMotorState(BR, BACKWARD);
		setMotorState(FR, BACKWARD);
	}
	
	private void driveSpinCCW(){
		setMotorState(FL, BACKWARD);
		setMotorState(BL, BACKWARD);
		setMotorState(BR, FORWARD);
		setMotorState(FR, FORWARD);
	}
	
	private void driveStop(){
		setMotorState(FL, RELEASE);
		setMotorState(BL, RELEASE);
		setMotorState(BR, RELEASE);
		setMotorState(FR, RELEASE);
	}
	
	private void driveTurnFL(){
		setMotorState(FL, RELEASE);
		setMotorState(BL, RELEASE);
		setMotorState(BR, FORWARD);
		setMotorState(FR, FORWARD);
	}
	
	private void driveTurnFR(){
		setMotorState(FL, FORWARD);
		setMotorState(BL, FORWARD);
		setMotorState(BR, RELEASE);
		setMotorState(FR, RELEASE);
	}
	
	private void driveTurnBL(){
		setMotorState(FL, RELEASE);
		setMotorState(BL, RELEASE);
		setMotorState(BR, BACKWARD);
		setMotorState(FR, BACKWARD);
	}
	
	private void driveTurnBR(){
		setMotorState(FL, BACKWARD);
		setMotorState(BL, BACKWARD);
		setMotorState(BR, RELEASE);
		setMotorState(FR, RELEASE);
	}
	
	// set up waiting for a response
	private void setWaitForResponse(char listen, long timeToWait){
		timeSinceMessage = Globals.TimeMillis; // when was the out message sent
		desiredResponse = listen; // what are we listening for
		responseWaitTime = timeToWait; // how long should we wait for it
		waitingForResponse = true; // we are waiting
	}
	
	private boolean sendSerial(String mess) { // send a message
		char[] message = mess.toCharArray();
		if (!mute) { // is we can talk
			int x = 0;
			while (x < message.length) {
				if (message[x] == '\0') { // while not the end of string
					break;
				}
				Globals.writeToSerial(message[x], IDcode); // Write to Serial one char at a time
				try {
					Thread.sleep((int) (5 / Globals.getTimeScale())); // Pause for sending
				} 
				catch (InterruptedException e) {
					Globals.reportError("RoverCode", "sendSerial", e);
				}
				x++;
			}
			addToSerialHistory(mess);
			return true;
		} 
		else {
			addToSerialHistory("Surpressed: " + mess);
			return false;
		}
	}

	private boolean sendSerial(float val) {
		return sendSerial(val + "");
	}

	private boolean sendSerial(char mess) {
		if (!mute) {
			Globals.writeToSerial(mess, IDcode);
			addToSerialHistory(mess + "");		
			return true;
		} else {
			addToSerialHistory("Supressed: " + mess);
			return false;
		}
	}
	
	private float ln(float a){ // natural log of a+1
		return (float) (a - (a*a)/2.0 + (a*a*a)/3.0 - (a*a*a*a)/4.0 + (a*a*a*a*a)/5.0 - (a*a*a*a*a*a)/6);
	}

	private void delay(double length) { //put the thread to sleep for a bit
		try{
			Thread.sleep((long)(length/Globals.getTimeScale()), (int)((length/Globals.getTimeScale()-(int)length/Globals.getTimeScale())*1000000));
		} 
		catch (Exception e) {
			e.printStackTrace();
			Globals.reportError("RoverCode", "delay", e);
		}
		//long start = System.nanoTime();
		//while (((System.nanoTime()-start) < (length*1000000))) {}
	}
	
	private double getTemperature(){ // read temperature from the "sensor"
		return Globals.addErrorToMeasurement(0, .5);//WrapperMain.MAP.getTemperatureAtLoc(), 0.73);
	}
	
	private int strcmp(char[] first, String second) { // see if 2 strings are equal
		try {
			String firstStr = "";
			int x = 0;
			while (first[x] != '\0' && x < first.length) {
				firstStr += first[x];
				x++;
			}
			if (firstStr.equals(second)){
				return 0;
			}
		} catch (Exception e) {
			Globals.reportError("RoverCode", "strcmp", e);
		}
		return 1;
	}
	
	private char[] strcat(char[] first, char[] second){
		char[] out = new char[first.length + second.length - 1];
		int x = 0;
		while (x < first.length - 1){
			out[x] = first[x];
			x++;
		}
		while (x < out.length){
			out[x] = second[x-first.length];
			x++;
		}
		return out;
	}
	
	private char[] strcat(char first, char[] second){
		char[] out = new char[second.length + 1];
		out[0] = first;
		int x = 1;
		while (x < out.length){
			out[x] = second[x-1];
			x++;
		}
		return out;
	}
	
	private char[] strcat(char[] first, char second){
		char[] out = new char[first.length + 1];
		int x = 0;
		while (x < first.length-1){
			out[x] = first[x];
			x++;
		}
		out[x] = second;
		x++;
		out[x] = '\0';
		return out;
	}
	
	private char[] strcat(char first, char second){
		return new char[] { first, second, '\0' };
	}
	
	private int strcmp(String first, String second){ // see if 2 strings are equal
		if (first.equals(second)){
			return 0;
		}
		else {
			return 1;
		}
	}
	
//TODO PHYSCIS STARTS HERE *****************************************************************************************************************************************************************************************************
	private void excecutePhysics(){
		try {
			// Motor Currents, based on voltage
			motor_current[FL] += ( motor_power[FL]*motor_states[FL]/255.0*battery_voltage - params.getmotor_voltage_transform()*wheel_speed[FL] - motor_current[FL]*params.getmotor_resistance()) / params.getmotor_inductance() * time_step;
			motor_current[FR] += ( motor_power[FR]*motor_states[FR]/255.0*battery_voltage - params.getmotor_voltage_transform()*wheel_speed[FR] - motor_current[FR]*params.getmotor_resistance()) / params.getmotor_inductance() * time_step;
			motor_current[BL] += ( motor_power[BL]*motor_states[BL]/255.0*battery_voltage - params.getmotor_voltage_transform()*wheel_speed[BL] - motor_current[BL]*params.getmotor_resistance()) / params.getmotor_inductance() * time_step;
			motor_current[BR] += ( motor_power[BR]*motor_states[BR]/255.0*battery_voltage - params.getmotor_voltage_transform()*wheel_speed[BR] - motor_current[BR]*params.getmotor_resistance()) / params.getmotor_inductance() * time_step;
			// min currents at 0, motor cannot generate current
			/*if (motor_current[FL]*motor_states[FL] <= 0){
				motor_current[FL] = 0;
			}
			if (motor_current[FR]*motor_states[FR] <= 0){
				motor_current[FR] = 0;
			}
			if (motor_current[BL]*motor_states[BL] <= 0){
				motor_current[BL] = 0;
			}
			if (motor_current[BR]*motor_states[BR] <= 0){
				motor_current[BR] = 0;
			}*/
			// angular motor speeds, based on torques
			wheel_speed[FL] += 1/params.getwheel_inertia() * ( params.getmotor_energy_transform()*motor_current[FL] - params.getwheel_radius()*slip[FL] + params.getwheel_radius()*fric_gr_all*Math.cos(params.getgamma()) - params.getfriction_axle()*wheel_speed[FL]/params.getwheel_radius()) * time_step;
			wheel_speed[FR] += 1/params.getwheel_inertia() * ( params.getmotor_energy_transform()*motor_current[FR] - params.getwheel_radius()*slip[FR] - params.getwheel_radius()*fric_gr_all*Math.cos(params.getgamma()) - params.getfriction_axle()*wheel_speed[FR]/params.getwheel_radius()) * time_step;
			wheel_speed[BL] += 1/params.getwheel_inertia() * ( params.getmotor_energy_transform()*motor_current[BL] - params.getwheel_radius()*slip[BL] + params.getwheel_radius()*fric_gr_all*Math.cos(params.getgamma()) - params.getfriction_axle()*wheel_speed[BL]/params.getwheel_radius()) * time_step;
			wheel_speed[BR] += 1/params.getwheel_inertia() * ( params.getmotor_energy_transform()*motor_current[BR] - params.getwheel_radius()*slip[BR] - params.getwheel_radius()*fric_gr_all*Math.cos(params.getgamma()) - params.getfriction_axle()*wheel_speed[BR]/params.getwheel_radius()) * time_step;
			// translational friction, approximately the same for all wheels
			fric_gr_all = params.getfriction_gr() * params.getmotor_arm() * angular_velocity;
			// Slip forces on wheels, based on speed differences
			slip[FL] = params.getfriction_s() * (wheel_speed[FL]*params.getwheel_radius() - speed);
			slip[FR] = params.getfriction_s() * (wheel_speed[FR]*params.getwheel_radius() - speed);
			slip[BL] = params.getfriction_s() * (wheel_speed[BL]*params.getwheel_radius() - speed);
			slip[BR] = params.getfriction_s() * (wheel_speed[BR]*params.getwheel_radius() - speed);
			// Acceleration changes based on forces
			acceleration = 1/params.getrover_mass()*(slip[FL] + slip[BL] + slip[FR] + slip[BR]) - Access.getPlanetParameters().getgrav_accel()*Math.sin(Access.getMapInclineAtPoint(location, direction));
			angular_acceleration = 1/params.getrover_inertia() * ((params.getmotor_arm()*(slip[FR] + slip[BR] - slip[FL] - slip[BL])*Math.cos(params.getgamma()) - params.getmotor_arm()*(4*fric_gr_all)));
			// Speed changes based on Acceleration
			speed += acceleration * time_step;
			angular_velocity += angular_acceleration * time_step;
				//System.out.println(round(wheel_speed[FL]) + " rad/s -> " + round(slip[FL]) + " N");
				//System.out.println(round(wheel_speed[FR]) + " rad/s -> " + round(slip[FR]) + " N -> " + round(acceleration) + " m/s^2 -> " + round(speed) + " m/s");
				//System.out.println(round(wheel_speed[BL]) + " rad/s -> " + round(slip[BL]) + " N -> " + round(angular_acceleration) + " rad/s^2 -> " + round(angular_velocity) + " rad/s");
				//System.out.println(round(wheel_speed[BR]) + " rad/s -> " + round(slip[BR]) + " N");
			// Calculate the amount the rover slips sideways
			slip_acceleration = (-params.getfriction_gr()*slip_velocity*4 - params.getrover_mass()*Access.getPlanetParameters().getgrav_accel()*Math.sin(Access.getMapCrossSlopeAtPoint(location, direction)) / params.getrover_mass());
			slip_velocity += slip_acceleration * time_step;
			// Calculate new location
			location.offsetThis(speed*time_step*Math.cos(direction), speed*time_step*(Math.sin(direction)));
			//TODO													  + here??
			location.offsetThis(slip_velocity*time_step*Math.cos(direction-Math.PI/2.0), slip_velocity*time_step*(Math.sin(direction-Math.PI/2.0)));
			direction += angular_velocity*time_step;
			// report new location to map
			Access.updateRoverLocation(name, location, direction);
			
			//Determining the current of the battery and the change in the stored charge
			if (motor_current[FL]*motor_states[FL] <= 0){
				motor_current[FL] = 0;
			}
			if (motor_current[FR]*motor_states[FR] <= 0){
				motor_current[FR] = 0;
			}
			if (motor_current[BL]*motor_states[BL] <= 0){
				motor_current[BL] = 0;
			}
			if (motor_current[BR]*motor_states[BR] <= 0){
				motor_current[BR] = 0;
			}
			battery_current = Math.abs(motor_current[FL]) + Math.abs(motor_current[FR]) + Math.abs(motor_current[BL]) + Math.abs(motor_current[BR]);
			double battery_change = battery_charge / params.getcapacitance_battery() / params.getresistance_parasite() + battery_current;
			double cp_change = battery_current - (battery_cp_charge / params.getcapacitance_cp() / resistance_cp());
			battery_charge -= battery_change * time_step;
			battery_cp_charge += cp_change * time_step;
			battery_voltage = battery_charge/params.getcapacitance_battery() - battery_cp_charge/params.getcapacitance_cp() - params.getresistance_s()*battery_current;
			SOC = 1 - (params.getbattery_max_charge() - battery_charge) / params.getbattery_max_charge();
				//System.out.println("Vb: " + battery_voltage + "\tVm: " + getMotorVoltage(FR) + "\tQcp: " + battery_cp_charge + "\tIb: " + battery_current + "\tIm: " + motor_current[FR]);
			
			//Determining the temperature of the battery
			battery_temperature += ((params.getresistance_parasite()*Math.pow(battery_change-battery_current, 2) + params.getresistance_s()*Math.pow(battery_current, 2) + resistance_cp()*Math.pow(battery_current-cp_change, 2)) - params.getbattery_heat_transfer()*(battery_temperature - Access.getMapTemperatureAtPoint(location)) / params.getbattery_thermal_cap()) * time_step;
			//Determining the temperature of the motor coils
			winding_temp[FL] += ((params.getmotor_resistance()*Math.pow(motor_current[FL], 2) - params.getwinding_heat_transfer()*(winding_temp[FL] - motor_temp[FL])) / params.getwinding_thermal_cap()) * time_step;
			winding_temp[FR] += ((params.getmotor_resistance()*Math.pow(motor_current[FR], 2) - params.getwinding_heat_transfer()*(winding_temp[FR] - motor_temp[FR])) / params.getwinding_thermal_cap()) * time_step;
			winding_temp[BL] += ((params.getmotor_resistance()*Math.pow(motor_current[BL], 2) - params.getwinding_heat_transfer()*(winding_temp[BL] - motor_temp[BL])) / params.getwinding_thermal_cap()) * time_step;
			winding_temp[BR] += ((params.getmotor_resistance()*Math.pow(motor_current[BR], 2) - params.getwinding_heat_transfer()*(winding_temp[BR] - motor_temp[BR])) / params.getwinding_thermal_cap()) * time_step;
			//Determining the surface temperature of the motor
			motor_temp[FL] += ((params.getwinding_heat_transfer()*(winding_temp[FL] - motor_temp[FL]) - params.getmotor_surface_heat_transfer()*(motor_temp[FL] - Access.getMapTemperatureAtPoint(location))) / params.getmotor_thermal_cap()) * time_step;
			motor_temp[FR] += ((params.getwinding_heat_transfer()*(winding_temp[FR] - motor_temp[FR]) - params.getmotor_surface_heat_transfer()*(motor_temp[FR] - Access.getMapTemperatureAtPoint(location))) / params.getmotor_thermal_cap()) * time_step;
			motor_temp[BL] += ((params.getwinding_heat_transfer()*(winding_temp[BL] - motor_temp[BL]) - params.getmotor_surface_heat_transfer()*(motor_temp[BL] - Access.getMapTemperatureAtPoint(location))) / params.getmotor_thermal_cap()) * time_step;
			motor_temp[BR] += ((params.getwinding_heat_transfer()*(winding_temp[BR] - motor_temp[BR]) - params.getmotor_surface_heat_transfer()*(motor_temp[BR] - Access.getMapTemperatureAtPoint(location))) / params.getmotor_thermal_cap()) * time_step;
			
			//RoverEvents.updateStats();
		}
		catch (Exception e){
			Globals.reportError("RoverDriveModel", "execute", e);
		}
	}
	
	private double resistance_cp(){ // get the resistance of the CP resistor as a function of SOC
		return params.getR_cp0()+params.getR_cp1()*Math.exp(params.getR_cp2()*(1-SOC));
	}
	
	private void setMotorPower(int which, int power){ // set power to a motor
		if (power >= 0 && power <= 255){
			motor_power[which] = power;
		}
	}
	
	private void setMotorState(int which, int state){ // set a state to a motor
		if (Math.abs(state) <= 1){
			motor_states[which] = state;
		}
	}
	
	public void addToSerialHistory(String out){
		serialHistory += out + "\t\t\t" + Globals.TimeMillis + "\n";
	}
	
	public String getName(){
		return name;
	}
	
	public String getIDTag(){
		return IDcode;
	}
	
	public String getSerialHistory(){
		return serialHistory;
	}
	
	public boolean getLEDisLit(String name){
		return LEDs.get(name);
	}
	
	public RoverParametersList getParameters(){
		return params;
	}
	
	public DecimalPoint getLocation(){
		return location;
	}
	
	public double getDirection(){
		return direction;
	}
	
	public double getBatteryVoltage(){
		return battery_voltage;
	}

	public double getBatteryCharge(){
		return battery_charge;
	}
	
	public double getBatterCPCharge(){
		return battery_cp_charge;
	}
	
	public double getSOC(){
		return SOC;
	}
	
	public double getBatteryCurrent(){
		return battery_current;
	}

	public double getSpeed(){
		return  speed;
	}

	public double getAngularVelocity(){
		return angular_velocity;
	}
	
	public double getSlipVelocity(){
		return slip_velocity;
	}

	public double getAcceleration(){
		return acceleration;
	}

	public double getAngularAcceleration(){
		return angular_acceleration;
	}
	
	public double getSlipAcceleration(){
		return slip_acceleration;
	}

	public double getWheelSpeed(int which){
		if (0 <= which && which < 4){
			return wheel_speed[which];
		}
		return 0;
	}
	
	public double getMotorCurrent(int which){
		if (0 <= which && which < 4){
			return motor_current[which];
		}
		return 0;
	}
	
	public double getMotorVoltage(int which){
		if (0 <= which && which < 4){
			return (motor_power[which]/255.0)*battery_voltage*motor_states[which];
		}
		return 0;
	}
	
	public double getBatteryTemperature(){
		return battery_temperature;
	}
	
	public double getMotorTemp(int which){
		if (0 <= which && which < 4){
			return motor_temp[which];
		}
		return 0;
	}
	
}
