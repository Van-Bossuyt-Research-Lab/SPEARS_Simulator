package visual;

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
import java.util.Random;

import objects.DecimalPoint;
import rover.RoverObj;

public class LandMapPanel extends Panel{
	
	private Random rnd = new Random();
	
	private int mapSize = 6;
	private double mapRough = 0.06;
	
	private DecimalPoint focusPoint; //the center point of the display stored as a grid point, not a location point
	private int focusedRover;
	private boolean focusEngauged = false;
	
	private PlasmaPanel HeightMap;
	private double[][] TemperatureMap;
	private double[][] PressureMap;
	private double[][][][] WindMap;
	
	private RoverIcon[] roverIcons;
	private DecimalPoint[][] roverTrails;

	public LandMapPanel(Dimension size, String title){
		super(size, title);
		setBackground(Color.BLACK);
		
		roverIcons = new RoverIcon[0];
		
		HeightMap = new PlasmaPanel();
		HeightMap.genorateLandscape(mapSize, mapRough);
		HeightMap.genorateTargets();
		setFocusPoint(new DecimalPoint(HeightMap.getWidth()/HeightMap.getResolution()/2, HeightMap.getHeight()/HeightMap.getResolution()/2));
		add(HeightMap);
		
		//Allows the user to Pan across the map
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Integer key = e.getKeyCode();
				switch (key) {
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
						setFocusPoint(roverIcons[focusedRover].getMapLocation());
						focusEngauged = true;
					} catch (Exception ex) {}
					break;					
				case KeyEvent.VK_PAGE_DOWN:
					try {
						if (focusEngauged){
							focusedRover = (roverIcons.length + focusedRover - 1) % roverIcons.length;
						}
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
					if (HeightMap.getResolution() > 5){
						HeightMap.setResolution(HeightMap.getResolution()-5);
					}
				}
				redraw();
			}
		});
		
		setFocusPoint(new DecimalPoint(0, 0));
		redraw();
		
		setRoverSwarm(new RoverObj[] { 
				new RoverObj("Rover 1", null, null,	new DecimalPoint(5, 2), 360*rnd.nextDouble())
		});
	}
	
	//changes which point is in the center of the screen
	private void setFocusPoint(DecimalPoint focus){
		focusPoint = focus;
		redraw();
	}
	
	//replaces the height map with the focus point in the center
	private void redraw(){
		HeightMap.setLocation((int)(this.getWidth()/2-focusPoint.getX()*HeightMap.getResolution()-HeightMap.getWidth()/2), 
				(int)(this.getHeight()/2+focusPoint.getY()*HeightMap.getResolution())-HeightMap.getHeight()/2);
		int x = 0;
		while (x < roverIcons.length){
			Point p = mapWorldToScreen(roverIcons[x].getMapLocation());
			System.out.println(p);
			p.translate(HeightMap.getX(), HeightMap.getY());
			roverIcons[x].setLocation(p);
			x++;
		}
	}
	
	//populates an array of roverIcons in the simulator
	public void setRoverSwarm(RoverObj[] rovers){
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
}
