package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.objects.util.FreeThread;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.objects.ThreadItem;
import com.csm.rover.simulator.objects.util.ZDate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Globals {
	private static final Logger LOG = LogManager.getFormatterLogger(Globals.class);

	public static String versionNumber = "2.7.1";

	private static final double time_accelerant = 10;
	private double timeScale = 1.0;
	private long timeMillis = 0;
	
	private Random rnd = new Random();
	
	private boolean begun = false;
	private Map<String, ThreadItem> threads = new ConcurrentHashMap<>();
	private boolean milliDone = false;
	
	private int exitTime = -1;

    public ZDate dateTime;
    public SynchronousThread clock;
    private ArrayList<Runnable> clockEvents;
	
	private static Globals singleton_instance = null;

	/**
	 * Returns the singleton instance of the Globals class, initializes Globals if not created
	 *
	 * @return	Singleton of Globals
     */
	public static Globals getInstance(){
		if (singleton_instance == null) {
            singleton_instance = new Globals();
			singleton_instance.clock = new SynchronousThread(1000, () -> {
				singleton_instance.dateTime.advanceClock();
				for (Runnable event : singleton_instance.clockEvents){
					event.run();
				}
			}, SynchronousThread.FOREVER, "clock", false);
		}
		return singleton_instance;
	}

	/**
	 * Hidden constructor method for Globals.  Use {@link #getInstance()} getInstance} to get singleton instance.
	 */
    private Globals(){
        clockEvents = new ArrayList<>();
        dateTime = new ZDate();
        dateTime.setFormat("[hh:mm:ss]");
    }

    /**
     * Observable pattern that is triggered every time the clock advances a millisecond.
     *
     * @param event Runnable action to be called
     */
    public void addClockIncrementEvent(Runnable event){
        clockEvents.add(event);
    }

    /**
     * Begins the simulation clock allowing the simulation to run.
     *
     * @param accel Whether or not to accelerate the simulation.  False will run in Real-Time
     */
	public void startTime(boolean accel){
		begun = true;
		if (accel){
			timeScale = time_accelerant;
		}
        clock.start();
		ThreadItem.offset = 0;
		new FreeThread(0, () -> {
            long end = System.nanoTime() + (long)(1000000 / getTimeScale());
            while (System.nanoTime() < end) {}
            threadCheckIn("milli-clock");
		}, SynchronousThread.FOREVER, "milli-clock", true);
	}

	/**
	 * Returns the multiplication factor for accelerating the simulation time step.
	 *
	 * @return The acceleration multiplier
     */
	public double getTimeAccelerant() {
		return time_accelerant;
	}

	/**
	 * Returns the multiplication factor currently in use in the simulation.  Will be 1 for real-time
	 * and equivalent to {@link #getTimeAccelerant() getTimeAccelerant} when accelerated.
	 *
	 * @return The current multiplier
     */
	public double getTimeScale(){
		return timeScale;
	}

	/**
	 * Returns the current simulation time in milliseconds
	 *
	 * @return time in ms
     */
	public long timeMillis(){
		return timeMillis;
	}

	/**
	 * Returns the coordinate difference of the two angles.  If the second angle is clockwise
	 * of the first returns the negative, if counterclockwise returns positive.  Functions
	 * around the wrap point.
	 *
	 * @param first	The first angle
	 * @param second The second angle
	 * @return The coordinate difference between input angles
     */
	//NOT A TRUE SUBTRACTION:
	public double subtractAngles(double first, double second){
		first = normalize(first, 0, 2*Math.PI);
		second = normalize(second, -2*Math.PI, 0);
		double diff = first - second;
		diff = normalize(diff, -Math.PI, Math.PI);
		return -diff;
	}

	/**
	 * Equivalent to Math.toDegrees({@link #subtractAngles(double, double) subtractAngles}(Math.toRadians(double), Math.toRadians(double))).
	 *
	 * @param first	The first angle
	 * @param second The second angle
     * @return The coordinate difference between input angles
     */
	public double subtractAnglesDeg(double first, double second){
		return Math.toDegrees(subtractAngles(Math.toRadians(first), Math.toRadians(second)));
	}

	private double normalize(double angle, double low, double high){
		while (angle < low){
			angle += 2*Math.PI;
		}
		while (angle > high){
			angle -= 2*Math.PI;
		}
		return angle;
	}
	
	public void setUpAcceleratedRun(final HumanInterfaceAbstraction HI, int runtime){
		exitTime = runtime;
        HI.viewAccelerated(exitTime, time_accelerant);
		new FreeThread(1000, new Runnable(){
			public void run(){
				if (timeMillis >= exitTime){
					//Maybe not working? was an error
					LOG.log(Level.INFO, "Exiting");
					HI.exit();
				}
			}
		}, FreeThread.FOREVER, "accel-handler");
	}
	
	public void registerNewThread(String name, int delay, SynchronousThread thread){
		threads.put(name, new ThreadItem(name, delay, timeMillis, thread));
	}
	
	public void checkOutThread(String name){
		if (!name.contains("delay")){
            LOG.log(Level.DEBUG, name + " out.");
		}
        threadCheckIn(name);
		threads.remove(name);
	}

    public void killThread(String name){
        if (threads.containsKey(name)){
            threads.get(name).killThread();
        }
    }
	
	public void threadCheckIn(String name){
        if (!name.equals("milli-clock")){
            try {
                threads.get(name).markFinished();
                threads.get(name).advance();
            }
            catch (NullPointerException e) {
                LOG.log(Level.WARN, "Null in thread: " + name, e);
            }
        }
		if (name.equals("milli-clock") || milliDone){
			for (Object o : threads.keySet()){
				String key = (String) o;
				if (threads.containsKey(key)){
					if (key.equals("milli-clock")){
						continue;
					}
					if (!threads.get(key).isFinished()){
						milliDone = true;
						threads.get(key).shakeThread();
						return;
					}
				}
			}
			milliDone = false;
			timeMillis++;
			for (Object o : threads.keySet()){
				try {
					String key = (String) o;
					threads.get(key).reset();
					if (threads.get(key).getNext() <= timeMillis){
						threads.get(key).grantPermission();
					}
				}
				catch (NullPointerException e){ e.printStackTrace(); }
			}
		}
	}
	
	public String delayThread(String name, int time){
		try {
			if (threads.get(name) == null){
				throw new Exception();
			}
			String rtnname = name +"-delay";
			ThreadItem.offset = 0;
			registerNewThread(rtnname, time, null);
			threads.get(name).suspend();
			return rtnname;
		}
		catch (Exception e){
			try{
				Thread.sleep((long)(time/getTimeScale()), (int)((time/getTimeScale()-time/getTimeScale())*1000000));
			} catch (Exception ex) {
				LOG.log(Level.ERROR, "Delay thread failed", ex);
			}
			return "pass";
		}
	} 
	
	public void threadDelayComplete(String name){
		checkOutThread(name+"-delay");
		threads.get(name).unSuspend();
	}
	
	public void threadIsRunning(String name){
		try {
			threads.get(name).setRunning(true);
		} catch (NullPointerException e) { e.printStackTrace(); }
	}
	
	public boolean getThreadRunPermission(String name){
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
