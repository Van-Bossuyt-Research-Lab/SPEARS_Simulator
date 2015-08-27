package com.csm.rover.simulator.wrapper;

import java.util.Random;

import com.csm.rover.simulator.objects.FreeThread;
import com.csm.rover.simulator.objects.Map;
import com.csm.rover.simulator.objects.Queue;
import com.csm.rover.simulator.objects.ThreadItem;
import com.csm.rover.simulator.objects.SyncronousThread;
import com.csm.rover.simulator.visual.AccelPopUp;

//TODO make into a signularity thingy
public class Globals {

	public static String vrsionNumber = "2.2.1";
	
	private static Queue<Byte>[] SerialBuffers; // the buffer for messages
	private static String[] SerialBufferCodes;
	
	private static final double time_accelerant = 10;
	private static double timeScale = 1.0;
	public static long TimeMillis = 0;
	
	private static Random rnd = new Random();
	private static int access_key = "qwert".hashCode();
	
	private static boolean begun = false;
	private static Map<String, ThreadItem> threads = new Map<String, ThreadItem>();
	private static boolean milliDone = false;
	
	private static int exitTime = -1;
	private static AccelPopUp informer;
	
	public static void startTime(boolean accel){
		begun = true;
		if (accel){
			timeScale = time_accelerant;
		}
		Access.INTERFACE.clock.start();
		ThreadItem.offset = 0;
		new FreeThread(0, new Runnable(){
			public void run(){
				long end = System.nanoTime() + (long)(1000000 / getTimeScale());
				while (System.nanoTime() < end) {}
				threadCheckIn("milli-clock");
			}
		}, SyncronousThread.FOREVER, "milli-clock", true);
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
	
	public static double getTimeAccelerant() {
		return time_accelerant;
	}

	public static double getTimeScale(){
		return timeScale;
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
	
	@SuppressWarnings("unused")
	private static void printBuffers(){
		int x = 0;
		while (x < SerialBuffers.length){
			writeToLogFile("Timing", SerialBufferCodes[x] + ": " + SerialBuffers[x].toString());
			x++;
		}
	}
	
	//NOT A TRUE SUBTRACTION: if the second angle is clockwise of the first, returns the negative number of units between, positive if second is to ccw
	public static double subtractAngles(double first, double second){
		double one = first - second;
		double two = ((first+Math.PI)%(2*Math.PI)) - ((second+Math.PI)%(2*Math.PI));
		if (Math.abs(one-two) < 0.00001){
			return -1*one;
		}
		if (Math.abs(one) > Math.abs(two)){
			return -1*two;
		}
		else {
			return -1*one;
		}
	}
	
	//NOT A TRUE SUBTRACTION: if the second angle is clockwise of the first, returns the negative number of units between, positive if second is to ccw
	public static double subtractAnglesDeg(double first, double second){
		double one = first - second;
		double two = ((first+180)%360) - ((second+180)%360);
		if (Math.abs(one-two) < 0.00001){
			return -1*one;
		}
		if (Math.abs(one) > Math.abs(two)){
			return -1*two;
		}
		else {
			return -1*one;
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
	
	public static void setUpAcceleratedRun(int runtime){
		exitTime = runtime;
		informer = new AccelPopUp(exitTime, (int) (exitTime/time_accelerant/60000));
		new FreeThread(1000, new Runnable(){
			public void run(){
				informer.update((int)TimeMillis);
				if (TimeMillis >= exitTime){
					Admin.GUI.exit();
				}
			}
		}, FreeThread.FOREVER, "accel-pop-up");
	}
	
	public static void registerNewThread(String name, int delay, SyncronousThread thread){
		threads.add(name, new ThreadItem(name, delay, TimeMillis, thread));
	}
	
	public static void checkOutThread(String name){
		if (!name.contains("delay")){
			System.err.println(name + " out.");
		}
		threads.remove(name);
		threadCheckIn(name);
	}
	
	public static void threadCheckIn(String name){
		try {
			threads.get(name).markFinished();
			threads.get(name).advance();
		} catch (NullPointerException e) {}
		if (name.equals("milli-clock") || milliDone){
			for (Object o : threads.getKeys()){
				String key = (String) o;
				try {
					threads.get(key).equals(null);
					if (key.equals("milli-clock")){
						continue;
					}
					if (!threads.get(key).isFinished()){
						//System.out.println(key + " - " + threads.get(key).getState());
						milliDone = true;
						threads.get(key).shakeThread();
						return;
					}
				} catch (NullPointerException e) { continue; }
			}
			milliDone = false;
			TimeMillis++;
			for (Object o : threads.getKeys()){
				try {
					String key = (String) o;
					threads.get(key).reset();
					if (threads.get(key).getNext() <= TimeMillis){
						threads.get(key).grantPermission();
					}
				}
				catch (NullPointerException e){
					continue;
				}
			}
		}
	}
	
	public static String delayThread(String name, int time){
		try {
			threads.get(name).equals(null);
			String rtnname = name +"-delay";
			ThreadItem.offset = 0;
			registerNewThread(rtnname, time, null);
			threads.get(name).suspend();
			return rtnname;
		}
		catch (Exception e){
			try{
				double length = time;
				Thread.sleep((long)(length/getTimeScale()), (int)((length/getTimeScale()-(int)length/getTimeScale())*1000000));
			} catch (Exception ex) {
				Globals.reportError("Globals", "delayThread", ex);
			}
			return "pass";
		}
	} 
	
	public static void threadDelayComplete(String name){
		checkOutThread(name+"-delay");
		threads.get(name).unSuspend();
	}
	
	public static void threadIsRunning(String name){
		try {
			threads.get(name).setRunning(true);
		} catch (NullPointerException e) {}
	}
	
	public static boolean getThreadRunPermission(String name){
		try {
			if (threads.get(name).hasPermission() && begun){
				threads.get(name).revokePermission();
				return true;
			}
		}
		catch (NullPointerException e){
			e.printStackTrace();
		}
		return false;
	}
	
}
