package satellite;

public class SatelliteObject {

	private String name;
	private String tag;
	
	public SatelliteObject(String n, String t){
		name = n;
		tag = t;
	}
	
	public String getName(){
		return name;
	}
	
	public String getTag(){
		return tag;
	}
	
}
