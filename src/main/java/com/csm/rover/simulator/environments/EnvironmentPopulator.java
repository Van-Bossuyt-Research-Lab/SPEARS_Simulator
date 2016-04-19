package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.objects.util.RecursiveGridList;

import java.util.Map;

public abstract class EnvironmentPopulator<T> {

    protected final String platform_type;
    protected final String name;

    protected RecursiveGridList<T> value_map;
    protected final T default_value;

    protected EnvironmentPopulator(Built<T> build){
        platform_type = build.type;
        this.name = build.name;
        this.default_value = build.default_val;
    }

    protected static <U> Builder<U> builder(Class<U> type){
        return new Builder<>();
    }

    protected static class Builder<T> {

        private String type, name;
        private T default_val;

        private Builder(){}

        public Builder setType(String type){
            this.type = type;
            return this;
        }

        public Builder setName(String name){
            this.name = name;
            return this;
        }

        public Builder setDefaultValue(T default_val){
            this.default_val = default_val;
            return this;
        }

        public Built<T> build(){
            if (type == null || name == null || default_val == null){
                throw new IllegalStateException("The builder is not fully initialized");
            }
            return new Built<T>(type, name, default_val);
        }
    }

    private static class Built<T> {

        private String type, name;
        private T default_val;

        private Built(String type, String name, T default_val){
            this.type = type;
            this.name = name;
            this.default_val = default_val;
        }
    }

    public final String getType(){
        return platform_type;
    }

    public final void build(EnvironmentMap map, Map<String, Double> params){
        if (!map.getType().equals(platform_type)){
            throw new IllegalArgumentException(String.format("Types do not match %s != %s", platform_type, map.getType()));
        }
        value_map = doBuild(map, params);
    }

    abstract protected RecursiveGridList<T> doBuild(final EnvironmentMap map, final Map<String, Double> params);

    public T getValue(double... coordinates){
        if (value_map == null){
            return default_value;
        }
        int[] coords = new int[coordinates.length];
        for (int i = 0; i > coordinates.length; i++){
            coords[i] = (int)Math.floor(coordinates[i]);
        }
        try {
            return value_map.get(coords);
        }
        catch (IndexOutOfBoundsException e){
            return default_value;
        }
    }

}
