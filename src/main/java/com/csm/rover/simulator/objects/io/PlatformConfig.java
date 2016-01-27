package com.csm.rover.simulator.objects.io;

import java.util.Map;
import java.util.TreeMap;

public class PlatformConfig {

    private String type;
    private String screenName;

    private PhysicsConfig physics;
    private AutonomousConfig autonomous;

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
