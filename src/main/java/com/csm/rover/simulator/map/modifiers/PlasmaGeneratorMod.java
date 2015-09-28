package com.csm.rover.simulator.map.modifiers;

import com.csm.rover.simulator.objects.ArrayGrid;
import com.csm.rover.simulator.objects.FloatArrayArrayGrid;

import java.util.ArrayList;
import java.util.Random;

public class PlasmaGeneratorMod implements MapModifier {

    private double rough;

    private Random rnd = new Random();

    public PlasmaGeneratorMod(double rough){
        this.rough = rough;
    }

    @Override
    public void modifyMap(ArrayGrid<Float> map) {
        int size = getFractalLayerCount(map.getWidth());
        double rough = this.rough;
        double roughFactor = rough/size;

        ArrayGrid<Float> values = new FloatArrayArrayGrid();
        double seed = rnd.nextInt(30) * rnd.nextDouble();
        values.put(0, 0, (float) Math.abs(seed + random()));
        values.put(0, 1, (float) Math.abs(seed + random()));
        values.put(1, 0, (float) Math.abs(seed + random()));
        values.put(1, 1, (float) Math.abs(seed + random()));
        for (int master = 1; master < size; master++){
            expand(values);
            for (int x = 0; x < values.getWidth(); x++){
                for (int y = 0; y < values.getHeight(); y++){
                    if ((x+1) % 2 == 0){
                        if ((y+1) % 2 == 0){
                            values.put(x, y, center(values.get(x - 1, y - 1), values.get(x - 1, y + 1), values.get(x + 1, y - 1), values.get(x + 1, y + 1), rough));
                        }
                        else {
                            values.put(x, y, midpoint(values.get(x - 1, y), values.get(x + 1, y), rough));
                        }
                    }
                    else {
                        if ((y+1) % 2 == 0){
                            values.put(x, y, midpoint(values.get(x, y - 1), values.get(x, y + 1), rough));
                        }
                    }
                }
            }
            rough -= roughFactor;
            if (rough < 0){
                rough = 0;
            }
        }
        int xstart = (values.getWidth() - map.getWidth())/2;
        int ystart = (values.getHeight() - map.getHeight())/2;
        for (int x = 0; x < map.getWidth(); x++){
            for (int y = 0; y < map.getHeight(); y++){
                map.put(x, y, values.get(x+xstart, y+ystart));
            }
        }
    }

    private int getFractalLayerCount(int size){
        return (int)(Math.log(2*size-2)/Math.log(2.)) + 1;
    }

    //part of the plasma fractal generation, pushes the array from |x|x|x| to |x|_|x|_|x|
    private void expand(ArrayGrid<Float> vals){
        int size = vals.getWidth();
        for (int i = size-1; i > 0; i--){
            vals.addRowAt(i, new ArrayList<Float>());
            vals.addColumnAt(i, new ArrayList<Float>());
        }
    }

    private float center(float a, float b, float c, float d, double rough){
        return (float) ((a+b+c+d)/4 + (rough*random()));
    }

    private float midpoint(float a, float b, double rough){
        return (float) ((a+b)/2 + (rough*random()));
    }

    private float random(){
        float out = (float) (rough*10*rnd.nextDouble());
        if (rnd.nextBoolean()){
            out *= -1;
        }
        return out;
    }
}
