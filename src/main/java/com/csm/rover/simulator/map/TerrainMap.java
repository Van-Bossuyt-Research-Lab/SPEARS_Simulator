package com.csm.rover.simulator.map;

import com.csm.rover.simulator.map.modifiers.MapModifier;
import com.csm.rover.simulator.map.populators.MapHazardField;
import com.csm.rover.simulator.map.populators.MapTargetField;
import com.csm.rover.simulator.objects.ArrayGrid;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.FloatArrayArrayGrid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

public class TerrainMap {
    private static final Logger LOG = LogManager.getLogger(TerrainMap.class);
	
	private ArrayGrid<Float> values;
	private MapTargetField targets;
	private MapHazardField hazards;
	private int size;
	private float maxHeight = 4.5f;
	private int detail = 3;

    private ArrayList<MapModifier> mapModifiers;
	
	public TerrainMap(){
        mapModifiers = new ArrayList<MapModifier>();
        targets = new MapTargetField();
        hazards = new MapHazardField();
    }
	
	//Generates the height map using a plasma fractal
	public void generateLandscape(int size, int detail){
        values = new FloatArrayArrayGrid();
        this.size = size*detail;
        this.detail = detail;
        values.fillToSize(this.size, this.size, 0f);
        for (MapModifier mod : mapModifiers){
            mod.modifyMap(values);
        }
	}

    public void generateTargets(boolean mono, double density){
        targets.generate(mono, getMapSize(), density);
    }

    public boolean isPointAtTarget(DecimalPoint pnt){
        return targets.isPointMarked(getGridSquare(pnt));
    }

    public int getTargetValueAt(DecimalPoint pnt){
        return targets.getValueAt(getGridSquare(pnt));
    }

    public MapTargetField getTargets(){
        return targets;
    }

    public void generateHazards(boolean mono, double density){
        hazards.generate(mono, getMapSize(), density);
    }

    public boolean isPointInHazard(DecimalPoint pnt){
        return hazards.isPointMarked(getGridSquare(pnt));
    }

    public int getHazardValueAt(DecimalPoint pnt){
        return hazards.getValueAt(getGridSquare(pnt));
    }

    public MapHazardField getHazards(){
        return hazards;
    }

    public void addMapModifier(MapModifier mod){
        mapModifiers.add(mod);
    }

	//force a height map into the display
	public void setValues(int size, int detail, ArrayGrid<Float> vals){
		this.size = size;
		this.detail = detail;
		values = vals;
	}
	
	public ArrayGrid<Float> getValues(){
		return values;
	}

	public double getMax(){
		return this.maxHeight;
	}

	public double getTrueMax(){
		double max = 0;
		for (int x = 0; x < values.getWidth(); x++){
			for (int y = 0; y < values.getHeight(); y++){
				if (values.get(x, y) > max){
					max = values.get(x, y);
				}
			}
		}
		return max;
	}
	
	public double getMin(){
		float min = Float.MAX_VALUE;
		for (int x = 0; x < values.getWidth(); x++){
			for (int y = 0; y < values.getHeight(); y++){
				if (values.get(x, y) < min){
					min = values.get(x, y);
				}
			}
		}
		return min;
	}

    //returns the size of the map in meters (number of squares)
    public Dimension getMapSize(){
        return new Dimension(values.getWidth()/detail, values.getHeight()/detail);
    }
	
	public int getDetail(){
		return detail;
	}

	public Point getMapSquare(DecimalPoint loc){ // says which display square a given coordinate falls in
		int shift = values.getWidth() / (detail * 2);
		double x = loc.getX() + shift;
		double y = shift - loc.getY();
		int outx = (int)(x*detail);
		int outy = (int)(y*detail);
		return new Point(outx, outy);
	}

    public Point getGridSquare(DecimalPoint loc){
        Point square = getMapSquare(loc);
        return new Point(square.x/3, square.y/3);
    }
	
	public double getValueAtLocation(int x, int y){
		return values.get(x, y);
	}

    //returns the height of the map at the given point
    public double getHeightAt(DecimalPoint loc){
        Point mapSquare = getMapSquare(loc);
        int x = (int) mapSquare.getX();
        int y = (int) mapSquare.getY();
        DecimalPoint lifePnt = new DecimalPoint(loc.getX() + getMapSize().getWidth()/2.0, getMapSize().getHeight()/2.0 - loc.getY());
        double locx = ((int)((lifePnt.getX() - (int)lifePnt.getX())*1000) % (1000/detail)) / 1000.0 * detail;
        double locy = ((int)((lifePnt.getY() - (int)lifePnt.getY())*1000) % (1000/detail)) / 1000.0 * detail;
        try {
            return getIntermediateValue(values.get(x, y), values.get(x + 1, y), values.get(x, y + 1), values.get(x + 1, y + 1), locx, locy);
        }
        catch (NullPointerException e){
            LOG.log(Level.FATAL, "Rover fell of the map", e);
            System.exit(1);
            return 0;
        }
    }

    //returns the angle which the rover is facing
    public double getIncline(DecimalPoint loc, double dir){
        double radius = 0.1;
        double h0 = getHeightAt(loc);
        DecimalPoint loc2 = loc.offset(radius*Math.cos(dir), radius*Math.sin(dir));
        double hnew = getHeightAt(loc2);
        return Math.atan((hnew - h0) / radius);
    }

    //returns the angle perpendicular to the direction the rover is facing
    public double getCrossSlope(DecimalPoint loc, double dir){
        return getIncline(loc, (dir - Math.PI / 2.0 + Math.PI * 2) % (2 * Math.PI));
    }

    // interpolates between the corners of a square to find mid-range values
    private double getIntermediateValue(double topleft, double topright, double bottomleft, double bottomright, double relativex, double relativey){ //find the linear approximation of a value within a square where relative x and y are measured fro mtop left
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

	public void setDetail(int detail) {
		this.detail = detail;
	}
}
