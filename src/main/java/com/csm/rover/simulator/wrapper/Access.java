package com.csm.rover.simulator.wrapper;

import java.awt.event.KeyEvent;
import java.io.File;

import com.csm.rover.simulator.control.InstructionObj;
import com.csm.rover.simulator.control.InterfaceCode;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.map.PlanetParametersList;

//TODO get rid of this class
public class Access {

	public static Admin CODE = new Admin();
	public static InterfaceCode INTERFACE = new InterfaceCode();
	
	public static void addRoversToMap(RoverObject[] rovs){ 
		Admin.GUI.TerrainPnl.setRoverSwarm(rovs);
	}
	
	private static long lastEvent = 0;
	private static int lastCode = 0;
	public static void handleGlobalKeyEvent(KeyEvent arg){
		if (System.currentTimeMillis()-lastEvent > 300 || lastCode != arg.getKeyCode()){
			lastEvent = System.currentTimeMillis();
			lastCode = arg.getKeyCode();
			Admin.GUI.MasterKeyHandler(arg);
		}
		else {
			lastEvent = System.currentTimeMillis();
			lastCode = arg.getKeyCode();
		}
	}
	
	public static PlanetParametersList getPlanetParameters(){
		return Admin.GUI.TerrainPnl.getParameters();
	}
	
	public static double getMapInclineAtPoint(DecimalPoint loc, double dir){
		return Admin.GUI.TerrainPnl.getIncline(loc, dir);
	}
	
	public static double getMapCrossSlopeAtPoint(DecimalPoint loc, double dir){
		return Admin.GUI.TerrainPnl.getCrossSlope(loc, dir);
	}
	
	public static double getMapHeightatPoint(DecimalPoint loc){
		return Admin.GUI.TerrainPnl.getHeight(loc);
	}
	
	public static double getMapTemperatureAtPoint(DecimalPoint loc){
		return Admin.GUI.TerrainPnl.getTemperature(loc);
	}
	
	public static boolean isAtTarget(DecimalPoint loc){
		return Admin.GUI.TerrainPnl.HeightMap.isPointOnTarget(loc);
	}
	
	public static int getTargetValue(DecimalPoint loc){
		return Admin.GUI.TerrainPnl.HeightMap.getTargetValue(loc);
	}
	
	public static boolean isInHazard(DecimalPoint loc){
		return Admin.GUI.TerrainPnl.HeightMap.isPointInHazard(loc);
	}
	
	public static int getHazardValue(DecimalPoint loc){
		return Admin.GUI.TerrainPnl.HeightMap.getHazardValue(loc);
	}
	
	public static void updateRoverLocation(String name, DecimalPoint loc, double dir){
		Admin.GUI.TerrainPnl.updateRover(name, loc, dir);
	}
	
	public static void requestFocusToMap(){
		Admin.GUI.TerrainPnl.requestFocus();
	}
	
	public static void toggleHUDonMap(){
		Admin.GUI.RoverHubPnl.setInHUDMode(!Admin.GUI.RoverHubPnl.isInHUDMode());
		Admin.GUI.RoverHubPnl.setVisible(!Admin.GUI.RoverHubPnl.isVisible());
	}
	
	public static void setFocusDisplayHUD(int which){
		Admin.GUI.RoverHubPnl.setfocusedRover(which);
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
		if (Admin.GUI.InterfacePnl.InstructionsList.getSelectedIndex() != -1){
			Admin.GUI.InterfacePnl.InstructionsDeleteBtn.setEnabled(true);
			Admin.GUI.InterfacePnl.InstructionsEditBtn.setEnabled(true);
			Admin.GUI.InterfacePnl.InstructionsUpBtn.setEnabled(Admin.GUI.InterfacePnl.InstructionsList.getSelectedIndex() != 0);
			Admin.GUI.InterfacePnl.InstructionsDownBtn.setEnabled(Admin.GUI.InterfacePnl.InstructionsList.getSelectedIndex() != Admin.GUI.InterfacePnl.InstructionsList.getItems().length-1);
		}
		else {
			Admin.GUI.InterfacePnl.InstructionsDeleteBtn.setEnabled(false);
			Admin.GUI.InterfacePnl.InstructionsEditBtn.setEnabled(false);
			Admin.GUI.InterfacePnl.InstructionsUpBtn.setEnabled(false);
			Admin.GUI.InterfacePnl.InstructionsDownBtn.setEnabled(false);
		}
	}
	
	public static void InstructionEditorFinish(boolean addRover, boolean addSat, String title, InstructionObj[] instruct){
		INTERFACE.addInstructionsToList2(addRover, addSat, title, instruct);
	}
	
	public static File getLogFile(){
		return INTERFACE.getLogFile();
	}
}
