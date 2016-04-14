package com.csm.rover.simulator.objects.util;

import java.util.ArrayList;

public class FloatArrayArrayGrid implements ArrayGrid<Float> {

    private float[][] grid;

    public FloatArrayArrayGrid(){
        grid = new float[0][0];
    }

    public FloatArrayArrayGrid(Float[][] values){
        loadFromArray(values);
    }

    public FloatArrayArrayGrid(FloatArrayArrayGrid original){
        grid = original.grid.clone();
    }

    @Override
    public void loadFromArray(Float[][] values) {
        grid = new float[values.length][values[0].length];
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
        grid = new float[width][height];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                grid[x][y] = val;
            }
        }
    }

    @Override
    public void put(int x, int y, Float val){
        while (x >= getWidth()){
            addColumn(new ArrayList<Float>());
        }
        while (y >= getHeight()){
            addRow(new ArrayList<Float>());
        }
        grid[x][y] = val;
    }

    @Override
    public void addColumn(ArrayList<Float> col){
        addColumnAt(getWidth(), col);
    }

    @Override
    public void addColumnAt(int x, ArrayList<Float> col){
        while (getWidth() < x){
            addColumn(new ArrayList<Float>());
        }
        normalizeColumn(col);

        float[][] grid2 = new float[getWidth()+1][getHeight()];
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
    public void addRow(ArrayList<Float> row){
        addRowAt(getHeight(), row);
    }

    @Override
    public void addRowAt(int y, ArrayList<Float> row){
        normalizeRow(row);
        while (getHeight() < y){
            addRow(new ArrayList<Float>());
        }
        normalizeRow(row);

        float[][] grid2 = new float[getWidth()][getHeight()+1];
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

    private void normalizeRow(ArrayList<Float> row){
        while (getWidth() > row.size()){
            row.add(0f);
        }
        while (getWidth() < row.size()){
            addColumn(new ArrayList<Float>());
        }
    }

    private void normalizeColumn(ArrayList<Float> col){
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
            for (float val : grid[x]){
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
            for (float[] col : grid){
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
}
