package com.csm.rover.simulator.environments.sub;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.annotations.Map;
import com.csm.rover.simulator.objects.ArrayGrid3D;
import com.csm.rover.simulator.objects.util.DecimalPoint3D;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javafx.geometry.Point3D;

import java.util.Optional;

@Map(type="Sub")
@JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@JsonIgnoreProperties({"type"})
public class AquaticMap extends EnvironmentMap {

    @JsonProperty("SubMap")
    private ArrayGrid3D SubMap;

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
        this(size, detail, (ArrayGrid3D)values);
    }

    public AquaticMap(int size, int detail, ArrayGrid3D values) {
        this(size, detail);
        this.SubMap = values;
        checkSize();
    }

    private void checkSize() {
        if (SubMap.getWidth() != size * detail || SubMap.getHeight() != size * detail) {
            System.out.print(size*detail);
            System.out.print(SubMap.getWidth());
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
                for (int k = 0; k < SubMap.getLength(); k++) {
                    float val = SubMap.get(i, j, k);
                    if (val > max) {
                        max = val;
                    }
                    if (val < min) {
                        min = val;
                    }
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

    private Point3D getMapSquare(DecimalPoint3D loc) { // says which display square a given coordinate falls in
        int shift = SubMap.getWidth() / (detail * 2);
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        int outx = (int) (x * detail);
        int outy = (int) (y * detail);
        int outz = (int) (z * detail);
        return new Point3D(x, y, z);
    }

    private Point3D getGridSquare(DecimalPoint3D loc) {
        Point3D square = getMapSquare(loc);
        return new Point3D(square.getX() / 3, square.getY() / 3, square.getZ()/3);
    }

    public double getValueAt(DecimalPoint3D loc){
        Point3D mapSquare = getMapSquare(loc);
        int x = (int) mapSquare.getX();
        int y = (int) mapSquare.getY();
        int z = (int) mapSquare.getZ();
        double locx = (loc.getX()-(int)loc.getX()) * detail;
        double locy = (loc.getY()-(int)loc.getY()) * detail;
        double locz = (loc.getZ()-(int)loc.getZ()) * detail;
        return SubMap.get(x,y,z);
    }

    private double getIntermediateValue(double topleft, double topright, double bottomleft, double bottomright, double relativex, double relativey, double relativez){ //find the linear approximation of a value within a square where relative x and y are measured fro mtop left
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

    public int getSize() {
        return size;
    }

    public int getDetail() {
        return detail;
    }

    public ArrayGrid3D rawValues() {
        return SubMap;
    }
}

