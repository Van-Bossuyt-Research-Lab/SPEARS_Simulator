package com.csm.rover.simulator.map;

import com.csm.rover.simulator.map.modifiers.MapModifier;
import com.csm.rover.simulator.map.populators.MapHazardField;
import com.csm.rover.simulator.map.populators.MapTargetField;
import com.csm.rover.simulator.objects.ArrayGrid;
import com.csm.rover.simulator.objects.DecimalPoint;

import java.awt.Point;
import java.util.ArrayList;

public class TerrainMap {
	
	private ArrayGrid<Float> values;
	private MapTargetField targets;
	private MapHazardField hazards;
	private boolean viewTargets = true;
	private boolean viewHazards = true;
	private boolean monoTargets = true;
	private boolean monoHazards = true;
	private int size;
	private float minval;
	private float maxval;
	private float maxHeight = 4.5f;
	private int detail = 3;

    private ArrayList<MapModifier> mapModifiers;
	
	public TerrainMap(){
        mapModifiers = new ArrayList<MapModifier>();
	}
	
	//Generates the height map using a plasma fractal
	public void generateLandscape(){
        values = new ArrayGrid<Float>();
        values.fillToSize(size * detail, size * detail);
        for (MapModifier mod : mapModifiers){
            mod.modifyMap(values);
        }
	}

    public void addMapModifier(MapModifier mod){
        mapModifiers.add(mod);
    }

	//force a height map into the display
	public void setValues(ArrayGrid<Float> vals){
		values = vals;
		minval = getMin();
		maxval = maxHeight;
	}
	
	public ArrayGrid<Float> getValues(){
		return values;
	}
	
	private double getMax(){
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
	
	private float getMin(){
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
