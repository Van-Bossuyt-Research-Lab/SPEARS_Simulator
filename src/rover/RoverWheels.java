package rover;

public enum RoverWheels {
	
	FL(0),
	FRONT_LEFT(0),
	FR(1),
	FRONT_RIGHT(1),
	BL(2),
	BACK_LEFT(2),
	BR(3),
	BACK_RIGHT(3);
	
	private int value;
	
	RoverWheels(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public boolean equals(RoverWheels other){
		return value == other.value;
	}
}