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

import com.spears.objects.CoverageIgnore;
import com.spears.objects.io.jsonserial.RecursiveGridListDeserializer;
import com.spears.objects.io.jsonserial.RecursiveGridListSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;

@JsonSerialize(using=RecursiveGridListSerializer.class)
@JsonDeserialize(using=RecursiveGridListDeserializer.class)
public class RecursiveGridList<T> {

    private Map<Integer, RecursiveGridList<T>> list;

    private int layers;
    private Optional<T> value;

    public static <U> RecursiveGridList<U> newGridList(Class<U> type, int layers){
        return new RecursiveGridList<U>(layers);
    }

    private RecursiveGridList(int layers){
        this.layers = layers;
        list = new HashMap<>();
        value = Optional.empty();
    }

    public T get(int... coords){
        if (coords.length != layers){
            throw new ArrayIndexOutOfBoundsException("The number of coordinates expected did not meet the expected "+layers);
        }
        if (layers == 0){
            return value.get();
        }
        if (!list.containsKey(coords[0])){
            throw new IndexOutOfBoundsException(coords[0]+" is not defined");
        }
        try {
            return list.get(coords[0]).get(Arrays.copyOfRange(coords, 1, layers));
        }
        catch (IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException(coords[0] + ", " + e.getMessage());
        }
    }

    public void put(T val, int... coords){
        if (coords.length != layers){
            throw new ArrayIndexOutOfBoundsException("The number of coordinates expected did not meet the expected "+layers);
        }
        if (layers == 0){
            value = Optional.of(val);
            return;
        }
        if (!list.containsKey(coords[0])){
            list.put(coords[0], new RecursiveGridList<>(layers - 1));
        }
        list.get(coords[0]).put(val, Arrays.copyOfRange(coords, 1, layers));
    }

    public Set<int[]> keySet(){
        Set<int[]> out = new HashSet<>();
        if (layers == 1){
            for (int key : list.keySet()){
                out.add(new int[]{ key });
            }
        }
        else {
            for (Integer key : list.keySet()){
                for (int[] recKeys : list.get(key).keySet()){
                    out.add(prepend(key, recKeys));
                }
            }
        }
        return out;
    }

    public int getDimension(){
        return layers;
    }

    private int[] prepend(int val, int[] list) {
        int[] out = new int[list.length+1];
        out[0] = val;
        System.arraycopy(list, 0, out, 1, list.length);
        return out;
    }

    @Override
    public boolean equals(Object other){
        return other instanceof RecursiveGridList &&
                this.layers == ((RecursiveGridList)other).layers &&
                (this.value.isPresent() ?
                    valuesMatch(this.value.get(), ((RecursiveGridList)other).value.get()) :
                    this.list.equals(((RecursiveGridList)other).list));
    }

    private boolean valuesMatch(Object obj1, Object obj2){
        double tolerance = 0.000001;
        if (obj1 instanceof Double || obj2 instanceof Double){
            double a, b;
            if (obj1 instanceof Float){
                a = (Float)obj1;
            }
            else {
                a = (Double)obj1;
            }
            if (obj2 instanceof Float){
                b = (Float)obj2;
            }
            else if (obj2 instanceof Double){
                b = (Double)obj2;
            }
            else {
                return false;
            }
            System.out.println(a + " - " + b + " = " + Math.abs(a - b));
            return Math.abs(a - b) < tolerance;
        }
        else {
            return obj1.equals(obj2);
        }
    }

    @CoverageIgnore
    @Override
    public String toString(){
        if (value.isPresent()){
            return String.format("{value=%s}", value.get().toString());
        }
        else {
            return String.format("{layers=%d, values=%s}", layers, list);
        }
    }

}
