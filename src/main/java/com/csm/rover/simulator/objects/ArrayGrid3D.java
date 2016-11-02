package com.csm.rover.simulator.objects;
import java.util.ArrayList;
/**
 * Created by PHM-Lab2 on 1/12/2016.
 */
public interface ArrayGrid3D<T> extends Cloneable {

    void loadFromArray(T[][][] values);

    void fillToSize(int width, int height, int length);

    void fillToSize(int width, int height,  int length, T val);

    void put(int X, int Y, int Z, T val);

    void addColumn(ArrayList<T> col);

    void addColumnAt(int x, int y, ArrayList<T> col);

    void addRow(ArrayList<T> row);

    void addRowAt(int y,int z, ArrayList<T> row);

    float get(int x, int y, int z);

    ArrayList<T> getColumn(int x, int y);

    ArrayList<T> getRow(int y, int z);

    int getWidth();

    int getHeight();

    int getLength();

    int size();

    boolean isEmpty();

    ArrayGrid3D<T> clone();

}
