package com.csm.rover.simulator.control;

import java.io.Serializable;

public class SaveFile implements Serializable{

	private static final long serialVersionUID = 1L;
	private String[][] commands;
	private String[][] tooltips;
	private String[][] icons;
	
	private String[] RoverOptions;
	private InstructionObj[][] RoverInstructs;
	private String[] SatOptions;
	private InstructionObj[][] SatInstructs;
	
	public SaveFile(String[][] cmds, String[][] tips, String[][] icons, String[] roverOptions, InstructionObj[][] roverInstructs, String[] satOptions, InstructionObj[][] satInstructs){
		commands = cmds;
		tooltips = tips;
		this.icons = icons;
		RoverOptions = roverOptions;
		RoverInstructs = roverInstructs;
		SatOptions = satOptions;
		SatInstructs = satInstructs;
	}
	
	public String[][] getCommands(){
		return commands;
	}
	
	public String[][] getTooltips(){
		return tooltips;
	}
	
	public String[][] getIcons(){
		return icons;
	}
	
	public String[] getRoverOptions(){
		return RoverOptions;
	}
	
	public InstructionObj[][] getRoverInstructions(){
		return RoverInstructs;
	}
	
	public String[] getSatelliteOptions(){
		return SatOptions;
	}
	
	public InstructionObj[][] getSatelliteInstructions(){
		return SatInstructs;
	}
}
