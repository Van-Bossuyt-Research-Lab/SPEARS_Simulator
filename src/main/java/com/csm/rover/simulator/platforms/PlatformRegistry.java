package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.objects.CoverageIgnore;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import com.csm.rover.simulator.platforms.annotations.State;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;

public class PlatformRegistry {
    @CoverageIgnore
    private static final Logger LOG = LogManager.getLogger(PlatformRegistry.class);

    private static final Reflections reflect = new Reflections("com.csm.rover.simulator.platforms");

    private static Map<String, String> platforms;

    private static Map<String, String> platformStates;

    private static Map<String, Map<String, String>> autoModels;
    private static Map<String, Map<String, String>> physicsModels;

    private static Map<String, Map<String, String[]>> autoModelParameters;
    private static Map<String, Map<String, String[]>> physicsModelParameters;

    public static void fillRegistry() {
        platforms = new TreeMap<>();
        platformStates = new TreeMap<>();
        autoModels = new TreeMap<>();
        physicsModels = new TreeMap<>();
        autoModelParameters = new TreeMap<>();
        physicsModelParameters = new TreeMap<>();

        LOG.log(Level.INFO, "Initializing Platform Registry");

        fillPlatforms(reflect);
        fillStates(reflect);
        fillAutoModels(reflect);
        fillPhysicsModels(reflect);

        LOG.log(Level.INFO, "Platform Registration Complete");
    }

    private static void fillPlatforms(Reflections reflect){
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
            try {
                platform.newInstance();
                platforms.put(type, getClassPath(platform));
            }
            catch (InstantiationException | IllegalAccessException e) {
                LOG.log(Level.WARN, "{} does not have a default constructor", platform.toString());
            }
        }
        if (platforms.size() == 0){
            LOG.log(Level.WARN, "No platforms were found to load");
        }
        else {
            LOG.log(Level.INFO, "Identified Platforms: {}", platforms.toString());
        }
    }

    private static void fillStates(Reflections reflect){
        Set<Class<? extends PlatformState>> classstates = reflect.getSubTypesOf(PlatformState.class);
        Set<Class<?>> labeledstates = reflect.getTypesAnnotatedWith(com.csm.rover.simulator.platforms.annotations.State.class);
        List<Set<?>> sortedSets = sortSets(classstates, labeledstates);
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset1 = (Set<Class<?>>)sortedSets.get(0);
        for (Class<?> clazz : warnset1){
            LOG.log(Level.WARN, "{} extends PlatformState but is not registered as a PlatformState", clazz.toString());
        }
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset2 = (Set<Class<?>>)sortedSets.get(2);
        for (Class<?> clazz : warnset2){
            LOG.log(Level.WARN, "{} is a registered PlatformState but does not extend PlatformState", clazz.toString());
        }

        @SuppressWarnings("unchecked")
        Set<Class<? extends PlatformState>> realstates = (Set<Class<? extends PlatformState>>)sortedSets.get(1);
        for (Class<? extends PlatformState> state: realstates){
            String type = state.getAnnotation(State.class).type();
            if (platforms.keySet().contains(type)){
                try {
                    state.newInstance();
                    platformStates.put(type, getClassPath(state));
                }
                catch (InstantiationException | IllegalAccessException e) {
                    LOG.log(Level.WARN, "{} does not have a default constructor.", state.toString());
                }
            }
            else {
                LOG.log(Level.WARN, "PlatformState {} has type {} which is not a recognized platform", getClassPath(state), type);
            }
        }
        for (String type : platforms.keySet()){
            if (platformStates.containsKey(type)){
                LOG.log(Level.INFO, "For Platform {} identified state {}", type, platformStates.get(type));
            }
            else {
                LOG.log(Level.WARN, "No PlatformState found for type {}", type);
            }
        }
    }

    private static void fillAutoModels(Reflections reflect){
        for (String type : platforms.keySet()){
            autoModels.put(type, new TreeMap<>());
            autoModelParameters.put(type, new TreeMap<>());
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
            AutonomousCodeModel annotation = auto.getAnnotation(AutonomousCodeModel.class);
            String type = annotation.type();
            String name = annotation.name();
            String[] params = annotation.parameters();
            String clazz = getClassPath(auto);
            if (Modifier.isAbstract(auto.getModifiers())){
                LOG.log(Level.DEBUG, "Abstract parent model {} is ignored", auto.toString());
                continue;
            }
            if (autoModels.keySet().contains(type)){
                try {
                    auto.newInstance();
                    autoModels.get(type).put(name, clazz);
                    autoModelParameters.get(type).put(name, params);
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

    private static void fillPhysicsModels(Reflections reflect){
        for (String type : platforms.keySet()){
            physicsModels.put(type, new TreeMap<>());
            physicsModelParameters.put(type, new TreeMap<>());
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
            PhysicsModel annotation = physic.getAnnotation(PhysicsModel.class);
            String type = annotation.type();
            String name = annotation.name();
            String[] params = annotation.parameters();
            String clazz = getClassPath(physic);
            if (Modifier.isAbstract(physic.getModifiers())){
                LOG.log(Level.DEBUG, "Abstract parent model {} is ignored", physic.toString());
                continue;
            }
            if (physicsModels.keySet().contains(type)){
                try {
                    physic.newInstance();
                    physicsModels.get(type).put(name, clazz);
                    physicsModelParameters.get(type).put(name, params);
                }
                catch (InstantiationException | IllegalAccessException e) {
                    LOG.log(Level.WARN, "{} does not have a default constructor. Parameters should be set using constructParameters()", physic.toString());
                }
            }
            else {
                LOG.log(Level.WARN, "PhysicsModel {} has type {} which is not a recognized platform", clazz, type);
            }
        }
        for (String type : physicsModels.keySet()){
            if (physicsModels.get(type).size() == 0){
                LOG.log(Level.WARN, "Found no PhysicsModels for platform type {}", type);
            }
            else {
                LOG.log(Level.INFO, "For platform type {} found PhysicsModels: {}", type, physicsModels.get(type).toString());
            }
        }
    }

    public static List<String> getTypes(){
        return new ArrayList<>(platforms.keySet());
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends Platform> getPlatform(String type){
        if (platforms.keySet().contains(type)){
            try {
                return (Class<? extends Platform>)Class.forName(platforms.get(type));
            }
            catch (ClassNotFoundException | ClassCastException e){
                LOG.log(Level.ERROR, "Registry failed to properly load class " + platforms.get(type) + " for Platform " + type, e);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "The Platform \"{}\" is not registered and cannot be returned", type);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends PlatformState> getPlatformState(String type){
        if (platformStates.containsKey(type)){
            try {
                return (Class<? extends PlatformState>)Class.forName(platformStates.get(type));
            }
            catch (ClassNotFoundException | ClassCastException e){
                LOG.log(Level.ERROR, "Registry failed to properly load class " + platforms.get(type) + " as a PlatformState of " + type, e);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "The platform type {} is not registered and cannot be returned");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends PlatformAutonomousCodeModel> getAutonomousCodeModel(String type, String name){
        if (platforms.keySet().contains(type)){
            Map<String, String> models = autoModels.get(type);
            if (models.keySet().contains(name)) {
                try {
                    return (Class<? extends PlatformAutonomousCodeModel>) Class.forName(models.get(name));
                }
                catch (ClassNotFoundException | ClassCastException e) {
                    LOG.log(Level.ERROR, "Registry failed to properly load class " + autoModels.get(type) + " for AutonomousCodeModel " + type + "." + name, e);
                    return null;
                }
            }
            else {
                LOG.log(Level.ERROR, "The AutonomousCodeModel \"{}\" for platform \"{}\" is not registered and cannot be returned", name, type);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "The Platform \"{}\" is not registered and cannot be requested", type);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends PlatformPhysicsModel> getPhysicsModel(String type, String name){
        if (platforms.keySet().contains(type)){
            Map<String, String> models = physicsModels.get(type);
            if (models.keySet().contains(name)) {
                try {
                    return (Class<? extends PlatformPhysicsModel>) Class.forName(models.get(name));
                }
                catch (ClassNotFoundException | ClassCastException e) {
                    LOG.log(Level.ERROR, "Registry failed to properly load class " + physicsModels.get(type) + " for PhysicsModel " + type + "." + name, e);
                    return null;
                }
            }
            else {
                LOG.log(Level.ERROR, "The PhysicsModel \"{}\" for platform \"{}\" is not registered and cannot be returned", name, type);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "The Platform \"{}\" is not registered and cannot be requested", type);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> listPhysicsModels(String type){
        if (physicsModels.keySet().contains(type)){
            return new ArrayList<>(physicsModels.get(type).keySet());
        }
        else {
            LOG.log(Level.ERROR, "Requested type \"{}\" is not registered", type);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> listAutonomousCodeModels(String type){
        if (autoModels.keySet().contains(type)){
            return new ArrayList<>(autoModels.get(type).keySet());
        }
        else {
            LOG.log(Level.ERROR, "Requested type \"{}\" is not registered", type);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getParametersForAutonomousCodeModel(String type, String name){
        if (autoModelParameters.keySet().contains(type)){
            if (autoModelParameters.get(type).keySet().contains(name)){
                return Arrays.asList(autoModelParameters.get(type).get(name));
            }
            else {
                LOG.log(Level.ERROR, "Requested type autonomous code model \"{}\" of type {} is not registered", name, type);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "Requested type \"{}\" is not registered", type);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getParametersForPhysicsModel(String type, String name){
        if (physicsModelParameters.keySet().contains(type)){
            if (physicsModelParameters.get(type).keySet().contains(name)){
                return Arrays.asList(physicsModelParameters.get(type).get(name));
            }
            else {
                LOG.log(Level.ERROR, "Requested type physics model \"{}\" of type {} is not registered", name, type);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "Requested type \"{}\" is not registered", type);
            return null;
        }
    }

    private static String getClassPath(Class<?> clazz){
        return clazz.toString().substring(clazz.toString().indexOf(' ')+1);
    }

    private static <T,V> List<Set<?>> sortSets(Set<T> set1, Set<V> set2){
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
