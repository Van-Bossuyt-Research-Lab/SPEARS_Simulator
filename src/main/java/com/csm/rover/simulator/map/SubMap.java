package com.csm.rover.simulator.map;

import com.csm.rover.simulator.map.modifiers.MapModifier;
import com.csm.rover.simulator.map.populators.MapHazardField;
import com.csm.rover.simulator.map.populators.MapTargetField;
import com.csm.rover.simulator.objects.ArrayGrid;
import com.csm.rover.simulator.objects.ArrayGrid3D;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.FloatArrayArrayGrid;
import com.csm.rover.simulator.objects.FloatArrayArrayArrayGrid;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

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

    //Generates a 3D map of 0 for water 1 for rock (all water right now)
    public void generatePool(int size, int detail){
       new FloatArrayArrayArrayGrid();



        this.size = size*detail;
        this.detail = detail;
        values.fillToSize(this.size, this.size, 0);

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
    public void setValues(int size, int detail, ArrayGrid3D<Float> vals){
        this.size = size;
        this.detail = detail;
        values = vals;
    }

    public ArrayGrid3D<Float> getValues(){
        return values;
    }

    //returns the size of the map in meters (number of squares)
    public int[] getMapSize(){
        int[] sizes;
        sizes = new int[3];
        sizes[0]=values.getWidth()/detail;
        sizes[1]=values.getLength()/detail;
        sizes[2]=values.getHeight()/detail;
        return sizes;
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

    public double getValueAtLocation(int x, int y, int z){
        return values.get(x, y,z);
    }

    //returns the height of the map at the given point
    public double getMaterialAt(DecimalPoint loc){
        Point mapSquare = getMapSquare(loc);
        int x = (int) values.getX();
        int y = (int) values.getY();
        int z = (int) values.getZ();
        DecimalPoint lifePnt = new DecimalPoint(loc.getX() + getMapSize()[0]/2.0, getMapSize()[1]/2.0 - loc.getY(), getMapSize()[2]/2.0 - loc.getZ());
        double locx = ((int)((lifePnt.getX() - (int)lifePnt.getX())*1000) % (1000/detail)) / 1000.0 * detail;
        double locy = ((int)((lifePnt.getY() - (int)lifePnt.getY())*1000) % (1000/detail)) / 1000.0 * detail;
        double locz = ((int)((lifePnt.getZ() - (int)lifePnt.getZ())*1000) % (1000/detail)) / 1000.0 * detail;
        return getIntermediateValue(values.get(x, y,z), values.get(x + 1, y,z), values.get(x, y + 1,z), values.get(x + 1, y + 1,z), locx, locy);
    }

    // interpolates between the corners of a cube to find mid-range values
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