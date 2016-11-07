package com.csm.rover.simulator.objects;

import com.csm.rover.simulator.wrapper.Globals;

public class ThreadItem {
	
	private String name;
	private int delay;
	private long next;
	
	private boolean permission;
	private boolean running;
	private boolean complete;
	private boolean suspended = false;
	
	private SynchronousThread thread;
	
	public static int offset = 0;

	public ThreadItem(String name, int delay, long start, SynchronousThread thread) {
		this.thread = thread;
		this.name = name;
		this.delay = delay;
		next = start + delay + offset;
		offset++;
	}

	public String getName() {
		return name;
	}

	public int getDelay() {
		return delay;
	}

	public void advance(){
		next = Globals.getInstance().timeMillis() + delay;
	}
	
	public long getNext() {
		return next;
	}
	
	public void grantPermission(){
		permission = true;
		complete = false;
		try {
			thread.Shake();
		} catch (Exception e) {}
	}
	
	public void revokePermission(){
		permission = false;
	}
	
	public void markFinished(){
		complete = true;
	}

	public boolean hasPermission() {
		return permission;
	}
	
	public void suspend(){
		suspended = true;
	}
	
	public void unSuspend(){
		suspended = false;
	}
	
	public void setRunning(boolean b){
		running = b;
	}
	
	public boolean isFinished(){
		if (suspended){
			return true;
		}
		else if (running){
			return complete;
		}
		else {
			return !permission;
		}
	}
	
	public void reset(){
		running = false;
		complete = false;
	}
	
	public char getState(){
		if (suspended){
			return 's';
		}
		else if (running){
			return 'r';
		}
		else if (complete){
			return 'c';
		}
		else if (permission){
			return 'p';
		}
		else {
			return 'w';
		}
	}

	public void shakeThread() {
		try {
			thread.Shake();
		} catch (NullPointerException e) {}
	}

    public void killThread(){
        revokePermission();
        thread.Stop();
    }

	@Override
	public String toString() {
		return "ThreadItem [name=" + name + ", delay=" + delay + ", next="
				+ next + ", permission=" + permission + ", running=" + running
				+ ", complete=" + complete + ", suspended=" + suspended + "]";
	}
	
}
