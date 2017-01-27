package com.csm.rover.simulator.objects.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParamMap {

    public static Builder newParamMap(){
        return new Builder();
    }

    public static Map<String, Double> emptyParamMap(){
        return Collections.emptyMap();
    }

    public static class Builder {

        private Map<String, Double> map;

        private Builder(){
            map = new HashMap<>();
        }

        public Builder addParameter(String name, double value){
            map.put(name, value);
            return this;
        }

        public Map<String, Double> build(){
            return map;
        }

    }

}
