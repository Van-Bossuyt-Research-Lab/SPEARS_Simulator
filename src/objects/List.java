package objects;

import java.io.Serializable;

public class List <Type> implements Serializable {

	private Type[] array;
	public int length;
	
	private boolean editing = false;
	
	@SuppressWarnings("unchecked")
	public List(){
		array = (Type[]) new Object[0];
		length = 0;
	}
	
	public synchronized Type get(int index){
		while (editing) {}
		if (index >= 0 && index < array.length){
			return array[index];
		}
		else {
			return null;
		}
	}
	
	public synchronized void set(int index, Type val){
		while (editing) {}
		editing = true;
		if (index >= 0 && index < array.length){
			array[index] = val;
		}
		else if (index >= array.length){
			add(val, true);
		}
		editing = false;
	}
	
	private void add(Type val, boolean override){
		while (editing && !override) {}
		editing = true;
		array = Augment(array, val);
		length++;
		editing = override;
	}
	
	public synchronized void add(Type val){
		add(val, false);
	}
	
	public synchronized void remove(Type val){
		while (editing) {}
		editing = true;
		array = remove(array, val);
		editing = false;
	}
	
	@SuppressWarnings("unchecked")
	private Type[] Augment(Type[] array, Type val){
		Type[] out = (Type[]) new Object[array.length + 1];
		for (int i = 0; i < array.length; i++){
			out[i] = array[i];
		}
		out[array.length] = val;
		return out;
	}
	
	@SuppressWarnings("unchecked")
	private Type[] remove(Type[] array, Type val) {
		Type[] out = (Type[]) new Object[array.length - 1];
		int x = 0;
		while (x < out.length && !array[x].equals(val)){
			out[x] = array[x];
			x++;
		}
		if (x != out.length){
			while (x < out.length){
				out[x] = array[x+1];
				x++;
			}
		}
		else {
			return array;
		}
		return out;
	}
	
	@Override
	public String toString(){
		String out = "<";
		int x = 0;
		while (x < array.length-1){
			out += x + ":" + array[x] + ", ";
			x++;
		}
		out += x + ":" + array[x] + ">";
		return out;
	}
	
}
