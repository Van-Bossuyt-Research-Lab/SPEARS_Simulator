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
    private String physicsModelName;
    private String autonomousModelName;

    private Map<String, Double> stateParameters;
    private Map<String, Double> physicsModelParameters;
    private Map<String, Double> autonomousModelParameters;

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
        return physicsModelName;
    }

    public Map<String, Double> getPhysicsModelParameters(){
        try {
            return Collections.unmodifiableMap(physicsModelParameters);
        }
        catch(NullPointerException n){
            System.out.println("no parameters listed");
        }
        return Collections.emptyMap();
    }

    public String getAutonomousModelName(){
        return autonomousModelName;
    }

    public Map<String, Double> getAutonomousModelParameters(){
        try {
            return Collections.unmodifiableMap(autonomousModelParameters);
        }
        catch(NullPointerException n){
            System.out.println("no parameters listed");
            return Collections.emptyMap();
        }
    }

    public Map<String, Double> getStateParameters(){
        return Collections.unmodifiableMap(stateParameters);
    }

    public void setPhysicsModelParameters( Map<String,Double> params) {
            if (params == null){
                throw new NullPointerException("Parameters cannot be null");
            }
            this.physicsModelParameters = params;
    }

    public void setAutonomousModelParameters( Map<String,Double> params) {
        if (params == null){
            throw new NullPointerException("Parameters cannot be null");
        }
        this.autonomousModelParameters = params;
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

        public Builder setPhysicsModelName(String name) {config.physicsModelName = name; return this;}

        public Builder setPhysicsModelParameters(Map<String, Double> params){
            if (params == null){
                params = new TreeMap<String, Double>();
            }
            config.physicsModelParameters = params;
            return this;
        }

        public Builder setPhysicsModel(String name){return setPhysicsModel(name, null);
        }


        public Builder setPhysicsModel(String name, Map<String, Double> params){
            if (params == null){
                params = new TreeMap<String, Double>();
            }
            config.physicsModelName = name;
            config.physicsModelParameters = params;
            return this;
        }

        public Builder setAutonomousModelName(String name) {config.autonomousModelName = name; return this;}

        public Builder setAutonomousModelParameters(Map<String, Double> params){
            if (params == null){
                params = new TreeMap<String, Double>();
            }
            config.autonomousModelParameters= params;
            return this;
        }

        public Builder setAutonomousModel(String name){
            return setAutonomousModel(name, null);
        }

        public Builder setAutonomousModel(String name, Map<String, Double> params){
            if (params == null){
                params = new TreeMap<String, Double>();
            }
            config.autonomousModelName = name;
            config.autonomousModelParameters = params;
            return this;
        }

        public Builder addStateVariable(String param, double value){
            config.stateParameters.put(param, value);
            return this;
        }

    }
}
