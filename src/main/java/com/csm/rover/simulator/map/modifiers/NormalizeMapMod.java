package com.csm.rover.simulator.map.modifiers;

import com.csm.rover.simulator.objects.util.ArrayGrid;

public class NormalizeMapMod implements MapModifier {

    @Override
    public void modifyMap(ArrayGrid<Float> map) {
        float minval = getMin(map);
        for (int x = 0; x < map.getWidth(); x++){
            for (int y = 0; y < map.getHeight(); y++){
                map.put(x, y, map.get(x, y) - minval);
            }
        }
    }

    private float getMin(ArrayGrid<Float> map){
        float min = Float.MAX_VALUE;
        for (int x = 0; x < map.getWidth(); x++){
            for (int y = 0; y < map.getHeight(); y++){
                if (map.get(x, y) < min){
                    min = map.get(x, y);
                }
            }
        }
        return min;
    }

}
