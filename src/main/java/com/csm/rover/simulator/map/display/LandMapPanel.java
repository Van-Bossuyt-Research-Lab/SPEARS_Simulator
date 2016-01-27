package com.csm.rover.simulator.map.display;

import com.csm.rover.simulator.map.PlanetParametersList;
import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.rover.RoverHub;
import com.csm.rover.simulator.rover.RoverObject;
import com.csm.rover.simulator.visual.Panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

public class LandMapPanel extends Panel{
	
	private static final long serialVersionUID = 3715566110839859923L;

	private RoverHub roverHubPnl;

	private PlanetParametersList params;

	private int mapSize = 33;
	private int mapDetail = 3;
	private double mapRough = 0.03;
    private int initial_map_res = 53;
    private int map_zoom = 5;
	
	private DecimalPoint focusPoint; //the center point of the display stored as a grid point, not a location point
	private int focusedRover;
	private boolean focusEngaged = false;
	
	public TerrainMap heightMap;
	public MapDisplayPanel mapPanel;
	
	private ArrayList<RoverIcon> roverIcons;

	public LandMapPanel(Dimension size, PlanetParametersList params, RoverHub roverHub, ArrayList<RoverObject> rovers, TerrainMap map){
        super(size /*new Dimension(700, 500)*/, "Terrain View");
		setBackground(Color.GRAY);
		super.hasImage = false;

        this.roverHubPnl = roverHub;
		addPopup(this, new MapOptionsMenu(this));
		
		roverIcons = new ArrayList<RoverIcon>();
		this.params = params;
		
		heightMap = map;

        mapPanel = new MapDisplayPanel();
		mapPanel.setTerrainMap(heightMap);
        mapPanel.setResolution(initial_map_res);
		setFocusPoint(new DecimalPoint(mapPanel.getWidth() / mapPanel.getResolution() / 2, mapPanel.getHeight() / mapPanel.getResolution() / 2));
		add(mapPanel);
		
		//Allows the user to Pan across the map
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Integer key = e.getKeyCode();
				switch (key) {
				case KeyEvent.VK_H:
                    roverHubPnl.setInHUDMode(!roverHubPnl.isInHUDMode());
            		roverHubPnl.setVisible(!roverHubPnl.isVisible());
					break;
				case KeyEvent.VK_LEFT:
					setFocusPoint(new DecimalPoint((int)focusPoint.getX()-1, (int)focusPoint.getY()));
					focusEngaged = false;
					break;
				case KeyEvent.VK_RIGHT:
					setFocusPoint(new DecimalPoint((int)focusPoint.getX()+1, (int)focusPoint.getY()));
					focusEngaged = false;
					break;
				case KeyEvent.VK_UP:
					setFocusPoint(new DecimalPoint((int)focusPoint.getX(), (int)focusPoint.getY()+1));
					focusEngaged = false;
					break;
				case KeyEvent.VK_DOWN:
					setFocusPoint(new DecimalPoint((int)focusPoint.getX(), (int)focusPoint.getY()-1));
					focusEngaged = false;
					break;
				case KeyEvent.VK_PAGE_UP:
					try {
						if (focusEngaged){
							focusedRover = (focusedRover + 1) % roverIcons.size();
						}
                        roverHubPnl.setFocusedRover(focusedRover);
						setFocusPoint(roverIcons.get(focusedRover).getMapLocation());
						focusEngaged = true;
					} catch (Exception ex) { ex.printStackTrace(); }
					break;
				case KeyEvent.VK_PAGE_DOWN:
					try {
						if (focusEngaged){
							focusedRover = (roverIcons.size() + focusedRover - 1) % roverIcons.size();
						}
                        roverHubPnl.setFocusedRover(focusedRover);
						setFocusPoint(roverIcons.get(focusedRover).getMapLocation());
						focusEngaged = true;
					} catch (Exception ex) { ex.printStackTrace(); }
				break;
				case KeyEvent.VK_SPACE:
					try {
						setFocusPoint(roverIcons.get(focusedRover).getMapLocation());
						focusEngaged = !focusEngaged;
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
                    mapPanel.setResolution(mapPanel.getResolution()+map_zoom);
				}
				else {
					mapPanel.setResolution(mapPanel.getResolution()-map_zoom);
				}
				redraw();
			}
		});

		this.setRoverSwarm(rovers);

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

	protected void centerOnFocusedRover(){
		try {
			setFocusPoint(roverIcons.get(focusedRover).getMapLocation());
			focusEngaged = !focusEngaged;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
				if (focusEngaged && x == focusedRover){
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
	
	// retrieve temperature from a coordinate, with interpolation
	public double getTemperature(DecimalPoint loc){
		//TODO add temp map
		return -30;
	}
}
