package com.csm.rover.simulator.environments.rover;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;

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

    public int getSize(){
        return size;
    }

    public int getDetail(){
        return detail;
    }

    

}
