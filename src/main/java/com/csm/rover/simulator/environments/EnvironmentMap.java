package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.objects.CoverageIgnore;
import com.csm.rover.simulator.objects.io.jsonserial.EnvironmentMapDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EnvironmentMapDeserializer.class)
public abstract class EnvironmentMap {

    @JsonIgnore
    protected final String platform_type;

    protected EnvironmentMap(String type){
        platform_type = type;
    }

    public String getType(){
        return platform_type;
    }

    @CoverageIgnore
    @Override
    public boolean equals(Object o){
        return o instanceof EnvironmentMap && isEqual((EnvironmentMap)o);
    }

    protected abstract boolean isEqual(EnvironmentMap other);

}
