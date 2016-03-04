package com.csm.rover.simulator.platforms;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class PlatformState {
    private static final Logger LOG = LogManager.getLogger(PlatformState.class);

    private final String platform_type;

    private boolean readOnly = false;

    private final Map<String, Class<?>> parameters;

    private final Map<String, Double> double_values;
    private final Map<String, Double[]> double_list_values;
    private final Map<String, String> string_values;

    private final Map<String, Double> double_default_values;
    private final Map<String, Double[]> double_list_default_values;
    private final Map<String, String> string_default_values;

    protected PlatformState(Builder builder){
        this(builder.build());
    }

    private PlatformState (
            String type,
            Map<String, Class<?>> params,
            Map<String, Double> double_default_values,
            Map<String, Double[]> double_list_default_values,
            Map<String, String> string_default_values
            ){
        this.platform_type = type;
        this.parameters = copyMap(params);
        this.double_values = new TreeMap<>();
        this.double_default_values = copyMap(double_default_values);
        this.double_list_values = new TreeMap<>();
        this.double_list_default_values = copyMap(double_list_default_values);
        this.string_values = new TreeMap<>();
        this.string_default_values = copyMap(string_default_values);
    }

    private PlatformState(PlatformState orig){
        platform_type = orig.getType();
        this.parameters = copyMap(orig.parameters);
        this.double_values = copyMap(orig.double_values);
        this.double_default_values = copyMap(orig.double_default_values);
        this.double_list_values = copyMap(orig.double_list_values);
        this.double_list_default_values = copyMap(orig.double_list_default_values);
        this.string_values = copyMap(orig.string_values);
        this.string_default_values = copyMap(orig.string_default_values);
    }

    public String getType(){
        return platform_type;
    }

    public final List<String> expectedValues(){
        return Collections.unmodifiableList(new ArrayList<>(parameters.keySet()));
    }

    public final Class<?> getParameterType(String name){
        checkParam(name);
        return parameters.get(name);
    }

    public final List<String> requiredValues(){
        ArrayList<String> out = new ArrayList<>(parameters.keySet());
        out.removeAll(double_default_values.keySet());
        out.removeAll(double_list_default_values.keySet());
        out.removeAll(string_default_values.keySet());
        return Collections.unmodifiableList(out);
    }

    public final List<String> optionalValues(){
        List<String> out = new ArrayList<>();
        out.addAll(double_default_values.keySet());
        out.addAll(double_list_default_values.keySet());
        out.addAll(string_default_values.keySet());
        return out;
    }

    public final <T> void set(String param, T val){
        checkReadOnly();
        Class<?> type = val.getClass();
        checkParam(param, type);
        if (type == Double.class){
            double_values.put(param, (Double)val);
        }
        else if (type == Double[].class){
            double_list_values.put(param, (Double[])val);
        }
        else if (type == String.class){
            string_values.put(param, (String)val);
        }
    }

    @SuppressWarnings("unchecked")
    public final <T> T get(String param){
        checkParam(param);
        Class<?> type = parameters.get(param);
        if (type == Double.class){
            double out;
            if (double_values.containsKey(param)){
                out = double_values.get(param);
            }
            else if (double_default_values.containsKey(param)){
                out = double_default_values.get(param);
            }
            else {
                throw new IndexOutOfBoundsException("\"%s\" has not been set");
            }
            try {
                return (T)new Double(out);
            }
            catch (ClassCastException e){
                throw new TypeNotPresentException(String.format("The parameter \"%s\" is stored as a double", param), e);
            }
        }
        else if (type == Double[].class){
            Double[] out;
            if (double_list_values.containsKey(param)){
                out = double_list_values.get(param);
            }
            else if (double_list_default_values.containsKey(param)){
                out = double_list_default_values.get(param);
            }
            else {
                throw new IndexOutOfBoundsException("\"%s\" has not been set");
            }
            try {
                return (T)out;
            }
            catch (ClassCastException e){
                throw new TypeNotPresentException(String.format("The parameter \"%s\" is stored as a double array", param), e);
            }
        }
        else if (type == String.class){
            String out;
            if (string_values.containsKey(param)){
                out = string_values.get(param);
            }
            else if (string_default_values.containsKey(param)){
                out = string_default_values.get(param);
            }
            else {
                throw new IndexOutOfBoundsException("\"%s\" has not been set, no default value");
            }
            try {
                return (T)out;
            }
            catch (ClassCastException e){
                throw new TypeNotPresentException(String.format("The parameter \"%s\" is stored as a string", param), e);
            }
        }
        else {
            throw new IllegalStateException("You did something very very wrong");
        }
    }

    public final void remove(String param){
        checkReadOnly();
        checkParam(param);
        Class<?> type = parameters.get(param);
        if (type == Double.class){
            double_values.remove(param);
        }
        else if (type == Double[].class){
            double_list_values.remove(param);
        }
        else if (type == String.class){
            string_values.remove(param);
        }
    }
    
    private void checkReadOnly(){
        if (readOnly){
            throw new IllegalAccessError("This PlatformState is immutable");
        }
    }

    private void checkParam(String param, Class<?> type){
        checkParam(param);
        if (parameters.get(param) != type){
            throw new IllegalArgumentException(String.format("The parameter %s of of type %s not %s", param, parameters.get(param).toString(), type.toString()));
        }
    }

    private void checkParam(String param){
        if (!parameters.containsKey(param)){
            throw new IllegalArgumentException(String.format("\"%s\" is not a parameter of the set", param));
        }
    }

    private void setReadOnly(){
        readOnly = true;
    }

    public PlatformState immutableCopy(){
        PlatformState out = new PlatformState(this);
        out.setReadOnly();
        return out;
    }

    private <T> Map<String, T> copyMap(Map<String, T> orig){
        Map<String, T> out = new TreeMap<>();
        for (String key : orig.keySet()){
            out.put(key, orig.get(key));
        }
        return out;
    }

    protected static Builder builder(String type){
        return new Builder(type);
    }

    protected static class Builder {

        protected String type;
        protected Map<String, Class<?>> parameters;

        protected Map<String, Double> double_default_values;
        protected Map<String, Double[]> double_list_default_values;
        protected Map<String, String> string_default_values;


        private Builder(String type){
            this.type = type;
            parameters = new TreeMap<>();
            double_default_values = new TreeMap<>();
            double_list_default_values = new TreeMap<>();
            string_default_values = new TreeMap<>();
        }

        public <T> Builder addParameter(String name, Class<T> type){
            addParameter(name, type, null);
            return this;
        }

        public <T> Builder addParameter(String name, Class<T> type, T default_val){
            if (parameters.containsKey(name)){
                throw new IllegalArgumentException(String.format("A parameter of name \"%s\" has already been written", name));
            }
            if (type == Double.class){
                if (default_val != null){
                    double_default_values.put(name, (Double) default_val);
                }
            }
            else if (type == Double[].class){
                if (default_val != null){
                    double_list_default_values.put(name, (Double[])default_val);
                }
            }
            else if (type == String.class){
                if (default_val != null){
                    string_default_values.put(name, (String)default_val);
                }
            }
            else {
                throw new IllegalArgumentException(String.format("The given type \"%s\" is not allowed in the state", type.toString()));
            }
            parameters.put(name, type);
            return this;
        }

        private PlatformState build(){
            return new PlatformState(
                    type,
                    parameters,
                    double_default_values,
                    double_list_default_values,
                    string_default_values
            );
        }
    }
    
    public PlatformState overrideValues(Map<String, Double> vals){
        for (String key : vals.keySet()){
            if (parameters.containsKey(key)){
                try {
                    set(key, parameters.get(key).cast(vals.get(key)));
                }
                catch (ClassCastException e){
                    LOG.log(Level.WARN, String.format("\"%s\" did not match the expected parameter type", key), e);
                }
            }
            else {
                LOG.log(Level.WARN, "\"{}\" is not a state parameter for type {}", key, platform_type);
            }
        }
        return this;
    }

}
