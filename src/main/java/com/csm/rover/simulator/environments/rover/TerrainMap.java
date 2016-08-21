package com.csm.rover.simulator.environments.rover;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;

import java.awt.Point;
import java.util.Optional;

public class TerrainMap extends EnvironmentMap {

    private ArrayGrid<Float> heightMap;

    private int size, detail;

    private TerrainMap(int size, int detail) {
        super("Rover");
        this.size = size;
        this.detail = detail;
    }

    public TerrainMap(int size, int detail, ArrayGrid<Float> values){
        this(size, detail);
        this.heightMap = new FloatArrayArrayGrid((FloatArrayArrayGrid)values);
        checkSize();
    }

    public TerrainMap(int size, int detail, Float[][] values){
        this(size, detail);
        this.heightMap = new FloatArrayArrayGrid(values);
        checkSize();
    }

    private void checkSize(){
        if (heightMap.getWidth() != size*detail || heightMap.getHeight() != size*detail){
            throw new IllegalArgumentException("The map does not match the given sizes");
        }
    }

    private Optional<Float> max_val = Optional.empty();
    private Optional<Float> min_val = Optional.empty();

    private void findMaxMin(){
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (int i = 0; i < heightMap.getWidth(); i++){
            for (int j = 0; j < heightMap.getHeight(); j++){
                float val = heightMap.get(i, j);
                if (val > max){
                    max = val;
                }
                if (val < min){
                    min = val;
                }
            }
        }
        max_val = Optional.of(max);
        min_val = Optional.of(min);
    }

    public float getMaxValue(){
        if (!max_val.isPresent()){
            findMaxMin();
        }
        return max_val.get();
    }

    public float getMinValue(){
        if (!min_val.isPresent()){
            findMaxMin();
        }
        return min_val.get();
    }

    private Point getMapSquare(DecimalPoint loc){ // says which display square a given coordinate falls in
        int shift = heightMap.getWidth() / (detail * 2);
        double x = loc.getX() + shift;
        double y = shift - loc.getY();
        int outx = (int)(x*detail);
        int outy = (int)(y*detail);
        return new Point(outx, outy);
    }

    private Point getGridSquare(DecimalPoint loc){
        Point square = getMapSquare(loc);
        return new Point(square.x/3, square.y/3);
    }

    //returns the height of the map at the given point
    public double getHeightAt(DecimalPoint loc){
        Point mapSquare = getMapSquare(loc);
        int x = (int) mapSquare.getX();
        int y = (int) mapSquare.getY();
        DecimalPoint lifePnt = new DecimalPoint(loc.getX() + getSize()/2.0, getSize()/2.0 - loc.getY());
        double locx = ((int)((lifePnt.getX() - (int)lifePnt.getX())*1000) % (1000/detail)) / 1000.0 * detail;
        double locy = ((int)((lifePnt.getY() - (int)lifePnt.getY())*1000) % (1000/detail)) / 1000.0 * detail;
        return getIntermediateValue(heightMap.get(x, y), heightMap.get(x + 1, y), heightMap.get(x, y + 1), heightMap.get(x + 1, y + 1), locx, locy);
    }

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

    public int getSize(){
        return size;
    }

    public int getDetail(){
        return detail;
    }

    public ArrayGrid<Float> rawValues(){
        return heightMap.clone();
    }

}
