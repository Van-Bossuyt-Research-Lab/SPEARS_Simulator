package com.csm.rover.simulator.platforms.sub;

import com.csm.rover.simulator.environments.sub.AquaticMap;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.sub.physicsModels.SubDriveCommands;
import com.csm.rover.simulator.platforms.sub.physicsModels.subPhysicsModel;
import com.csm.rover.simulator.platforms.sub.subAuto.SubAutonomousCode;
import com.csm.rover.simulator.wrapper.Globals;
import com.csm.rover.simulator.wrapper.SerialBuffers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Point;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

//TODO actually debug instructions
//TODO make for modular for OCP, SRP
@com.csm.rover.simulator.platforms.annotations.Platform(type="Sub")
public class SubObject extends Platform {
    private static final Logger LOG = LogManager.getLogger(SubObject.class);

    private static final long serialVersionUID = 1L;

    private static AquaticMap MAP;
    private static SerialBuffers serialBuffers;

    private String name;
    private String IDcode;
    private subPhysicsModel physics = new subPhysicsModel();
    private SubAutonomousCode autoCode;

    @SuppressWarnings("unused")
    private boolean connected = false; // Can the ground station hear/talk to us
    private boolean mute = false; // Can we talk at all
    private boolean moving = false; // are we moving
    @SuppressWarnings("unused")
    private String motorState = ""; // how are we moving

    private boolean hasInstructions = false; // has a list of instructions on file
    private String instructions = ""; // the list of instructions
    private int instructsComplete = 0; // how many items on the list have we done
    private long timeOfLastCmd = Globals.getInstance().timeMillis(); // how long since the ground station talked last
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

    @SuppressWarnings("unused")
    private double EOLbattery = -1;
    @SuppressWarnings("unused")
    private double EOLmotor = Double.MAX_VALUE;

    private boolean checkCurrent = false; // are we checking current
    private float currentIntegral = 0; // the sum of all currents checked
    private long lastCurrentCheck = 0; // time of last current check
    private long startCurretIntegral = 0; // time of initial integral check
    @SuppressWarnings("unused")
    private float averageCurrent = 0; // integral divided by time

    private HashSet<Point> visitedScience = new HashSet<Point>();

    private String serialHistory = "";
    private Map<String, Boolean> LEDs = new TreeMap<String, Boolean>();

    public SubObject(){
        super("Sub");
        LEDs.put("Mute", false);
        LEDs.put("Instructions", false);
        LEDs.put("Autonomous", false);
    }

    public static void setSubMap(AquaticMap map){
        SubAutonomousCode.setSubMap(map);
        subPhysicsModel.setSubMap(map);
    }

    public static void setSerialBuffers(SerialBuffers buffers){
        serialBuffers = buffers;
    }

    public void start(){
        new SynchronousThread(100, new Runnable(){
            public void run(){
                //System.out.println(name + "-CODE\t" + Globals.getInstance().timeMillis);
                excecuteCode();
            }
        },
                SynchronousThread.FOREVER, name+"-code");
        physics.start();
        timeOfLastCmd = Globals.getInstance().timeMillis();
    }

    private void excecuteCode() {
        try {
            //Globals.getInstance().writeToLogFile(this.name, Globals.getInstance().timeMillis + "\t" + physics.getLocation().getX() + "\t" + physics.getLocation().getY() + "\t" + Access.getMapHeightatPoint(physics.getLocation()) + "\t" + visitedScience.size()*10 + "\t" + physics.getBatteryCharge());
            try {

                if (serialBuffers.RFAvailable(IDcode) > 1) { // if there is a message
                    delay(500);
                    char[] id = strcat((char) serialBuffers.ReadSerial(IDcode), (char) serialBuffers.ReadSerial(IDcode));
                    if (strcmp(id, IDcode) == 0 && go) { // if the message is for us and are we allowed to read it
                        // go is set to false if the first read is not IDcode
                        // to prevent starting a message not intended for the rover
                        // from within the body of another message
                        serialBuffers.ReadSerial(IDcode); // white space
                        tag = (char) serialBuffers.ReadSerial(IDcode); // get type tag
                        if (serialBuffers.RFAvailable(IDcode) > 0) { // if there is more to the message
                            run_auto = false; // stop running autonomously
                            data[0] = tag; // tag is not actually import, just read the entire body of the message
                            index++;
                            while (serialBuffers.RFAvailable(IDcode) > 0 && index < data.length - 1) { //read in message body
                                data[index] = (char) serialBuffers.ReadSerial(IDcode);
                                index++;
                            }
                            data[index] = '\0'; // end of string
                            // switch through commands
                            if (strcmp(data, "move") == 0) {
                                sendSerial("s1 g %"); // confirm message reciet
                                driveForward();
                                moving = true;
                            } else if (strcmp(data, "stop") == 0) {
                                sendSerial("s1 g %");
                                driveStop();
                                moving = true;
                            } else if (strcmp(data, "spin_ccw") == 0) {
                                sendSerial("s1 g %");
                                driveSpinCCW();
                                moving = true;
                            } else if (strcmp(data, "spin_cw") == 0) {
                                sendSerial("s1 g %");
                                driveSpinCW();
                                moving = true;
                            } else if (strcmp(data, "backward") == 0) {
                                sendSerial("s1 g %");
                                driveBackward();
                                moving = true;
                            } else if (strcmp(data, "turnFL") == 0) {
                                sendSerial("s1 g %");
                                driveTurnFL();
                                moving = true;
                            } else if (strcmp(data, "turnFR") == 0) {
                                sendSerial("s1 g %");
                                driveTurnFR();
                                moving = true;
                            } else if (strcmp(data, "turnBL") == 0) {
                                sendSerial("s1 g %");
                                driveTurnBL();
                                moving = true;
                            } else if (strcmp(data, "turnBR") == 0) {
                                sendSerial("s1 g %");
                                driveTurnBR();
                                moving = true;
                            } else if (strcmp(data, "getvolts") == 0) {
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
                            } else if (strcmp(data, "photo") == 0) {
                                sendSerial("s1 g %");
                                delay(2000);
                                takePicture();
                            } else if (strcmp(data, "instructions") == 0) { // if we are recieving instructions
                                instructions = ""; // clear existing "file"
                                while (serialBuffers.RFAvailable(IDcode) == 0) { // wait for transmission to start
                                    delay(5);
                                }
                                delay(1000); // begin syncopation of read/write
                                while (serialBuffers.RFAvailable(IDcode) > 0) {
                                    while (serialBuffers.RFAvailable(IDcode) > 0) {
                                        instructions += (char) serialBuffers.ReadSerial(IDcode); // read in character, add to list
                                    }
                                    delay(2000); // continue syncopation, we want to avoid the incoming message running out of room in the buffer
                                }
                                sendSerial("s1 g {"); // unmute the ground station
                                delay(1500); // allow unmute to arrive
                                sendSerial("s1 g n Instructions Transfered"); // confirm received of instructions
                                hasInstructions = true; // we now have instructions
                                instructsComplete = 0; // we haven't started them
                                System.out.print(instructions); // show console what they are so I can figure out why they don't work
                            } else if (strcmp(data, "!instruct") == 0) { // if we want to delete existing instructions / abort
                                hasInstructions = false; // we don't have instructions
                                instructions = ""; // delete "file"
                                if (moving) {
                                    driveStop(); // stop the rover
                                }
                                delay(1000);
                                sendSerial("s1 g KillDone"); // confirm abort
                            } else if (strcmp(data, "auto") == 0) { // force into autonomous mode
                                run_auto = true;
                                sendSerial("s1 g %");
                                /*
                            } else if (strcmp(data, "score") == 0) {
                                if (MAP.isPointAtTarget(getLocation())) {
                                    this.visitedScience.add(MAP.getMapSquare(getLocation()));
                                    System.out.println("Aquired.  New Score = " + visitedScience.size());
                                }
                                */
                            }

                        }
                        // if there isn't more to the message interpret the tag
                        else if (tag == '#') { // message confirmation from ground
                            if (waitingForResponse && tag == desiredResponse) {
                                responseRecieved = true;
                            }
                        } else if (tag == '^') { // the ground pinged us
                            connected = true;
                            sendSerial("s1 g ^");
                            delay(2000);
                            pingGround(); // test the connection
                        } else if (tag == '*') { // our ping of the ground station returned
                            if (waitingForResponse && tag == desiredResponse) {
                                responseRecieved = true;
                            }
                        } else if (tag == '}') { // we have been muted
                            mute = true;
                            LEDs.put("Mute", mute);
                        } else if (tag == '{') { // we have been unmuted
                            mute = false;
                            LEDs.put("Mute", mute);
                        }
                        data = new char[data.length]; // reset data array
                        index = 0;
                        tag = '\0';
                        timeOfLastCmd = Globals.getInstance().timeMillis(); // reset time since command
                        if (moving) {
                            cmdWaitTime += Globals.getInstance().timeMillis() + 60000; // reset command wait
                        }
                    } else { // the message wasn't for us
                        go = false; // ignore it
                        int waiting = serialBuffers.RFAvailable(IDcode);
                        while (waiting > 0) { // delete it
                            serialBuffers.ReadSerial(IDcode);
                            waiting--;
                        }
                    }
                } else { // there isn't a message waiting
                    go = true; // allowed to read the next message
                }

                // Listening for response
                if (Globals.getInstance().timeMillis() - timeSinceMessage > responseWaitTime && waitingForResponse) { // if waiting and appropriate time has passed
                    waitingForResponse = false; // we're no longer waiting
                    // switch possible responses
                    if (desiredResponse == '*') {
                        connected = responseRecieved; // are we connected = did they answer
                    } else if (desiredResponse == '%') { // message confirmation
                        if (!responseRecieved) {
                            pingGround(); // if failed, make sure the ground station is still there
                        }
                    }
                }

                // Autonomous and Instruction handling
                if (Globals.getInstance().timeMillis() - timeOfLastCmd > 60000) { // if it has been a minute since we heard from them
                    //System.out.println(this.name);
                    if (hasInstructions && !mute) { // if we have instructions, can send things
                        LEDs.put("Instructions", true);
                        LEDs.put("Autonomus", false);
                        run_auto = false; // don't run autonomously
                        if (!waiting || (Globals.getInstance().timeMillis() > cmdWaitTime)) { // if we're not waiting or have waiting long enough
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
                                while (x < temp.length()) {
                                    cmd += temp.charAt(x);
                                    x++;
                                }
                                System.out.println(cmd);
                                // switch all known commands
                                if (strcmp(cmd, "move") == 0) {
                                    driveForward();
                                    moving = true;
                                    motorState = cmd;
                                } else if (strcmp(cmd, "backward") == 0) {
                                    driveBackward();
                                    moving = true;
                                    motorState = cmd;
                                } else if (strcmp(cmd, "spin_cw") == 0) {
                                    driveSpinCW();
                                    moving = true;
                                    motorState = cmd;
                                } else if (strcmp(cmd, "spin_ccw") == 0) {
                                    driveSpinCCW();
                                    moving = true;
                                    motorState = cmd;
                                } else if (strcmp(cmd, "stop") == 0) {
                                    driveStop();
                                    moving = false;
                                } else if (strcmp(cmd, "turnFL") == 0) {
                                    driveTurnFL();
                                    moving = true;
                                    motorState = cmd;
                                } else if (strcmp(cmd, "turnFR") == 0) {
                                    driveTurnFR();
                                    moving = true;
                                    motorState = cmd;
                                } else if (strcmp(cmd, "turnBL") == 0) {
                                    driveTurnBL();
                                    moving = true;
                                    motorState = cmd;
                                } else if (strcmp(cmd, "turnBR") == 0) {
                                    driveTurnBR();
                                    moving = true;
                                    motorState = cmd;
                                } else if (cmd.equals("photo")) {
                                    takePicture();
                                } else if (strcmp(cmd, "recTemp") == 0) { // record temperature
                                    if (temperatureData.equals("")) { // if there is not an existing "file," create one
                                        temperatureData += "Graph Title,Temperatures," + "Vertical Units,*C," + "Horizontal Units,s," + "Label,\nSys Time,Temperature,";
                                    }
                                    temperatureData += Globals.getInstance().timeMillis() + "," + getTemperature() + ",\n"; // get temperature data and add it to the file
                                } else if (strcmp(cmd, "sendTemp") == 0) { // report temperature
                                    sendSerial("s1 g }"); // mute the ground so they can't interrupt
                                    delay(2000);
                                    sendSerial("s c CSV"); // tell the satellite a file is coming
                                    delay(2000);
                                    int i = 0;
                                    while (i < temperatureData.length()) { // send the file in 60 byte chunks
                                        index = 0;
                                        while (index < 60 && i < temperatureData.length()) {
                                            sendSerial(temperatureData.charAt(i));
                                            index++;
                                            i++;
                                        }
                                        delay(2000); // synocpation
                                    }
                                    temperatureData = ""; // delete existing "file"
                                } else if (strcmp(cmd, "delay1") == 0) { // wait a second
                                    waiting = true;
                                    cmdWaitTime = Globals.getInstance().timeMillis() + 1000;
                                } else if (strcmp(cmd, "report") == 0) { // report completion of instructions to ground
                                    if (sendSerial("s1 g n Rover Instructs Done")) { // if we're not muted
                                        hasInstructions = false; // we no longer have instructions
                                        instructsComplete = 0;
                                    } else { // if we are muted, try to report again later
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
                    } else {
                        run_auto = true;
                    }
                } else {
                    LEDs.put("Autonomus", run_auto);
                    LEDs.put("Instructions", false);
                }

                if (run_auto) {//Follow Autonomous Thought
                    LEDs.put("Autonomus", true);
                    LEDs.put("Instructions", false);

                    String cmd = autoCode.nextCommand(Globals.getInstance().timeMillis(), physicsModel.getState());
                    //TODO switch all known commands
                    if (strcmp(cmd, "") == 0) { /*Do Nothing*/ } else if (strcmp(cmd, "move") == 0) {
                        driveForward();
                        moving = true;
                        motorState = cmd;
                    } else if (strcmp(cmd, "backward") == 0) {
                        driveBackward();
                        moving = true;
                        motorState = cmd;
                    } else if (strcmp(cmd, "spin_cw") == 0) {
                        driveSpinCW();
                        moving = true;
                        motorState = cmd;
                    } else if (strcmp(cmd, "spin_ccw") == 0) {
                        driveSpinCCW();
                        moving = true;
                        motorState = cmd;
                    } else if (strcmp(cmd, "stop") == 0) {
                        driveStop();
                        moving = false;
                    } else if (strcmp(cmd, "turnFL") == 0) {
                        driveTurnFL();
                        moving = true;
                        motorState = cmd;
                    } else if (strcmp(cmd, "turnFR") == 0) {
                        driveTurnFR();
                        moving = true;
                        motorState = cmd;
                    } else if (strcmp(cmd, "turnBL") == 0) {
                        driveTurnBL();
                        moving = true;
                        motorState = cmd;
                    } else if (strcmp(cmd, "turnBR") == 0) {
                        driveTurnBR();
                        moving = true;
                        motorState = cmd;
                    } else if (cmd.equals("photo")) {
                        takePicture();
                    } else if (strcmp(cmd, "recTemp") == 0) { // record temperature
                        if (temperatureData.equals("")) { // if there is not an existing "file," create one
                            temperatureData += "Graph Title,Temperatures," + "Vertical Units,*C," + "Horizontal Units,s," + "Label,\nSys Time,Temperature,";
                        }
                        temperatureData += Globals.getInstance().timeMillis() + "," + getTemperature() + ",\n"; // get temperature data and add it to the file
                    } else if (strcmp(cmd, "sendTemp") == 0) { // report temperature
                        sendSerial("s1 g }"); // mute the ground so they can't interrupt
                        delay(2000);
                        sendSerial("s c CSV"); // tell the satellite a file is coming
                        delay(2000);
                        int i = 0;
                        while (i < temperatureData.length()) { // send the file in 60 byte chunks
                            index = 0;
                            while (index < 60 && i < temperatureData.length()) {
                                sendSerial(temperatureData.charAt(i));
                                index++;
                                i++;
                            }
                            delay(2000); // synocpation
                        }
                        temperatureData = ""; // delete existing "file"
                    } else if (strcmp(cmd.substring(0, 5), "delay") == 0) { // wait a second
                        waiting = true;
                        cmdWaitTime = Globals.getInstance().timeMillis() + Integer.parseInt(cmd.substring(5, cmd.length()));
                    } else if (strcmp(cmd.substring(0, 7), "chngmtr") == 0) { //to change motor speed "chngmtr*###" where *=motorID and ###=new power
                        int motor = cmd.charAt(7);
                        int power = Integer.parseInt(cmd.substring(8, cmd.length()));
                        if (power < 0) {
                            power = 0;
                        } else if (power > 255) {
                            power = 255;
                        }
                        if (motor >= 0 && motor < 4) {
                            SubProp prop;
                            switch (motor) {
                                case 0:
                                    prop = SubProp.L;
                                    break;
                                case 1:
                                    prop = SubProp.R;
                                    break;
                                case 2:
                                    prop = SubProp.F;
                                    break;
                                case 3:
                                default:
                                    prop = SubProp.B;
                                    break;
                            }
                            physicsModel.sendDriveCommand(SubDriveCommands.CHANGE_MOTOR_PWR.getCmd(), prop.getValue(), power);
                        }
                    }

				/*if (Globals.getInstance().timeMillis >= autoWaitUntil){
					driveSpinCCW();
					// EOL Calculation
					setEOL(resistanceP*capacitanceB*ln(motorVoltage/(motorCurrent*(resistanceP+resistanceCP+resistanceS))), "battery");
					Globals.getInstance().writeToLogFile("PHM Calc", "Time Elapsed:\t" + (Globals.getInstance().timeMillis-this.startCurretIntegral) + "\tEOL:\t" + getEndOfLife("battery") + "\tMotor Voltage:\t" + motorVoltage + "\tMotor Current:\t" + motorCurrent);
					autoWaitUntil = Globals.getInstance().timeMillis + 600000;
				}
				else {
					//System.out.println(Globals.getInstance().timeMillis + " < " + autoWaitUntil);
				}

				if (Globals.getInstance().timeMillis - this.lastLogWrite > 10*60*1000){
					Globals.getInstance().writeToLogFile("Autonomus", "Battery Voltage = " + getBatteryVoltage() + " V\t\tSOC = " + getSOC() + "%");
					lastLogWrite = Globals.getInstance().timeMillis;
				}

				if (Access.isAtTarget(location) && !atTarget){
					atTarget = true;
					driveStop();
					takePicture();
					timeOfLastCmd = Globals.getInstance().timeMillis;
					autoWaitUntil = Globals.getInstance().timeMillis + 1000;

				}
				else {
					atTarget = false;
				}
				if (Globals.getInstance().timeMillis >= autoWaitUntil){
					actionCounter++;
					int step = actionCounter % 2;
					if (step == 0){
						driveTurnFR();
						autoWaitUntil = Globals.getInstance().timeMillis + 2000;
					}
					else if (step == 1){
						driveForward();
						autoWaitUntil = Globals.getInstance().timeMillis + 4*1000;
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
                System.out.println("Error in Sub Run Code");
                //Globals.getInstance().reportError("SubCode", "runCode - code", e);
            }
        } catch (Exception f){
            System.out.println("Something's bad");
           // Globals.getInstance().reportError("SubCode", "runCode - code", f);
        }
    }

    private void takePicture() { // take a picture
        try {
            sendSerial("s1 g }"); // mute the ground so it can't interrupt
            InputStream data = SubObject.class.getResourceAsStream(getSampleImageName()); // get the buffer from the "camera"
            delay(2000);
            sendSerial("s c [o]"); // tell the satellite a file is coming
            delay(2000);
            int hold = data.read();
            int index;
            while (hold != -1) { // send the file in 60 byte chunks
                index = 0;
                while (index < 60 && hold != -1) {
                    serialBuffers.writeToSerial((byte) hold, IDcode);
                    hold = data.read();
                    index++;
                }
                delay(2000);
            }
        }
        catch (Exception e){
           // Globals.getInstance().reportError("RoverCode", "takePicture", e);
        }
    }

    private String getSampleImageName() {
        Random rnd = new Random();
        return String.format("/%s/%s %d.%s", "images", "Rover Photo", rnd.nextInt(10));
    }

    private void pingGround(){ // ping the ground station and listen for a response
        sendSerial("s1 g *");
        setWaitForResponse('*', 3000);
    }

    // how to move the motors for driving
    private void driveForward(){
        physicsModel.sendDriveCommand(SubDriveCommands.DRIVE_FORWARD.getCmd());
    }

    private void driveBackward(){
        physicsModel.sendDriveCommand(SubDriveCommands.DRIVE_BACKWARD.getCmd());
    }

    private void driveSpinCW(){
        physicsModel.sendDriveCommand(SubDriveCommands.SPIN_CW.getCmd());
    }

    private void driveSpinCCW(){
        physicsModel.sendDriveCommand(SubDriveCommands.SPIN_CCW.getCmd());
    }

    private void driveStop(){
        physicsModel.sendDriveCommand(SubDriveCommands.STOP.getCmd());
    }

    private void driveTurnFL(){
        physicsModel.sendDriveCommand(SubDriveCommands.TURN_FRONT_LEFT.getCmd());
    }

    private void driveTurnFR(){
        physicsModel.sendDriveCommand(SubDriveCommands.TURN_FRONT_RIGHT.getCmd());
    }

    private void driveTurnBL(){
        physicsModel.sendDriveCommand(SubDriveCommands.TURN_BACK_LEFT.getCmd());
    }

    private void driveTurnBR(){
        physicsModel.sendDriveCommand(SubDriveCommands.TURN_BACK_RIGHT.getCmd());
    }

    // set up waiting for a response
    private void setWaitForResponse(char listen, long timeToWait){
        timeSinceMessage = Globals.getInstance().timeMillis(); // when was the out message sent
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
                serialBuffers.writeToSerial(message[x], IDcode); // Write to Serial one char at a time
                delay(1);
                x++;
            }
            addToSerialHistory(mess);
            //Globals.getInstance().writeToLogFile(name + " Serial", "Message Sent: \'" + mess + "\'");
            return true;
        }
        else {
            addToSerialHistory("Surpressed: " + mess);
            //Globals.getInstance().writeToLogFile(name + " Serial", "Message Surpressed: \'" + mess + "\'");
            return false;
        }
    }

    private boolean sendSerial(float val) {
        return sendSerial(val + "");
    }

    private boolean sendSerial(char mess) {
        if (!mute) {
            serialBuffers.writeToSerial(mess, IDcode);
            addToSerialHistory(mess + "");
            //Globals.getInstance().writeToLogFile(name + " Serial", "Message Sent: \'" + mess + "\'");
            return true;
        } else {
            addToSerialHistory("Supressed: " + mess);
            //Globals.getInstance().writeToLogFile(name + " Serial", "Message Sent: \'" + mess + "\'");
            return false;
        }
    }

    private void delay(int length) { //put the thread to sleep for a bit
        Globals.getInstance().delayThread(Thread.currentThread().getName(), length);
    }

    private double getTemperature(){ // read temperature from the "sensor"
        return 0;
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
            //Globals.getInstance().reportError("RoverCode", "strcmp", e);
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
        serialHistory += out + "\t\t\t" + Globals.getInstance().timeMillis() + "\n";
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


}
