package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.io.jsonserial.RecursiveGridListDeserializer;
import com.csm.rover.simulator.objects.io.jsonserial.RecursiveGridListSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;

@JsonSerialize(using=RecursiveGridListSerializer.class)
@JsonDeserialize(using=RecursiveGridListDeserializer.class)
public class RecursiveGridList<T> {

    private Map<Integer, RecursiveGridList<T>> list;

    private int layers;
    private Optional<T> value;

    public static <U> RecursiveGridList newGridList(Class<? extends U> type, int layers){
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
            for (Integer key : list.keySet()){
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
                this.layers == 0 ?
                    this.value.get().equals(((RecursiveGridList)other).value.get()) :
                    this.list.equals(((RecursiveGridList)other).list);
    }

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
