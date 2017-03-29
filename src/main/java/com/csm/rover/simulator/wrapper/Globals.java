package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.objects.FreeThread;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.objects.ThreadItem;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A singleton class that handles simulation time and some globally required functions.
 */
public class Globals {
	private static final Logger LOG = LogManager.getFormatterLogger(Globals.class);

	public final static String versionNumber = "3.0.1";

	private static final double time_accelerant = 10;
	private double timeScale = 1.0;
	private long timeMillis = 0;
	
	private boolean begun = false;
	private Map<String, ThreadItem> threads = new ConcurrentHashMap<>();
	private boolean milliDone = false;
	
	private int exitTime = -1;

    public DateTime dateTime;
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
				singleton_instance.dateTime = singleton_instance.dateTime.plusSeconds(1);
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
        dateTime = new DateTime();
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

	public boolean isAccelerated(){
		return timeScale==time_accelerant;
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
	public double subtractAngles(double first, double second){
		first = normalize(first, 0, 2*Math.PI);
		second = normalize(second, -2*Math.PI, 0);
		double diff = first - second;
		diff = normalize(diff, -Math.PI, Math.PI);
		return -diff;
	}

	/**
	 * Equivalent to {@link Math#toDegrees Math.toDegrees}({@link #subtractAngles(double, double) subtractAngles}({@link Math#toRadians Math.toRadians}(first), {@link Math#toRadians Math.toRadians}(second))).
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
	
	void setUpAcceleratedRun(int runtime){
		new SynchronousThread(runtime*60*1000, Admin.getInstance()::shutDownSimulator, 1, "AccelBrake");
	}

	/**
	 * Register a thread to the synchronous system
	 *
	 * @param name The name of the thread
	 * @param delay The interval the thread should be given permission on in milliseconds
	 * @param thread The thread
     */
	public void registerNewThread(String name, int delay, SynchronousThread thread){
		threads.put(name, new ThreadItem(name, delay, timeMillis, thread));
	}

	/**
	 * Remove a thread from the synchronous system
	 *
	 * @param name Name of the thread
     */
	public void checkOutThread(String name){
		if (!name.contains("delay")){
            LOG.log(Level.DEBUG, name + " out.");
		}
        threadCheckIn(name);
		threads.remove(name);
	}

	/**
	 * Kill a thread and remove it from the synchronous system
	 *
	 * @param name Name of the thread
     */
    public void killThread(String name){
        if (threads.containsKey(name)){
            threads.get(name).killThread();
        }
    }

	/**
	 * Should be called to indicate when a thread has completed an operation.  When all threads
	 * including the simulation clock are complete, time is advanced and new run permissions
	 * granted.
	 *
	 * @param name The name of the thread
     */
	public void threadCheckIn(String name){
        if (!name.equals("milli-clock")){
            try {
                threads.get(name).markFinished();
                threads.get(name).advance();
				if (name.contains("-delay")){
					killThread(name);
				}
            }
            catch (NullPointerException e) {
                LOG.log(Level.WARN, "Null in thread: " + name, e);
            }
        }
		if (name.equals("milli-clock") || milliDone){
			for (String key : threads.keySet()){
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
			for (String key : threads.keySet()){
				try {
					threads.get(key).reset();
					if (threads.get(key).getNext() <= timeMillis){
						threads.get(key).grantPermission();
					}
				}
				catch (NullPointerException e){
					LOG.log(Level.ERROR, "Failed to advance thread: "+key, e);
				}
			}
		}
	}

	/**
	 * Suspends a thread from operating for a given period of time.  If the thread is
	 * not registered in the synchronous system, delays the current thread.
	 *
	 * @param name The name of the thread
	 * @param time Time to delay in milliseconds
     */
	public void delayThread(String name, int time){
		if (threads.get(name) != null){
			String delayName = name + "-delay";
			registerNewThread(delayName, time,
					new SynchronousThread(time, () -> threads.get(name).unSuspend(), SynchronousThread.FOREVER, delayName));
			threads.get(name).suspend();
		}
		else {
			try{
				long start = System.nanoTime();
				while (System.nanoTime()-start < time*1000000/getTimeScale()) {}
			}
			catch (Exception ex) {
				LOG.log(Level.ERROR, "Delay thread failed", ex);
			}
		}
	}

	/**
	 * Informs the synchronous systems that the thread is running.
	 *
	 * @param name The name of the thread
     */
	public void threadIsRunning(String name){
		try {
			threads.get(name).setRunning(true);
		}
		catch (NullPointerException e) {
			LOG.log(Level.WARN, "Thread \"" + name + "\" not registered", e);
		}
	}

	/**
	 * Checks is the thread has permission to run.  This check will revoke run permission.
	 *
	 * @param name The name of the thread
	 * @return true if the thread has permission
     */
	public boolean getThreadRunPermission(String name){
		try {
			if (threads.get(name).hasPermission() && begun){
				threads.get(name).revokePermission();
				return true;
			}
		}
		catch (NullPointerException e){
			LOG.log(Level.WARN, "Asking about unregistered thread \'"+name+"\'", e);
		}
		return false;
	}
	
}
