package com.csm.rover.simulator.map.populators;

import com.csm.rover.simulator.objects.GridList;
import java.awt.Dimension;

public class MapTargetField extends MapPopularsField {

    @Override
    public void generate(boolean mono, Dimension mapSize, double density) {
        if (mono){
            generateTargets(mapSize, density);
        }
        else {
            generateValuedTargets(mapSize, density);
        }
    }

    //Generate a target distribution
    private void generateTargets(Dimension mapSize, double density){
        values = new GridList<Integer>();
        mono = true;
        int size = (int)(mapSize.getWidth()*mapSize.getHeight()*density);
        int x = 0;
        while (x < size){
            values.put(10, rnd.nextInt((int)mapSize.getWidth()), rnd.nextInt((int)mapSize.getHeight()));
            x++;
        }
    }

    private void generateValuedTargets(Dimension mapSize, double density){
        values = new GridList<Integer>();
        mono = true;
        int size = (int)(mapSize.getWidth()*mapSize.getHeight()*density);
        int x = 0;
        while (x < size){
            int value;
            double rand = rnd.nextDouble();
            if (rand < 0.3){
                value = 1;
            }
            else if (rand < 0.45){
                value = 2;
            }
            else if (rand < 0.6){
                value = 3;
            }
            else if (rand < 0.7){
                value = 4;
            }
            else if (rand < 0.8){
                value = 5;
            }
            else if (rand < 0.86){
                value = 6;
            }
            else if (rand < 0.91){
                value = 7;
            }
            else if (rand < 0.95){
                value = 8;
            }
            else if (rand < 0.98){
                value = 9;
            }
            else {
                value = 10;
            }
            values.put(value, rnd.nextInt((int)mapSize.getWidth()), rnd.nextInt((int)mapSize.getHeight()));
            x++;
        }
    }
}
