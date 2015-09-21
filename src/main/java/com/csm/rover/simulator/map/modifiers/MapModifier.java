package com.csm.rover.simulator.map.modifiers;

import com.csm.rover.simulator.objects.ArrayGrid;

public interface MapModifier {

    void modifyMap(ArrayGrid<Float> map);

}
