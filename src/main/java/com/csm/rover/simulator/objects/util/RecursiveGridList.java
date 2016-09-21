package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.io.jsonserial.RecursiveGridListDeserializer;
import com.csm.rover.simulator.objects.io.jsonserial.RecursiveGridListSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;

@JsonSerialize(using=RecursiveGridListSerializer.class)
@JsonDeserialize(using=RecursiveGridListDeserializer.class)
public class RecursiveGridList<T> {

    private Map<Double, RecursiveGridList<T>> list;

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

    public T get(double... coords){
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

    public void put(T val, double... coords){
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

    public Set<double[]> keySet(){
        Set<double[]> out = new HashSet<>();
        if (layers == 1){
            for (Double key : list.keySet()){
                out.add(new double[]{ key });
            }
        }
        else {
            for (Double key : list.keySet()){
                for (double[] recKeys : list.get(key).keySet()){
                    out.add(prepend(key, recKeys));
                }
            }
        }
        return out;
    }

    public double getDimension(){
        return layers;
    }

    private double[] prepend(double val, double[] list) {
        double[] out = new double[list.length+1];
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
