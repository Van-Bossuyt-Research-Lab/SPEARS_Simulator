package com.csm.rover.simulator.objects.util;

import java.util.ArrayList;

//TODO Implment methods
public class FloatArrayArrayArrayGrid implements ArrayGrid3D<Float> {

    private float[][][] grid3;

    public FloatArrayArrayArrayGrid(){
        grid3 = new float[0][0][0];
    }

    public FloatArrayArrayArrayGrid(Float[][][] values){
        loadFromArray(values);
    }

    public FloatArrayArrayArrayGrid(FloatArrayArrayArrayGrid original){
        grid3 = original.grid3.clone();
    }

    public void loadFromArray(Float[][][] values) {
        grid3 = new float[values.length][values[0].length][values[0][0].length];
        for (int x = 0; x < values.length; x++) {
            for (int y = 0; y < values[x].length; y++) {
                for (int z = 0; z < values[x][y].length; z++) {
                    grid3[x][y][z] = values[x][y][z];
                }
            }
        }
    }

    public void fillToSize(int width, int height, int length) {
        fillToSize(width, height, length, 0f);
    }

    public void fillToSize(int width, int height,int length, Float val) {
        grid3 = new float[width][height][length];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                for (int z = 0; z < length; z++) {
                    grid3[x][y][z] = val;
                }
            }
        }
    }


    public void put(int x, int y, int z, Float val){
        while (x >= getWidth()){
            addColumn(new ArrayList<>());
        }
        while (y >= getHeight()){
            addRow(new ArrayList<>());
        }
        while (z >= getDepth()){
            addRow(new ArrayList<>());
        }
        grid3[x][y][z] = val;
    }

    public void addColumn(ArrayList<Float> col){
        addColumnAt(getWidth(), getDepth(), col);
    }

    @Override
    public void addColumnAt(int x, ArrayList<Float> col) {

    }


    public void addColumnAt(int x, int y, ArrayList<Float> col){
        while (getWidth() < x){
            addColumn(new ArrayList<>());
        }
        normalizeColumn(col);

        float[][][] grid2 = new float[getWidth()+1][getHeight()][getDepth()];
        int i = 0;
        int j =0;
        while (i < x){
            j =0;
            while (j < y) {
                grid2[i] = grid3[i];
                j++;
            }
            i++;
        }

        for (int k = 0; k < getHeight(); k++){
            grid2[i][j][k] = col.get(k);
        }
        while (i < grid3.length){
            grid2[i+1] = grid3[i];
            i++;
        }
        grid3 = grid2;
    }

    public void addRow(ArrayList<Float> row){
        addRowAt(getHeight(), getWidth(),row);
    }

    @Override
    public void addRowAt(int y, ArrayList<Float> row) {

    }

    @Override
    public void addLayer(ArrayList<Float> lay) {

    }

    @Override
    public void addLayerAt(int z, ArrayList<Float> lay) {

    }


    public void addRowAt(int z, int y, ArrayList<Float> row){
        normalizeRow(row);
        while (getHeight() < y){
            addRow(new ArrayList<>());
        }
        normalizeRow(row);

        float[][][] grid2 = new float[getWidth()][getHeight()+1][getDepth()];
        int j = 0;
        while (j < y){
            for (int x = 0; x < getWidth(); x++){
                grid2[z][x][j] = grid3[z][x][j];
            }
            j++;
        }
        for (int x = 0; x < getWidth(); x++){
            grid2[z][x][y] = row.get(x);
        }
        while (j < getHeight()){
            for (int x = 0; x < getWidth(); x++){
                grid2[z][x][j+1] = grid3[z][x][j];
            }
            j++;
        }
        grid3 = grid2;
    }

    private void normalizeRow(ArrayList<Float> row){
        while (getWidth() > row.size()){
            row.add(0f);
        }
        while (getWidth() < row.size()){
            addColumn(new ArrayList<>());
        }
    }

    private void normalizeColumn(ArrayList<Float> col){
        if (grid3.length > 0){
            while (col.size() < getHeight()){
                col.add(0f);
            }
            while (col.size() > getHeight()){
                addRow(new ArrayList<>());
            }
        }
    }

    public ArrayList<Float> getColumn(int x, int y){
        if (x >= 0 && x < getWidth()){
            ArrayList<Float> col = new ArrayList<>(grid3[x][y].length);
            for (float val : grid3[x][y]){
                col.add(val);
            }
            return col;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The column (%d) is out of bounds.  Expected a column less than %d", x, getWidth()));
        }
    }

    public ArrayList<Float> getRow(int y, int z){
        if (y >= 0 && y < getHeight()){
            ArrayList<Float> row = new ArrayList<>();
            for (float[][] col : grid3){
                row.add(col[y][z]);
            }
            return row;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The row (%d) is out of bounds.  Expected a row less than %d", y, getHeight()));
        }
    }

    public Float get(int x, int y, int z){
        return grid3[x][y][z];
    }

    @Override
    public ArrayList<Float> getColumn(int x) {
        return null;
    }

    @Override
    public ArrayList<Float> getRow(int y) {
        return null;
    }

    public int getWidth() {
        return grid3.length;
    }


    public int getHeight() {
        return grid3.length == 0 ? 0 : grid3[0].length;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getZ() {
        return 0;
    }

    public int getDepth() {
        return grid3[0].length == 0 ? 0 : grid3[0][0].length;
    }


    public int size() {
        return getWidth()*getHeight();
    }


    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public ArrayGrid3D<Float> clone(){
        return new FloatArrayArrayArrayGrid(this);
    }
    
}
