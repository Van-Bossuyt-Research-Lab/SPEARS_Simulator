package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.wrapper.Globals;

/**
 * An object for creating new threads that run outside of the simulation time step defined
 * in {@link Globals}.
 */
public class FreeThread extends Thread {

	/**
	 * Constant for allowing the thread to run continuously
	 */
	public static final int FOREVER = -1;

	private int delay;
	private Runnable action;
	private int actions;
	private boolean forever = false;

	/**
	 * Basic constructor, automatically starts the thread.  Equivalent to {@link #FreeThread(int, Runnable, int, String, boolean) FreeThread}(interval, run, times, name, true)
	 *
	 * @param interval Time delay in milliseconds between executions
	 * @param run The runnable to execute
	 * @param times The number of times the thread should run, can be {@link #FOREVER}
     * @param name A name to assign to the thread
     */
	public FreeThread(int interval, Runnable run, int times, String name){
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
	public FreeThread(int interval, Runnable run, int times, String name, boolean start){
		super.setName(name);
		delay = interval;
		action = run;
		actions = times;
		forever = (times == FOREVER);
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
			if (delay > 0){
				try{
					Thread.sleep(delay);
				}
				catch (InterruptedException e) {
					return;
				}
			}
			action.run();
			if (!forever){
				actions--;
			}
		}
	}

	/**
	 * Forces the thread to cease operations.
	 */
	public void Stop(){
		this.interrupt();
	}

	@CoverageIgnore
	@Override
	public String toString() {
		return "ThreadTimer [name=" + getName() + ", delay=" + delay + ", forever=" + forever + "]";
	}
	
}