package com.csm.rover.simulator.objects.io;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class PlatformConfig implements Serializable {

    String type;
    String screenName;

    PhysicsConfig physics;
    AutonomousConfig autonomous;

    PlatformConfig(){}

    public static Builder builder(){
        return new Builder();
    }

    public String getType(){
        return type;
    }

    public String getScreenName(){
        return screenName;
    }

    public String getPhysicsModelName(){
        return physics.type;
    }

    public Map<String, Double> getPhysicsModelParameters(){
        Map<String, Double> outmap = new TreeMap<String, Double>();
        for (Parameter param : physics.params){
            outmap.put(param.name, param.val);
        }
        return outmap;
    }

    public String getAutonomousModelName(){
        return autonomous.type;
    }

    public Map<String, Double> getAutonomousModelParameters(){
        Map<String, Double> outmap = new TreeMap<String, Double>();
        for (Parameter param : autonomous.params){
            outmap.put(param.name, param.val);
        }
        return outmap;
    }
}

class PhysicsConfig {
    String type;
    Parameter[] params;
}

class AutonomousConfig {
    String type;
    Parameter[] params;
}

class Parameter {
    String name;
    double val;
}

class Builder {

    private PlatformConfig config;

    public Builder(){
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

    public Builder setPhysicsModel(String name){
        return setPhysicsModel(name, null);
    }

    public Builder setPhysicsModel(String name, Map<String, Double> params){
        PhysicsConfig physics = new PhysicsConfig();
        physics.type = name;
        if (params == null){
            params = new TreeMap<String, Double>();
        }
        Parameter[] pars = new Parameter[params.size()];
        int i = 0;
        for (String key : params.keySet()){
            Parameter par = new Parameter();
            par.name = key;
            par.val = params.get(key);
            pars[i] = par;
            i++;
        }
        physics.params = pars;
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
        Parameter[] pars = new Parameter[params.size()];
        int i = 0;
        for (String key : params.keySet()){
            Parameter par = new Parameter();
            par.name = key;
            par.val = params.get(key);
            pars[i] = par;
            i++;
        }
        autonomous.params = pars;
        config.autonomous = autonomous;
        return this;
    }

}
