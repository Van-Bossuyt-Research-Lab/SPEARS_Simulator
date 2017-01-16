package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.CoverageIgnore;

import java.util.ArrayList;
import java.util.List;

public class GenericArrayGrid<T> implements ArrayGrid<T>,Cloneable {

    private ArrayList<List<T>> grid;

    /**
     * Creates an empty 2D data structure
     */
    public GenericArrayGrid(){
        grid = new ArrayList<>();
    }

    /**
     * Creates a 2D data structure using the provided values.  See {@link #loadFromArray(Object[][])}
     *
     * @param values 2D array of values, get(x, y) == [x][y]
     */
    public GenericArrayGrid(T[][] values){
        loadFromArray(values);
    }


    /**
     * Creates a clone of the original ArrayGrid.  See {@link #clone()}
     *
     * @param original ArrayGrid to be cloned
     */
    public GenericArrayGrid(ArrayGrid<T> original){
        grid = new ArrayList<>();
        for (int x = 0; x < original.getWidth(); x++){
            for (int y = 0; y < original.getHeight(); y++){
                put(x, y, original.get(x, y));
            }
        }
    }

    /**
     * Turns this ArrayGrid into a copy of the provided data.  Overwrites in existing values and size.
     *
     * @param values 2D array of values, get(x, y) == [x][y]
     */
    @Override
    public void loadFromArray(T[][] values){
        grid = new ArrayList<>();
        for (int x = 0; x < values.length; x++){
            for (int y = 0; y < values[x].length; y++){
                put(x, y, values[x][y]);
            }
        }
    }

    /**
     * Fills the entire data structure with a null value to the given size.  Overwrites any existing
     * data or size.
     *
     * @param width New width
     * @param height New height
     */
    @Override
    public void fillToSize(int width, int height){
        fillToSize(width, height, null);
    }

    /**
     * Fills the entire data structure with the givin value to the given size.  Overwrites
     * any existing data or size.
     *
     * @param width New width
     * @param height New height
     * @param val Default value to use
     */
    @Override
    public void fillToSize(int width, int height, T val){
        grid = new ArrayList<>();
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                put(x, y, val);
            }
        }
    }

    /**
     * Places the value at the prescribed location.  Will cause the to grow to accommodate the placement.
     *
     * @param x First coordinate to use
     * @param y Second coordinate to use
     * @param val Value to insert
     */
    @Override
    public void put(int x, int y, T val){
        while (x >= getWidth()){
            addColumn(new ArrayList<>());
        }
        while (y >= getHeight()){
            addRow(new ArrayList<>());
        }
        grid.get(x).set(y, val);
    }

    /**
     * Adds a column at the edge of the data.  See {@link #insertColumnAt(int, List) insertColumnAt({@link #getWidth()}, List)}
     *
     * @param col List of values to add
     */
    @Override
    public void addColumn(List<T> col){
        insertColumnAt(getWidth(), col);
    }

    /**
     * Inserts a column containing the list at the given x coordinate.  Will shift any existing data.  If the
     * list is taller than the structure the structure will grow.
     *
     * @param x Where to add the column
     * @param col Column of data to add
     */
    @Override
    public void insertColumnAt(int x, List<T> col){
        while (getWidth() < x){
            addColumn(new ArrayList<>());
        }
        normalizeColumn(col);
        grid.add(x, col);
    }

    /**
     * Adds a row at the bottom of the data.  See {@link #insertRowAt(int, List) insertRowAt({@link #getHeight()}, List)}
     *
     * @param row List of values to add
     */
    @Override
    public void addRow(List<T> row){
        insertRowAt(getHeight(), row);
    }

    /**
     * Inserts a row containing the list at the given y coordinate.  Will shift any existing data.  If the
     * list is wider than the structure the structure will grow.
     *
     * @param y Where to add the row
     * @param row Row of data to add
     */
    @Override
    public void insertRowAt(int y, List<T> row){
        normalizeRow(row);
        while (getHeight() < y){
            addRow(new ArrayList<>());
        }
        normalizeRow(row);
        for (int x = 0; x < grid.size(); x++){
            grid.get(x).add(y, row.get(x));
        }
    }

    private void normalizeRow(List<T> row){
        while (getWidth() > row.size()){
            row.add(null);
        }
        while (getWidth() < row.size()){
            addColumn(new ArrayList<>());
        }
    }

    private void normalizeColumn(List<T> col){
        if (grid.size() > 0){
            while (col.size() < getHeight()){
                col.add(null);
            }
            while (col.size() > getHeight()){
                addRow(new ArrayList<>());
            }
        }
    }

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
    @Override
    public T get(int x, int y){
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()){
            return grid.get(x).get(y);
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The point (%d, %d) is out of bounds.  Expected a point within {%d, %d}.", x, y, getWidth(), getHeight()));
        }
    }

    /**
     * Returns the column of data at the given x value.
     *
     * @param x Which column
     *
     * @return A vertical List of values
     *
     * @throws ArrayIndexOutOfBoundsException If x is negative or greater than or equal to {@link #getWidth()}
     */
    @Override
    public List<T> getColumn(int x){
        if (x >= 0 && x < getWidth()){
            return grid.get(x);
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The column (%d) is out of bounds.  Expected a column less than %d", x, getWidth()));
        }
    }

    /**
     * Returns the row of data at the given y value.
     *
     * @param y Which row
     *
     * @return A horizontal List of values
     *
     * @throws ArrayIndexOutOfBoundsException If y is negative or greater than or equal to {@link #getHeight()}
     */
    @Override
    public List<T> getRow(int y){
        if (y >= 0 && y < getHeight()){
            ArrayList<T> row = new ArrayList<T>();
            for (List<T> col : grid){
                row.add(col.get(y));
            }
            return row;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The row (%d) is out of bounds.  Expected a row less than %d", y, getHeight()));
        }
    }

    /**
     * The number of columns.
     *
     * @return Max x + 1
     */
    @Override
    public int getWidth(){
        return grid.size();
    }

    /**
     * The number of rows.
     *
     * @return Max y + 1
     */
    @Override
    public int getHeight() {
        if (getWidth() > 0){
            return grid.get(0).size();
        }
        else {
            return 0;
        }
    }

    /**
     * The total size of the data.  {@link #getWidth()} * {@link #getHeight()}.
     *
     * @return Total size of the 2D structure
     */
    @Override
    public int size(){
        return getWidth()*getHeight();
    }

    /**
     * True if there is no data stored at all.
     *
     * @return
     */
    @Override
    public boolean isEmpty(){
        return size() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ArrayGrid){
            ArrayGrid other = (ArrayGrid) o;
            if (other.getWidth() == getWidth() && other.getHeight() == getHeight()){
                for (int i = 0; i < getWidth(); i++){
                    for (int j = 0; j < getHeight(); j++){
                        if (get(i, j) == null){
                            if (other.get(i, j) != null){
                                return false;
                            }
                        }
                        else {
                            if (!get(i, j).equals(other.get(i, j))) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @CoverageIgnore
    @Override
    public String toString(){
        ArrayList<String> rows = new ArrayList<String>(getHeight());
        for (int y = 0; y < getHeight(); y++){
            String row = "| ";
            for (int x = 0; x < getWidth()-1; x++){
                row += get(x, y) + ",\t";
            }
            row += get(getWidth()-1, y) + "\t|";
            rows.add(row);
        }
        String out = "";
        for (String row : rows){
            out += row + "\n";
        }
        return out;
    }

    @Override
    public GenericArrayGrid<T> clone(){
        return new GenericArrayGrid<T>(this);
    }
}
