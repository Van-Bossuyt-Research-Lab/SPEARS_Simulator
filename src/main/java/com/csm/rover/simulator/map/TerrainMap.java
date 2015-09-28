package com.csm.rover.simulator.map;

import com.csm.rover.simulator.map.modifiers.MapModifier;
import com.csm.rover.simulator.map.populators.MapHazardField;
import com.csm.rover.simulator.map.populators.MapTargetField;
import com.csm.rover.simulator.objects.ArrayGrid;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.FloatArrayArrayGrid;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

public class TerrainMap {
	
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
        return targets.isPointMarked(getMapSquare(pnt));
    }

    public int getTargetValueAt(DecimalPoint pnt){
        return targets.getValueAt(getMapSquare(pnt));
    }

    public MapTargetField getTargets(){
        return targets;
    }

    public void generateHazards(boolean mono, double density){
        hazards.generate(mono, getMapSize(), density);
    }

    public boolean isPointInHazard(DecimalPoint pnt){
        return hazards.isPointMarked(getMapSquare(pnt));
    }

    public int getHazardValueAt(DecimalPoint pnt){
        return hazards.getValueAt(getMapSquare(pnt));
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
	
	public double getValueAtLocation(int x, int y){
		return values.get(x, y);
	}

	public void setDetail(int detail) {
		this.detail = detail;
	}
}
