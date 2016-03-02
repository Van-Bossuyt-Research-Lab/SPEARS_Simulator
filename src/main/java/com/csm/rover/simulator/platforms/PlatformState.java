package com.csm.rover.simulator.platforms;

import java.util.*;

public class PlatformState {

    protected final String platform_type;

    private boolean readOnly = false;

    private final List<String> params;
    private final Map<String, Double> value_map;
    private final Map<String, Double> default_map;
    
    protected PlatformState(String type, String[] params){
        platform_type = type;
        this.params = Arrays.asList(params);
        value_map = new TreeMap<>();
        default_map = new TreeMap<>();
    }

    private PlatformState(String type, List<String> params){
        platform_type = type;
        this.params = params;
        value_map = new TreeMap<>();
        default_map = new TreeMap<>();
    }

    public String getType(){
        return platform_type;
    }

    public final List<String> expectedValues(){
        return Collections.unmodifiableList(params);
    }

    public final List<String> requiredValues(){
        ArrayList<String> out = new ArrayList<>(params);
        out.removeAll(default_map.keySet());
        return Collections.unmodifiableList(out);
    }

    public final List<String> optionalValues(){
        return Collections.unmodifiableList(new ArrayList<>(default_map.keySet()));
    }

    public final void set(String param, double val){
        checkReadOnly();
        checkParam(param);
        value_map.put(param, val);
    }

    public final double get(String param){
        checkParam(param);
        if (!value_map.keySet().contains(param)){
            try {
                return getDefaultValue(param);
            }
            catch (IndexOutOfBoundsException e){
                throw new IndexOutOfBoundsException("\"%s\" has not been set");
            }
        }
        return value_map.get(param);
    }

    public final void remove(String param){
        checkReadOnly();
        checkParam(param);
        if (value_map.keySet().contains(param)){
            value_map.remove(param);
        }
    }

    protected final void setDefaultValue(String param, double val){
        checkReadOnly();
        checkParam(param);
        default_map.put(param, val);
    }

    protected final double getDefaultValue(String param){
        checkParam(param);
        if (!default_map.keySet().contains(param)) {
            throw new IndexOutOfBoundsException(String.format("\"%s\" has no default value default", param));
        }
        return default_map.get(param);
    }
    
    private void checkReadOnly(){
        if (readOnly){
            throw new IllegalAccessError("This PlatformState is immutable");
        }
    }

    private void checkParam(String param){
        if (!params.contains(param)){
            throw new IllegalArgumentException(String.format("\"%s\" is not a parameter of the set", param));
        }
    }

    private void setReadOnly(){
        readOnly = true;
    }

    public PlatformState immutableCopy(){
        PlatformState out = new PlatformState(platform_type, params);
        for (String key : value_map.keySet()){
            out.value_map.put(key, value_map.get(key));
        }
        for (String key : default_map.keySet()){
            out.default_map.put(key, default_map.get(key));
        }
        out.setReadOnly();
        return out;
    }

}
