package map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import control.InterfacePanel;
import objects.DecimalPoint;
import objects.ImageFileFilter;
import objects.MapFileFilter;
import wrapper.Access;
import wrapper.Globals;
import rover.RoverObject;
import visual.ImageButton;
import visual.Panel;
import visual.PlasmaPanel;

import javax.swing.JPopupMenu;

import java.awt.Component;

import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LandMapPanel extends Panel{
	
	private PlanetParametersList params;
	
	private int mapSize = 7;
	private double mapRough = 0.03;
	
	private DecimalPoint focusPoint; //the center point of the display stored as a grid point, not a location point
	private int focusedRover;
	private boolean focusEngauged = false;
	
	public PlasmaPanel HeightMap;
	private double[][] TemperatureMap;
	private double[][] PressureMap;
	private double[][][][] WindMap;
	
	private JPopupMenu MapOptionsPopMenu;
	private JMenuItem mntmShowFocusedRover;
	private JMenuItem mntmSaveMapFile;
	private JMenuItem mntmSaveMapImage;
	private JRadioButtonMenuItem rdbtnmntmShowTargets;
	private JRadioButtonMenuItem rdbtnmntmShowHazards;
	
	private RoverIcon[] roverIcons;
	private DecimalPoint[][] roverTrails;

	public LandMapPanel(Dimension size, PlanetParametersList params){
		super(size /*new Dimension(700, 500)*/, "Terrain View");
		setBackground(Color.GRAY);
		super.hasImage = false;
		
		MapOptionsPopMenu = new JPopupMenu();
		MapOptionsPopMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				MapOptionsPopMenu.setVisible(false);
			}
		});
		addPopup(this, MapOptionsPopMenu);
		
		mntmShowFocusedRover = new JMenuItem("Show Focused Rover");
		mntmShowFocusedRover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					setFocusPoint(roverIcons[focusedRover].getMapLocation());
					focusEngauged = !focusEngauged;
				} catch (Exception ex) {}
			}
		});
		MapOptionsPopMenu.add(mntmShowFocusedRover);
		
		mntmSaveMapFile = new JMenuItem("Save Map File");
		mntmSaveMapFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser finder = new JFileChooser();
				finder.setFileFilter(new MapFileFilter());
				finder.setApproveButtonText("Save");
				int option = finder.showSaveDialog(getParent());
				if (option == JFileChooser.APPROVE_OPTION){
					HeightMap.saveMap(new File(finder.getSelectedFile().getAbsolutePath() + ".map"));
				}
			}
		});
		MapOptionsPopMenu.add(mntmSaveMapFile);
		
		mntmSaveMapImage = new JMenuItem("Save Map Image");
		mntmSaveMapImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser finder = new JFileChooser();
				finder.setFileFilter(new ImageFileFilter());
				finder.setApproveButtonText("Save");
				int option = finder.showSaveDialog(getParent());
				if (option == JFileChooser.APPROVE_OPTION){
					HeightMap.SaveImage(HeightMap.getValues(), HeightMap.getDetail(), PlasmaPanel.REDtoGREEN, finder.getSelectedFile().getAbsolutePath());
				}
			}
		});
		MapOptionsPopMenu.add(mntmSaveMapImage);
		
		rdbtnmntmShowTargets = new JRadioButtonMenuItem("Show Targets");
		rdbtnmntmShowTargets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HeightMap.setTargetsVisible(rdbtnmntmShowTargets.isSelected());
			}
		});
		rdbtnmntmShowTargets.setSelected(true);
		MapOptionsPopMenu.add(rdbtnmntmShowTargets);
		
		rdbtnmntmShowHazards = new JRadioButtonMenuItem("Show Hazards");
		rdbtnmntmShowHazards.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HeightMap.setHazardsVisible(rdbtnmntmShowHazards.isSelected());
			}
		});
		rdbtnmntmShowHazards.setSelected(true);
		MapOptionsPopMenu.add(rdbtnmntmShowHazards);
		
		roverIcons = new RoverIcon[0];
		this.params = params;
		
		HeightMap = new PlasmaPanel();
		HeightMap.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON3){
					MapOptionsPopMenu.show(HeightMap, arg0.getX()-5, arg0.getY()-5);
				}
			}
		});
		HeightMap.genorateLandscape(mapSize, mapRough);
		HeightMap.genorateTargets();
		HeightMap.genorateHazards();
		HeightMap.setResolution(53);
		setFocusPoint(new DecimalPoint(HeightMap.getWidth()/HeightMap.getResolution()/2, HeightMap.getHeight()/HeightMap.getResolution()/2));
		add(HeightMap);
		
		//Allows the user to Pan across the map
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Integer key = e.getKeyCode();
				switch (key) {
				case KeyEvent.VK_H:
					Access.toggleHUDonMap();
					break;
				case KeyEvent.VK_LEFT:
					setFocusPoint(new DecimalPoint((int)focusPoint.getX()-1, (int)focusPoint.getY()));
					focusEngauged = false;
					break;
				case KeyEvent.VK_RIGHT:
					setFocusPoint(new DecimalPoint((int)focusPoint.getX()+1, (int)focusPoint.getY()));
					focusEngauged = false;
					break;
				case KeyEvent.VK_UP:
					setFocusPoint(new DecimalPoint((int)focusPoint.getX(), (int)focusPoint.getY()+1));
					focusEngauged = false;
					break;
				case KeyEvent.VK_DOWN:
					setFocusPoint(new DecimalPoint((int)focusPoint.getX(), (int)focusPoint.getY()-1));
					focusEngauged = false;
					break;
				case KeyEvent.VK_PAGE_UP:
					try {
						if (focusEngauged){
							focusedRover = (focusedRover + 1) % roverIcons.length;
						}
						Access.setFocusDisplayHUD(focusedRover);
						setFocusPoint(roverIcons[focusedRover].getMapLocation());
						focusEngauged = true;
					} catch (Exception ex) {}
					break;					
				case KeyEvent.VK_PAGE_DOWN:
					try {
						if (focusEngauged){
							focusedRover = (roverIcons.length + focusedRover - 1) % roverIcons.length;
						}
						Access.setFocusDisplayHUD(focusedRover);
						setFocusPoint(roverIcons[focusedRover].getMapLocation());
						focusEngauged = true;
					} catch (Exception ex) {}
					break;
				case KeyEvent.VK_SPACE:
					try {
						setFocusPoint(roverIcons[focusedRover].getMapLocation());
						focusEngauged = !focusEngauged;
					} catch (Exception ex) {}
					break;
				}
			}
		});
		
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}			
		});
		
		//Allows the user to easily scroll in and out
		this.addMouseWheelListener(new MouseWheelListener(){
			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if (arg0.getPreciseWheelRotation() < 0){
					HeightMap.setResolution(HeightMap.getResolution()+5);
				}
				else {
					if (HeightMap.getResolution()-5 > 0){
						HeightMap.setResolution(HeightMap.getResolution()-5);
					}
				}
				redraw();
			}
		});
		
		setFocusPoint(new DecimalPoint(0, 0));
		redraw();
	}
	
	//Returns the assigned parameters list of the loaded planet
	public PlanetParametersList getParameters(){
		return params;
	}
	
	//changes which point is in the center of the screen
	private void setFocusPoint(DecimalPoint focus){
		focusPoint = focus;
		redraw();
	}
	
	//replaces the height map with the focus point in the center
	private void redraw(){
		HeightMap.setLocation((int)Math.round(this.getWidth()/2.0-focusPoint.getX()*HeightMap.getResolution()-HeightMap.getWidth()/2.0), 
				(int)Math.round(this.getHeight()/2.0+focusPoint.getY()*HeightMap.getResolution()-HeightMap.getHeight()/2.0));
		int x = 0;
		while (x < roverIcons.length){
			Point p = mapWorldToScreen(roverIcons[x].getMapLocation());
			p.translate(HeightMap.getX(), HeightMap.getY());
			roverIcons[x].setLocation(p);
			x++;
		}
		try {
			this.getParent().repaint();
		} catch (Exception e) {}
	}
	
	//populates an array of roverIcons in the simulator
	public void setRoverSwarm(RoverObject[] rovers){
		roverIcons = new RoverIcon[rovers.length];
		int x = 0;
		while (x < roverIcons.length){
			roverIcons[x] = new RoverIcon(rovers[x].getName(), rovers[x].getLocation(), rovers[x].getDirection());
			this.add(roverIcons[x]);
			this.setComponentZOrder(roverIcons[x], 0);
			x++;
		}
		focusedRover = 0;
		redraw();
	}
	
	//update a rover icon to match current stats
	public boolean updateRover(String name, DecimalPoint loc, double dir){
		int x = 0;
		while (x < roverIcons.length){
			if (roverIcons[x].getName().equals(name)){
				roverIcons[x].updatePlacement(loc, dir);
				if (focusEngauged && x == focusedRover){
					setFocusPoint(roverIcons[focusedRover].getMapLocation());
				}
				redraw();
				break;
			}
			x++;
		}
		return !(x == roverIcons.length);
	}
	
	//Converts a location from being a value on the map to the equivalent point on the screen
	private Point mapWorldToScreen(DecimalPoint loc){
		return new Point(
			HeightMap.getWidth()/2 + (int)(loc.getX()*HeightMap.getResolution()),
			HeightMap.getHeight()/2 - (int)(loc.getY()*HeightMap.getResolution())
		);
	}
	
	//returns the array point in which the map location falls
	private Point getMapSquare(DecimalPoint loc){ // says which display square a given coordinate falls in
		double shift = HeightMap.getWidth()/HeightMap.getResolution() / 2.0;
		double x = loc.getX() + shift;
		double y = shift - loc.getY();
		int outx = (int)(x*HeightMap.getDetail());
		int outy = (int)(y*HeightMap.getDetail());
		return new Point(outx, outy);
	}
	private Point getMapSquare(DecimalPoint loc, boolean offset){ // says which display square a given coordinate falls in
		double x = loc.getX();
		double y = loc.getY();
		if (offset){
			double shift = HeightMap.getWidth()/HeightMap.getResolution() / 2.0;
			x = loc.getX() + shift;
			y = shift - loc.getY();
		}
		int outx = (int)(x*HeightMap.getDetail());
		int outy = (int)(y*HeightMap.getDetail());
		return new Point(outx, outy);
	}
	
	//returns the height of the map at the given point
	public double getHeight(DecimalPoint loc){
		Point mapSquare = getMapSquare(loc);
		int x = (int) mapSquare.getX();
		int y = (int) mapSquare.getY();
		DecimalPoint lifePnt = new DecimalPoint(loc.getX() + HeightMap.getWidth()/HeightMap.getResolution() / 2.0, HeightMap.getWidth()/HeightMap.getResolution() / 2.0 - loc.getY());
		double locx = ((int)((lifePnt.getX() - (int)lifePnt.getX())*1000) % (int)(1000/HeightMap.getDetail())) / 1000.0 * HeightMap.getDetail();
		double locy = ((int)((lifePnt.getY() - (int)lifePnt.getY())*1000) % (int)(1000/HeightMap.getDetail())) / 1000.0 * HeightMap.getDetail();
		return getIntermidiateValue(HeightMap.getValueAtLocation(x, y), HeightMap.getValueAtLocation(x+1, y), HeightMap.getValueAtLocation(x, y+1), HeightMap.getValueAtLocation(x+1, y+1), locx, locy);
	}
	
	//returns the angle which the rover is facing
	public double getIncline(DecimalPoint loc, double dir){
		Point mapSquare = getMapSquare(loc);
		int x = (int) mapSquare.getX();
		int y = (int) mapSquare.getY();
		DecimalPoint lifePnt = new DecimalPoint(loc.getX() + HeightMap.getWidth()/HeightMap.getResolution() / 2.0, HeightMap.getWidth()/HeightMap.getResolution() / 2.0 - loc.getY());
		double locx = ((int)((lifePnt.getX() - (int)lifePnt.getX())*1000) % (int)(1000/HeightMap.getDetail())) / 1000.0 * HeightMap.getDetail();
		double locy = ((int)((lifePnt.getY() - (int)lifePnt.getY())*1000) % (int)(1000/HeightMap.getDetail())) / 1000.0 * HeightMap.getDetail();
		double h0 = getIntermidiateValue(HeightMap.getValueAtLocation(x, y), HeightMap.getValueAtLocation(x+1, y), HeightMap.getValueAtLocation(x, y+1), HeightMap.getValueAtLocation(x+1, y+1), locx, locy);
		DecimalPoint point2 = lifePnt.offset(Math.cos(dir), -Math.sin(dir));
		x = (int) getMapSquare(point2, false).getX();
		y = (int) getMapSquare(point2, false).getY();
		locx = (point2.getX() - (lifePnt.getX()-locx/HeightMap.getDetail())) * HeightMap.getDetail();
		locy = (point2.getY() - (lifePnt.getY()-locy/HeightMap.getDetail())) * HeightMap.getDetail();
		double hnew = getIntermidiateValue(HeightMap.getValueAtLocation(x, y), HeightMap.getValueAtLocation(x+1, y), HeightMap.getValueAtLocation(x, y+1), HeightMap.getValueAtLocation(x+1, y+1), locx, locy);
		return Math.atan(hnew-h0);
	}
	
	//returns the angle perpendicular to the direction the rover is facing
	public double getCrossSlope(DecimalPoint loc, double dir){
		return getIncline(loc, dir-Math.PI/2.0);
	}
	
	// retrieve temperature from a coordinate, with interpolation
	public double getTemperature(DecimalPoint loc){ 
		//uniform temp map
		return -30;
		//variable temp map
		/*
		try {
			Point mapSquare = getMapSquare(loc);
			int x = (int) mapSquare.getX();
			int y = (int) mapSquare.getY();
			double locx = ((int)((loc.getX() - (int)loc.getX())*1000) % (int)(1000/HeightMap.getDetail())) / 1000.0;
			double locy = ((int)((loc.getY() - (int)loc.getY())*1000) % (int)(1000/HeightMap.getDetail())) / 1000.0;
			try {
				return getIntermidiateValue(TemperatureMap[x][y], TemperatureMap[x+1][y], TemperatureMap[x][y+1], TemperatureMap[x+1][y+1], locx, locy);
			}
			catch (Exception e){
				//TODO actually initialize this map
				return 0;
			}
		}
		catch (Exception e){
			Globals.reportError("MapFrame", "getTempAtLoc", e);
			return 0;
		}
		*/
	}
	
	// interpolates between the corners of a square to find mid-range values
	public double getIntermidiateValue(double topleft, double topright, double bottomleft, double bottomright, double relativex, double relativey){ //find the linear approximation of a value within a square where relative x and y are measured fro mtop left
		if (relativex > relativey){ //top right triangle
			return (topright - topleft) * relativex - (topright - bottomright) * relativey + topleft;
		}
		else if (relativex < relativey){ //bottom left triangle
			return (bottomright - bottomleft) * relativex + (bottomleft - topleft) * relativey + topleft;
		}
		else { //center line
			return ((bottomright - topleft) * relativex + topleft);
		}
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
