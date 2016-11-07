package com.csm.rover.simulator.objects.util;
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

    void addColumnAt(int x, ArrayList<T> col);

    void addRow(ArrayList<T> row);

    void addRowAt(int y, ArrayList<T> row);

    void addLayer(ArrayList<T> lay);

    void addLayerAt(int z, ArrayList<T> lay);

    int get(int x, int y, int z);

    ArrayList<T> getColumn(int x);

    ArrayList<T> getRow(int y);

    int getWidth();

    int getHeight();

    int getLength();

    int getX();

    int getY();

    int getZ();

    int size();

    boolean isEmpty();

    ArrayGrid3D<T> clone();

}
