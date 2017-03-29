package com.csm.rover.simulator.platforms.sub;

import com.csm.rover.simulator.objects.CoverageIgnore;

/**
 * Enum to track the location of motor/props in an array.
 */
public enum SubProp {
    L(0),
    LEFT(0),
    R(1),
    RIGHT(1),
    F(2),
    FRONT(2),
    B(3),
    BACK(3);

    private int value;

    SubProp(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    @CoverageIgnore
    public boolean equals(SubProp other){
        return value == other.value;
    }
}
