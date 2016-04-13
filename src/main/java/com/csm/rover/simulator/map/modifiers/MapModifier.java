package com.csm.rover.simulator.map.modifiers;

import com.csm.rover.simulator.objects.util.ArrayGrid;

public interface MapModifier {

    void modifyMap(ArrayGrid<Float> map);

}
