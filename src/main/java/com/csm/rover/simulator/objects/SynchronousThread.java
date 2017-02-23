package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.wrapper.Globals;

/**
 * An object for creating new threads that run in the time step architecture in {@link Globals}.
 * All SynchronousThreads will advance together and are effected by acceleration.
 */
public class SynchronousThread extends Thread {

	/**
	 * Constant for allowing the thread to run continuously
	 */
	public static final int FOREVER = -1;

	private Globals GLOBAL;
	
	private int delay;
	private Runnable action;
	private int actions;
	private boolean forever = false;
	private boolean stopped = false;
	private boolean running = false;

	/**
	 * Basic constructor, automatically starts the thread.  Equivalent to {@link #SynchronousThread(int, Runnable, int, String, boolean) SynchronousThread}(interval, run, times, name, true)
	 *
	 * @param interval Time delay in milliseconds between executions
	 * @param run The runnable to execute
	 * @param times The number of times the thread should run, can be {@link #FOREVER}
	 * @param name A name to assign to the thread
	 */
	public SynchronousThread(int interval, Runnable run, int times, String name){
		this(interval, run, times, name, true);
	}

	/**
	 * Constructor allowing the called to delay the starting of the thread.
	 *
	 * @param interval Time delay in milliseconds between executions
	 * @param run The runnable to execute
	 * @param times The number of times the thread should run, can be {@link #FOREVER}
	 * @param name A name to assign to the thread
	 * @param start Whether the thread should start now.  Can be started later with {@link #start()}
	 */
	public SynchronousThread(int interval, Runnable run, int times, String name, boolean start){
		super.setName(name);
        GLOBAL = Globals.getInstance();
		delay = interval;
		action = run;
		actions = times;
		forever = (times == FOREVER);
		GLOBAL.registerNewThread(name, delay, this);
		if (start){
			this.start();
		}
	}

	/**
	 * Run action for the thread.  Should not be called by user.  Use {@link #start()} to
	 * start the thread.
	 */
	@Override
	public void run(){
		while (actions > 0 || forever){
			try{
				Thread.sleep(Integer.MAX_VALUE);
			}
			catch (InterruptedException e) {
				running = true;
				if (stopped){
					GLOBAL.checkOutThread(getName());
					return;
				}
				if (GLOBAL.getThreadRunPermission(getName())){
					GLOBAL.threadIsRunning(getName());
					action.run();
					GLOBAL.threadCheckIn(getName());
					if (!forever){
						actions--;
					}
				}
				running  = false;
			}
		}
		GLOBAL.checkOutThread(getName());
	}

	/**
	 * Forces the thread to cease operations.
	 */
	public void Stop(){
		stopped = true;
		this.interrupt();
	}

	/**
	 * Wakes the thread up if it is sleeping.
	 */
	public void Shake(){
		if (!running){
			this.interrupt();
		}
	}

	@CoverageIgnore
	@Override
	public String toString() {
		return "SynchronousThread [name=" + getName() + ", delay=" + delay + ", forever=" + forever + "]";
	}
	
}