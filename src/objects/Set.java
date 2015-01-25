package objects;

public class Set <Type> {

	private Type[] array;
	
	private boolean editing = false;
	
	public Set(){
		array = (Type[]) new Object[0];
	}
	
	public synchronized boolean containts(Type val){
		while (editing) {}
		int layer = 1;
		int loc = array.length / 2;
		while (true){
			if (val.equals(array[loc])){
				return true;
			}
			else {
				layer++;
				if (array.length/Math.pow(2, layer) < 1){
					return false;
				}
				loc += array.length/Math.pow(2, layer)*compare(val.toString(), array[loc].toString());
			}
		}
	}
	
	public synchronized void push(Type val){
		while (editing) {}
		editing = true;
		Type[] out = (Type[]) new Object[array.length + 1];
		int x = 0;
		while (compare(val.toString(), array[x].toString()) < 0){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		x++;
		while (x < out.length){
			out[x] = array[x - 1]; 
			x++;
		}
		array = out;
		editing = false;
	}
	
	private int compare(String a, String b){
		if (a.equals(b)){
			return 0;
		}
		int x = 0;
		while (x < a.length() && x < b.length()){
			if (a.charAt(x) == b.charAt(x)){
				x++;
				continue;
			}
			else if (a.charAt(x) < b.charAt(x)){
				return -1;
			}
			else {
				return 1;
			}
		}
		if (x == a.length()){
			return -1;
		}
		else {
			return 1;
		}
	}
	
	@Override
	public String toString(){
		String out = "<";
		int x = 0;
		while (x < array.length-1){
			out += array[x] + ", ";
			x++;
		}
		out += array[x] + ">";
		return out;
	}
	
}
