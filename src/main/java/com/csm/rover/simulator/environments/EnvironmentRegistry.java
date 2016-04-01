package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.PlatformAutonomousCodeModel;
import com.csm.rover.simulator.platforms.PlatformPhysicsModel;
import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;

public class EnvironmentRegistry {

    private static final Logger LOG = LogManager.getLogger(EnvironmentRegistry.class);

    static {
        instance = Optional.empty();
    }

    private static Optional<EnvironmentRegistry> instance;
    private EnvironmentRegistry(){
        environments = new TreeMap<>();
        modifiers = new TreeMap<>();
        modifierParameters = new TreeMap<>();
        populators = new TreeMap<>();
        populatorParameters = new TreeMap<>();
    }

    private Map<String, String> environments;

    private Map<String, Map<String, String>> modifiers;
    private Map<String, Map<String, String[]>> modifierParameters;

    private Map<String, Map<String, String>> populators;
    private Map<String, Map<String, String[]>> populatorParameters;

    public static void fillRegistry(){
        EnvironmentRegistry reg = new EnvironmentRegistry();
        instance = Optional.of(reg);
        instance.get().doFillRegistry();
    }

    private void doFillRegistry(){
        LOG.log(Level.INFO, "Initializing Platform Registry");
        Reflections reflect = new Reflections("com.csm.rover.simulator");

        fillEnvironments(reflect);
        fillModifiers(reflect);
        fillPopulators(reflect);

        LOG.log(Level.INFO, "Platform Registration Complete");
    }

    private void fillEnvironments(Reflections reflect){
        Set<Class<? extends PlatformEnvironment>> classenviros = reflect.getSubTypesOf(PlatformEnvironment.class);
        Set<Class<?>> labelenviros = reflect.getTypesAnnotatedWith(Environment.class);
        List<Set<?>> sortedSets = sortSets(classenviros, labelenviros);
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset1 = (Set<Class<?>>)sortedSets.get(0);
        for (Class<?> clazz : warnset1){
            LOG.log(Level.WARN, "{} extends PlatformEnvironment but is not registered as an Environment", clazz.toString());
        }
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset2 = (Set<Class<?>>)sortedSets.get(2);
        for (Class<?> clazz : warnset2){
            LOG.log(Level.WARN, "{} is a registered Environment but does not extend PlatformEnvironment", clazz.toString());
        }

        @SuppressWarnings("unchecked")
        Set<Class<? extends PlatformEnvironment>> realenviros = (Set<Class<? extends PlatformEnvironment>>)sortedSets.get(1);
        for (Class<? extends PlatformEnvironment> enviro : realenviros){
            String type = enviro.getAnnotation(Environment.class).type();
            try {
                enviro.newInstance();
                this.environments.put(type, getClassPath(enviro));
            }
            catch (InstantiationException | IllegalAccessException e) {
                LOG.log(Level.WARN, "{} does not have a default constructor", enviro.toString());
            }
        }
        if (this.environments.size() == 0){
            LOG.log(Level.WARN, "No Environments were found to load");
        }
        else {
            LOG.log(Level.INFO, "Identified Environments: {}", this.environments.toString());
        }
    }

    private void fillModifiers(Reflections reflect){
        for (String type : environments.keySet()){
            autoModels.put(type, new TreeMap<String, String>());
            autoModelParameters.put(type, new TreeMap<String, String[]>());
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

    private void fillPhysicsModels(Reflections reflect){
        for (String type : platforms.keySet()){
            physicsModels.put(type, new TreeMap<String, String>());
            physicsModelParameters.put(type, new TreeMap<String, String[]>());
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
        for (String type : autoModels.keySet()){
            if (physicsModels.get(type).size() == 0){
                LOG.log(Level.WARN, "Found no PhysicsModels for platform type {}", type);
            }
            else {
                LOG.log(Level.INFO, "For platform type {} found PhysicsModels: {}", type, physicsModels.get(type).toString());
            }
        }
    }

    public static Class<? extends Platform> getPlatform(String type){
        checkInitialized();
        return instance.get().doGetPlatform(type);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Platform> doGetPlatform(String type){
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

    public static Class<? extends PlatformAutonomousCodeModel> getAutonomousCodeModel(String type, String name){
        checkInitialized();
        return instance.get().doGetAutonomousCodeModel(type, name);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends PlatformAutonomousCodeModel> doGetAutonomousCodeModel(String type, String name){
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

    public static Class<? extends PlatformPhysicsModel> getPhysicsModel(String type, String name){
        checkInitialized();
        return instance.get().doGetPhysicsModel(type, name);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends PlatformPhysicsModel> doGetPhysicsModel(String type, String name){
        if (platforms.keySet().contains(type)){
            Map<String, String> models = physicsModels.get(type);
            if (models.keySet().contains(name)) {
                try {
                    return (Class<? extends PlatformPhysicsModel>) Class.forName(models.get(name));
                }
                catch (ClassNotFoundException | ClassCastException e) {
                    LOG.log(Level.ERROR, "Registry failed to properly load class " + autoModels.get(type) + " for PhysicsModel " + type + "." + name, e);
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

    public static List<String> listPhysicsModels(String type){
        checkInitialized();
        return instance.get().doListPhysicsModels(type);
    }

    private List<String> doListPhysicsModels(String type){
        if (physicsModels.keySet().contains(type)){
            return new ArrayList<>(physicsModels.get(type).keySet());
        }
        else {
            LOG.log(Level.ERROR, "Requested type \"{}\" is not registered", type);
            return null;
        }
    }

    public static List<String> listAutonomousCodeModels(String type){
        checkInitialized();
        return instance.get().doListAutonomousCodeModels(type);
    }

    private List<String> doListAutonomousCodeModels(String type){
        if (autoModels.keySet().contains(type)){
            return new ArrayList<>(autoModels.get(type).keySet());
        }
        else {
            LOG.log(Level.ERROR, "Requested type \"{}\" is not registered", type);
            return null;
        }
    }

    public static List<String> getParametersForAutonomousCodeModel(String type, String name){
        checkInitialized();
        return instance.get().doGetParametersForAutonomousCodeModel(type, name);
    }

    private List<String> doGetParametersForAutonomousCodeModel(String type, String name){
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

    public static List<String> getParametersForPhysicsModel(String type, String name){
        checkInitialized();
        return instance.get().doGetParametersForPhysicsModel(type, name);
    }

    private List<String> doGetParametersForPhysicsModel(String type, String name){
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

    private static void checkInitialized(){
        if (!instance.isPresent()){
            throw new IllegalStateException("The registry has not been filled initialized");
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
