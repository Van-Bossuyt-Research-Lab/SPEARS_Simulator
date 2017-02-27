package com.csm.rover.simulator.environments.rover;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.annotations.Map;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.awt.Point;
import java.util.Optional;

@Map(type="Rover")
@JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@JsonIgnoreProperties({"type"})
public class TerrainMap extends EnvironmentMap {

    @JsonProperty("heightMap")
    private ArrayGrid<Float> heightMap;

    @JsonProperty("size")
    private int size;
    @JsonProperty("detail")
    private int detail;

    private TerrainMap(int size, int detail) {
        super("Rover");
        this.size = size;
        this.detail = detail;
    }

    public TerrainMap(){
        this(0, 0);
    }

    @JsonCreator
    public TerrainMap(@JsonProperty("size") int size, @JsonProperty("detail") int detail, @JsonProperty("heightMap") ArrayGrid<Float> values){
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
        if (heightMap.getWidth() != size*detail+1 || heightMap.getHeight() != size*detail+1){
            throw new IllegalArgumentException("The map does not match the given sizes");
        }
    }

    @JsonIgnore
    private Optional<Float> max_val = Optional.empty();
    @JsonIgnore
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

    @JsonIgnore
    public float getMaxValue(){
        if (!max_val.isPresent()){
            findMaxMin();
        }
        return max_val.get();
    }

    @JsonIgnore
    public float getMinValue(){
        if (!min_val.isPresent()){
            findMaxMin();
        }
        return min_val.get();
    }

    /**
     * returns the point in the 2D array closest to the provided map location.
     *
     * @param loc Location on the map from -size/2 to size/2
     *
     * @return Location of point in array from 0 to size
     */
    private Point getMapSquare(DecimalPoint loc){
        return new Point(
                (int)(loc.getX()*detail) + heightMap.getWidth()/2,
                (int)(loc.getY()*detail) + heightMap.getHeight()/2);
    }

    /**
     * Returns the height of the surface at the given point, interpolates between discrete height map values.
     *
     * @param loc Location on the map from -size/2 to size/2
     * @return surface elevation
     */
    public double getHeightAt(DecimalPoint loc){
        Point mapSquare = getMapSquare(loc);
        int x = (int) mapSquare.getX();
        int y = (int) mapSquare.getY();
        loc = new DecimalPoint(loc.getX() + getSize()/2.0,  loc.getY() + getSize()/2.0);
        double locx = loc.getX()*detail - x;
        double locy = loc.getY()*detail - y;
        return getIntermediateValue(heightMap.get(x, y), heightMap.get(x + 1, y), heightMap.get(x, y + 1), heightMap.get(x + 1, y + 1), locx, locy);
    }

    private double getIntermediateValue(double point00, double point01, double point10, double point11,
                                        double x, double y){
        return point00*(1-x)*(1-y) + point01*(1-x)*y + point10*x*(1-y) + point11*x*y;
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

    @Override
    protected boolean isEqual(EnvironmentMap o) {
        if (o instanceof TerrainMap){
            TerrainMap other = (TerrainMap)o;
            return other.size == size && other.detail == detail &&
                    other.heightMap.equals(heightMap);
        }
        else {
            return false;
        }
    }
}
