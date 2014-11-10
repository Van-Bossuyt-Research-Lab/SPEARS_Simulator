package wrapper;

import objects.DecimalPoint;
import map.PlanetParametersList;

public class Access {

	static Admin CODE = new Admin();
	
	public static PlanetParametersList getPlanetParameters(){
		return CODE.GUI.TerrainPnl.getParameters();
	}
	
	public static double getMapInclineAtPoint(DecimalPoint loc, double dir){
		return CODE.GUI.TerrainPnl.getIncline(loc, dir);
	}
	
	public static double getMapCrossSlopeAtPoint(DecimalPoint loc, double dir){
		return CODE.GUI.TerrainPnl.getCrossSlope(loc, dir);
	}
	
	public static double getMapTemperatureAtPoint(DecimalPoint loc){
		return CODE.GUI.TerrainPnl.getTemperature(loc);
	}
	
	public static boolean isAtTarget(DecimalPoint loc){
		return CODE.GUI.TerrainPnl.HeightMap.isPointOnTarget(loc);
	}
	
	public static void updateRoverLocation(String name, DecimalPoint loc, double dir){
		CODE.GUI.TerrainPnl.updateRover(name, loc, dir);
	}
}
