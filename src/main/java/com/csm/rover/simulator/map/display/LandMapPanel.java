package com.csm.rover.simulator.map.display;

import com.csm.rover.simulator.map.PlanetParametersList;
import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.map.io.TerrainMapWriter;
import com.csm.rover.simulator.map.modifiers.NormalizeMapMod;
import com.csm.rover.simulator.map.modifiers.PlasmaGeneratorMod;
import com.csm.rover.simulator.map.modifiers.SurfaceSmoothMod;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.ImageFileFilter;
import com.csm.rover.simulator.objects.MapFileFilter;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.visual.Panel;
import com.csm.rover.simulator.wrapper.Access;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class LandMapPanel extends Panel{
	
	private static final long serialVersionUID = 3715566110839859923L;

	private PlanetParametersList params;

	private int mapSize = 7;
	private int mapDetail = 3;
	private double mapRough = 0.03;
	
	private DecimalPoint focusPoint; //the center point of the display stored as a grid point, not a location point
	private int focusedRover;
	private boolean focusEngauged = false;
	
	public TerrainMap heightMap;
	public MapDisplayPanel mapPanel;
	
	private JPopupMenu mapOptionsPopMenu;
	private JRadioButtonMenuItem rdbtnmntmShowTargets;
	private JRadioButtonMenuItem rdbtnmntmShowHazards;
	
	private ArrayList<RoverIcon> roverIcons;

	public LandMapPanel(Dimension size, PlanetParametersList params){
		super(size /*new Dimension(700, 500)*/, "Terrain View");
		setBackground(Color.GRAY);
		super.hasImage = false;

		mapOptionsPopMenu = new JPopupMenu();
		mapOptionsPopMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				mapOptionsPopMenu.setVisible(false);
			}
		});
		addPopup(this, mapOptionsPopMenu);

		JMenuItem mntmShowFocusedRover = new JMenuItem("Show Focused Rover");
		mntmShowFocusedRover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					setFocusPoint(roverIcons.get(focusedRover).getMapLocation());
					focusEngauged = !focusEngauged;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		mapOptionsPopMenu.add(mntmShowFocusedRover);

		JMenuItem mntmSaveMapFile = new JMenuItem("Save Map File");
		mntmSaveMapFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser finder = new JFileChooser();
				finder.setFileFilter(new MapFileFilter());
				finder.setApproveButtonText("Save");
				int option = finder.showSaveDialog(getParent());
				if (option == JFileChooser.APPROVE_OPTION) {
					TerrainMapWriter.saveMap(heightMap, new File(finder.getSelectedFile().getAbsolutePath() + ".map"));
				}
			}
		});
		mapOptionsPopMenu.add(mntmSaveMapFile);

		JMenuItem mntmSaveMapImage = new JMenuItem("Save Map Image");
		mntmSaveMapImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser finder = new JFileChooser();
                finder.setFileFilter(new ImageFileFilter());
                finder.setApproveButtonText("Save");
                int option = finder.showSaveDialog(getParent());
                if (option == JFileChooser.APPROVE_OPTION) {
                    TerrainMapWriter.SaveImage(heightMap, MapDisplayPanel.REDtoGREEN, finder.getSelectedFile().getAbsolutePath());
                }
            }
        });
		mapOptionsPopMenu.add(mntmSaveMapImage);
		
		rdbtnmntmShowTargets = new JRadioButtonMenuItem("Show Targets");
		rdbtnmntmShowTargets.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPanel.setTargetsVisible(rdbtnmntmShowTargets.isSelected());
            }
        });
		rdbtnmntmShowTargets.setSelected(true);
		mapOptionsPopMenu.add(rdbtnmntmShowTargets);
		
		rdbtnmntmShowHazards = new JRadioButtonMenuItem("Show Hazards");
		rdbtnmntmShowHazards.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPanel.setHazardsVisible(rdbtnmntmShowHazards.isSelected());
            }
        });
		rdbtnmntmShowHazards.setSelected(true);
		mapOptionsPopMenu.add(rdbtnmntmShowHazards);
		
		roverIcons = new ArrayList<RoverIcon>();
		this.params = params;
		
		heightMap = new TerrainMap();
		heightMap.addMapModifier(new PlasmaGeneratorMod(mapRough));
		heightMap.addMapModifier(new SurfaceSmoothMod());
		heightMap.addMapModifier(new NormalizeMapMod());
		heightMap.generateLandscape(mapSize, mapDetail);

        mapPanel = new MapDisplayPanel();
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                if (arg0.getButton() == MouseEvent.BUTTON3) {
                    mapOptionsPopMenu.show(mapPanel, arg0.getX() - 5, arg0.getY() - 5);
                }
            }
        });
		mapPanel.setResolution(53);
        mapPanel.setTerrainMap(heightMap);
		setFocusPoint(new DecimalPoint(mapPanel.getWidth() / mapPanel.getResolution() / 2, mapPanel.getHeight() / mapPanel.getResolution() / 2));
		add(mapPanel);
		
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
							focusedRover = (focusedRover + 1) % roverIcons.size();
						}
						Access.setFocusDisplayHUD(focusedRover);
						setFocusPoint(roverIcons.get(focusedRover).getMapLocation());
						focusEngauged = true;
					} catch (Exception ex) { ex.printStackTrace(); }
					break;					
				case KeyEvent.VK_PAGE_DOWN:
					try {
						if (focusEngauged){
							focusedRover = (roverIcons.size() + focusedRover - 1) % roverIcons.size();
						}
						Access.setFocusDisplayHUD(focusedRover);
						setFocusPoint(roverIcons.get(focusedRover).getMapLocation());
						focusEngauged = true;
					} catch (Exception ex) { ex.printStackTrace(); }
				break;
				case KeyEvent.VK_SPACE:
					try {
						setFocusPoint(roverIcons.get(focusedRover).getMapLocation());
						focusEngauged = !focusEngauged;
					} catch (Exception ex) { ex.printStackTrace(); }
					break;
				}
			}
		});
		
		//Allows the user to easily scroll in and out
		this.addMouseWheelListener(new MouseWheelListener(){
			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if (arg0.getPreciseWheelRotation() < 0){
                    mapPanel.setResolution(mapPanel.getResolution()+5);
				}
				else {
					if (mapPanel.getResolution()-5 > 0){
                        mapPanel.setResolution(mapPanel.getResolution()-5);
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
        mapPanel.setLocation((int)Math.round(this.getWidth()/2.0-focusPoint.getX()* mapPanel.getResolution()- mapPanel.getWidth()/2.0),
				(int)Math.round(this.getHeight()/2.0+focusPoint.getY()* mapPanel.getResolution()- mapPanel.getHeight()/2.0));
		int x = 0;
		while (x < roverIcons.size()){
			Point p = mapWorldToScreen(roverIcons.get(x).getMapLocation());
			p.translate(mapPanel.getX(), mapPanel.getY());
			roverIcons.get(x).setLocation(p);
			x++;
		}
		try {
			this.getParent().repaint();
		} catch (Exception e) {}
	}
	
	//populates an array of roverIcons in the simulator
	public void setRoverSwarm(ArrayList<RoverObject> rovers){
		roverIcons = new ArrayList<RoverIcon>();
		int x = 0;
		while (x < rovers.size()){
			roverIcons.add(new RoverIcon(rovers.get(x).getName(), rovers.get(x).getLocation(), rovers.get(x).getDirection()));
			this.add(roverIcons.get(x));
			this.setComponentZOrder(roverIcons.get(x), 0);
			x++;
		}
		focusedRover = 0;
		redraw();
	}
	
	//update a rover icon to match current stats
	public boolean updateRover(String name, DecimalPoint loc, double dir){
		int x = 0;
		while (x < roverIcons.size()){
			if (roverIcons.get(x).getName().equals(name)){
				roverIcons.get(x).updatePlacement(loc, dir);
				if (focusEngauged && x == focusedRover){
					setFocusPoint(roverIcons.get(focusedRover).getMapLocation());
				}
				redraw();
				break;
			}
			x++;
		}
		return !(x == roverIcons.size());
	}
	
	//Converts a location from being a value on the map to the equivalent point on the screen
	private Point mapWorldToScreen(DecimalPoint loc){
		return new Point(
            mapPanel.getWidth()/2 + (int)(loc.getX()* mapPanel.getResolution()),
            mapPanel.getHeight()/2 - (int)(loc.getY()* mapPanel.getResolution())
		);
	}
	
	//returns the height of the map at the given point
	public double getHeight(DecimalPoint loc){
		Point mapSquare = heightMap.getMapSquare(loc);
		int x = (int) mapSquare.getX();
		int y = (int) mapSquare.getY();
		DecimalPoint lifePnt = new DecimalPoint(loc.getX() + mapPanel.getWidth()/ mapPanel.getResolution() / 2.0, mapPanel.getWidth()/ mapPanel.getResolution() / 2.0 - loc.getY());
		double locx = ((int)((lifePnt.getX() - (int)lifePnt.getX())*1000) % (1000/ heightMap.getDetail())) / 1000.0 * heightMap.getDetail();
		double locy = ((int)((lifePnt.getY() - (int)lifePnt.getY())*1000) % (1000/ heightMap.getDetail())) / 1000.0 * heightMap.getDetail();
		return getIntermediateValue(heightMap.getValueAtLocation(x, y), heightMap.getValueAtLocation(x + 1, y), heightMap.getValueAtLocation(x, y + 1), heightMap.getValueAtLocation(x + 1, y + 1), locx, locy);
	}
	
	//returns the angle which the rover is facing
	public double getIncline(DecimalPoint loc, double dir){
		double radius = 0.1;
		double h0 = getHeight(loc);
		DecimalPoint loc2 = loc.offset(radius*Math.cos(dir), radius*Math.sin(dir));
		double hnew = getHeight(loc2);
		return Math.atan((hnew-h0)/radius);
	}
	
	//returns the angle perpendicular to the direction the rover is facing
	public double getCrossSlope(DecimalPoint loc, double dir){
		return getIncline(loc, (dir-Math.PI/2.0+Math.PI*2)%(2*Math.PI));
	}
	
	// retrieve temperature from a coordinate, with interpolation
	public double getTemperature(DecimalPoint loc){ 
		return -30;
	}
	
	// interpolates between the corners of a square to find mid-range values
	public double getIntermediateValue(double topleft, double topright, double bottomleft, double bottomright, double relativex, double relativey){ //find the linear approximation of a value within a square where relative x and y are measured fro mtop left
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
