package com.csm.rover.simulator.rover;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.csm.rover.simulator.control.InterfaceCode;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.visual.Panel;
import com.csm.rover.simulator.wrapper.Access;
import com.csm.rover.simulator.wrapper.Globals;

public class RoverHub extends Panel {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<RoverObject> rovers;
	private boolean inHUDmode = false;
	private int[][] standardDisplayLinks;
	private int currentPage = 0;
	
	private int[] HUDDisplayLinks;
	private int numberOfHUDDisplays;
	
	private int numberOfDisplays = 9;
    private ArrayList<RoverDisplayWindow> displayWindows = new ArrayList<RoverDisplayWindow>(numberOfDisplays);

	private JComboBox<String> satSelect;
	private JComboBox<String> rovSelect;
	private JTextField commandInput;

	public RoverHub(Dimension size){
		super(size, "Rover Hub");
		
		standardDisplayLinks = new int[1][numberOfDisplays];
		int x = 0;
		while (x < numberOfDisplays){
			standardDisplayLinks[0][x] = -1;
			x++;
		}
		
		numberOfHUDDisplays = 4;
		if (numberOfHUDDisplays > numberOfDisplays){
			numberOfHUDDisplays = numberOfDisplays;
		}
		HUDDisplayLinks = new int[numberOfHUDDisplays];
		x = 0;
		while (x < numberOfHUDDisplays){
			HUDDisplayLinks[x] = -1;
			x++;
		}
		this.rovers = new ArrayList<RoverObject>();

		initialize();
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (!inHUDmode) {
					if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
						if (currentPage > 0) {
							currentPage--;
							updateDisplays();
						}
					} else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
						if (currentPage < standardDisplayLinks.length - 1) {
							currentPage++;
							updateDisplays();
						}
					}
				} else {
					Access.requestFocusToMap();
				}
			}
		});
		
		setVisible(false);
	}
	
	private void initialize(){
		for (int x = 0; x < numberOfDisplays; x++){
            final int a = x;
            RoverDisplayWindow displayWindow = new RoverDisplayWindow();
			displayWindow.setRoverList(rovers);
			displayWindow.addPageForwardAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    changeLinkedRover(a, 1);
                }
            });
			displayWindow.addPageBackAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    changeLinkedRover(a, -1);
                }
            });
			displayWindows.add(x, displayWindow);
			this.add(displayWindow);
		}
		
		commandInput = new JTextField();
		commandInput.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		commandInput.setSize(300, 28);
		commandInput.setLocation(super.getWidth()-commandInput.getWidth()-10, super.getTopOfPage()+super.getWorkingHeight()*7/8);
		commandInput.setVisible(false);
		commandInput.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					char[] out = (satSelect.getSelectedItem() + " " + rovSelect.getSelectedItem() + " " + commandInput.getText()).toCharArray();
					commandInput.setText("");
					for (char c : out){
						Globals.writeToSerial(c, InterfaceCode.IDcode);
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
					focusOnHub();
				}
			}
		});
		this.add(commandInput);
		
		satSelect = new JComboBox<String>();
		satSelect.setSize(commandInput.getWidth()/2, 28);
		satSelect.setLocation(commandInput.getX(), commandInput.getY()-satSelect.getHeight());
		satSelect.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		satSelect.setVisible(false);
		this.add(satSelect);
		
		rovSelect = new JComboBox<String>();
		rovSelect.setSize(commandInput.getWidth()/2, 28);
		rovSelect.setLocation(commandInput.getX()+commandInput.getWidth()-rovSelect.getWidth(), commandInput.getY()-rovSelect.getHeight());
		rovSelect.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		rovSelect.setVisible(false);
		this.add(rovSelect);
		
	}
	
	public void start(){
		new SynchronousThread(500, new Runnable(){
			public void run(){
				updateDisplays();
			}
		}, SynchronousThread.FOREVER, "Rover Hub Update");
		int x = 0;
		for (RoverObject rover : rovers){
			rover.start();
		}
	}
	
	public void setIdentifiers(Collection<String> rovs, Collection<String> sats){
		rovSelect.removeAllItems();
		for (Object rov : rovs){
			rovSelect.addItem((String)rov);
		}
		satSelect.removeAllItems();
		for (Object sat : sats){
			satSelect.addItem((String)sat);
		}
	}
	
	private void focusOnHub(){
		requestFocus();
		try {
			this.getKeyListeners()[0].keyPressed(null);
		} catch (ArrayIndexOutOfBoundsException e) { e.printStackTrace(); }
	}

	//adds the rover objects to the hub
	public void setRovers(RoverObject[] rovers){
		for (RoverObject rover : rovers){
			this.rovers.add(rover);
		}
		Access.addRoversToMap(rovers);

		standardDisplayLinks = new int[rovers.length][numberOfDisplays];
		int x = 0;
		while (x < rovers.length){
			//set the first n displays to the the nth rover
			standardDisplayLinks[x/numberOfDisplays][x%numberOfDisplays] = x;
			x++;
		}
		while (x < numberOfDisplays*standardDisplayLinks.length){
			standardDisplayLinks[x/numberOfDisplays][x%numberOfDisplays] = -1;
			x++;
		}
		
		updateDisplays();
	}
	
	public void setFocusedRover(int which){
		if (which >= 0 && which < rovers.size()){
			HUDDisplayLinks[0] = which;
			displayWindows.get(0).lockOnRover(which);
			updateDisplays();
			rovSelect.setSelectedIndex(which);
		}
	}
	
	public void updateDisplays(){
		int x = 0;
		while (x < numberOfDisplays){
			if (inHUDmode){
				displayWindows.get(x).update(HUDDisplayLinks[x]);
				if (x+1 == numberOfHUDDisplays){
					break;
				}
			}
			else {
				displayWindows.get(x).update(standardDisplayLinks[currentPage][x]);
			}
			x++;
		}
	}
	
	//change which rover is connected to a certain display
	private void changeLinkedRover(int display, int by){
		if (rovers.size() == 0){
			return;
		}
		if (inHUDmode){
			HUDDisplayLinks[display] += by;
        }
		else {
			standardDisplayLinks[currentPage][display] += by;
		}
		updateDisplays();
	}
	
	//whether or not to show the panel as an overlay on the map
	public void setInHUDMode(boolean b){
		for (RoverDisplayWindow display : displayWindows){
			display.setHUDMode(b);
		}
		if (b){
			displayWindows.get(0).lockOnRover(HUDDisplayLinks[0]);
		}
		else {
			displayWindows.get(0).unlock();
		}
		inHUDmode = b;
		updateDisplays();
		commandInput.setVisible(b);
		satSelect.setVisible(b);
		rovSelect.setVisible(b);
	}
	
	public boolean isInHUDMode(){
		return inHUDmode;
	}
	
	@Override
	public void setVisible(boolean b){
		if (inHUDmode){ //organize everything as an overlaid display
			this.getParent().setComponentZOrder(this, 0);
			super.titleLbl.setVisible(false);
			super.postScript.setVisible(false);
			super.setOpaque(false);
			int spacing = 20;
			displayWindows.get(0).setLocation(spacing, super.getTopOfPage()+spacing);
			displayWindows.get(0).setSize(displayWindows.get(0).getPreferredSize());
			displayWindows.get(1).setLocation(spacing, displayWindows.get(0).getY()+displayWindows.get(0).getHeight()+spacing);
			displayWindows.get(1).setSize(displayWindows.get(1).getPreferredSize());
			displayWindows.get(2).setSize(displayWindows.get(2).getPreferredSize());
			displayWindows.get(2).setLocation(this.getWidth()-displayWindows.get(2).getWidth()-spacing, displayWindows.get(0).getY());
			displayWindows.get(3).setLocation(displayWindows.get(2).getX(), displayWindows.get(1).getY());
			displayWindows.get(3).setSize(displayWindows.get(3).getPreferredSize());
			int x = numberOfHUDDisplays;
			while (x < numberOfDisplays){
				displayWindows.get(x).setVisible(false);
				x++;
			}
		}
		else { //organize everything as an independent panel
			super.titleLbl.setVisible(true);
			super.postScript.setVisible(true);
			super.setOpaque(true);
			int numbAcross = (numberOfDisplays/3+numberOfDisplays%3);
			int spacing = 10;
			int width = (this.getWidth()-spacing*(numbAcross-1))/numbAcross;
			int height = (this.getWorkingHeight()-spacing*(numberOfDisplays/numbAcross))/3;
			int x = 0;
			while (x < numberOfDisplays){
				displayWindows.get(x).setBounds(
						(width+spacing)*(x%numbAcross),
						this.getTopOfPage() + (height+spacing)*(x/numbAcross),
						width,
						height
				);
				displayWindows.get(x).setVisible(true);
				x++;
			}
		}
		super.setVisible(b);
	}
	
}
