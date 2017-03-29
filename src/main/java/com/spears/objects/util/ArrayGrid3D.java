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

package com.spears.objects.util;
import com.spears.objects.io.jsonserial.ArrayGrid3DDeserializer;
import com.spears.objects.io.jsonserial.ArrayGrid3DSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using=ArrayGrid3DSerializer.class)
@JsonDeserialize(using=ArrayGrid3DDeserializer.class)
public interface ArrayGrid3D<T> extends Cloneable {

    /**
     * Sets the values and size of the ArrayGrid to those of the array.
     *
     * @param values 3D array to wrap
     */
    void loadFromArray(T[][][] values);

    /**
     * Initializes the ArrayGrid to given size and fills it with null.
     *
     * @param width Desired width
     * @param height Desired height
     * @param depth Desired depth
     */
    void fillToSize(int width, int height, int depth);

    /**
     * Initializes the ArrayGrid to the given size and fill it with the provided value.
     *
     * @param width Desired width
     * @param height Desired height
     * @param depth Desired depth
     * @param val The default value to fill the grid with
     */
    void fillToSize(int width, int height,  int depth, T val);

    /**
     * Places the given value at the coordinates.  Expands the size of the ArrayGrid to include the point in
     * necessary.
     *
     * @param x Coordinate in width
     * @param y Coordinate in height
     * @param z Coordinate in depth
     * @param val Value to put
     */
    void put(int x, int y, int z, T val);

    /**
     * Returns the value stored at the given point.
     *
     * @param x Coordinate in width
     * @param y Coordinate in height
     * @param z Coordinate in depth
     *
     * @return The value or null if unfilled
     *
     * @throws ArrayIndexOutOfBoundsException If the point is not in the ArrayGrid's bounds
     */
    T get(int x, int y, int z);

    /**
     * Adds a y-z pane to the grid at the far end of width.  The layer will be grown to fit the grid, or
     * the grid will be grown to fit the layer.
     *
     * @param lay The layer to add
     */
    void addXLayer(ArrayGrid<T> lay);

    /**
     * Inserts a y-z pane at the given x value, expanding the width as needed.  The layer will be grown to
     * fit the grid, or the grid will be grown to fit the layer.
     *
     * @param x Where to insert the layer
     * @param lay The layer to insert
     */
    void insertXLayerAt(int x, ArrayGrid<T> lay);

    /**
     * Adds a x-z pane to the grid at the far end of height.  The layer will be grown to fit the grid, or
     * the grid will be grown to fit the layer.
     *
     * @param lay The layer to add
     */
    void addYLayer(ArrayGrid<T> lay);

    /**
     * Inserts a x-z pane at the given y value, expanding the height as needed.  The layer will be grown to
     * fit the grid, or the grid will be grown to fit the layer.
     *
     * @param y Where to insert the layer
     * @param lay The layer to insert
     */
    void insertYLayerAt(int y, ArrayGrid<T> lay);

    /**
     * Adds a x-y pane to the grid at the far end of width.  The layer will be grown to fit the grid, or
     * the grid will be grown to fit the layer.
     *
     * @param lay The layer to add
     */
    void addZLayer(ArrayGrid<T> lay);

    /**
     * Inserts a x-y pane at the given z value, expanding the depth as needed.  The layer will be grown to
     * fit the grid, or the grid will be grown to fit the layer.
     *
     * @param z Where to insert the layer
     * @param lay The layer to insert
     */
    void insertZLayerAt(int z, ArrayGrid<T> lay);

    /**
     * Returns a slice in the y-z plane at the given x coordinate
     *
     * @param x Location of the layer
     *
     * @return y-z Slice of the grid
     *
     * @throws ArrayIndexOutOfBoundsException If x is not within the bounds of width
     */
    ArrayGrid<T> getXLayer(int x);

    /**
     * Returns a slice in the x-z plane at the given y coordinate
     *
     * @param y Location of the layer
     *
     * @return x-z Slice of the grid
     *
     * @throws ArrayIndexOutOfBoundsException If y is not within the bounds of height
     */
    ArrayGrid<T> getYLayer(int y);

    /**
     * Returns a slice in the x-y plane at the given z coordinate
     *
     * @param z Location of the layer
     *
     * @return x-y Slice of the grid
     *
     * @throws ArrayIndexOutOfBoundsException If z is not within the bounds of depth
     */
    ArrayGrid<T> getZLayer(int z);

    /**
     * Returns the width of the grid.
     *
     * @return Length in the x direction
     */
    int getWidth();

    /**
     * Returns the height of the grid.
     *
     * @return Length in the y direction
     */
    int getHeight();

    /**
     * Returns the depth of the grid.
     *
     * @return Length in the z direction
     */
    int getDepth();

    /**
     * Returns the number of space in the grid.
     *
     * @return width*height*depth
     */
    int size();

    /**
     * Returns whether there is anything in the grid.
     *
     * @return size == 0
     */
    boolean isEmpty();

    /**
     * Produces a clone of the grid.
     *
     * @return Exact but distinct copy of this grid
     */
    ArrayGrid3D<T> clone();

}
