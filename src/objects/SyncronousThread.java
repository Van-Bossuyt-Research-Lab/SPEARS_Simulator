package objects;

import java.io.Serializable;
import wrapper.Globals;

public class SyncronousThread extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int delay;
	private Runnable action;
	private int actions;
	private boolean forever = false;
	public static int FOREVER = -1;
	private boolean stopped = false;
	private boolean shaken = false;
	
	public SyncronousThread(int interval, Runnable run, int times, String name){
		super.setName(name);
		delay = interval;
		action = run;
		actions = times;
		forever = (times == FOREVER);
		Globals.registerNewThread(name, delay, this);
		this.start();
	}
	
	public SyncronousThread(int interval, Runnable run, int times, String name, boolean start){
		super.setName(name);
		delay = interval;
		action = run;
		actions = times;
		forever = (times == FOREVER);
		Globals.registerNewThread(name, delay, this);
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
					if (stopped || !shaken){
						Globals.checkOutThread(getName());
						return;
					}
					if (Globals.getThreadRunPermission(getName())){
						Globals.threadIsRunning(getName());
						action.run();
						Globals.threadCheckIn(getName());
						if (!forever){
							actions--;
						}
						shaken = false;
					}
				}
			}			
		}
		Globals.checkOutThread(getName());
		return;
	}
	
	public void Stop(){
		stopped = true;
		this.interrupt();
	}
	
	public void Shake(){
		shaken = true;
		this.interrupt();
	}
	
	@Override
	public String toString() {
		return "ThreadTimer [name=" + getName() + ", delay=" + delay + ", forever=" + forever + "]";
	}
	
}