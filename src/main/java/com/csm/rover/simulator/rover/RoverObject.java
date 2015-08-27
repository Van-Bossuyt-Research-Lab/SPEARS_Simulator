package rover;

import java.awt.Point;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;

import objects.DecimalPoint;
import objects.Map;
import objects.SyncronousThread;
import rover.autoCode.RoverAutonomusCode;
import rover.phsicsModels.RoverPhysicsModel;
import wrapper.Access;
import wrapper.Globals;

//TODO actually debug instructions
//TODO make for modular for OCP, SRP
public class RoverObject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name;
	private String IDcode;
	private RoverPhysicsModel physics;
	private RoverAutonomusCode autoCode;
	
	private boolean connected = false; // Can the ground station hear/talk to us
	private boolean mute = false; // Can we talk at all
	private boolean moving = false; // are we moving
	private String motorState = ""; // how are we moving

	private boolean hasInstructions = false; // has a list of instructions on file
	private String instructions = ""; // the list of instructions
	private int instructsComplete = 0; // how many items on the list have we done
	private long timeOfLastCmd = Globals.TimeMillis; // how long since the ground station talked last
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
	
	private boolean checkCurrent = false; // are we checking current
	private float currentIntegral = 0; // the sum of all currents checked
	private long lastCurrentCheck = 0; // time of last current check
	private long startCurretIntegral = 0; // time of initial integral check
	private float averageCurrent = 0; // integral divided by time
	
	private HashSet<Point> visitedScience = new HashSet<Point>();
		
	private String serialHistory = "";
	private Map<String, Boolean> LEDs = new Map<String, Boolean>();
	
	public RoverObject(String name, String ID, RoverPhysicsModel param, RoverAutonomusCode code, DecimalPoint loc, double dir, double temp){
		this.name = name;
		IDcode = ID;
		physics = param;
		autoCode = code;
		physics.initalizeConditions(name, physics.getbattery_max_charge(), temp);
		physics.setLocation(loc);
		physics.setDirection(dir);
		LEDs.add("Mute", false);
		LEDs.add("Instructions", false);
		LEDs.add("Autonomus", false);
	}
	
	public void start(){
		new SyncronousThread(100, new Runnable(){
			public void run(){
				//System.out.println(name + "-CODE\t" + Globals.TimeMillis);
				excecuteCode();
			}
		},
		SyncronousThread.FOREVER, name+"-code");
		physics.start();
		timeOfLastCmd = Globals.TimeMillis;
	}
	
	private void excecuteCode(){
		try {
			//Globals.writeToLogFile(this.name, Globals.TimeMillis + "\t" + physics.getLocation().getX() + "\t" + physics.getLocation().getY() + "\t" + Access.getMapHeightatPoint(physics.getLocation()) + "\t" + visitedScience.size()*10 + "\t" + physics.getBatteryCharge());
			try {
				motorVoltage = (float) getBatteryVoltage(); // check battery voltage
				if (motorVoltage < 0.0001){
					Globals.writeToLogFile("Rover", "Rover out of Power:\t" + Globals.TimeMillis);
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
															// go is set to false if the first read is not IDcode 
															// to prevent starting a message not intended for the rover 
															// from within the body of another message
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
						else if (strcmp(data, "score") == 0){
							if (Access.isAtTarget(getLocation())){
								this.visitedScience.add(Access.CODE.GUI.TerrainPnl.HeightMap.getMapSquare(getLocation()));
								System.out.println("Aquired.  New Score = " + visitedScience.size());
							}
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
				//System.out.println(this.name);
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
						physics.getLocation(),
						physics.getDirection(),
						physics.getAcceleration(),
						physics.getAngularAcceleration(),
						physics.getWheelSpeed(RoverWheels.FL),
						physics.getWheelSpeed(RoverWheels.FR),
						physics.getWheelSpeed(RoverWheels.BL),
						physics.getWheelSpeed(RoverWheels.BR),
						physics.getMotorCurrent(RoverWheels.FL),
						physics.getMotorCurrent(RoverWheels.FR),
						physics.getMotorCurrent(RoverWheels.BL),
						physics.getMotorCurrent(RoverWheels.BR),
						physics.getMotorTemp(RoverWheels.FL),
						physics.getMotorTemp(RoverWheels.FR),
						physics.getMotorTemp(RoverWheels.BL),
						physics.getMotorTemp(RoverWheels.BR),
						physics.getBatteryVoltage(),
						physics.getBatteryCurrent(),
						physics.getBatteryTemperature(),
						physics.getBatteryCharge()
				);
				//TODO switch all known commands
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
			    else if (strcmp(cmd.substring(0, 7), "chngmtr") == 0){ //to change motor speed "chngmtr*###" where *=motorID and ###=new power
			    	int motor = cmd.charAt(7);
			    	int power = Integer.parseInt(cmd.substring(8, cmd.length()));
			    	if (power < 0){
			    		power = 0;
			    	}
			    	else if (power > 255){
			    		power = 255;
			    	}
			    	if (motor >= 0 && motor < 4){
			    		RoverWheels wheel;
			    		switch (motor){
			    		case 0:
			    			wheel = RoverWheels.FL;
			    			break;
			    		case 1:
			    			wheel = RoverWheels.FR;
			    			break;
			    		case 2:
			    			wheel = RoverWheels.BL;
			    			break;
			    		case 3:
			    		default:
			    			wheel = RoverWheels.BR;
			    			break;
			    		}
			    		physics.setMotorPower(wheel, power);
			    	}
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
		physics.setMotorState(RoverWheels.FL, MotorState.FORWARD);
		physics.setMotorState(RoverWheels.BL, MotorState.FORWARD);
		physics.setMotorState(RoverWheels.BR, MotorState.FORWARD);
		physics.setMotorState(RoverWheels.FR, MotorState.FORWARD);
	}
	
	private void driveBackward(){
		physics.setMotorState(RoverWheels.FL, MotorState.BACKWARD);
		physics.setMotorState(RoverWheels.BL, MotorState.BACKWARD);
		physics.setMotorState(RoverWheels.BR, MotorState.BACKWARD);
		physics.setMotorState(RoverWheels.FR, MotorState.BACKWARD);
	}
	
	private void driveSpinCW(){
		physics.setMotorState(RoverWheels.FL, MotorState.FORWARD);
		physics.setMotorState(RoverWheels.BL, MotorState.FORWARD);
		physics.setMotorState(RoverWheels.BR, MotorState.BACKWARD);
		physics.setMotorState(RoverWheels.FR, MotorState.BACKWARD);
	}
	
	private void driveSpinCCW(){
		physics.setMotorState(RoverWheels.FL, MotorState.BACKWARD);
		physics.setMotorState(RoverWheels.BL, MotorState.BACKWARD);
		physics.setMotorState(RoverWheels.BR, MotorState.FORWARD);
		physics.setMotorState(RoverWheels.FR, MotorState.FORWARD);
	}
	
	private void driveStop(){
		physics.setMotorState(RoverWheels.FL, MotorState.RELEASE);
		physics.setMotorState(RoverWheels.BL, MotorState.RELEASE);
		physics.setMotorState(RoverWheels.BR, MotorState.RELEASE);
		physics.setMotorState(RoverWheels.FR, MotorState.RELEASE);
	}
	
	private void driveTurnFL(){
		physics.setMotorState(RoverWheels.FL, MotorState.RELEASE);
		physics.setMotorState(RoverWheels.BL, MotorState.RELEASE);
		physics.setMotorState(RoverWheels.BR, MotorState.FORWARD);
		physics.setMotorState(RoverWheels.FR, MotorState.FORWARD);
	}
	
	private void driveTurnFR(){
		physics.setMotorState(RoverWheels.FL, MotorState.FORWARD);
		physics.setMotorState(RoverWheels.BL, MotorState.FORWARD);
		physics.setMotorState(RoverWheels.BR, MotorState.RELEASE);
		physics.setMotorState(RoverWheels.FR, MotorState.RELEASE);
	}
	
	private void driveTurnBL(){
		physics.setMotorState(RoverWheels.FL, MotorState.RELEASE);
		physics.setMotorState(RoverWheels.BL, MotorState.RELEASE);
		physics.setMotorState(RoverWheels.BR, MotorState.BACKWARD);
		physics.setMotorState(RoverWheels.FR, MotorState.BACKWARD);
	}
	
	private void driveTurnBR(){
		physics.setMotorState(RoverWheels.FL, MotorState.BACKWARD);
		physics.setMotorState(RoverWheels.BL, MotorState.BACKWARD);
		physics.setMotorState(RoverWheels.BR, MotorState.RELEASE);
		physics.setMotorState(RoverWheels.FR, MotorState.RELEASE);
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
				delay(1);
				x++;
			}
			addToSerialHistory(mess);
			Globals.writeToLogFile(name+" Serial", "Message Sent: \'" + mess + "\'");
			return true;
		} 
		else {
			addToSerialHistory("Surpressed: " + mess);
			Globals.writeToLogFile(name+" Serial", "Message Surpressed: \'" + mess + "\'");
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
			Globals.writeToLogFile(name+" Serial", "Message Sent: \'" + mess + "\'");
			return true;
		} else {
			addToSerialHistory("Supressed: " + mess);
			Globals.writeToLogFile(name+" Serial", "Message Sent: \'" + mess + "\'");
			return false;
		}
	}

	private void delay(int length) { //put the thread to sleep for a bit
		String newname = Globals.delayThread(Thread.currentThread().getName(), length);
		while (!Globals.getThreadRunPermission(newname)) {}
		Globals.threadDelayComplete(Thread.currentThread().getName());
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
	
//PHYSCIS Related Stuff *****************************************************************************************************************************************************************************************************
	
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
	
	public RoverPhysicsModel getParameters(){
		return physics;
	}
	
	public DecimalPoint getLocation(){
		return physics.getLocation();
	}
	
	public double getDirection(){
		return physics.getDirection();
	}
	
	public double getBatteryVoltage(){
		return physics.getBatteryVoltage();
	}

	public double getBatteryCharge(){
		return physics.getBatteryCharge();
	}
	
	public double getBatterCPCharge(){
		return physics.getBattery_cp_charge();
	}
	
	public double getSOC(){
		return physics.getSOC();
	}
	
	public double getBatteryCurrent(){
		return physics.getBatteryCurrent();
	}

	public double getSpeed(){
		return physics.getSpeed();
	}

	public double getAngularVelocity(){
		return physics.getAngularVelocity();
	}
	
	public double getSlipVelocity(){
		return physics.getSlipVelocity();
	}

	public double getAcceleration(){
		return physics.getAcceleration();
	}

	public double getAngularAcceleration(){
		return physics.getAngularAcceleration();
	}
	
	public double getSlipAcceleration(){
		return physics.getSlip_acceleration();
	}

	public double getWheelSpeed(RoverWheels which){
		return physics.getWheelSpeed()[which.getValue()];
	}
	
	public double getMotorCurrent(RoverWheels which){
		return physics.getMotor_current()[which.getValue()];
	}
	
	public double getMotorVoltage(RoverWheels which){
		return (physics.getMotor_power()[which.getValue()]/255.0)*physics.getBatteryVoltage()*physics.getMotorStates()[which.getValue()];
	}
	
	public double getBatteryTemperature(){
		return physics.getBatteryTemperature();
	}
	
	public double getMotorTemp(RoverWheels which){
		return physics.getMotorTemp(which);
	}
	
}