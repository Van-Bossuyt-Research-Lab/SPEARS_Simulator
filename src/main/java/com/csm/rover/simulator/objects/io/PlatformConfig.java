package com.csm.rover.simulator.objects.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
@JsonIgnoreProperties(ignoreUnknown = true)

public class PlatformConfig implements Serializable {

    private String type;
    private String id;
    private String screenName;

    private PhysicsConfig physics;
    private AutonomousConfig autonomous;

    private Map<String, Double> stateParameters;

    private PlatformConfig(){
        stateParameters = new TreeMap<>();
    }

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
        return physics.type;
    }

    public Map<String, Double> getPhysicsModelParameters(){
        return Collections.unmodifiableMap(physics.params);
    }

    public String getAutonomousModelName(){
        return autonomous.type;
    }

    public Map<String, Double> getAutonomousModelParameters(){
        return Collections.unmodifiableMap(autonomous.params);
    }

    public Map<String, Double> getStateParameters(){
        return Collections.unmodifiableMap(stateParameters);
    }

    public static class Builder {

        private PlatformConfig config;

        private Builder(){
            config = new PlatformConfig();
        }

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
            if (config.physics == null){
                missing += " PhysicsModel";
            }
            if (config.autonomous == null) {
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

        public Builder setPhysicsModel(String name){
            return setPhysicsModel(name, null);
        }

        public Builder setPhysicsModel(String name, Map<String, Double> params){
            PhysicsConfig physics = new PhysicsConfig();
            physics.type = name;
            if (params == null){
                params = new TreeMap<String, Double>();
            }
            physics.params = params;
            config.physics = physics;
            return this;
        }

        public Builder setAutonomousModel(String name){
            return setAutonomousModel(name, null);
        }

        public Builder setAutonomousModel(String name, Map<String, Double> params){
            AutonomousConfig autonomous = new AutonomousConfig();
            autonomous.type = name;
            if (params == null){
                params = new TreeMap<String, Double>();
            }
            autonomous.params = params;
            config.autonomous = autonomous;
            return this;
        }

        public Builder addStateVariable(String param, double value){
            config.stateParameters.put(param, value);
            return this;
        }

    }
}

class PhysicsConfig {
    String type;
    Map<String, Double> params;
}

class AutonomousConfig {
    String type;
    Map<String, Double> params;
}

