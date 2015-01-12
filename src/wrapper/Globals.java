package wrapper;

import java.util.Random;

import objects.List;
import objects.Map;
import objects.Pair;
import objects.Queue;
import objects.ThreadItem;
import objects.ThreadTimer;

public class Globals {

	public static String vrsionNumber = "2.0";
	
	private static Queue<Byte>[] SerialBuffers; // the buffer for messages
	private static String[] SerialBufferCodes;
	
	private static double timeScale = 1.0;
	public static long TimeMillis = 0;
	private static ThreadTimer milliTimer;
	
	private static Random rnd = new Random();
	private static int access_key = "qwert".hashCode();
	
	private static boolean begun = false;
	private static Map<String, ThreadItem> threads = new Map<String, ThreadItem>();
	private static long lastSystemNanoTime; 
	private static boolean ready = false;
	
	public static void startTime(){
		lastSystemNanoTime = System.nanoTime();
		begun = true;
		milliTimer = new ThreadTimer(1, new Runnable(){
			public void run(){
				long end = System.nanoTime() + (long)(1000000 / getTimeScale());
				while (System.nanoTime() < end) {}
			}
		}, ThreadTimer.FOREVER, "milli-clock");
		TimeMillis++;
		String[] keys = threads.getKeys();
		int x = 0;
		while (x < keys.length){
			System.out.println(keys[x]);
			if (keys[x].equals(null)){
				break;
			}
			threads.get(keys[x]).reset();
			if (threads.get(keys[x]).getNext() == TimeMillis){
				threads.get(keys[x]).grantPermission();
			}
			x++;
		}
		System.exit(0);
		ready = true;
	}
	
	public static void writeToSerial(char write, String from){
		writeToSerial((byte)write, from);
	}
	
	@SuppressWarnings("unchecked")
	public static void initalizeLists(String[] IDs){
		SerialBufferCodes = IDs;
		SerialBuffers = new Queue[IDs.length];
		int x = 0;
		while (x < IDs.length){
			SerialBuffers[x] = new Queue<Byte>();
			x++;
		}
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
		Access.CODE.updateSerialDisplays();
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
				out = SerialBuffers[x].pop().byteValue();
				break;
			}
			x++;
		}
		Access.CODE.updateSerialDisplays();
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
		Access.INTERFACE.writeToLog(from, write);
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
	
	private static void printBuffers(){
		int x = 0;
		while (x < SerialBuffers.length){
			System.out.println(SerialBufferCodes[x] + ": " + SerialBuffers[x].toString());
			x++;
		}
	}
	
	public static Queue<Byte>[] getSerialQueues(int key){
		try {
			if (key == access_key){
				@SuppressWarnings("unchecked")
				Queue<Byte>[] out = new Queue[SerialBuffers.length];
				int x = 0;
				while (x < out.length){
					out[x] = new Queue<Byte>(SerialBuffers[x]);
					x++;
				}
				return out;
			}
			else {
				return null;
			}
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	public static void registerNewThread(String name, int delay){
		threads.add(name, new ThreadItem(name, delay, TimeMillis));
	}
	
	public static void checkOutThread(String name){
		threads.remove(name);
	}
	
	public static void threadCheckIn(String name){
		//TODO figure out how to recover from something else taking longer than the milli-clock
		threads.get(name).markFinished();
		System.out.println("\n\nCheck In: " + name + "\nTime: " + TimeMillis);
		if (System.nanoTime()-lastSystemNanoTime >= 1000000/timeScale){
			System.out.println("Wait Elapsed");
			for (String key : threads.getKeys()){
				System.out.print(key + ".isFinished: ");
				if (!threads.get(key).isFinished()){
					System.out.println("false");
					return;
				}
				System.out.println("true");
			}
			ready = false;
			TimeMillis++;
			for (String key : threads.getKeys()){
				threads.get(key).reset();
				if (threads.get(key).getNext() == TimeMillis){
					threads.get(key).grantPermission();
				}
			}
			ready = true;
		}		
	}
	
	public static boolean getThreadRunPermission(String name){
		//System.out.print("\nRequesting: " + name + " - ");
		if (threads.get(name).hasPermission() && ready && begun){
			threads.get(name).revokePermission();
			//System.out.println("approved");
			return true;
		}
		//System.out.println("denied");
		//System.out.println(threads.get(name).hasPermission() +", " + ready + ", " + begun);
		return false;
	}
	
}
