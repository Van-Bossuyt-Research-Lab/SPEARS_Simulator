package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.CoverageIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link ArrayGrid} which uses {@link ArrayList}.  Default value  is null.
 *
 * @param <T> Type to store
 */
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

    @Override
    public void loadFromArray(T[][] values){
        grid = new ArrayList<>();
        for (int x = 0; x < values.length; x++){
            for (int y = 0; y < values[x].length; y++){
                put(x, y, values[x][y]);
            }
        }
    }

    @Override
    public void fillToSize(int width, int height){
        fillToSize(width, height, null);
    }

    @Override
    public void fillToSize(int width, int height, T val){
        grid = new ArrayList<>();
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                put(x, y, val);
            }
        }
    }

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

    @Override
    public void addColumn(List<T> col){
        insertColumnAt(getWidth(), col);
    }

    @Override
    public void insertColumnAt(int x, List<T> col){
        while (getWidth() < x){
            addColumn(new ArrayList<>());
        }
        normalizeColumn(col);
        grid.add(x, col);
    }

    @Override
    public void addRow(List<T> row){
        insertRowAt(getHeight(), row);
    }

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

    @Override
    public T get(int x, int y){
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()){
            return grid.get(x).get(y);
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The point (%d, %d) is out of bounds.  Expected a point within {%d, %d}.", x, y, getWidth(), getHeight()));
        }
    }

    @Override
    public List<T> getColumn(int x){
        if (x >= 0 && x < getWidth()){
            return grid.get(x);
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The column (%d) is out of bounds.  Expected a column less than %d", x, getWidth()));
        }
    }

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

    @Override
    public int getWidth(){
        return grid.size();
    }

    @Override
    public int getHeight() {
        if (getWidth() > 0){
            return grid.get(0).size();
        }
        else {
            return 0;
        }
    }

    @Override
    public int size(){
        return getWidth()*getHeight();
    }

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
        ArrayList<String> rows = new ArrayList<>(getHeight());
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
