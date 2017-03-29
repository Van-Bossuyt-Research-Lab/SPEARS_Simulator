/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

/**
 * Map implementation for the Sub Platform.
 */
@Map(type="Sub")
@JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@JsonIgnoreProperties({"type"})
public class AquaticMap extends EnvironmentMap {

    @JsonProperty("valueMap")
    private ArrayGrid3D<Float> densityMap;

    @JsonProperty("size")
    private int size;
    @JsonProperty("detail")
    private int detail;

    private AquaticMap(int size, int detail) {
        super("Sub");
        this.size = size;
        this.detail = detail;
    }

    /**
     * Constructs a new AquaticMap based on the provided values as a density map.
     *
     * @param size The size of the map in meters
     * @param detail The resolution of the map as points per meter
     * @param values The density map to use
     *
     * @throws IllegalArgumentException if the size of the ArrayGrid3D does not match the size needed for the given size-detail
     */
    @JsonCreator
    public AquaticMap(@JsonProperty("size") int size, @JsonProperty("detail") int detail, @JsonProperty("valueMap") ArrayGrid3D<Float> values) {
        this(size, detail);
        this.densityMap = values;
        checkSize();
    }

    private void checkSize() {
        if (densityMap.getWidth() != size*detail+1 || densityMap.getHeight() != size*detail+1) {
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
        for (int i = 0; i < densityMap.getWidth(); i++) {
            for (int j = 0; j < densityMap.getHeight(); j++) {
                for (int k = 0; k < densityMap.getDepth(); k++) {
                    float val = densityMap.get(i, j, k);
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

    /**
     * Returns the maximum density of the map.
     *
     * @return Max value
     */
    @JsonIgnore
    public float getMaxValue() {
        if (!max_val.isPresent()) {
            findMaxMin();
        }
        return max_val.get();
    }

    /**
     * Returns the minimum density of the map.
     *
     * @return Min value
     */
    @JsonIgnore
    public float getMinValue() {
        if (!min_val.isPresent()) {
            findMaxMin();
        }
        return min_val.get();
    }

    private Point3D getMapSquare(DecimalPoint3D loc) { // says which display square a given coordinate falls in
        int outx = (int) (loc.getX() * detail) + densityMap.getWidth()/2;
        int outy = (int) (loc.getY() * detail) + densityMap.getHeight()/2;
        int outz = (int) (loc.getZ() * detail) + densityMap.getDepth()/2;
        return new Point3D(outx, outy, outz);
    }

    /**
     * Returns the density of the map at the given point, interpolating between points of the grid.
     *
     * @param loc Point to query
     *
     * @return Density in kg/m^3
     */
    public double getDensityAt(DecimalPoint3D loc){
        Point3D mapSquare = getMapSquare(loc);
        int x = (int) mapSquare.getX();
        int y = (int) mapSquare.getY();
        int z = (int) mapSquare.getZ();
        loc = new DecimalPoint3D(loc.getX()+getSize()/2.0, loc.getY()+getSize()/2.0, loc.getZ()+getSize()/2.0);
        double locx = loc.getX()* detail-x;
        double locy = loc.getY()* detail-y;
        double locz = loc.getZ()* detail-z;
        return getIntermediateValue(densityMap.get(x, y, z), densityMap.get(x, y, z+1), densityMap.get(x, y+1, z), densityMap.get(x+1, y, z),
                densityMap.get(x, y+1, z+1), densityMap.get(x+1, y+1, z), densityMap.get(x+1, y, z+1), densityMap.get(x+1, y+1, z+1),
                locx, locy, locz);
    }

    private double getIntermediateValue(double point000, double point001, double point010, double point100,
                                        double point011, double point110, double point101, double point111,
                                        double x, double y, double z){
        //find the linear approximation of a value within a square where relative x and y are measured from top left
        return point000*(1-x)*(1-y)*(1-z) + point001*(1-x)*(1-y)*z + point010*(1-x)*y*(1-z) + point100*x*(1-y)*(1-z) +
                point011*(1-x)*y*z + point110*x*y*(1-z) + point101*x*(1-y)*z + point111*x*y*z;
    }

    public int getSize() {
        return size;
    }

    public int getDetail() {
        return detail;
    }

    /**
     * Returns a copy of the raw density values the map is based on.
     *
     * @return Density point map
     */
    public ArrayGrid3D<Float> rawValues() {
        return densityMap.clone();
    }

    /**
     * Force implementation of equals as defined by super.
     *
     * @param o Another EnvironmentMap
     * @return Whether the Maps are equal
     */
    @Override
    protected boolean isEqual(EnvironmentMap o) {
        if (o instanceof AquaticMap){
            AquaticMap other = (AquaticMap)o;
            return other.size == size && other.detail == detail &&
                    other.densityMap.equals(this.densityMap);
        }
        else {
            return false;
        }
    }
}

