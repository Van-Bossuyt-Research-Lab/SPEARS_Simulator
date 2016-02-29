package com.csm.rover.simulator.wrapper;

import java.io.Serializable;
import java.util.ArrayList;


public class NamesAndTags implements Serializable {

    public ArrayList<String> groundNames, groundTags, roverNames, roverTags, satelliteNames, satelliteTags;

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

    public NamesAndTags(){

    }

    public String getNameByTag(String tag){
        for (int i = 0; i < groundTags.size(); i++){
            if (groundTags.get(i).equals(tag)){
                return groundNames.get(i);
            }
        }
        for (int i = 0; i < roverTags.size(); i++){
            if (roverTags.get(i).equals(tag)){
                return roverNames.get(i);
            }
        }
        for (int i = 0; i < satelliteTags.size(); i++){
            if (satelliteTags.get(i).equals(tag)){
                return satelliteNames.get(i);
            }
        }
        return null;
    }

    public String getTagByName(String name){
        for (int i = 0; i < groundNames.size(); i++){
            if (groundNames.get(i).equals(name)){
                return groundTags.get(i);
            }
        }
        for (int i = 0; i < roverNames.size(); i++){
            if (roverNames.get(i).equals(name)){
                return roverTags.get(i);
            }
        }
        for (int i = 0; i < satelliteNames.size(); i++){
            if (satelliteNames.get(i).equals(name)){
                return satelliteTags.get(i);
            }
        }
        return null;
    }

    public ArrayList<String> getNames(){
        ArrayList<String> names = new ArrayList<String>();
        for (String name : groundNames){
            names.add(name);
        }
        for (String name : roverNames){
            names.add(name);
        }
        for (String name : satelliteNames){
            names.add(name);
        }
        return names;
    }

    public ArrayList<String> getTags(){
        ArrayList<String> tags = new ArrayList<String>();
        for (String tag : groundTags){
            tags.add(tag);
        }
        for (String tag : roverTags){
            tags.add(tag);
        }
        for (String tag : satelliteTags){
            tags.add(tag);
        }
        return tags;
    }

    public ArrayList<String> getGroundNames() {
        return groundNames;
    }

    public ArrayList<String> getGroundTags() {
        return groundTags;
    }

    public ArrayList<String> getRoverNames() {
        return roverNames;
    }

    public ArrayList<String> getRoverTags() {
        return roverTags;
    }

    public ArrayList<String> getSatelliteNames() {
        return satelliteNames;
    }

    public ArrayList<String> getSatelliteTags() {
        return satelliteTags;
    }
}
