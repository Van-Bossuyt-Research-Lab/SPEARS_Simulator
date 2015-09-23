package com.csm.rover.simulator.map.populators;

import com.csm.rover.simulator.objects.GridList;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

public abstract class MapPopularsField {

    protected GridList<Integer> values;

    protected boolean mono;

    protected Random rnd;

    protected int defaultVal = 0;

    public MapPopularsField(){
        values = new GridList<Integer>();
        mono = true;
        rnd = new Random();
    }

    //force a target distribution
    public void setValues(Point[] vals){
        values = new GridList<Integer>();
        mono = true;
        for (Point p : vals){
            values.put(10, p.x, p.y);
        }
    }

    public void setValues(Point[] targs, int[] values){
        mono = false;
        this.values = new GridList<Integer>();
        for (int i = 0; i < targs.length; i++){
            this.values.put(values[i], targs[i].x, targs[i].y);
        }
    }

    //get the target distribution
    public GridList<Integer> getValues(){
        return values;
    }

    //Generate a target distribution
    abstract public void generate(boolean mono, Dimension mapSize, double density);

    //is the given point a target
    public boolean isPointMarked(Point loc){
        try {
            return values.get((int)loc.getX(), (int)loc.getY()) > 0;
        }
        catch (NullPointerException e){
            return false;
        }
        catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
    }

    public int getValueAt(Point loc){
        try {
            return values.get((int)loc.getX(), (int)loc.getY());
        }
        catch (NullPointerException e){
            return defaultVal;
        }
        catch (ArrayIndexOutOfBoundsException e){
            return defaultVal;
        }
    }

    public boolean isMono(){
        return mono;
    }

}
