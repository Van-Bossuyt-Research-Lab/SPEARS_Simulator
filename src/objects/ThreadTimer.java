package objects;

import wrapper.Globals;

public class ThreadTimer extends Thread {

	private double delay;
	private Runnable action;
	private int actions;
	private boolean forever = false;
	public static int FOREVER = -1;
	
	public ThreadTimer(double interval, Runnable run, int times){
		delay = interval;
		action = run;
		actions = times;
		forever = (times == FOREVER);
		this.start();
	}
	
	public void run(){
		while (actions > 0 || forever){
//			long start = System.nanoTime();
//			while (((System.nanoTime()-start) < (delay/Globals.getTimeScale())*1000000) && !isInterrupted()) {}
//			if (isInterrupted()){
//				return;
//			}
			try{
				Thread.sleep((long)(delay/Globals.getTimeScale()), (int)((delay/Globals.getTimeScale()-(int)(delay/Globals.getTimeScale()))*1000000));
			} 
			catch (Exception e) {
				return;
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
	
}
