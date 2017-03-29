/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.objects;

import com.spears.wrapper.Globals;

/**
 * This object is used as a wrapper for managing synchronous threads.  It tracks whether a thread
 * is running, complete, or suspended and when it has permission to run.
 */
public class ThreadItem {
	
	private String name;
	private int delay;
	private long next;
	
	private boolean permission;
	private boolean running;
	private boolean complete;
	private boolean suspended = false;
	
	private SynchronousThread thread;

	/**
	 * Creates a new tracker for the provided thread.  The other parameters are used to
	 * identify when the thread should have run permission.
	 *
	 * @param name The name of the thread
	 * @param delay The time the thread should sleep between executions
	 * @param start The current time, the time the thread should start counting from
     * @param thread The thread being handled by the ThreadItem
     */
	public ThreadItem(String name, int delay, long start, SynchronousThread thread) {
		this.thread = thread;
		this.name = name;
		this.delay = delay;
		next = start + delay;
	}

	/**
	 * Returns the name of the thread.
	 *
	 * @return The name of the thread
     */
	public String getName() {
		return name;
	}

	/**
	 * Returns the time the thread sleeps.
	 *
	 * @return Time between executions
     */
	public int getDelay() {
		return delay;
	}

	/**
	 * Increments the time until the next run is approved by the delay.
	 */
	public void advance(){
		next = Globals.getInstance().timeMillis() + delay;
	}

	/**
	 * Returns the nearest time the thread has permission to run.
	 *
	 * @return Time of next execution
     */
	public long getNext() {
		return next;
	}

	/**
	 * Gives the thread permission to run and wakes the thread.
	 */
	public void grantPermission(){
		permission = true;
		complete = false;
		shakeThread();
	}

	/**
	 * Removes the threads permission to execute.
	 */
	public void revokePermission(){
		permission = false;
	}

	/**
	 * Marks the thread as having completed its run.
	 */
	public void markFinished(){
		complete = true;
	}

	/**
	 * Returns whether the thread has permission.
	 *
	 * @return Whether the thread has permission to run
     */
	public boolean hasPermission() {
		return permission;
	}

	/**
	 * Force pause the thread.
	 */
	public void suspend(){
		suspended = true;
	}

	/**
	 * Un-pause the thread.
	 */
	public void unSuspend(){
		suspended = false;
	}

	/**
	 * Set whether the thread is running.
	 *
	 * @param b Thread is running
     */
	public void setRunning(boolean b){
		running = b;
	}

	/**
	 * Returns whether or not the thread has completed an approved execution.  Returns true
	 * if the thread is suspended or lacks run permission.
	 *
	 * @return Whether the thread's execution is complete
     */
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

	/**
	 * Reset the thread as incomplete and dormant
	 */
	public void reset(){
		running = false;
		complete = false;
	}

	public enum STATES { SUSPENDED, RUNNING, COMPLETE, PERMISSION, WAITING };

	/**
	 * Returns the current state of the thread
	 *
	 * @return State as STATES enum value
     */
	public STATES getState(){
		if (suspended){
			return STATES.SUSPENDED;
		}
		else if (running){
			return STATES.RUNNING;
		}
		else if (complete){
			return STATES.COMPLETE;
		}
		else if (permission){
			return STATES.PERMISSION;
		}
		else {
			return STATES.WAITING;
		}
	}

	/**
	 * Wakes up the thread
	 */
	public void shakeThread() {
		try {
			thread.Shake();
		} catch (NullPointerException e) {}
	}

	/**
	 * Stop and destroy the thread
	 */
    public void killThread(){
        revokePermission();
        thread.Stop();
    }

	@CoverageIgnore
	@Override
	public String toString() {
		return "ThreadItem [name=" + name + ", delay=" + delay + ", next="
				+ next + ", permission=" + permission + ", running=" + running
				+ ", complete=" + complete + ", suspended=" + suspended + "]";
	}
	
}
