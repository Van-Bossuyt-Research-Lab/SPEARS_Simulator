package objects;

import java.io.Serializable;

public class Stack < Type extends Object > implements Serializable {

	private Type[] array;
	
	private boolean editing = false;
	
	@SuppressWarnings("unchecked")
	public Stack(){
		array =  (Type[]) new Object[0];
	}
	
	public synchronized void push(Type a){
		while (editing) {}
		editing = true;
		array = augment(array, a);
		editing = false;
	}
	
	public synchronized Type peek(){
		while (editing) {}
		return array[array.length-1];
	}
	
	public synchronized Type pop(){
		while (editing) {}
		editing = true;
		Type out = array[array.length-1];
		array = diminish(array);
		editing = false;
		return out;
	}
	
	public synchronized int size(){
		while (editing) {}
		return array.length;
	}
	
	public synchronized boolean isEmpty(){
		while (editing) {}
		return (array.length == 0);
	}
	
	@Override
	public String toString(){
		String out = "TOS <";
		int x = 0;
		while (x < size()-1){
			out += array[x] + ", ";
			x++;
		}
		out += array[x] + "> BOS";
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
			out[x] = array[x];
		}
		return out;
	}
}
