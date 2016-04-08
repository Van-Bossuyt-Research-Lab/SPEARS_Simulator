package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.platforms.Platform;
import com.google.common.collect.Lists;

import java.util.*;


public class NamesAndTags implements Cloneable {

    public ArrayList<String> groundNames, groundTags, roverNames, roverTags, satelliteNames, satelliteTags;
    private Map<String, Map<String, String>> correlation;
    //          type        name    tag

    public NamesAndTags(ArrayList<String> roverNames,
                        ArrayList<String> roverTags,
                        ArrayList<String> satelliteNames,
                        ArrayList<String> satelliteTags){
        groundNames = new ArrayList<String>();
        groundTags = new ArrayList<String>();
        groundNames.add("Ground");
        groundTags.add("g");
        this.roverNames = roverNames;
        this.roverTags = roverTags;
        this.satelliteNames = satelliteNames;
        this.satelliteTags = satelliteTags;
    }

    public static Builder newNamesAndTags(){
        return new Builder();
    }

    public static class Builder {
        private NamesAndTags nat;

        private Builder(){
            nat = new NamesAndTags();
        }

        public Builder addPlatform(List<PlatformConfig> platforms){
            if (platforms == null){
                throw new NullPointerException("platforms cannot be null lists");
            }
            if (platforms.size() == 0){
                throw new IllegalArgumentException("list must have at least one value");
            }
            String type = platforms.get(0).getType();
            if (!nat.correlation.containsKey(type)){
                nat.correlation.put(type, new TreeMap<String, String>());
            }
            for (PlatformConfig platform : platforms){
                nat.correlation.get(type).put(platform.getScreenName(), platform.getID());
            }
            return this;
        }

        public NamesAndTags build(){
            Map<String, String> groundMap = new TreeMap<>();
            groundMap.put("Ground", "g");
            nat.correlation.put("Ground", groundMap);
            return nat;
        }
    }

    private NamesAndTags(){
        this.correlation = new TreeMap<>();
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
