package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.io.jsonserial.ArrayGridDeserializer;
import com.csm.rover.simulator.objects.io.jsonserial.ArrayGridSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

@JsonSerialize(using=ArrayGridSerializer.class)
@JsonDeserialize(using=ArrayGridDeserializer.class)
public interface ArrayGrid<T> extends Cloneable {

    void loadFromArray(T[][] values);

    void fillToSize(int width, int height);

    void fillToSize(int width, int height, T val);

    void put(int x, int y, T val);

    void addColumn(ArrayList<T> col);

    void addColumnAt(int x, ArrayList<T> col);

    void addRow(ArrayList<T> row);

    void addRowAt(int y, ArrayList<T> row);

    T get(int x, int y);

    ArrayList<T> getColumn(int x);

    ArrayList<T> getRow(int y);

    int getWidth();

    int getHeight();

    int size();

    boolean isEmpty();

    ArrayGrid<T> clone();

}
