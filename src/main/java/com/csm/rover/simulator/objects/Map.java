package com.csm.rover.simulator.objects;

import java.io.Serializable;

public class Map<KeyType, ItemType> implements Serializable {
	
	private static final long serialVersionUID = 5131097728579814550L;
	
	private KeyType[] keys;
	private ItemType[] items;
	
	private boolean editing = false;
	
	@SuppressWarnings("unchecked")
	public Map(){
		keys =  (KeyType[]) new Object[0];
		items = (ItemType[]) new Object[0];
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void add(KeyType key, ItemType item){
		while (editing) {}
		editing = true;
		if (!contains(key, true)){
			keys = (KeyType[]) augment(keys, key);
			items = (ItemType[]) augment(items, item);
		}
		else {
			set(key, item, true);
		}
		editing = false;
	}
	
	private ItemType get(KeyType key, boolean override){
		while (editing && !override) {}
		for (int x = 0; x < keys.length; x++){
			if (key.equals(keys[x])){
				return items[x];
			}
		}
		return null;
	}
	
	public synchronized ItemType get(KeyType key){
		return get(key, false);
	}
	
	private void set(KeyType key, ItemType item, boolean override){
		while (editing && !override) {}
		editing = true;
		int x = 0;
		while (x < keys.length){
			if (keys[x].equals(key)){
				items[x] = item;
				break;
			}
			x++;
		}
		editing = override;
	}
	
	public synchronized void set(KeyType key, ItemType item){
		set(key, item, false);
	}
	
	private boolean contains(KeyType key, boolean override){
		while (editing && !override) {}
		int x = 0;
		while (x < keys.length){
			if (keys[x].equals(key)){
				return true;
			}
			x++;
		}
		return false;
	}
	
	public synchronized boolean contains(KeyType key){
		return contains(key, false);
	}
	
	public synchronized KeyType[] getKeys(){
		while (editing) {}
		return keys;
	}
	
	public synchronized ItemType[] getValues(){
		while (editing) {}
		return items;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void remove(KeyType key){
		while (editing) {}
		editing = true;
		try {
			items = (ItemType[]) diminish(items, get(key, true));
			keys = (KeyType[]) diminish(keys, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		editing = false;
	}
	
	public synchronized int size(){
		while (editing) {}
		return items.length;
	}
	
	public synchronized boolean isEmpty(){
		while (editing) {}
		return (items.length == 0);
	}
	
	private Object[] augment(Object[] array, Object val){
		Object[] out = new Object[array.length + 1];
		for (int x = 0; x < array.length; x++){
			out[x] = array[x];
		}
		out[array.length] = val;
		return out;
	}
	
	private Object[] diminish(Object[] array, Object val) {
		Object[] out = new Object[array.length - 1];
		int x = 0;
		while (x < out.length && !array[x].equals(val)){
			out[x] = array[x];
			x++;
		}
		if (x == out.length){
			if (!array[x].equals(val)){
				return array;
			}
		}
		else {
			while (x < out.length){
				out[x] = array[x+1];
				x++;
			}
		}
		return out;
	}

	@Override
	public String toString(){
		String out = "<";
		int x = 0;
		while (x < items.length-1){
			out += keys[x] + ":" + items[x] + ", ";
			x++;
		}
		out += keys[x] + ":" + items[x] + ">";
		return out;
	}
	
}