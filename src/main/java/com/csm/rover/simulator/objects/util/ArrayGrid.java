package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.io.jsonserial.ArrayGridDeserializer;
import com.csm.rover.simulator.objects.io.jsonserial.ArrayGridSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic 2D data structure that functions very much like {@link ArrayList}.  Used to store large, dense sets
 * of data.  The structure is always rectangular.
 *
 * @param <T> Data type to be stored
 */
@JsonSerialize(using=ArrayGridSerializer.class)
@JsonDeserialize(using=ArrayGridDeserializer.class)
public interface ArrayGrid<T> extends Cloneable {

    /**
     * Turns this ArrayGrid into a copy of the provided data.  Overwrites in existing values and size.
     *
     * @param values 2D array of values, get(x, y) == [x][y]
     */
    void loadFromArray(T[][] values);

    /**
     * Fills the entire data structure with a null value to the given size.  Overwrites any existing
     * data or size.
     *
     * @param width New width
     * @param height New height
     */
    void fillToSize(int width, int height);

    /**
     * Fills the entire data structure with the givin value to the given size.  Overwrites
     * any existing data or size.
     *
     * @param width New width
     * @param height New height
     * @param val Default value to use
     */
    void fillToSize(int width, int height, T val);

    /**
     * Places the value at the prescribed location.  Will cause the to grow to accommodate the placement.
     *
     * @param x First coordinate to use
     * @param y Second coordinate to use
     * @param val Value to insert
     */
    void put(int x, int y, T val);

    /**
     * Adds a column at the edge of the data.  See {@link #insertColumnAt(int, List) insertColumnAt({@link #getWidth()}, List)}
     *
     * @param col List of values to add
     */
    void addColumn(List<T> col);

    /**
     * Inserts a column containing the list at the given x coordinate.  Will shift any existing data.  If the
     * list is taller than the structure the structure will grow.
     *
     * @param x Where to add the column
     * @param col Column of data to add
     */
    void insertColumnAt(int x, List<T> col);

    /**
     * Adds a row at the bottom of the data.  See {@link #insertRowAt(int, List) insertRowAt({@link #getHeight()}, List)}
     *
     * @param row List of values to add
     */
    void addRow(List<T> row);

    /**
     * Inserts a row containing the list at the given y coordinate.  Will shift any existing data.  If the
     * list is wider than the structure the structure will grow.
     *
     * @param y Where to add the row
     * @param row Row of data to add
     */
    void insertRowAt(int y, List<T> row);

    /**
     * Returns the value stored at the given coordinates.
     *
     * @param x First coordinate
     * @param y Second coordinate
     *
     * @return The value stored at the point, null if nothing is stored
     *
     * @throws ArrayIndexOutOfBoundsException If either coordinate is negative our outside the structure size
     */
    T get(int x, int y);

    /**
     * Returns the column of data at the given x value.
     *
     * @param x Which column
     *
     * @return A vertical List of values
     *
     * @throws ArrayIndexOutOfBoundsException If x is negative or greater than or equal to {@link #getWidth()}
     */
    List<T> getColumn(int x);

    /**
     * Returns the row of data at the given y value.
     *
     * @param y Which row
     *
     * @return A horizontal List of values
     *
     * @throws ArrayIndexOutOfBoundsException If y is negative or greater than or equal to {@link #getHeight()}
     */
    List<T> getRow(int y);

    /**
     * The number of columns.
     *
     * @return Max x + 1
     */
    int getWidth();

    /**
     * The number of rows.
     *
     * @return Max y + 1
     */
    int getHeight();

    /**
     * The total size of the data.  {@link #getWidth()} * {@link #getHeight()}.
     *
     * @return Total size of the 2D structure
     */
    int size();

    /**
     * A boolean indicator for whether the size of the structure is 0.
     *
     * @return True if there is no data stored
     */
    boolean isEmpty();

    ArrayGrid<T> clone();

}
