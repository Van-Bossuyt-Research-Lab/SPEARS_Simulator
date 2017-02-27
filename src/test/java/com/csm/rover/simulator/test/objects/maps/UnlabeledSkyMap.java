package com.csm.rover.simulator.test.objects.maps;

import com.csm.rover.simulator.environments.EnvironmentMap;

public class UnlabeledSkyMap extends EnvironmentMap{

    public UnlabeledSkyMap() {
        super("UAV");
    }

    private boolean equalsCalled = false;

    @Override
    protected boolean isEqual(EnvironmentMap other) {
        equalsCalled = true;
        return this == other;
    }

    public boolean wasEqualsCalled(){
        return equalsCalled;
    }

}
