package wrapper;

import java.awt.event.KeyEvent;
import java.io.File;

import control.InstructionObj;
import control.InterfaceCode;
import objects.DecimalPoint;
import rover.RoverObj;
import map.PlanetParametersList;

public class Access {

	public static Admin CODE = new Admin();
	public static InterfaceCode INTERFACE = new InterfaceCode();
	
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
	
	public static double getMapHeightatPoint(DecimalPoint loc){
		return CODE.GUI.TerrainPnl.getHeight(loc);
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
	
	public static void COMPortChanged(){
		INTERFACE.changeCOMPort();
	}
	
	public static void actionButtonClicked(int section, int which){
		INTERFACE.ActionButtonClicked(section, which);
	}
	
	public static void roverActionsPageChanged(int dir){
		INTERFACE.advanceActionPage(dir, 0);
	}
	
	public static void satelliteActionsPageChanged(int dir){
		INTERFACE.advanceActionPage(dir, 1);
	}
	
	public static void addNoteToLog(String from, String note){
		INTERFACE.writeToLog(from, note);
	}
	
	public static void sendMsg(String msg){
		INTERFACE.writeToSerial(msg);
	}
	
	public static void sendRoverMsg(){
		INTERFACE.sendRoverCommand();
	}
	
	public static void sendSatMessage(){
		INTERFACE.sendSatCommand();
	}
	
	public static void roverLinkClicked(int which){
		switch (which){
		case 0:
			INTERFACE.addRoverBtn();
			break;
		case 1:
			INTERFACE.editRoverBtn1();
			break;
		case 2:
			INTERFACE.deleteRoverBtn1();
			break;
		}
	}
	
	public static void satLinkClicked(int which){
		switch (which){
		case 0:
			INTERFACE.addSatBtn();
			break;
		case 1:
			INTERFACE.editSatBtn1();
			break;
		case 2:
			INTERFACE.deleteSatBtn1();
			break;
		}
	}
	
	public static void cancelInstructs_Clicked(){
		INTERFACE.cancelInstructions();
	}
	
	public static void AddInstructionClicked(){
		INTERFACE.AddInstruction();
	}
	
	public static void SubmitInstructionClicked(){
		INTERFACE.SendInstructions();
	}
	
	public static void InstructionModClicked(int which){
		INTERFACE.editInstructionSendList(which);
	}
	
	public static void RoverListChanged(){
		INTERFACE.RoverCommandChanged();
	}
	
	public static void SatelliteListChanged(){
		INTERFACE.SalelliteCommandChanged();
	}
	
	public static void ParametersListChanged(){
		INTERFACE.ParametersChanged();
	}
	
	public static void InstructionListChanged(){
		if (CODE.GUI.InterfacePnl.InstructionsList.getSelectedIndex() != -1){
			CODE.GUI.InterfacePnl.InstructionsDeleteBtn.setEnabled(true);
			CODE.GUI.InterfacePnl.InstructionsEditBtn.setEnabled(true);
			CODE.GUI.InterfacePnl.InstructionsUpBtn.setEnabled(CODE.GUI.InterfacePnl.InstructionsList.getSelectedIndex() != 0);
			CODE.GUI.InterfacePnl.InstructionsDownBtn.setEnabled(CODE.GUI.InterfacePnl.InstructionsList.getSelectedIndex() != CODE.GUI.InterfacePnl.InstructionsList.getItems().length-1);
		}
		else {
			CODE.GUI.InterfacePnl.InstructionsDeleteBtn.setEnabled(false);
			CODE.GUI.InterfacePnl.InstructionsEditBtn.setEnabled(false);
			CODE.GUI.InterfacePnl.InstructionsUpBtn.setEnabled(false);
			CODE.GUI.InterfacePnl.InstructionsDownBtn.setEnabled(false);
		}
	}
	
	public static void InstructionEditorFinish(boolean addRover, boolean addSat, String title, InstructionObj[] instruct){
		INTERFACE.addInstructionsToList2(addRover, addSat, title, instruct);
	}
	
	public static File getLogFile(){
		return INTERFACE.getLogFile();
	}
}
