package com.csm.rover.simulator.platforms.sub;

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

    public boolean equals(SubProp other){
        return value == other.value;
    }
}