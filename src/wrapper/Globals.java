package wrapper;

import java.util.Random;

import objects.Map;
import objects.Queue;
import objects.ThreadItem;
import objects.ThreadTimer;

public class Globals {

	public static String vrsionNumber = "2.0.3";
	
	private static Queue<Byte>[] SerialBuffers; // the buffer for messages
	private static String[] SerialBufferCodes;
	
	private static double timeScale = 1.0;
	public static long TimeMillis = 0;
	private static ThreadTimer milliTimer;
	
	private static Random rnd = new Random();
	private static int access_key = "qwert".hashCode();
	
	private static boolean begun = false;
	private static Map<String, ThreadItem> threads = new Map<String, ThreadItem>();
	private static boolean ready = false;
	private static boolean milliDone = false;
	
	public static void startTime(){
		begun = true;
		Access.INTERFACE.clock.start();
		ThreadItem.offset = 0;
		milliTimer = new ThreadTimer(0, new Runnable(){
			public void run(){
				long end = System.nanoTime() + (long)(1000000 / getTimeScale());
				while (System.nanoTime() < end) {}
			}
		}, ThreadTimer.FOREVER, "milli-clock", true);
		TimeMillis++;
		Object[] keys = threads.getKeys();
		int x = 0;
		while (x < keys.length){
			if (keys[x].equals(null)){
				break;
			}
			String key = (String) keys[x];
			threads.get(key).reset();
			if (threads.get(key).getNext() == TimeMillis){
				threads.get(key).grantPermission();
			}
			x++;
		}
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
		writeToLogFile("ERROR - " + Thread.currentThread().getName() + ": " + obj + ": " + method, error.toString());
		System.err.println(Thread.currentThread().getName());
		error.printStackTrace();
	}
	
	public synchronized static void writeToLogFile(String from, String write){ // writes the message to the log genorated by the interface
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
			writeToLogFile("Timing", SerialBufferCodes[x] + ": " + SerialBuffers[x].toString());
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
		threadCheckIn(name);
	}
	
	public static void threadCheckIn(String name){
		try {
			threads.get(name).markFinished();
			threads.get(name).advance();
		} catch (Exception e) {}
		if (name.equals("milli-clock") || milliDone){
			for (Object o : threads.getKeys()){
				String key = (String) o;
				try {
					threads.get(key).equals(null);
				} catch (NullPointerException e) { continue; }
				if (key.equals("milli-clock")){
					continue;
				}
				if (!threads.get(key).isFinished()){
					milliDone = true;
					return;
				}
			}
			ready = false;
			milliDone = false;
			TimeMillis++;
			for (Object o : threads.getKeys()){
				try {
					String key = (String) o;
					threads.get(key).reset();
					if (threads.get(key).getNext() == TimeMillis){
						threads.get(key).grantPermission();
					}
				}
				catch (NullPointerException e){
					continue;
				}
			}
			ready = true;
		}
	}
	
	public static String delayThread(String name, int time){
		try {
			threads.get(name).equals(null);
			String rtnname = name +"-delay";
			ThreadItem.offset = 0;
			registerNewThread(rtnname, time);
			threads.get(name).suspend();
			return rtnname;
		}
		catch (Exception e){
			try{
				double length = time;
				Thread.sleep((long)(length/Globals.getTimeScale()), (int)((length/Globals.getTimeScale()-(int)length/Globals.getTimeScale())*1000000));
			} catch (Exception ex) {
				Globals.reportError("InterfaceCode", "delay", ex);
			}
			return "pass";
		}
	}
	
	public static void threadDelayComplete(String name){
		checkOutThread(name+"-delay");
		threads.get(name).unSuspend();
	}
	
	public static boolean getThreadRunPermission(String name){
		if (name.equals("milli-clock")){
			return true;
		}
		try {
			if (threads.get(name).hasPermission() && ready && begun){
				threads.get(name).revokePermission();
				return true;
			}
		}
		catch (NullPointerException e){
			// If not on the list don't hold it up
			return true;
		}
		return false;
	}
	
}
