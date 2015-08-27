package com.csm.rover.simulator.objects;

import java.io.Serializable;

public class Queue < Type extends Object > implements Serializable {

	private Type[] array;
	
	private boolean editing = false;
	
	@SuppressWarnings("unchecked")
	public Queue(){
		array =  (Type[]) new Object[0];
	}
	
	public Queue(Queue<Type> q){
		array = q.array;
	}
	
	@Override
	public Queue<Type> clone(){
		while (editing) {}
		Queue<Type> out = new Queue<Type>();
		int x = 0;
		while (x < array.length){
			out.push(array[x]);
			x++;
		}
		return out;
	}
	
	public synchronized void push(Type a){
		while (editing) {}
		editing = true;
		array = augment(array, a);
		editing = false;
	}
	
	public synchronized Type peek(){
		while (editing) {}
		if (isEmpty(true)){
			return null;
		}
		return array[0];
	}
	
	public synchronized Type pop(){
		while (editing) {}
		editing = true;
		if (isEmpty(true)){
			return null;
		}
		Type out = array[0];
		array = diminish(array);
		editing = false;
		return out;
	}

	public synchronized int size(){
		while (editing) {}
		return array.length;
	}
	
	private boolean isEmpty(boolean override){
		while (editing && !override) {}
		return (array.length == 0);
	}
	
	public synchronized boolean isEmpty(){
		return isEmpty(false);
	}
	
	@Override
	public String toString(){
		if (isEmpty()){
			return "Queue Empty";
		}
		String out = "FOQ <";
		int x = 0;
		while (x < size()-1){
			out += array[x] + ", ";
			x++;
		}
		out += array[x] + "> BOQ";
		return out;
	}
	
	@SuppressWarnings("unchecked")
	private Type[] augment(Type[] array, Type val){
		Type[] out = (Type[]) new Object[array.length + 1];
		for (int x = 0; x < array.length; x++){
			out[x] = array[x];
		}
		out[array.length] = val;
		return out;
	}
	
	@SuppressWarnings("unchecked")
	private Type[] diminish(Type[] array) {
		Type[] out = (Type[]) new Object[array.length - 1];
		for (int x = 0; x < out.length; x++){
			out[x] = array[x+1];
		}
		return out;
	}
	
}
