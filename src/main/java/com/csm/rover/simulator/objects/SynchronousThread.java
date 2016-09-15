package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.wrapper.Globals;


public class SynchronousThread extends Thread {
	
	private Globals GLOBAL;
	
	private int delay;
	private Runnable action;
	private int actions;
	private boolean forever = false;
	public static int FOREVER = -1;
	private boolean stopped = false;
	private boolean running = false;
	
	public SynchronousThread(int interval, Runnable run, int times, String name){
		this(interval, run, times, name, true);
	}
	
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

	public void run(){
		while (actions > 0 || forever){
			if (delay > 0){
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
		}
		GLOBAL.checkOutThread(getName());
	}
	
	public void Stop(){
		stopped = true;
		this.interrupt();
	}
	
	public void Shake(){
		if (!running){
			this.interrupt();
		}
	}
	
	@Override
	public String toString() {
		return "ThreadTimer [name=" + getName() + ", delay=" + delay + ", forever=" + forever + "]";
	}
	
}