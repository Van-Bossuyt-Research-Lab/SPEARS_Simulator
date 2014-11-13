package wrapper;

import java.awt.event.KeyEvent;

import objects.DecimalPoint;
import rover.RoverObj;
import map.PlanetParametersList;

public class Access {

	static Admin CODE = new Admin();
	
	public static void beginSimulation(){
		CODE.beginSimulation();
	}
	
	public static void addRoversToMap(RoverObj[] rovs){ 
		CODE.GUI.TerrainPnl.setRoverSwarm(rovs);
	}
	
	
	private static long lastEvent = 0;
	private static int lastCode = 0;
	public static void handleGlobalKeyEvent(KeyEvent arg){
		if (System.currentTimeMillis()-lastEvent > 300 || lastCode != arg.getKeyCode()){
			lastEvent = System.currentTimeMillis();
			lastCode = arg.getKeyCode();
			CODE.GUI.MasterKeyHandler(arg);
		}
		else {
			lastEvent = System.currentTimeMillis();
			lastCode = arg.getKeyCode();
		}
	}
	
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
	
	public static void requestFocusToMap(){
		CODE.GUI.TerrainPnl.requestFocus();
	}
	
	public static void toggleHUDonMap(){
		CODE.GUI.RoverHubPnl.setInHUDMode(!CODE.GUI.RoverHubPnl.isInHUDMode());
		CODE.GUI.RoverHubPnl.setVisible(!CODE.GUI.RoverHubPnl.isVisible());
	}
	
	public static void setFocusDisplayHUD(int which){
		CODE.GUI.RoverHubPnl.setfocusedRover(which);
	}
}
