package com.csm.rover.simulator.objects.io;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class TypeConfig implements Cloneable {

    public final String type;
    @JsonIgnore
    public final File mapFile;
    @JsonProperty("platforms")
    public final List<PlatformConfig> platformConfigs;

    @JsonCreator
    public TypeConfig(
            @JsonProperty("type") String type,
            @JsonProperty("map") String mapFile,
            @JsonProperty("platforms") List<PlatformConfig> platforms){
        this.type = type;
        this.mapFile = new File(mapFile);
        this.platformConfigs = Collections.unmodifiableList(platforms);
    }

    public TypeConfig(String type, File mapFile, List<PlatformConfig> platformConfigs){
        this(type, mapFile.getAbsolutePath(), platformConfigs);
    }

    private TypeConfig(TypeConfig orig){
        this.type = orig.type;
        this.mapFile = orig.mapFile;
        this.platformConfigs = Collections.unmodifiableList(orig.platformConfigs);
    }

    @JsonProperty("map")
    public String getMapPath(){
        return mapFile.getAbsolutePath();
    }

    @Override
    public TypeConfig clone(){
        return new TypeConfig(this);
    }

}
