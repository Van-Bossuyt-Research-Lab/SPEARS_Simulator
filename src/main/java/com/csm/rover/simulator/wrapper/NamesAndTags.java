package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.objects.io.TypeConfig;
import com.csm.rover.simulator.platforms.Platform;
import com.google.common.collect.Lists;

import java.util.*;


public class NamesAndTags implements Cloneable {

    private Map<String, Map<String, String>> correlation;
    //          type        name    tag

    private NamesAndTags(){
        this.correlation = new TreeMap<>();
    }

    public static NamesAndTags newFromPlatforms(List<TypeConfig> platforms){
        NamesAndTags nat = new NamesAndTags();
        for (TypeConfig typeConfig : platforms){
            String type = typeConfig.type;
            if (!nat.correlation.containsKey(type)){
                nat.correlation.put(type, new TreeMap<>());
            }
            for (PlatformConfig platformConfig : typeConfig.platformConfigs) {
                nat.correlation.get(type).put(platformConfig.getScreenName(), platformConfig.getID());
            }
        }
        Map<String, String> groundMap = new TreeMap<>();
        groundMap.put("Ground", "g");
        nat.correlation.put("Ground", groundMap);
        return nat;
    }

    private NamesAndTags(NamesAndTags orig){
        this();
        for (String key : orig.correlation.keySet()){
            this.correlation.put(key, new TreeMap<String, String>());
            for (String name : orig.correlation.get(key).keySet()){
                this.correlation.get(key).put(name, orig.correlation.get(key).get(name));
            }
        }
    }

    public String getNameByTag(String tag){
        for (String type : correlation.keySet()){
            for (String name : correlation.get(type).keySet()){
                if (correlation.get(type).get(name).equals(tag)){
                    return name;
                }
            }
        }
        throw new IndexOutOfBoundsException("The tag \""+tag+"\" is not registered");
    }

    public String getTagByName(String name){
        for (String type : correlation.keySet()){
            if (correlation.get(type).containsKey(name)){
                return correlation.get(type).get(name);
            }
        }
        throw new IndexOutOfBoundsException("The name \""+name+"\" is not registered");
    }

    public ArrayList<String> getNames(){
        ArrayList<String> names = new ArrayList<>();
        for (String type : correlation.keySet()){
            names.addAll(correlation.get(type).keySet());
        }
        return names;
    }

    public ArrayList<String> getNames(String platform) {
        if (correlation.containsKey(platform)){
            return new ArrayList<>(correlation.get(platform).keySet());
        }
        else {
            throw new IndexOutOfBoundsException("The platform \""+platform+"\" is not registered");
        }
    }

    public ArrayList<String> getTags(){
        ArrayList<String> tags = new ArrayList<>();
        for (String type : correlation.keySet()){
            for (String name : correlation.get(type).keySet()){
                tags.add(correlation.get(type).get(name));
            }
        }
        return tags;
    }

    public ArrayList<String> getTags(String platform) {
        if (correlation.containsKey(platform)) {
            return new ArrayList<>(correlation.get(platform).values());
        }
        else {
            throw new IndexOutOfBoundsException("The platform \""+platform+"\" is not registered");
        }
    }

    @Override
    public NamesAndTags clone(){
        return new NamesAndTags(this);
    }
}
