package com.csm.rover.simulator.objects.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloatArrayArrayGrid implements ArrayGrid<Float> {

    private Float[][] grid;

    public FloatArrayArrayGrid(){
        grid = new Float[0][0];
    }

    public FloatArrayArrayGrid(Float[][] values){
        loadFromArray(values);
    }

    public FloatArrayArrayGrid(FloatArrayArrayGrid original){
        grid = original.grid.clone();
    }

    @Override
    public void loadFromArray(Float[][] values) {
        grid = new Float[values.length][values[0].length];
        for (int x = 0; x < values.length; x++){
            for (int y = 0; y < values[x].length; y++){
                grid[x][y] = values[x][y];
            }
        }
    }

    @Override
    public void fillToSize(int width, int height) {
        fillToSize(width, height, 0f);
    }

    @Override
    public void fillToSize(int width, int height, Float val) {
        grid = new Float[width][height];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                grid[x][y] = val;
            }
        }
    }

    @Override
    public void put(int x, int y, Float val){
        while (x >= getWidth()){
            addColumn(new ArrayList<>());
        }
        while (y >= getHeight()){
            addRow(new ArrayList<Float>());
        }
        grid[x][y] = val;
    }

    @Override
    public void addColumn(List<Float> col){
        insertColumnAt(getWidth(), col);
    }

    @Override
    public void insertColumnAt(int x, List<Float> col){
        while (getWidth() < x){
            addColumn(new ArrayList<>());
        }
        normalizeColumn(col);

        Float[][] grid2 = new Float[getWidth()+1][getHeight()];
        int i = 0;
        while (i < x){
            grid2[i] = grid[i];
            i++;
        }
        for (int j = 0; j < getHeight(); j++){
            grid2[i][j] = col.get(j);
        }
        while (i < grid.length){
            grid2[i+1] = grid[i];
            i++;
        }
        grid = grid2;
    }

    @Override
    public void addRow(List<Float> row){
        insertRowAt(getHeight(), row);
    }

    @Override
    public void insertRowAt(int y, List<Float> row){
        normalizeRow(row);
        while (getHeight() < y){
            addRow(new ArrayList<Float>());
        }
        normalizeRow(row);

        Float[][] grid2 = new Float[getWidth()][getHeight()+1];
        int j = 0;
        while (j < y){
            for (int x = 0; x < getWidth(); x++){
                grid2[x][j] = grid[x][j];
            }
            j++;
        }
        for (int x = 0; x < getWidth(); x++){
            grid2[x][y] = row.get(x);
        }
        while (j < getHeight()){
            for (int x = 0; x < getWidth(); x++){
                grid2[x][j+1] = grid[x][j];
            }
            j++;
        }
        grid = grid2;
    }

    private void normalizeRow(List<Float> row){
        while (getWidth() > row.size()){
            row.add(0f);
        }
        while (getWidth() < row.size()){
            addColumn(new ArrayList<>());
        }
    }

    private void normalizeColumn(List<Float> col){
        if (grid.length > 0){
            while (col.size() < getHeight()){
                col.add(0f);
            }
            while (col.size() > getHeight()){
                addRow(new ArrayList<Float>());
            }
        }
    }

    @Override
    public Float get(int x, int y){
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()){
            return grid[x][y];
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The point (%d, %d) is out of bounds.  Expected a point within {%d, %d}.", x, y, getWidth(), getHeight()));
        }
    }

    @Override
    public ArrayList<Float> getColumn(int x){
        if (x >= 0 && x < getWidth()){
            ArrayList<Float> col = new ArrayList<Float>(grid[x].length);
            for (Float val : grid[x]){
                col.add(val);
            }
            return col;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The column (%d) is out of bounds.  Expected a column less than %d", x, getWidth()));
        }
    }

    @Override
    public ArrayList<Float> getRow(int y){
        if (y >= 0 && y < getHeight()){
            ArrayList<Float> row = new ArrayList<Float>();
            for (Float[] col : grid){
                row.add(col[y]);
            }
            return row;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The row (%d) is out of bounds.  Expected a row less than %d", y, getHeight()));
        }
    }

    @Override
    public int getWidth() {
        return grid.length;
    }

    @Override
    public int getHeight() {
        return grid.length == 0 ? 0 : grid[0].length;
    }

    @Override
    public int size() {
        return getWidth()*getHeight();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public ArrayGrid<Float> clone() {
        return new FloatArrayArrayGrid(this);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FloatArrayArrayGrid && Arrays.deepEquals(this.grid, ((FloatArrayArrayGrid) other).grid);
    }

    @Override
    public String toString(){
        return String.format("ArrayGrid:[type=Float, width=%d, height=%d]", getWidth(), getHeight());
    }
}
