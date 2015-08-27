package objects;

import java.io.Serializable;

public class Pair <T1, T2> implements Serializable {
		
	private T1 first;
	private T2 second;
	
	public Pair(){
		first = null;
		second = null;
	}
	
	public Pair(T1 f, T2 s){
		first = f;
		second = s;
	}
	
	public T1 getFirst() {
		return first;
	}
	
	public void setFirst(T1 first) {
		this.first = first;
	}
	
	public T2 getSecond() {
		return second;
	}
	
	public void setSecond(T2 second) {
		this.second = second;
	}
	
	@Override
	public String toString() {
		return "Pair <" + first + ", " + second + ">";
	}

}