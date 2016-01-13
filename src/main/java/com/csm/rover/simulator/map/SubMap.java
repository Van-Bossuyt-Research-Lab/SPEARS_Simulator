package com.csm.rover.simulator.map;

import com.csm.rover.simulator.map.modifiers.MapModifier;
import com.csm.rover.simulator.map.populators.MapHazardField;
import com.csm.rover.simulator.map.populators.MapTargetField;
import com.csm.rover.simulator.objects.ArrayGrid;
import com.csm.rover.simulator.objects.ArrayGrid3D;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.FloatArrayArrayGrid;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

public class SubMap {

    private ArrayGrid3D<Float> values;
    private MapTargetField targets;
    private MapHazardField hazards;
    private int size;
    private int detail = 3;

    private ArrayList<MapModifier> mapModifiers;

    public SubMap(){
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
        ArrayGrid[] grid3d;
        for(int x=0;x< this.size;x++){
            grid3d[x]=values;
        }
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

    //returns the size of the map in meters (number of squares)
    public Dimension getMapSize(){
        return new Dimension(values.getWidth()/detail, values.getHeight()/detail, values.getLength());
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

    //returns the height of the map at the given point
    public double getHeightAt(DecimalPoint loc){
        Point mapSquare = getMapSquare(loc);
        int x = (int) mapSquare.getX();
        int y = (int) mapSquare.getY();
        DecimalPoint lifePnt = new DecimalPoint(loc.getX() + getMapSize().getWidth()/2.0, getMapSize().getHeight()/2.0 - loc.getY());
        double locx = ((int)((lifePnt.getX() - (int)lifePnt.getX())*1000) % (1000/detail)) / 1000.0 * detail;
        double locy = ((int)((lifePnt.getY() - (int)lifePnt.getY())*1000) % (1000/detail)) / 1000.0 * detail;
        return getIntermediateValue(values.get(x, y), values.get(x + 1, y), values.get(x, y + 1), values.get(x + 1, y + 1), locx, locy);
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