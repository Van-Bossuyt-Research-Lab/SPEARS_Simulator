package com.csm.rover.simulator.control;

import com.csm.rover.simulator.wrapper.SerialBuffers;

import java.io.File;

public class InterfaceAccess {

    public static InterfaceCode CODE;
    public static InterfacePanel GUI;

    public static InterfaceCode setCode(InterfacePanel gui, SerialBuffers serialBuffers){
        CODE = new InterfaceCode(gui, serialBuffers);
        GUI = gui;
        return CODE;
    }

    public static void COMPortChanged(){
        CODE.changeCOMPort();
    }

    public static void actionButtonClicked(int section, int which){
        CODE.ActionButtonClicked(section, which);
    }

    public static void roverActionsPageChanged(int dir){
        CODE.advanceActionPage(dir, 0);
    }

    public static void satelliteActionsPageChanged(int dir){
        CODE.advanceActionPage(dir, 1);
    }

    public static void addNoteToLog(String from, String note){
        CODE.writeToLog(from, note);
    }

    public static void sendMsg(String msg){
        CODE.writeToSerial(msg);
    }

    public static void sendRoverMsg(){
        CODE.sendRoverCommand();
    }

    public static void sendSatMessage(){
        CODE.sendSatCommand();
    }

    public static void roverLinkClicked(int which){
        switch (which){
            case 0:
                CODE.addRoverBtn();
                break;
            case 1:
                CODE.editRoverBtn1();
                break;
            case 2:
                CODE.deleteRoverBtn1();
                break;
        }
    }

    public static void satLinkClicked(int which){
        switch (which){
            case 0:
                CODE.addSatBtn();
                break;
            case 1:
                CODE.editSatBtn1();
                break;
            case 2:
                CODE.deleteSatBtn1();
                break;
        }
    }

    public static void cancelInstructs_Clicked(){
        CODE.cancelInstructions();
    }

    public static void AddInstructionClicked(){
        CODE.AddInstruction();
    }

    public static void SubmitInstructionClicked(){
        CODE.SendInstructions();
    }

    public static void InstructionModClicked(int which){
        CODE.editInstructionSendList(which);
    }

    public static void RoverListChanged(){
        CODE.RoverCommandChanged();
    }

    public static void SatelliteListChanged(){
        CODE.SalelliteCommandChanged();
    }

    public static void ParametersListChanged(){
        CODE.ParametersChanged();
    }

    public static void InstructionListChanged(){
        if (GUI.InstructionsList.getSelectedIndex() != -1){
            GUI.InstructionsDeleteBtn.setEnabled(true);
            GUI.InstructionsEditBtn.setEnabled(true);
            GUI.InstructionsUpBtn.setEnabled(GUI.InstructionsList.getSelectedIndex() != 0);
            GUI.InstructionsDownBtn.setEnabled(GUI.InstructionsList.getSelectedIndex() != GUI.InstructionsList.getItems().size()-1);
        }
        else {
            GUI.InstructionsDeleteBtn.setEnabled(false);
            GUI.InstructionsEditBtn.setEnabled(false);
            GUI.InstructionsUpBtn.setEnabled(false);
            GUI.InstructionsDownBtn.setEnabled(false);
        }
    }

    public static void InstructionEditorFinish(boolean addRover, boolean addSat, String title, InstructionObj[] instruct){
        CODE.addInstructionsToList2(addRover, addSat, title, instruct);
    }

    public static File getLogFile(){
        return CODE.getLogFile();
    }

}
