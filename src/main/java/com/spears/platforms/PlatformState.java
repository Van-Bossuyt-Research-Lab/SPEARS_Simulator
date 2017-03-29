/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.platforms;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * A multi-type map that stores values relevant to the operational state of the platform.
 */
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

    /**
     * Protected constructor requires an {@link Builder} parameter to define the fields.
     *
     * @param builder See {@link #builder(String)}
     */
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

    /**
     * Returns the platform type of the State.
     *
     * @return Type name
     */
    public String getType(){
        return platform_type;
    }

    /**
     * Returns the names of all valid map fields.
     *
     * @return Set of param names
     */
    public final Set<String> expectedValues(){
        return Collections.unmodifiableSet(parameters.keySet());
    }

    /**
     * Returns the Class type a named parameter is stored as.
     *
     * @param name The name of the parameter.
     *
     * @return The type of the variable
     *
     * @throws IllegalArgumentException if the parameter is unknown
     */
    public final Class<?> getParameterType(String name){
        checkParam(name);
        return parameters.get(name);
    }

    /**
     * Returns a list of all the parameters which do not have default values.  These should be filled in
     * prior to starting a simulation to avoid errors.
     *
     * @return A set of parameter names
     */
    public final Set<String> requiredValues(){
        Set<String> out = new TreeSet<>(parameters.keySet());
        out.removeAll(double_default_values.keySet());
        out.removeAll(double_list_default_values.keySet());
        out.removeAll(string_default_values.keySet());
        return Collections.unmodifiableSet(out);
    }

    /**
     * Returns a list of all the parameter which have a default value and are not required to be filled in
     * to avoid errors.
     *
     * @return A set of parameter names.
     */
    public final Set<String> optionalValues(){
        Set<String> out = new TreeSet<>();
        out.addAll(double_default_values.keySet());
        out.addAll(double_list_default_values.keySet());
        out.addAll(string_default_values.keySet());
        return Collections.unmodifiableSet(out);
    }

    /**
     * Stores a value in the state map.
     *
     * WARNING: the map can only store Double, Double[], and String.  Do not attempt to use
     * int, double[], or Integer.
     *
     * @param param The name of the parameter to be set
     * @param val The value to store
     * @param <T> Unchecked type, should be Double, Double[], or Stirng
     *
     * @throws IllegalArgumentException if the parameter is unknown
     * @throws ClassCastException if val is not the type param is stored as
     * @throws IllegalAccessError if the state is read only
     */
    public final <T> void set(String param, T val){
        checkReadOnly();
        Class<?> type = val.getClass();
        try {
            checkParam(param, type);
        }
        catch (ClassCastException e){
            throw new IllegalArgumentException("Unexpected value \'" + val + "\' for parameter " + param, e);
        }
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

    /**
     * Retrieves a value from the state map
     *
     * WARNING: the map can only store Double, Double[], and String.  Do not attempt to use
     * int, double[], or Integer.  Class casting will not be done until the return value is used.
     *
     * @param param The name of the parameter to be retrieved
     * @param <T> Unchecked type, will be Double, Double[], or Stirng
     *
     * @return Value stored in the map
     *
     * @throws IllegalArgumentException if the parameter is unknown
     * @throws IndexOutOfBoundsException if the parameter has not been set and doesn't have a default
     */
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
                throw new IndexOutOfBoundsException(String.format("\"%s\" has not been set", param));
            }
            return (T)new Double(out);
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
                throw new IndexOutOfBoundsException(String.format("\"%s\" has not been set", param));
            }
            return (T)out;
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
            return (T)out;
        }
        else {
            throw new IllegalStateException("You did something very very wrong");
        }
    }

    /**
     * Removed a value from the state map.  If the param has a default, it will revert to that.
     *
     * @param param The name of the parameter to be cleared
     *
     * @throws IllegalArgumentException if the parameter is unknown
     * @throws IllegalAccessError if the state is read only
     */
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
            throw new ClassCastException("The parameter " + param + " is of type " + parameters.get(param).toString() + " not " + type.toString());
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

    /**
     * Creates a clone of the State which cannot be edited.
     *
     * @return A new immutable PlatformState
     */
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

    /**
     * Creates a new {@link Builder} which is passed to the constructor.  Builder should define all
     * of the fields for the State.
     *
     * @param type Platform type name of the State
     * @return Builder object
     */
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

        /**
         * Adds a required field to the state.
         *
         * WARNING: the map can only store Double, Double[], and String.  Do not attempt to use
         * int, double[], or Integer.
         *
         * @param name Name of the field
         * @param type Type to store the field as
         * @param <T> Type parameter, should be Double, Double[], or String
         *
         * @return this
         */
        public <T> Builder addParameter(String name, Class<T> type){
            addParameter(name, type, null);
            return this;
        }

        /**
         * Adds an optional field to the state.
         *
         * WARNING: the map can only store Double, Double[], and String.  Do not attempt to use
         * int, double[], or Integer.
         *
         * @param name Name of the field
         * @param type Type to store the field as
         * @param default_val default value of the field
         * @param <T> Type parameter, should be Double, Double[], or String
         *
         * @return
         */
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

    /**
     * Overrides values of the State with values extracted from the map.  Unknown values or values
     * stored with the wrong type will be ignored and reported to the LOG.
     *
     * @param vals Parameter map to set
     *
     * @return this
     */
    public PlatformState overrideValues(Map<String, Object> vals){
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
