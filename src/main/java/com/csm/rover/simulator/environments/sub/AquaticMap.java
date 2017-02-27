package com.csm.rover.simulator.environments.sub;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.environments.annotations.Map;
import com.csm.rover.simulator.objects.util.ArrayGrid3D;
import com.csm.rover.simulator.objects.util.DecimalPoint3D;
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

    @JsonProperty("valueMap")
    private ArrayGrid3D<Float> SubMap;

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
    public AquaticMap(@JsonProperty("size") int size, @JsonProperty("detail") int detail, @JsonProperty("valueMap") ArrayGrid3D<Float> values) {
        this(size, detail);
        this.SubMap = values;
        checkSize();
    }

    private void checkSize() {
        if (SubMap.getWidth() != size*detail+1 || SubMap.getHeight() != size*detail+1) {
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
                for (int k = 0; k < SubMap.getDepth(); k++) {
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
        int outx = (int) (loc.getX() * detail) + SubMap.getWidth()/2;
        int outy = (int) (loc.getY() * detail) + SubMap.getHeight()/2;
        int outz = (int) (loc.getZ() * detail) + SubMap.getDepth()/2;
        return new Point3D(outx, outy, outz);
    }

    public double getValueAt(DecimalPoint3D loc){
        Point3D mapSquare = getMapSquare(loc);
        int x = (int) mapSquare.getX();
        int y = (int) mapSquare.getY();
        int z = (int) mapSquare.getZ();
        loc = new DecimalPoint3D(loc.getX()+getSize()/2.0, loc.getY()+getSize()/2.0, loc.getZ()+getSize()/2.0);
        double locx = loc.getX()* detail-x;
        double locy = loc.getY()* detail-y;
        double locz = loc.getZ()* detail-z;
        return getIntermediateValue(SubMap.get(x, y, z), SubMap.get(x, y, z+1), SubMap.get(x, y+1, z), SubMap.get(x+1, y, z),
                SubMap.get(x, y+1, z+1), SubMap.get(x+1, y+1, z), SubMap.get(x+1, y, z+1), SubMap.get(x+1, y+1, z+1),
                locx, locy, locz);
    }

    private double getIntermediateValue(double point000, double point001, double point010, double point100,
                                        double point011, double point110, double point101, double point111,
                                        double x, double y, double z){ //find the linear approximation of a value within a square where relative x and y are measured fro mtop left
        return point000*(1-x)*(1-y)*(1-z) + point001*(1-x)*(1-y)*z + point010*(1-x)*y*(1-z) + point100*x*(1-y)*(1-z) +
                point011*(1-x)*y*z + point110*x*y*(1-z) + point101*x*(1-y)*z + point111*x*y*z;
    }

    private double calcArm(double x, double y, double z){
        return Math.sqrt(x*x + y*y + z*z);
    }

    public int getSize() {
        return size;
    }

    public int getDetail() {
        return detail;
    }

    public ArrayGrid3D<Float> rawValues() {
        return SubMap;
    }

    @Override
    protected boolean isEqual(EnvironmentMap o) {
        if (o instanceof AquaticMap){
            AquaticMap other = (AquaticMap)o;
            return other.size == size && other.detail == detail &&
                    other.SubMap.equals(this.SubMap);
        }
        else {
            return false;
        }
    }
}

