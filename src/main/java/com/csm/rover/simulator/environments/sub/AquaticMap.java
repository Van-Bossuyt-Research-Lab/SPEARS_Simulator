package com.csm.rover.simulator.environments.sub;
import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.annotations.Map;
import com.csm.rover.simulator.objects.ArrayGrid3D;
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

@Map(type="Sub")
@JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@JsonIgnoreProperties({"type"})
public class AquaticMap extends EnvironmentMap {

    @JsonProperty("SubMap")
    private FloatArrayArrayGrid SubMap;

    @JsonProperty("size")
    private int size;
    @JsonProperty("detail")
    private int detail;

    private AquaticMap(int size, int detail) {
        super("Sub");
        this.size = size;
        this.detail = detail;
    }

    public AquaticMap() {
        this(0, 0);
    }

    @JsonCreator
    public AquaticMap(@JsonProperty("size") int size, @JsonProperty("detail") int detail, @JsonProperty("SubMap") FloatArrayArrayGrid values) {
        this(size, detail);
        this.SubMap = new ArrayGrid3D<Float>(values);
        checkSize();
    }

    public AquaticMap(int size, int detail, Float[][] values) {
        this(size, detail);
        this.SubMap = new FloatArrayArrayGrid(values);
        checkSize();
    }

    private void checkSize() {
        if (SubMap.getWidth() != size * detail || SubMap.getHeight() != size * detail) {
            throw new IllegalArgumentException("The map does not match the given sizes");
        }
    }

    @JsonIgnore
    private Optional<Float> max_val = Optional.empty();
    @JsonIgnore
    private Optional<Float> min_val = Optional.empty();

    private void findMaxMin() {
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (int i = 0; i < SubMap.getWidth(); i++) {
            for (int j = 0; j < SubMap.getHeight(); j++) {
                float val = SubMap.get(i, j);
                if (val > max) {
                    max = val;
                }
                if (val < min) {
                    min = val;
                }
            }
        }
        max_val = Optional.of(max);
        min_val = Optional.of(min);
    }

    @JsonIgnore
    public float getMaxValue() {
        if (!max_val.isPresent()) {
            findMaxMin();
        }
        return max_val.get();
    }

    @JsonIgnore
    public float getMinValue() {
        if (!min_val.isPresent()) {
            findMaxMin();
        }
        return min_val.get();
    }

    private Point getMapSquare(DecimalPoint loc) { // says which display square a given coordinate falls in
        int shift = SubMap.getWidth() / (detail * 2);
        double x = loc.getX() + shift;
        double y = shift - loc.getY();
        int outx = (int) (x * detail);
        int outy = (int) (y * detail);
        return new Point(outx, outy);
    }

    private Point getGridSquare(DecimalPoint loc) {
        Point square = getMapSquare(loc);
        return new Point(square.x / 3, square.y / 3);
    }

    //returns the height of the map at the given point
    public double getHeightAt(DecimalPoint loc) {
        Point mapSquare = getMapSquare(loc);
        int x = (int) mapSquare.getX();
        int y = (int) mapSquare.getY();
        DecimalPoint lifePnt = new DecimalPoint(loc.getX() + getSize() / 2.0, getSize() / 2.0 - loc.getY());
        double locx = ((int) ((lifePnt.getX() - (int) lifePnt.getX()) * 1000) % (1000 / detail)) / 1000.0 * detail;
        double locy = ((int) ((lifePnt.getY() - (int) lifePnt.getY()) * 1000) % (1000 / detail)) / 1000.0 * detail;
        return getIntermediateValue(SubMap.get(x, y), SubMap.get(x + 1, y), SubMap.get(x, y + 1), SubMap.get(x + 1, y + 1), locx, locy);
    }

    private double getIntermediateValue(double topleft, double topright, double bottomleft, double bottomright, double relativex, double relativey) { //find the linear approximation of a value within a square where relative x and y are measured fro mtop left
        if (relativex > relativey) { //top right triangle
            return (topright - topleft) * relativex - (topright - bottomright) * relativey + topleft;
        } else if (relativex < relativey) { //bottom left triangle
            return (bottomright - bottomleft) * relativex + (bottomleft - topleft) * relativey + topleft;
        } else { //center line
            return ((bottomright - topleft) * relativex + topleft);
        }
    }

    public int getSize() {
        return size;
    }

    public int getDetail() {
        return detail;
    }

    public ArrayGrid<Float> rawValues() {
        return SubMap.clone();
    }
}

