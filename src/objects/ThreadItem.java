package objects;

import wrapper.Globals;

public class ThreadItem {
	
	private String name;
	private int delay;
	private long next;
	
	private boolean permission;
	private boolean running;
	private boolean complete;
	private boolean suspended = false;
	
	public static int offset = 0;

	public ThreadItem(String name, int delay, long start) {
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
		next = Globals.TimeMillis + delay;
	}
	
	public long getNext() {
		return next;
	}
	
	public void grantPermission(){
		permission = true;
		running = true;
		complete = false;
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
	
	public boolean isFinished(){
		if (suspended){
			return true;
		}
		else if (running){
			return complete;
		}
		else {
			return true;
		}
	}
	
	public void reset(){
		running = false;
		complete = false;
	}

	@Override
	public String toString() {
		return "ThreadItem [name=" + name + ", delay=" + delay + ", next="
				+ next + ", permission=" + permission + ", running=" + running
				+ ", complete=" + complete + ", suspended=" + suspended + "]";
	}
	
}
