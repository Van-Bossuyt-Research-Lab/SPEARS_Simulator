package com.csm.rover.simulator.platforms.satellite;

import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.wrapper.Globals;
import com.csm.rover.simulator.wrapper.SerialBuffers;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

@SuppressWarnings("unused")
@com.csm.rover.simulator.platforms.annotations.Platform(type="Satellite")
public class SatelliteObject extends Platform implements Serializable {
	private static final Logger LOG = LogManager.getLogger(SatelliteObject.class);

	private static final long serialVersionUID = 1L;
	
    private static SerialBuffers serialBuffers;
    
	private String name;
	private String IDcode;
	private SatelliteParametersList params;
	private SatelliteAutonomusCode autoCode;
	
	private double time_step = 0.01; // time step of physics, in seconds
	
	private String instructions = ""; // the "file" of instructions
	private boolean hasInstructions = false; // do we have instructions
	private int instructsComplete = 0; // how many instructions have we done
	private long timeSinceCmd = 0; // how long since we heard from anybody

	// artifact picture stuff
	private int imageSize;
	private File imgFile;
	private File myFile;
	private char[] filename = new char[13];
	
	private char[] data = new char[30]; // message data
	private int index = 2;
	private char[] tag = new char[2];
	
	private String serialHistory = "";
	private Map<String, Boolean> LEDs = new TreeMap<String, Boolean>();
	
	public SatelliteObject(String name, String id, SatelliteParametersList params, SatelliteAutonomusCode code, double altitue, double orbitIncline, double theta){
		super("Satellite");
        this.name = name;
		IDcode = id;
		this.params = params;
		autoCode = code;
	}

    public static void setSerialBuffers(SerialBuffers buffers){
        serialBuffers = buffers;
    }
	
	public void start(){
		new SynchronousThread(100, new Runnable(){
			public void run(){
				excecuteCode();
			}
		},
		SynchronousThread.FOREVER, name+"-code");
		//TODO Satellite Physics
		//new SynchronousThread((int) (time_step*1000), new Runnable(){
		//	public void run(){
		//		excecutePhysics();
		//	}
		//},
		//SynchronousThread.FOREVER, name+"-physics");
	}
	
	public String getName(){
		return name;
	}
	
	public String getIDCode(){
		return IDcode;
	}
	
	public void excecuteCode(){
		try {
			if (serialBuffers.RFAvailable(IDcode) > 1) { // if there is a message
				delay(500);
				char[] id = strcat((char)serialBuffers.ReadSerial(IDcode), (char)serialBuffers.ReadSerial(IDcode));
				if (strcmp(id, IDcode) == 0) { // if the message is for us and are we allowed to read it
					delay(500);
					serialBuffers.ReadSerial(IDcode); // white space
					tag[0] = (char) serialBuffers.ReadSerial(IDcode); // message platform_type tag
					if (tag[0] == 'r'){
						tag[1] = (char) serialBuffers.ReadSerial(IDcode); //number of rover
						index = 3;
					}
					else {
						tag[1] = '\0';
						index = 2;
					}
					serialBuffers.ReadSerial(IDcode); // white space
					data[index-1] = ' '; // fills in for message forward
					while (serialBuffers.RFAvailable(IDcode) > 0){
						data[index] = (char) serialBuffers.ReadSerial(IDcode); // read message body
						index++;
					}
					data[index] = '\0'; // end string
					if (tag[0] == 'g'){ // forward to ground
						data[0] = 'g';
						sendSerial(data);
						delay(1000);
						if (data[2] != '^' && data[2] != '*' && data[2] != '%' && data[2] != '}' && data[2] != '{'){ // don't confirm tags
							sendSerial("r #"); // confirm
						}
					}
					if (tag[0] == 'r'){ // forward to rover
						data[0] = 'r';
						data[1] = tag[1];
						sendSerial(data);
						delay(1000);
						if (data[3] != '^' && data[3] != '*' && data[3] != '%' && data[3] != '}' && data[3] != '{'){ // don't confirm tags
							sendSerial("g #"); // confirm
						}
					}
					if (tag[0] == 'c'){ // command for the satellite
						index = 0;
						while (index < data.length - 2){ // remove first to characters to isolate message body
							data[index] = data[index + 2];
							index++;
						}
						// switch commands
						if (strcmp(data, "photo") == 0){
							sendSerial("g #");
							delay(600);
							takePhoto();
						}
						else if (strcmp(data, "[o]") == 0){ // receive and forward image
							byte[] data = new byte[0]; // output data
							while (serialBuffers.RFAvailable(IDcode) == 0) { // wait for transmission to start
								delay(5);
							}
							delay(1000); // syncopate
							int index = 0;
							while (serialBuffers.RFAvailable(IDcode) > 0) { // read data in 60 byte chunks
								while (serialBuffers.RFAvailable(IDcode) > 0) {
									data = Augment(data, serialBuffers.ReadSerial(IDcode));
									index++;
								}
								delay(2000); // continue syncapation for smooth data transmission
							}
							sendSerial("r }"); // mute rover
							delay(1200);
							sendSerial("g {"); // unmute ground station
							delay(1000);
							sendSerial("g i " + index); // tell ground station to receive image
							delay(1000);
							index = 0;
							int x = 0;
							while (x < data.length) {
								index = 0;
								while ((index < 60) && x < data.length) { // send data in 60 byte chunks
									serialBuffers.writeToSerial(data[x], IDcode);
									index++;
									x++;
								}
								delay(1000); // still syncopated but faster
							}
							sendSerial("r {");
						}
						else if (strcmp(data, "CSV") == 0){ // recieve and forward data file, works same as picture
							String data = "";
							index = 0;
						    char buffer;
						    while (serialBuffers.RFAvailable(IDcode) == 0) {}
							delay(1000);
							while (serialBuffers.RFAvailable(IDcode) > 0){
								while (serialBuffers.RFAvailable(IDcode) > 0){
								    buffer = (char)serialBuffers.ReadSerial(IDcode);
								    data += buffer;
								    index++;
							    }
							    delay(2000);
						    }
						        
						    sendSerial("r }");
						    delay(1200);
						    sendSerial("g {");
						    delay(1000);
						    sendSerial("g d " + index);
						    delay(1000);
						    index = 0;
						    int i = 0;
						    while (i < data.length()) {
							    index = 0;
							    while ((index < 60) && (i < data.length())){
								    sendSerial(data.charAt(i));
								    index++;
								    i++;
							    }
							    delay(1000);
						    }
						    delay(300);
						    sendSerial("r {");
					    }
						else if (strcmp(data, "instructions") == 0){ // is recieving instructions
							instructions = ""; // create new "file"
							while (serialBuffers.RFAvailable(IDcode) == 0) { // wait for transmission to start
								delay(5);
							}
							delay(1000); // syncopate
							while (serialBuffers.RFAvailable(IDcode) > 0){
								while (serialBuffers.RFAvailable(IDcode) > 0){
									instructions += (char)serialBuffers.ReadSerial(IDcode); // read in byte at a time, add to file
									try {
										Thread.sleep(1);
									} catch (Exception e) {
										LOG.log(Level.ERROR, "Satellite {} failed to sleep", name);
									}
								}
								delay(2000);
							}
							hasInstructions = true; // has instructions now
							instructsComplete = 0; // we haven't run any instructions
							sendSerial("g }"); // mute ground station
							delay(700);
							sendSerial("r {"); // unmute rover
							delay(700);
							sendSerial("r instructions"); // tell rover instructions are coming
							delay(1000);
							int x = 0;
							while (x < instructions.length()) {
								index = 0;
								while ((index < 60) && (x < instructions.length())){
									serialBuffers.writeToSerial(instructions.charAt(x), IDcode); // send instructions to rover 60 byte chunks
									index++;
									x++;
								}
								delay(2000);
							}
						}
						else if (strcmp(data, "!instruct") == 0){ // clear instructions abort
							hasInstructions = false; // no longer have instructions
							instructions = ""; // delete "file"
							delay(600);
							sendSerial("r !instruct"); // tell rover to cancel/abort
						}
						// 	...
					}
					data = new char[30]; // reset data
					index = 2;
					tag = new char[2];
					timeSinceCmd = System.currentTimeMillis(); // reset time since command
				}
			}
			
			if (System.currentTimeMillis() - timeSinceCmd > (60000 / Globals.getInstance().getTimeScale()) && hasInstructions){ // if it has been a minute, and we have instructions
				// run instructions
				String cmd = ""; // command
				int x = 0;
				int count = 0;
				while (count <= instructsComplete){ // sift through completed instructions
					cmd = ""; // clear instruction
					while (instructions.charAt(x) != '\n'){
						cmd += instructions.charAt(x);
						x++;
					}
					x++;
					count++;
				}
				if (cmd.charAt(0) == 's'){ // if command is for the satellite
					String temp = cmd;
					cmd = "";
					x = 2;
					while (x < temp.length()){ // remove first ot characters
						cmd += temp.charAt(x);
						x++;
					}
					
					// switch commands
					if (cmd.equals("photo")){
						takePhoto();
					}
					else if (cmd.equals("report")){
						if (sendSerial("g n Sat Instructs Done")) { // if not muted
							hasInstructions = false; // we finished the instructions
							instructsComplete = 0;
						}
						else {
							instructsComplete--; // if send failed, back up and try again
						}
					}
				}
				instructsComplete++;
			}
		}
		catch (Exception e){
			LOG.log(Level.ERROR, "Error in Satellite Run Code: " + name, e);
		}
	}
		
	void takePhoto(){
	  try {
		  sendSerial("r }"); // mute ground
		  InputStream data = SatelliteObject.class.getResourceAsStream(getSampleImageName()); // "get data from camera"
		  delay(2000);
		  sendSerial("g i 12764"); //magic length
		  delay(2000);
		  int hold = data.read();
		  int index;
		  while (hold != -1) { // while there still is data
			  index = 0;
			  while (index < 60 && hold != -1) {
				  serialBuffers.writeToSerial((byte) hold, IDcode); // send data in 60 byte chunks
				  hold = data.read();
				  index++;
			  }  
			  delay(1000);
		  }
		  delay(500);
		  sendSerial("r {"); // unmute ground
		}
		catch (Exception e){
			LOG.log(Level.ERROR, String.format("Error occurred for satellite %s taking a picture", name), e);
		}
	}
	
	private String getSampleImageName() {
		Random rnd = new Random();
		return String.format("/%s/%s %d.%s", "images", "Satellite Photo", rnd.nextInt(7));
	}

	private void delay(int length) { // sleep thread for a bit
		String newname = Globals.getInstance().delayThread(Thread.currentThread().getName(), length);
		while (!Globals.getInstance().getThreadRunPermission(newname)) {}
		Globals.getInstance().threadDelayComplete(Thread.currentThread().getName());
	}

	boolean sendSerial(String mess){
		return sendSerial(mess.toCharArray());
	}

	boolean sendSerial(char[] message){ // write message to buffer
		String print = "";
		int x = 0;
		while (x < message.length){
			if (message[x] == '\0'){ // while not end of string
				break;
			}
			serialBuffers.writeToSerial(message[x], IDcode); // Write to Serial one char at a time
			print += message[x];
			delay(1);
			x++;
		}
		addToSerialHistory(print);
		LOG.log(Level.INFO, "Satellite {} sent serial message \"{}\"", name, message);
		return true;
	}

	boolean sendSerial(char mess){
		serialBuffers.writeToSerial(mess, IDcode);
		addToSerialHistory(mess + "");
		LOG.log(Level.INFO, "Satellite {} sent serial message \'{}\'", name, mess);
		return true;
	}
	
	private int strcmp(char[] first, String second){ // compare to strings/char[]
		try {
			char[] sec = second.toCharArray();
			int x = 0;
			while (first[x] != '\0'){
				if (first[x] != sec[x]){
					return 1;
				}
				x++;
			}
			return 0;
		}
		catch (Exception e){
			LOG.log(Level.ERROR, "strcmp error", e);
			return 1;
		}
	}
	
	private byte[] Augment(byte[] array, byte val){ // expand array by 1
		byte[] out = new byte[array.length + 1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private char[] strcat(char first, char second){
		return new char[] { first, second, '\0' };
	}
	
	public void addToSerialHistory(String out){
		serialHistory += out + "\t\t\t" + Globals.getInstance().timeMillis + "\n";
	}
	
	public String getSerialHistory(){
		return serialHistory;
	}	
	
	private void excecutePhysics(){
		//TODO don't fall out of the sky
	}
}
