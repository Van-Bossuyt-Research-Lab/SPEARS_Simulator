package com.csm.rover.simulator.map.modifiers;

import com.csm.rover.simulator.map.modifiers.MapModifier;
import com.csm.rover.simulator.objects.ArrayGrid;

public class NormalizeMapMod implements MapModifier {

    @Override
    public void modifyMap(ArrayGrid<Float> map) {
        minval = getMin();
        maxval = maxHeight;
        x = 0;
        while (x < this.values.length){
            int y = 0;
            while (y < this.values[0].length){
                this.values[x][y] -= minval;
                y++;
            }
            x++;
        }
        minval = 0;
    }

}
