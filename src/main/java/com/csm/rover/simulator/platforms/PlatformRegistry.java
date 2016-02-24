package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.util.*;

public class PlatformRegistry {
    private static final Logger LOG = LogManager.getLogger(PlatformRegistry.class);

    static {
        instance = Optional.empty();
    }

    private static Optional<PlatformRegistry> instance;
    private PlatformRegistry(){
        platforms = new TreeMap<>();
        autoModels = new TreeMap<>();
        physicsModels = new TreeMap<>();
    }

    private Map<String, String> platforms;
    private Map<String, Map<String, String>> autoModels;
    private Map<String, Map<String, String>> physicsModels;

    public static void fillRegistry(){
        PlatformRegistry reg = new PlatformRegistry();
        instance = Optional.of(reg);
        instance.get().doFillRegistry();
    }

    private void doFillRegistry(){
        LOG.log(Level.INFO, "Initializing Platform Registry");
        Reflections reflect = new Reflections("com.csm.rover.simulator");

        fillPlatforms(reflect);
        fillAutoModels(reflect);
        fillPhysicsModels(reflect);
    }

    private void fillPlatforms(Reflections reflect){
        Set<Class<? extends Platform>> classplatforms = reflect.getSubTypesOf(Platform.class);
        Set<Class<?>> labelplatforms = reflect.getTypesAnnotatedWith(com.csm.rover.simulator.platforms.annotations.Platform.class);
        List<Set<?>> sortedSets = sortSets(classplatforms, labelplatforms);
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset1 = (Set<Class<?>>)sortedSets.get(0);
        for (Class<?> clazz : warnset1){
            LOG.log(Level.WARN, "{} extends Platform but is not registered as a Platform", clazz.toString());
        }
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset2 = (Set<Class<?>>)sortedSets.get(2);
        for (Class<?> clazz : warnset2){
            LOG.log(Level.WARN, "{} is a registered Platform but does not extend Platform", clazz.toString());
        }

        @SuppressWarnings("unchecked")
        Set<Class<? extends Platform>> realplatforms = (Set<Class<? extends Platform>>)sortedSets.get(1);
        for (Class<? extends Platform> platform : realplatforms){
            String type = platform.getAnnotation(com.csm.rover.simulator.platforms.annotations.Platform.class).type();
            this.platforms.put(type, getClassPath(platform));
        }
        if (this.platforms.size() == 0){
            LOG.log(Level.WARN, "No platforms were found to load");
        }
        else {
            LOG.log(Level.INFO, "Identified Platforms: {}", this.platforms.toString());
        }
    }

    private void fillAutoModels(Reflections reflect){
        for (String type : platforms.keySet()){
            autoModels.put(type, new TreeMap<String, String>());
        }

        Set<Class<? extends PlatformAutonomousCodeModel>> classautos = reflect.getSubTypesOf(PlatformAutonomousCodeModel.class);
        Set<Class<?>> labelautos = reflect.getTypesAnnotatedWith(com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel.class);
        List<Set<?>> sortedSets = sortSets(classautos, labelautos);
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset1 = (Set<Class<?>>)sortedSets.get(0);
        for (Class<?> clazz : warnset1){
            LOG.log(Level.WARN, "{} extends PlatformAutonomousCodeModel but is not registered as an AutonomousCodeModel", clazz.toString());
        }
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset2 = (Set<Class<?>>)sortedSets.get(2);
        for (Class<?> clazz : warnset2){
            LOG.log(Level.WARN, "{} is a registered AutonomousCodeModel but does not extend PlatformAutonomousCodeModel", clazz.toString());
        }

        @SuppressWarnings("unchecked")
        Set<Class<? extends PlatformAutonomousCodeModel>> realautos = (Set<Class<? extends PlatformAutonomousCodeModel>>)sortedSets.get(1);
        for (Class<? extends PlatformAutonomousCodeModel> auto : realautos){
            if (reflect.getSubTypesOf(auto).size() > 0){
                //Model is a Platform Master
                continue;
            }
            String type = auto.getAnnotation(AutonomousCodeModel.class).type();
            String name = auto.getAnnotation(AutonomousCodeModel.class).name();
            String clazz = getClassPath(auto);
            if (autoModels.keySet().contains(type)){
                try {
                    auto.newInstance();
                    autoModels.get(type).put(name, clazz);
                }
                catch (InstantiationException | IllegalAccessException e) {
                    LOG.log(Level.WARN, "{} does not have a default constructor. Parameters should be set using constructParameters()", auto.toString());
                }
            }
            else {
                LOG.log(Level.WARN, "AutonomousCodeModel {} has type {} which is not a recognized platform", clazz, type);
            }
        }
        for (String type : autoModels.keySet()){
            if (autoModels.get(type).size() == 0){
                LOG.log(Level.WARN, "Found no AutonomousCodeModels for platform type {}", type);
            }
            else {
                LOG.log(Level.INFO, "For platform type {} found AutonomousCodeModels: {}", type, autoModels.get(type).toString());
            }
        }
    }

    private void fillPhysicsModels(Reflections reflect){
        for (String type : platforms.keySet()){
            physicsModels.put(type, new TreeMap<String, String>());
        }

        Set<Class<? extends PlatformPhysicsModel>> classphysics = reflect.getSubTypesOf(PlatformPhysicsModel.class);
        Set<Class<?>> labelphysics = reflect.getTypesAnnotatedWith(com.csm.rover.simulator.platforms.annotations.PhysicsModel.class);
        List<Set<?>> sortedSets = sortSets(classphysics, labelphysics);
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset1 = (Set<Class<?>>)sortedSets.get(0);
        for (Class<?> clazz : warnset1){
            LOG.log(Level.WARN, "{} extends PlatformPhysicsModel but is not registered as a PhysicsModel", clazz.toString());
        }
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset2 = (Set<Class<?>>)sortedSets.get(2);
        for (Class<?> clazz : warnset2){
            LOG.log(Level.WARN, "{} is a registered PhysicsModel but does not extend PlatformPhysicsModel", clazz.toString());
        }

        @SuppressWarnings("unchecked")
        Set<Class<? extends PlatformPhysicsModel>> realphysics = (Set<Class<? extends PlatformPhysicsModel>>)sortedSets.get(1);
        for (Class<? extends PlatformPhysicsModel> physic : realphysics){
            if (reflect.getSubTypesOf(physic).size() > 0){
                //Model is a Platform Master
                continue;
            }
            String type = physic.getAnnotation(PhysicsModel.class).type();
            String name = physic.getAnnotation(PhysicsModel.class).name();
            String clazz = getClassPath(physic);
            if (physicsModels.keySet().contains(type)){
                try {
                    physic.newInstance();
                    physicsModels.get(type).put(name, clazz);
                }
                catch (InstantiationException | IllegalAccessException e) {
                    LOG.log(Level.WARN, "{} does not have a default constructor. Parameters should be set using constructParameters()", physic.toString());
                }
            }
            else {
                LOG.log(Level.WARN, "PhysicsModel {} has type {} which is not a recognized platform", clazz, type);
            }
        }
        for (String type : autoModels.keySet()){
            if (physicsModels.get(type).size() == 0){
                LOG.log(Level.WARN, "Found no PhysicsModels for platform type {}", type);
            }
            else {
                LOG.log(Level.INFO, "For platform type {} found PhysicsModels: {}", type, physicsModels.get(type).toString());
            }
        }
    }

    private static String getClassPath(Class<?> clazz){
        return clazz.toString().substring(clazz.toString().indexOf(' ')+1);
    }

    private <T,V> List<Set<?>> sortSets(Set<T> set1, Set<V> set2){
        Map<String,T> strs1 = new HashMap<>();
        for (T t : set1){
            strs1.put(t.toString(), t);
        }
        Map<String, V> strs2 = new HashMap<>();
        for (V v : set2){
            strs2.put(v.toString(), v);
        }
        Set<T> just1 = new HashSet<>();
        Set<V> just2 = new HashSet<>();
        Map<String, T> both = new HashMap<>();
        for (String s1 : strs1.keySet()){
            for (String s2 : strs2.keySet()){
                if (s1.equals(s2)){
                    both.put(s1, strs1.get(s1));
                }
            }
            if (!both.keySet().contains(s1)){
                just1.add(strs1.get(s1));
            }
        }
        for (String s2 : strs2.keySet()){
            if (!both.keySet().contains(s2)){
                just2.add(strs2.get(s2));
            }
        }
        List<Set<?>> out = new ArrayList<>();
        out.add(just1);
        out.add(new HashSet<T>(both.values()));
        out.add(just2);
        return out;
    }

}
