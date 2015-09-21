package com.csm.rover.simulator.map.modifiers;

import com.csm.rover.simulator.map.modifiers.MapModifier;
import com.csm.rover.simulator.objects.ArrayGrid;

import java.util.Random;

public class PlasmaGeneratorMod implements MapModifier {

    private Random rnd = new Random();


    @Override
    public void modifyMap(ArrayGrid<Float> map) {
        double rough = size * roughFactor;
        this.layerSize = size;
        this.rough = rough;
        this.detail = det;
        double seed = rnd.nextInt(30) * rnd.nextDouble();
        float[][] values = new float[2][2];
        values[0][0] = (float) Math.abs(seed + random());
        values[0][1] = (float) Math.abs(seed + random());
        values[1][0] = (float) Math.abs(seed + random());
        values[1][1] = (float) Math.abs(seed + random());
        int master = 0;
        while (master <= size){
            values = expand(values);
            int x = 0;
            while (x < values.length){
                int y = 0;
                while (y < values.length){
                    if ((x+1) % 2 == 0){
                        if ((y+1) % 2 == 0){
                            values[x][y] = center(values[x-1][y-1], values[x-1][y+1], values[x+1][y-1], values[x+1][y+1], rough);
                        }
                        else {
                            values[x][y] = midpoint(values[x-1][y], values[x+1][y], rough);
                        }
                    }
                    else {
                        if ((y+1) % 2 == 0){
                            values[x][y] = midpoint(values[x][y-1], values[x][y+1], rough);
                        }
                    }
                    y++;
                }
                x++;
            }
            rough -= roughFactor;
            if (rough < 0){
                rough = 0;
            }
            master++;
        }
    }

    //part of the plasma fractal generation, pushes the array from |x|x|x| to |x|_|x|_|x|
    private float[][] expand(float[][] values2){
        float[][] out = new float[values2.length * 2 - 1][values2.length * 2 - 1];
        int x = 0;
        while (x < values2.length){
            int y = 0;
            while (y < values2.length){
                out[x*2][y*2] = values2[x][y];
                y++;
            }
            x++;
        }
        return out;
    }

    private float center(float a, float b, float c, float d, double rough){
        return (float) ((a+b+c+d)/4 + (rough*random()));
    }

    private float midpoint(float a, float b, double rough){
        return (float) ((a+b)/2 + (rough*random()));
    }

    private float random(){
        int rough = (int)(this.rough * 10.0);
        while (rough < 1){
            rough *= 10;
        }
        float out = (float) (rnd.nextInt(rough) + rnd.nextDouble());
        if (rnd.nextBoolean()){
            out *= -1;
        }
        return out;
    }
}
