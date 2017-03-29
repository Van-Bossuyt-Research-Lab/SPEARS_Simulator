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

package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.CoverageIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An implementation of {@link ArrayGrid} which uses a 2D array.  This implementation only supports float storage.
 * Default value is 0.
 */
public class FloatArrayArrayGrid implements ArrayGrid<Float> {

    private Float[][] grid;

    /**
     * Creates an empty {@link ArrayGrid ArrayGrid&#60;Float&#62;} of size 0, 0.
     */
    public FloatArrayArrayGrid(){
        grid = new Float[0][0];
    }

    /**
     * Creates a new {@link ArrayGrid ArrayGrid&#60;Float&#62;} that contains a copy of the values of the supplied array.
     * [i][j] == get(i, j)
     *
     * @param values Initial value to load from.
     */
    public FloatArrayArrayGrid(Float[][] values){
        loadFromArray(values);
    }

    /**
     * Creates a clone of the supplied FloatArrayArrayGrid.
     *
     * @param original ArrayGrid to copy
     */
    public FloatArrayArrayGrid(FloatArrayArrayGrid original){
        this(original.grid);
    }

    @Override
    public void loadFromArray(Float[][] values) {
        grid = new Float[values.length][values[0].length];
        for (int x = 0; x < values.length; x++){
            grid[x] = Arrays.copyOf(values[x], values[x].length);
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
        if (getWidth() == 0 && getHeight() == 0){
            fillToSize(x+1, y+1);
            grid[x][y] = val;
            return;
        }

        while (x >= getWidth()){
            addColumn(new ArrayList<>());
        }
        while (y >= getHeight()){
            addRow(new ArrayList<>());
        }
        grid[x][y] = val;
    }

    @Override
    public void addColumn(List<Float> col){
        insertColumnAt(getWidth(), col);
    }

    @Override
    public void insertColumnAt(int x, List<Float> col){
        if (getWidth() == 0 && getHeight() == 0){
            fillToSize(x+1, col.size());
            for (int j = 0; j < col.size(); j++){
                grid[x][j] = col.get(j);
            }
            return;
        }

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
        if (getWidth() == 0 && getHeight() == 0){
            fillToSize(row.size(), y+1);
            for (int i = 0; i < grid.length; i++){
                grid[i][y] = row.get(i);
            }
            return;
        }

        normalizeRow(row);
        while (getHeight() < y){
            addRow(new ArrayList<>());
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
                addRow(new ArrayList<>());
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
    public List<Float> getColumn(int x){
        if (x >= 0 && x < getWidth()){
            return Arrays.asList(grid[x]);
        }
        else {
            throw new ArrayIndexOutOfBoundsException(String.format("The column (%d) is out of bounds.  Expected a column less than %d", x, getWidth()));
        }
    }

    @Override
    public List<Float> getRow(int y){
        if (y >= 0 && y < getHeight()){
            ArrayList<Float> row = new ArrayList<>();
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
    public boolean equals(Object o) {
        if (o instanceof ArrayGrid){
            ArrayGrid other = (ArrayGrid) o;
            if (other.getWidth() == getWidth() && other.getHeight() == getHeight()){
                for (int i = 0; i < getWidth(); i++){
                    for (int j = 0; j < getHeight(); j++){
                        try {
                            if (Math.abs(get(i, j) - (Float) other.get(i, j)) > 0.0000001) {
                                return false;
                            }
                        }
                        catch (ClassCastException | NullPointerException e){
                            return false;
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
        return String.format("ArrayGrid:[type=Float, width=%d, height=%d]", getWidth(), getHeight());
    }
}
