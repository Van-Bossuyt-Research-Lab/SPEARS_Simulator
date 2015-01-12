package satellite;

import java.io.File;
import java.io.InputStream;

import wrapper.Globals;
import objects.Map;
import objects.ThreadTimer;

public class SatelliteObject {

	private String name;
	private String IDcode;
	private SatelliteParametersList params;
	private SatelliteAutonomusCode autoCode;
	
	private double time_step = 0.01; // time step of physics, in seconds
	
	private String instructions = ""; // the "file" of instructions
	private boolean hasInstructions = false; // do we have instructions
	private int instructsComplete = 0; // how many instructions have we done
	private long timeSinceCmd = 0; // how lond since we heard from anybody

	// artifact picture stuff
	private int imageSize;
	private File imgFile;
	private File myFile;
	private char[] filename = new char[13];
	
	private char[] data = new char[30]; // message data
	private int index = 2;
	private char[] tag = new char[2];
	
	private String serialHistory = "";
	private Map<String, Boolean> LEDs = new Map<String, Boolean>();
	
	public SatelliteObject(String name, String id, SatelliteParametersList params, SatelliteAutonomusCode code, double altitue, double orbitIncline, double theta){
		this.name = name;
		IDcode = id;
		this.params = params;
		autoCode = code;
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
	
	public String getName(){
		return name;
	}
	
	public String getIDCode(){
		return IDcode;
	}
	
	public void excecuteCode(){
		try {
			if (Globals.RFAvailable(IDcode) > 0){ // if there's a message
				delay(500);
				char[] id = strcat((char)Globals.ReadSerial(IDcode), (char)Globals.ReadSerial(IDcode));
				if (strcmp(id, IDcode) == 0){ // if the message is for us
					delay(500);
					Globals.ReadSerial(IDcode); // white space
					tag[0] = (char) Globals.ReadSerial(IDcode); // message type tag
					if (tag[0] == 'r'){
						tag[1] = (char) Globals.ReadSerial(IDcode); //number of rover
						index = 3;
					}
					else {
						tag[1] = '\0';
						index = 2;
					}
					Globals.ReadSerial(IDcode); // white space
					data[index-1] = ' '; // fills in for message forward
					while (Globals.RFAvailable(IDcode) > 0){
						data[index] = (char) Globals.ReadSerial(IDcode); // read message body
						index++;
					}
					data[index] = '\0'; // end string
					// switch tags
					if (tag[0] == 'g'){ // from ground station - relay to rover
						data[0] = 'g';
						sendSerial(data);
						delay(1000);
						if (data[2] != '^' && data[2] != '*' && data[2] != '}' && data[2] != '{'){ // don't confirm tags
							sendSerial("g #"); // confirm
						}
					}
					if (tag[0] == 'r'){ // from rover - relay to ground station
						data[0] = 'r';
						data[1] = tag[1];
						sendSerial(data);
						delay(1000);
						if (data[2] != '^' && data[2] != '*' && data[2] != '%' && data[2] != '}' && data[2] != '{'){ // don't confirm tags
							sendSerial("r #"); // confirm
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
							while (Globals.RFAvailable(IDcode) == 0) { // wait for transmission to start
								delay(5);
							}
							delay(1000); // syncopate
							int index = 0;
							while (Globals.RFAvailable(IDcode) > 0) { // read data in 60 byte chunks
								while (Globals.RFAvailable(IDcode) > 0) {
									data = Augment(data, Globals.ReadSerial(IDcode));
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
									Globals.writeToSerial(data[x], IDcode);
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
						    while (Globals.RFAvailable(IDcode) == 0) {}
							delay(1000);
							while (Globals.RFAvailable(IDcode) > 0){
								while (Globals.RFAvailable(IDcode) > 0){
								    buffer = (char)Globals.ReadSerial(IDcode);
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
							while (Globals.RFAvailable(IDcode) == 0) { // wait for transmission to start
								delay(5);
							}
							delay(1000); // syncopate
							while (Globals.RFAvailable(IDcode) > 0){
								while (Globals.RFAvailable(IDcode) > 0){
									instructions += (char)Globals.ReadSerial(IDcode); // read in byte at a time, add to file
									try {
										Thread.sleep(1);
									} catch (Exception e) {
										Globals.reportError("SatelliteCode", "runCode - Instructions", e);
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
									Globals.writeToSerial(instructions.charAt(x), IDcode); // send instructions to rover 60 byte chunks
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
			
			if (System.currentTimeMillis() - timeSinceCmd > (60000 / Globals.getTimeScale()) && hasInstructions){ // if it has been a minute, and we have instructions
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
			System.out.println("Error in Satellite Code");
			Globals.writeToLogFile("Satellite Run Code", e.toString());
		}
	}
		
	void takePhoto(){
	  try {
		  sendSerial("r }"); // mute ground
		  InputStream data = SatelliteObject.class.getResourceAsStream("/Satellite Sample.jpg"); // "get data from camera"
		  delay(2000);
		  sendSerial("g i 12764"); //magic length
		  delay(2000);
		  int hold = data.read();
		  int index;
		  int x = 0;
		  while (hold != -1) { // while there still is data
			  index = 0;
			  while (index < 60 && hold != -1) {
				  Globals.writeToSerial((byte) hold, IDcode); // send data in 60 byte chunks
				  hold = data.read();
				  index++;
				  x++;
			  }  
			  delay(1000);
		  }
		  delay(500);
		  sendSerial("r {"); // unmute ground
		}
		catch (Exception e){
			Globals.writeToLogFile("Satellite takePicture()", e.toString());
		}
	}
	
	private void delay(double length) { // sleep thread for a bit
		try{
			Thread.sleep((long)(length/Globals.getTimeScale()), (int)((length/Globals.getTimeScale()-(int)length/Globals.getTimeScale())*1000000));
		} catch (Exception e) {
			Globals.reportError("SatelliteCode", "delay", e);
		}
		//long start = System.nanoTime();
		//while (((System.nanoTime()-start) < (length*1000000))) {}
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
			Globals.writeToSerial(message[x], IDcode); // Write to Serial one char at a time
			print += message[x];
			try {
				Thread.sleep((int)(5 / Globals.getTimeScale())); // Pause for sending
			} catch (InterruptedException e) {
				Globals.reportError("SatelliteCode", "sendSerial", e);
			}
			x++;
		}
		addToSerialHistory(print);
		return true;
	}

	boolean sendSerial(char mess){
		Globals.writeToSerial(mess, IDcode);
		addToSerialHistory(mess + "");
		return true;
	}
	
	private int strcmp(char[] first, String second){ // compare to strings/char[]
		try {
			char[] sec = second.toCharArray();
			int count = 0;
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
			Globals.reportError("SatelliteCode", "strcmp", e);
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
	
	public void addToSerialHistory(String out){
		serialHistory += out + "\t\t\t" + Globals.TimeMillis + "\n";
	}
	
	public String getSerialHistory(){
		return serialHistory;
	}
	
	
	private void excecutePhysics(){
		//TODO don't fall out of the sky
	}
}
