package com.csm.rover.simulator.objects;

import java.io.Serializable;
import com.csm.rover.simulator.wrapper.Globals;

public class FreeThread extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int delay;
	private Runnable action;
	private int actions;
	private boolean forever = false;
	public static int FOREVER = -1;
		
	public FreeThread(int interval, Runnable run, int times, String name){
		super.setName(name);
		delay = interval;
		action = run;
		actions = times;
		forever = (times == FOREVER);
		this.start();
	}
	
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

	public void run(){
		while (actions > 0 || forever){
			if (delay > 0){
				try{
					Thread.sleep((long)((delay)/Globals.getTimeScale()), (int)(((delay)/Globals.getTimeScale()-(int)((delay)/Globals.getTimeScale()))*1000000));
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
		return;
	}
	
	public void Stop(){
		this.interrupt();
	}
	
	@Override
	public String toString() {
		return "ThreadTimer [name=" + getName() + ", delay=" + delay + ", forever=" + forever + "]";
	}
	
}