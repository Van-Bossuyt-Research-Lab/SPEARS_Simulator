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

import java.util.ArrayList;

public class FloatArrayArrayArrayGrid implements ArrayGrid3D<Float> {

    private Float[][][] grid3;

    public FloatArrayArrayArrayGrid(){
        grid3 = new Float[0][0][0];
    }

    public FloatArrayArrayArrayGrid(Float[][][] values){
        loadFromArray(values);
    }

    public FloatArrayArrayArrayGrid(FloatArrayArrayArrayGrid original){
        loadFromArray(original.grid3);
    }

    public void loadFromArray(Float[][][] values) {
        grid3 = new Float[values.length][values[0].length][values[0][0].length];
        for (int x = 0; x < values.length; x++) {
            for (int y = 0; y < values[x].length; y++) {
                for (int z = 0; z < values[x][y].length; z++) {
                    grid3[x][y][z] = values[x][y][z];
                }
            }
        }
    }

    public void fillToSize(int width, int height, int depth) {
        fillToSize(width, height, depth, 0f);
    }

    public void fillToSize(int width, int height,int depth, Float val) {
        grid3 = new Float[width][height][depth];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                for (int z = 0; z < depth; z++) {
                    grid3[x][y][z] = val;
                }
            }
        }
    }

    public void put(int x, int y, int z, Float val){
        if (getWidth() == 0 && getHeight() == 0 && getDepth() == 0){
            fillToSize(x+1, y+1, z+1);
            grid3[x][y][z] = val;
            return;
        }

        while (x >= getWidth()){
            addXLayer(new FloatArrayArrayGrid());
        }
        while (y >= getHeight()){
            addYLayer(new FloatArrayArrayGrid());
        }
        while (z >= getDepth()){
            addZLayer(new FloatArrayArrayGrid());
        }
        grid3[x][y][z] = val;
    }

    @Override
    public Float get(int x, int y, int z){
        return grid3[x][y][z];
    }

    @Override
    public void addXLayer(ArrayGrid<Float> xlay) {
        insertXLayerAt(getWidth(), xlay);
    }

    @Override
    public void insertXLayerAt(int x, ArrayGrid<Float> xlay) {
        if (getWidth() == 0 && getHeight() == 0 && getDepth() == 0){
            fillToSize(x+1, xlay.getWidth(), xlay.getHeight());
            for (int j = 0; j < getHeight(); j++){
                for (int k = 0; k < getDepth(); k++){
                    grid3[x][j][k] = xlay.get(j, k);
                }
            }
            return;
        }

        normalizeXLayer(xlay);
        while (getWidth() < x){
            addXLayer(new FloatArrayArrayGrid());
        }
        normalizeXLayer(xlay);

        Float[][][] grid2 = new Float[getWidth()+1][getHeight()][getDepth()];
        int i = 0;
        while (i < x){
            for (int j = 0; j < getHeight(); j++){
                for (int k = 0; k < getDepth(); k++){
                    grid2[i][j][k] = grid3[i][j][k];
                }
            }
            i++;
        }
        for (int j = 0; j < getHeight(); j++){
            for (int k = 0; k < getDepth(); k++){
                grid2[x][j][k] = xlay.get(j, k);
            }
        }
        while (i < getWidth()){
            for (int j = 0; j < getHeight(); j++){
                for (int k = 0; k < getDepth(); k++){
                    grid2[i+1][j][k] = grid3[i][j][k];
                }
            }
            i++;
        }
        grid3 = grid2;
    }

    private void normalizeXLayer(ArrayGrid<Float> layer){
        while (layer.getWidth() < getHeight()){
            layer.addColumn(new ArrayList<>());
        }
        while (layer.getHeight() < getDepth()){
            layer.addRow(new ArrayList<>());
        }
        while (getHeight() < layer.getWidth()){
            addYLayer(new FloatArrayArrayGrid());
        }
        while (getDepth() < layer.getHeight()){
            addZLayer(new FloatArrayArrayGrid());
        }
    }

    @Override
    public void addYLayer(ArrayGrid<Float> ylay) {
        insertYLayerAt(getHeight(), ylay);
    }

    @Override
    public void insertYLayerAt(int y, ArrayGrid<Float> ylay) {
        if (getWidth() == 0 && getHeight() == 0 && getDepth() == 0){
            fillToSize(ylay.getWidth(), y+1, ylay.getHeight());
            for (int i = 0; i < getWidth(); i++){
                for (int k = 0; k < getDepth(); k++){
                    grid3[i][y][k] = ylay.get(i, k);
                }
            }
            return;
        }

        normalizeYLayer(ylay);
        while (getHeight() < y){
            addYLayer(new FloatArrayArrayGrid());
        }
        normalizeYLayer(ylay);

        Float[][][] grid2 = new Float[getWidth()][getHeight()+1][getDepth()];
        int j = 0;
        while (j < y){
            for (int i = 0; i < getWidth(); i++){
                for (int k = 0; k < getDepth(); k++){
                    grid2[i][j][k] = grid3[i][j][k];
                }
            }
            j++;
        }
        for (int i = 0; i < getWidth(); i++){
            for (int k = 0; k < getDepth(); k++){
                grid2[i][y][k] = ylay.get(i, k);
            }
        }
        while (j < getHeight()){
            for (int i = 0; i < getWidth(); i++){
                for (int k = 0; k < getDepth(); k++){
                    grid2[i][j+1][k] = grid3[i][j][k];
                }
            }
            j++;
        }
        grid3 = grid2;
    }

    private void normalizeYLayer(ArrayGrid<Float> layer){
        while (layer.getWidth() < getWidth()){
            layer.addColumn(new ArrayList<>());
        }
        while (layer.getHeight() < getDepth()){
            layer.addRow(new ArrayList<>());
        }
        while (getWidth() < layer.getWidth()){
            addXLayer(new FloatArrayArrayGrid());
        }
        while (getDepth() < layer.getHeight()){
            addZLayer(new FloatArrayArrayGrid());
        }
    }

    @Override
    public void addZLayer(ArrayGrid<Float> zlay) {
        insertZLayerAt(getDepth(), zlay);
    }

    @Override
    public void insertZLayerAt(int z, ArrayGrid<Float> zlay) {
        if (getWidth() == 0 && getHeight() == 0 && getDepth() == 0){
            fillToSize(zlay.getWidth(), zlay.getHeight(), z+1);
            for (int i = 0; i < getWidth(); i++){
                for (int j = 0; j < getHeight(); j++){
                    grid3[i][j][z] = zlay.get(i, j);
                }
            }
            return;
        }

        normalizeZLayer(zlay);
        while (getDepth() < z){
            addZLayer(new FloatArrayArrayGrid());
        }
        normalizeZLayer(zlay);

        Float[][][] grid2 = new Float[getWidth()][getHeight()][getDepth()+1];
        int k = 0;
        while (k < z){
            for (int i = 0; i < getWidth(); i++){
                for (int j = 0; j < getHeight(); j++){
                    grid2[i][j][k] = grid3[i][j][k];
                }
            }
            k++;
        }
        for (int i = 0; i < getWidth(); i++){
            for (int j = 0; j < getHeight(); j++){
                grid2[i][j][z] = zlay.get(i, j);
            }
        }
        while (k < getDepth()){
            for (int i = 0; i < getWidth(); i++){
                for (int j = 0; j < getHeight(); j++){
                    grid2[i][j][k+1] = grid3[i][j][k];
                }
            }
            k++;
        }
        grid3 = grid2;
    }

    private void normalizeZLayer(ArrayGrid<Float> layer){
        while (layer.getWidth() < getWidth()){
            layer.addColumn(new ArrayList<>());
        }
        while (layer.getHeight() < getHeight()){
            layer.addRow(new ArrayList<>());
        }
        while (getWidth() < layer.getWidth()){
            addXLayer(new FloatArrayArrayGrid());
        }
        while (getHeight() < layer.getHeight()){
            addYLayer(new FloatArrayArrayGrid());
        }
    }

    @Override
    public ArrayGrid<Float> getXLayer(int x){
        if (x >= 0 && x < getWidth()) {
            ArrayGrid<Float> out = new FloatArrayArrayGrid();
            for (int j = getHeight() - 1; j >= 0; j--) {
                for (int k = getDepth() - 1; k >= 0; k--) {
                    out.put(j, k, grid3[x][j][k]);
                }
            }
            return out;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("The xLayer (%d) is out of bounds.  Expected a layer less than %d", x, getWidth()));
        }
    }

    @Override
    public ArrayGrid<Float> getYLayer(int y){
        if (y >= 0 && y < getHeight()){
            ArrayGrid<Float> out = new FloatArrayArrayGrid();
            for (int i = getWidth()-1; i >= 0; i--){
                for (int k = getDepth()-1; k >= 0; k--){
                    out.put(i, k, grid3[i][y][k]);
                }
            }
            return out;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("The yLayer (%d) is out of bounds.  Expected a layer less than %d", y, getHeight()));
        }
    }

    @Override
    public ArrayGrid<Float> getZLayer(int z){
        if (z >= 0 && z < getDepth()){
            ArrayGrid<Float> out = new FloatArrayArrayGrid();
            for (int i = getWidth()-1; i >= 0; i--){
                for (int j = getHeight()-1; j >= 0; j--){
                    out.put(i, j, grid3[i][j][z]);
                }
            }
            return out;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("The zLayer (%d) is out of bounds.  Expected a layer less than %d", z, getDepth()));
        }
    }

    @Override
    public int getWidth() {
        return grid3.length;
    }


    @Override
    public int getHeight() {
        return grid3.length == 0 ? 0 : grid3[0].length;
    }

    @Override
    public int getDepth() {
        return grid3.length == 0 || grid3[0].length == 0 ? 0 : grid3[0][0].length;
    }


    @Override
    public int size() {
        return getWidth()*getHeight()*getDepth();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof ArrayGrid3D){
            ArrayGrid3D other = (ArrayGrid3D)o;
            if (getWidth() == other.getWidth() &&
                    getHeight() == other.getHeight() &&
                    getDepth() == other.getDepth()){
                for (int i = 0; i < getWidth(); i++){
                    for (int j = 0; j < getHeight(); j++){
                        for (int k = 0; k < getDepth(); k++){
                            try {
                                if (Math.abs(get(i, j, k) - (Float) other.get(i, j, k)) > 0.0000001) {
                                    return false;
                                }
                            }
                            catch (ClassCastException | NullPointerException e){
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

    @Override
    public ArrayGrid3D<Float> clone(){
        return new FloatArrayArrayArrayGrid(this);
    }
    
}
