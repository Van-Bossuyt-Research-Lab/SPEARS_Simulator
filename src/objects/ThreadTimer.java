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
	
	public ThreadTimer(int interval, Runnable run, int times, String name, boolean start){
		super.setName(name);
		delay = interval;
		action = run;
		actions = times;
		forever = (times == FOREVER);
		Globals.registerNewThread(name, delay);
		if (start){
			this.start();
		}
	}

	public void run(){
		while (actions > 0 || forever){
			if (delay > 0){
				try{
					Thread.sleep((long)((delay-1)/Globals.getTimeScale()), (int)(((delay-1)/Globals.getTimeScale()-(int)((delay-1)/Globals.getTimeScale()))*1000000));
				}
				catch (InterruptedException e) {
					return;
				}
				catch (Exception e){
					System.err.println(toString());
					e.printStackTrace();
				}
			}
			while (!Globals.getThreadRunPermission(getName())) {
				if (super.isInterrupted()){
					Globals.checkOutThread(getName());
					return;
				}
			}
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
	
	public void deSync(){
		delay++;
		Globals.checkOutThread(getName());
	}
	
	@Override
	public String toString() {
		return "ThreadTimer [name=" + getName() + ", delay=" + delay + ", forever=" + forever + "]";
	}
	
}
