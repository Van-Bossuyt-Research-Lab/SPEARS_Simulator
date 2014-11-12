package wrapper;

import java.util.Random;

import objects.Queue;
import objects.Map;
import objects.ThreadTimer;

public class Globals {

	public static String vrsionNumber = "2.0";
	
	private static Queue<Byte>[] SerialBuffers; // the buffer for messages
	private static String[] SerialBufferCodes;
	
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
	
	public static void writeToSerial(char write, String from){
		writeToSerial((byte)write, from);
	}
	
	@SuppressWarnings("unchecked")
	public static void initalizeLists(String[] IDs){
		SerialBufferCodes = IDs;
		SerialBuffers = (Queue<Byte>[])(new Object[0]);
	}
	
	public static void writeToSerial(byte write, String from){ // writes the character to the other 2 buffers
		int x = 0;
		while (x < SerialBufferCodes.length){
			if (!SerialBufferCodes[x].equals(from)){
				if (SerialBuffers[x].size() < 64){
					SerialBuffers[x].push(write);
				}
				else {
					writeToLogFile(SerialBufferCodes[x], "Failed to recieve: " + (char)write + ", full buffer.");
				}
			}
			x++;
		}
		//WrapperEvents.SerialBuffersChanged(RoverRFserial, SatelliteRFserial, GroundRFserial);
	}
	
	public static int RFAvailable(String which){ // Returns the number of chars waiting
		int x = 0;
		while (x < SerialBufferCodes.length){
			if (SerialBufferCodes[x].equals(which)){
				return SerialBuffers[x].size();
			}
			x++;
		}
		return -1;
	}
	
	public static byte ReadSerial(String which){ // Returns the first waiting character
		byte out = '\0';
		int x = 0;
		while (x < SerialBufferCodes.length){
			if (SerialBufferCodes[x].equals(which)){
				out = SerialBuffers[x].pop();
				break;
			}
			x++;
		}
		//WrapperEvents.SerialBuffersChanged(RoverRFserial, SatelliteRFserial, GroundRFserial);
		return out;
	}
	
	public static byte PeekSerial(String which){  // get first waiting character without changing availability
		byte out = '\0';
		int x = 0;
		while (x < SerialBufferCodes.length){
			if (SerialBufferCodes[x].equals(which)){
				out = SerialBuffers[x].peek();
				break;
			}
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
