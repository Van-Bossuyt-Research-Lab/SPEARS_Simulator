package objects;

import wrapper.Globals;

public class ThreadTimer extends Thread {

	private int delay;
	private Runnable action;
	private int actions;
	private boolean forever = false;
	public static int FOREVER = -1;
	
	public ThreadTimer(int interval, Runnable run, int times, String name){
		super.setName(name);
		delay = interval;
		action = run;
		actions = times;
		forever = (times == FOREVER);
		Globals.registerNewThread(name, delay);
		this.start();
	}
	
	public ThreadTimer(int interval, Runnable run, int times, String name, boolean start, boolean override){
		super.setName(name);
		delay = interval;
		action = run;
		actions = times;
		forever = (times == FOREVER);
		Globals.registerNewThread(name, delay);
		if (override){
			delay++;
		}
		if (start){
			this.start();
		}
	}

	public void run(){
		while (actions > 0 || forever){
			try{
				Thread.sleep((long)((delay-1)/Globals.getTimeScale()), (int)(((delay-1)/Globals.getTimeScale()-(int)((delay-1)/Globals.getTimeScale()))*1000000));
			}
			catch (Exception e) {
				System.out.println(toString());
				e.printStackTrace();
				return;
			}
			while (!Globals.getThreadRunPermission(getName())) {}
			action.run();
			Globals.threadCheckIn(getName());
			if (!forever){
				actions--;
			}
		}
		Globals.checkOutThread(getName());
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
