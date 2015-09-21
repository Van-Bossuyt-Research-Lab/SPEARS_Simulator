package com.csm.rover.simulator.map.modifiers;

import com.csm.rover.simulator.map.modifiers.MapModifier;
import com.csm.rover.simulator.objects.ArrayGrid;

public class SurfaceSmoothMod implements MapModifier {

    @Override
    public void modifyMap(ArrayGrid<Float> map) {
        float[][] values2 = new float[values.length-4-((values.length-4)%(detail*2))][values.length-4-((values.length-4)%(detail*2))];
        int count = 9;
        int x = 0;
        while (x < values.length){
            int y = 0;
            while (y < values.length){
                if (x >= 2 && y >= 2 && x-2 < values2.length && y-2 < values2.length) {
                    if (count % 4 == 0){
                        values2[x-2][y-2] = (values[x][y] + (values[x-1][y-2] + values[x+1][y+2]) / 2) / 2;
                    }
                    else if (count % 4 == 1){
                        values2[x-2][y-2] = (values[x][y] + (values[x-2][y-1] + values[x+2][y+1]) / 2) / 2;
                    }
                    else if (count % 4 == 2){
                        values2[x-2][y-2] = (values[x][y] + (values[x-2][y+1] + values[x+2][y-1]) / 2) / 2;
                    }
                    else {
                        values2[x-2][y-2] = (values[x][y] + (values[x-1][y+2] + values[x+1][y-2]) / 2) / 2;
                    }
                }
                count++;
                if (count == Integer.MAX_VALUE){
                    count = 0;
                }
                y++;
            }
            x++;
        }
        this.values = values2;
    }
}
