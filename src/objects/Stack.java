package objects;

public class Stack < Type extends Object > {

	private Type[] array;
	
	@SuppressWarnings("unchecked")
	public Stack(){
		array =  (Type[]) new Object[0];
	}
	
	public void push(Type a){
		array = augment(array, a);
	}
	
	public Type peek(){
		return array[array.length-1];
	}
	
	public Type pop(){
		Type out = array[array.length-1];
		array = diminish(array);
		return out;
	}
	
	public int size(){
		return array.length;
	}
	
	public boolean isEmpty(){
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
