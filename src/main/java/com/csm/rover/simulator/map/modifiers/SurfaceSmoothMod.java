package com.csm.rover.simulator.map.modifiers;

import com.csm.rover.simulator.objects.util.ArrayGrid;

public class SurfaceSmoothMod implements MapModifier {

    @Override
    public void modifyMap(ArrayGrid<Float> map) {
        ArrayGrid<Float> values2 = map.clone();
        int count = 9;
        for (int x = 0; x < map.getWidth(); x++){
            for (int y = 0; y < map.getHeight(); y++){
                if (x > 2 && y > 2 && x < map.getWidth()-2 && y < map.getHeight()-2) {
                    if (count % 4 == 0) {
                        map.put(x, y, average(values2.get(x, y), average(values2.get(x - 1, y - 2), values2.get(x + 1, y + 2))));
                    }
                    else if (count % 4 == 1) {
                        map.put(x, y, average(values2.get(x, y), average(values2.get(x - 2, y - 1), values2.get(x + 2, y + 1))));
                    }
                    else if (count % 4 == 2) {
                        map.put(x, y, average(values2.get(x, y), average(values2.get(x - 2, y + 1), values2.get(x + 2, y - 1))));
                    }
                    else {
                        map.put(x, y, average(values2.get(x, y), average(values2.get(x - 1, y + 2), values2.get(x + 1, y - 2))));
                    }
                }
            }
            count = (count +1) % Integer.MAX_VALUE;
        }
    }
    
    private float average(float a, float b){
        return (a+b)/2.0f;
    }
}
