package com.csm.rover.simulator.control;

import java.io.Serializable;

public class InstructionObj implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;
	private char dest;
	private String[] cmds;
	private String title;
	private boolean cust;
	private int[] edits;
	private String param  = "";
	
	public InstructionObj(String[] commands, String title, boolean reqText){
		cmds = commands;
		this.title = title;
		cust = reqText;
	}
	
	public void setDestination(char dest){
		this.dest = dest;
	}
	
	public char getDestination(){
		return dest;
	}
	
	public String[] getCommands(){
		return cmds;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	@Override
	public String toString(){
		return title;
	}
	
	public String buildCommand(){
		String out = "";
		int x = 0;
		while (x < cmds.length){
			out += dest + " " + cmds[x];
			x++;
			if (x != cmds.length){
				out += "\n";
			}
		}
		return out;
	}
	
	public boolean getRequiresText(){
		return cust;
	}
	
	public void setEditIndexies(int index1, int index2){
		edits = new int[] { index1, index2 };
	}
	
	public int[] getEditIndexies(){
		return edits;
	}
	
	public void fillParameter(String param){
		this.param = param;
		int x = 0;
		while (x < cmds.length){
			cmds[x] = cmds[x].replaceAll("^&^", param);
			x++;
		}
	}
	
	public String getParameter(){
		return param;
	}
	
	public InstructionObj clone() throws CloneNotSupportedException{
		return (InstructionObj) super.clone();
	}
	
}
