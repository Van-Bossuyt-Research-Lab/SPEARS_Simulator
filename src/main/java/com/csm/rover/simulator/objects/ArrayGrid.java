package com.csm.rover.simulator.objects;

import java.util.ArrayList;

public class ArrayGrid<T> implements Cloneable {

    private ArrayList<ArrayList<T>> grid;

    public ArrayGrid(){
        grid = new ArrayList<ArrayList<T>>();
    }

    public ArrayGrid(T[][] values){
        loadFromArray(values);
    }

    public ArrayGrid(ArrayGrid<T> original){
        grid = new ArrayList<ArrayList<T>>();
        for (int x = 0; x < getWidth(); x++){
            for (int y = 0; y < getHeight(); y++){
                put(x, y, original.get(x, y));
            }
        }
    }

    public void loadFromArray(T[][] values){
        grid = new ArrayList<ArrayList<T>>();
        for (int x = 0; x < values.length; x++){
            for (int y = 0; y < values[x].length; y++){
                put(x, y, values[x][y]);
            }
        }
    }

    public void fillToSize(int width, int height){
        fillToSize(width, height, null);
    }

    public void fillToSize(int width, int height, T val){
        grid = new ArrayList<ArrayList<T>>();
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                put(x, y, val);
            }
        }
    }

    public void put(int x, int y, T val){
        while (x >= getWidth()){
            addColumn(new ArrayList<T>());
        }
        while (y >= getHeight()){
            addRow(new ArrayList<T>());
        }
        grid.get(x).set(y, val);
    }

    public void addColumn(ArrayList<T> col){
        addColumnAt(getWidth(), col);
    }

    public void addColumnAt(int x, ArrayList<T> col){
        while (getWidth() < x){
            addColumn(new ArrayList<T>());
        }
        normalizeColumn(col);
        grid.add(x, col);
    }

    public void addRow(ArrayList<T> row){
        addRowAt(getHeight(), row);
    }

    public void addRowAt(int y, ArrayList<T> row){
        normalizeRow(row);
        while (getHeight() < y){
            addRow(new ArrayList<T>());
        }
        normalizeRow(row);
        for (int x = 0; x < grid.size(); x++){
            grid.get(x).add(y, row.get(x));
        }
    }

    private void normalizeRow(ArrayList<T> row){
        while (getWidth() > row.size()){
            row.add(null);
        }
        while (getWidth() < row.size()){
            addColumn(new ArrayList<T>());
        }
    }

    private void normalizeColumn(ArrayList<T> col){
        if (grid.size() > 0){
            while (col.size() < getHeight()){
                col.add(null);
            }
            while (col.size() > getHeight()){
                addRow(new ArrayList<T>());
            }
        }
    }

    public T get(int x, int y){
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()){
            return grid.get(x).get(y);
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The point (%d, %d) is out of bounds.  Expected a point within {%d, %d}.", x, y, getWidth(), getHeight()));
        }
    }

    public ArrayList<T> getColumn(int x){
        if (x >= 0 && x < getWidth()){
            return grid.get(x);
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The column (%d) is out of bounds.  Expected a column less than %d", x, getWidth()));
        }
    }

    public ArrayList<T> getRow(int y){
        if (y >= 0 && y < getHeight()){
            ArrayList<T> row = new ArrayList<T>();
            for (ArrayList<T> col : grid){
                row.add(col.get(y));
            }
            return row;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The row (%d) is out of bounds.  Expected a row less than %d", y, getHeight()));
        }
    }

    public int getWidth(){
        return grid.size();
    }

    public int getHeight() {
        if (getWidth() > 0){
            return grid.get(0).size();
        }
        else {
            return 0;
        }
    }

    public int size(){
        return getWidth()*getHeight();
    }

    public boolean isEmpty(){
        return size() == 0;
    }

    @Override
    public String toString(){
        ArrayList<String> rows = new ArrayList<String>(getHeight());
        for (int y = 0; y < getHeight(); y++){
            String row = "|\t";
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
    public ArrayGrid<T> clone(){
        return new ArrayGrid<T>(this);
    }
}
