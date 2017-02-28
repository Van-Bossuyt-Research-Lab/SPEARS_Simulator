package com.csm.rover.simulator.objects.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * A data structure object that stores information about platform instances.  Object and its contents are not
 * modifiable after initialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformConfig {

    private String type;
    private String id;
    private String screenName;
    private String physicsModelName;
    private String autonomousModelName;

    private Map<String, Double> stateParameters;
    private Map<String, Double> physicsModelParameters;
    private Map<String, Double> autonomousModelParameters;

    private PlatformConfig(){
        stateParameters = new TreeMap<>();
    }

    /**
     * Builder initialization method.
     *
     * @return A builder
     */
    public static Builder builder(){
        return new Builder();
    }

    public String getType(){
        return type;
    }

    public String getScreenName(){
        return screenName;
    }

    public String getID(){
        return id;
    }

    public String getPhysicsModelName(){
        return physicsModelName;
    }

    public Map<String, Double> getPhysicsModelParameters(){
        return Collections.unmodifiableMap(physicsModelParameters);
    }

    public String getAutonomousModelName(){
        return autonomousModelName;
    }

    public Map<String, Double> getAutonomousModelParameters(){
        return Collections.unmodifiableMap(autonomousModelParameters);
    }

    public Map<String, Double> getStateParameters(){
        return Collections.unmodifiableMap(stateParameters);
    }

    public static class Builder {

        private PlatformConfig config;

        private Builder(){
            config = new PlatformConfig();
        }

        /**
         * Creates and returns the completed PlatformConfig.  Checks to make sure that all fields were initialized
         * correctly.
         *
         * @return Completed platform config
         * @throws IllegalStateException If the builder was not fully initialized
         */
        public PlatformConfig build(){
            String missing = "";
            if (config.type == null){
                missing += " platform_type";
            }
            if (config.screenName == null){
                missing += " screenName";
            }
            if (config.id == null){
                missing += " ID";
            }
            if (config.physicsModelName == null || config.physicsModelParameters == null){
                missing += " PhysicsModel";
            }
            if (config.autonomousModelName == null || config.autonomousModelParameters == null) {
                missing += " AutonomousModel";
            }
            if (!missing.equals("")){
                throw new IllegalStateException("The PlatformConfiguration being built was not fully initialized.  Missing:" + missing);
            }
            return config;
        }

        public Builder setType(String type){
            config.type = type;
            return this;
        }

        public Builder setScreenName(String name){
            config.screenName = name;
            return this;
        }

        public Builder setID(String ID){
            config.id = ID;
            return this;
        }

        /**
         * JSON use function.  Recommended to use {@link #setPhysicsModel(String, Map)}.
         *
         * @param name The name of the physics model
         * @return this
         */
        public Builder setPhysicsModelName(String name) {config.physicsModelName = name; return this;}

        /**
         * JSON use function.  Recommended to use {@link #setPhysicsModel(String, Map)}.
         *
         * @param params Build parameters for the physics model
         * @return this
         */
        public Builder setPhysicsModelParameters(Map<String, Double> params){
            if (params == null){
                params = new TreeMap<>();
            }
            config.physicsModelParameters = params;
            return this;
        }

        public Builder setPhysicsModel(String name, Map<String, Double> params){
            return setPhysicsModelName(name).setPhysicsModelParameters(params);
        }

        /**
         * JSON use function.  Recommended to use {@link #setAutonomousModel(String, Map)}.
         *
         * @param name The name of the autonomous code model
         * @return this
         */
        public Builder setAutonomousModelName(String name) {config.autonomousModelName = name; return this;}

        /**
         * JSON use function.  Recommended to use {@link #setAutonomousModel(String, Map)}.
         *
         * @param params Build parameters for the autonomous code model
         * @return this
         */
        public Builder setAutonomousModelParameters(Map<String, Double> params){
            if (params == null){
                params = new TreeMap<>();
            }
            config.autonomousModelParameters= params;
            return this;
        }

        public Builder setAutonomousModel(String name, Map<String, Double> params){
            return setAutonomousModelName(name).setAutonomousModelParameters(params);
        }

        /**
         * Modifies entries in the {@link com.csm.rover.simulator.platforms.PlatformState}.  This is the only optional
         * builder field.
         *
         * @param param Name of parameter
         * @param value Value to set it to
         * @return this
         */
        public Builder addStateVariable(String param, double value){
            config.stateParameters.put(param, value);
            return this;
        }

    }
}
