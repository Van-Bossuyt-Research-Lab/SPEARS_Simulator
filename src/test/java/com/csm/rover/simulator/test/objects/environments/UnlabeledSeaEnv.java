package com.csm.rover.simulator.test.objects.environments;

import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UnlabeledSeaEnv extends PlatformEnvironment {

    @JsonProperty("ID")
    private final String ID;

    public UnlabeledSeaEnv() {
        super("Sub");
        ID = toString();
    }

    @JsonCreator
    public UnlabeledSeaEnv(@JsonProperty("ID") String id){
        super("Sub");
        this.ID = id;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean equals(Object o){
        return o instanceof UnlabeledSeaEnv &&
                this.ID.equals(((UnlabeledSeaEnv)o).ID);
    }

}
