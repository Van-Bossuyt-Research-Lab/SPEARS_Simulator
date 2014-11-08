package wrapper;

import java.util.Random;

import objects.ThreadTimer;

public class Globals {

	public static String vrsionNumber = "2.0";
	
	private static byte[] SatelliteRFserial = new byte[0]; // the buffer for messages received by the Satellite
	private static byte[] RoverRFserial = new byte[0]; // the buffer for the messages received by the Rover
	private static byte[] GroundRFserial = new byte[0]; // the buffer for the Ground Station
	
	private static double timeScale = 1.0;
	public static long TimeMillis = 0;
	private static ThreadTimer milliTimer;
	
	private static Random rnd = new Random();
	
	public static void startTime(){
		milliTimer = new ThreadTimer(0, new Runnable(){
			public void run(){
				long end = System.nanoTime() + (long)(1000000 / getTimeScale());
				while (true){
					while (System.nanoTime() < end) {}
					TimeMillis++;
					end += (long)(1000000 / getTimeScale());
				}
			}
		}, 1);
	}
	
	public static void writeToSerial(char write, char from){
		writeToSerial((byte)write, from);
	}
	
	public static void writeToSerial(byte write, char from){ // writes the character to the other 2 buffers
		if (from == 'r'){
			writeToLogFile("Rover Comm", "Sent: " + (char)write);
			if (SatelliteRFserial.length < 64){ // Serial buffer is only 64 bytes
				SatelliteRFserial = Augment(SatelliteRFserial, write);
			}
			else {
				writeToLogFile("Satellite Comm", "Failed to recieve: " + (char)write + ", full buffer.");
			}
			if (GroundRFserial.length < 64){
				GroundRFserial = Augment(GroundRFserial, write);
			}
			else {
				writeToLogFile("Ground Comm", "Failed to recieve: " + (char)write + ", full buffer.");
			}
		}
		else if (from == 's'){
			writeToLogFile("Satellite Comm", "Sent: " + (char)write);
			if (RoverRFserial.length < 64){
				RoverRFserial = Augment(RoverRFserial, write);
			}
			else {
				writeToLogFile("Rover Comm", "Failed to recieve: " + (char)write + ", full buffer.");
			}
			if (GroundRFserial.length < 64){
				GroundRFserial = Augment(GroundRFserial, write);
			}
			else {
				writeToLogFile("Ground Comm", "Failed to recieve: " + (char)write + ", full buffer.");
			}
		}
		else if (from == 'g'){
			writeToLogFile("Ground Comm", "Sent: " + (char)write);
			if (SatelliteRFserial.length < 64){
				SatelliteRFserial = Augment(SatelliteRFserial, write);
			}
			else {
				writeToLogFile("Satellite Comm", "Failed to recieve: " + (char)write + ", full buffer.");
			}
			if (RoverRFserial.length < 64){
				RoverRFserial = Augment(RoverRFserial, write);
			}
			else {
				writeToLogFile("Rover Comm", "Failed to recieve: " + (char)write + ", full buffer.");
			}
		}
		//WrapperEvents.SerialBuffersChanged(RoverRFserial, SatelliteRFserial, GroundRFserial);
	}
	
	public static int RFAvailable(char which){ // Returns the number of chars waiting
		if (which == 'r'){
			return RoverRFserial.length;
		}
		else if (which == 's'){
			return SatelliteRFserial.length;
		}
		else if (which == 'g'){
			return GroundRFserial.length;
		}
		return -1;
	}
	
	public static byte ReadSerial(char which){ // Returns the first waiting character
		byte out = '\0';
		if (RFAvailable(which) > 0){
			if (which == 'r'){
				out = RoverRFserial[0];
				RoverRFserial = dropFirst(RoverRFserial);
			}
			else if (which == 's'){
				out = SatelliteRFserial[0];
				SatelliteRFserial = dropFirst(SatelliteRFserial);
			}
			else if (which == 'g'){
				out = GroundRFserial[0];
				GroundRFserial = dropFirst(GroundRFserial);
			}
		}
		//WrapperEvents.SerialBuffersChanged(RoverRFserial, SatelliteRFserial, GroundRFserial);
		return out;
	}
	
	public static byte PeekSerial(char which){  // get first waiting character without changing availability
		byte out = '\0';
		if (RFAvailable(which) > 0){
			if (which == 'r'){
				out = RoverRFserial[0];
			}
			else if (which == 's'){
				out = SatelliteRFserial[0];
			}
			else if (which == 'g'){
				out = GroundRFserial[0];
			}
		}
		return out;
	}
	
	private static byte[] Augment(byte[] array, byte val){ // expand the array by one
		byte[] out = new byte[array.length + 1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private static byte[] dropFirst(byte[] val){ // remove the first term of an array
		byte[] out = new byte[val.length - 1];
		int x = 0;
		while (x < out.length){
			out[x] = val[x + 1];
			x++;
		}
		return out;
	}
	
	public static double getTimeScale(){
		return timeScale;
	}
	
	public static void setTimeScale(double time){ // changes the multiplier for time stepping (shows up in the delay funcions)
		timeScale = time;
	}
	
	public static void reportError(String obj, String method, Exception error){
		writeToLogFile("ERROR - " + obj + ": " + method, error.toString());
		error.printStackTrace();
	}
	
	public static void writeToLogFile(String from, String write){ // writes the message to the log genorated by the interface
		//InterfaceEvents.addNoteToLog(from, write);
	}
	
	public static double addErrorToMeasurement(double measurement, double percentError){ // takes in a measurement an adds error by the given percent
		double deviation = rnd.nextDouble()*(measurement*percentError);
		if (rnd.nextBoolean()){
			measurement += deviation;
		}
		else {
			measurement -= deviation;
		}
		return Math.round(measurement*1000)/1000;
	}
	
	public static double addErrorToMeasurement(double measurement, double lowDeviation, double highDeviation){ // takes in a measurement and varies it within measure-lowDev to measure+highDev
		double deviation = rnd.nextDouble()*(lowDeviation + highDeviation) - lowDeviation;
		return Math.round((measurement + deviation)*1000)/1000;
	}
	
}
