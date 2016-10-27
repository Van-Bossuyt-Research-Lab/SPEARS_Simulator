package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.util.*;

public class EnvironmentRegistry {

    private static final Logger LOG = LogManager.getLogger(EnvironmentRegistry.class);

    static {
        environments = new TreeMap<>();
        maps = new TreeMap<>();
        modifiers = new TreeMap<>();
        modifierParameters = new TreeMap<>();
        populators = new TreeMap<>();
        populatorParameters = new TreeMap<>();
        populatorDisplays = new TreeMap<>();
        fillRegistry();
    }

    private static Map<String, String> environments;
    private static Map<String, String> maps;

    private static Map<String, Map<String, String>> modifiers;
    private static Map<String, Map<String, String[]>> modifierParameters;

    private static Map<String, Map<String, String>> populators;
    private static Map<String, Map<String, String[]>> populatorParameters;
    private static Map<String, Map<String, PopulatorDisplayFunction>> populatorDisplays;

    private static void fillRegistry(){
        LOG.log(Level.INFO, "Initializing Environment Registry");
        Reflections reflect = new Reflections("com.csm.rover.simulator.environments");

        fillEnvironments(reflect);
        fillMaps(reflect);
        fillModifiers(reflect);
        fillPopulators(reflect);

        LOG.log(Level.INFO, "Environment Registration Complete");
    }

    private static void fillEnvironments(Reflections reflect){
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
                environments.put(type, getClassPath(enviro));
            }
            catch (InstantiationException | IllegalAccessException e) {
                LOG.log(Level.WARN, "{} does not have a default constructor", enviro.toString());
            }
        }
        if (environments.size() == 0){
            LOG.log(Level.WARN, "No Environments were found to load");
        }
        else {
            LOG.log(Level.INFO, "Identified Environments: {}", environments.toString());
        }
    }

    private static void fillMaps(Reflections reflect){
        Set<Class<? extends EnvironmentMap>> classmaps = reflect.getSubTypesOf(EnvironmentMap.class);
        Set<Class<?>> labelmaps = reflect.getTypesAnnotatedWith(com.csm.rover.simulator.environments.annotations.Map.class);
        List<Set<?>> sortedSets = sortSets(classmaps, labelmaps);
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset1 = (Set<Class<?>>)sortedSets.get(0);
        for (Class<?> clazz : warnset1){
            LOG.log(Level.WARN, "{} extends EnvironmentMap but is not registered as a Map", clazz.toString());
        }
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset2 = (Set<Class<?>>)sortedSets.get(2);
        for (Class<?> clazz : warnset2){
            LOG.log(Level.WARN, "{} is a registered Map but does not extend EnvironmentMap", clazz.toString());
        }

        @SuppressWarnings("unchecked")
        Set<Class<? extends EnvironmentMap>> realmaps = (Set<Class<? extends EnvironmentMap>>)sortedSets.get(1);
        for (Class<? extends EnvironmentMap> map : realmaps){
            String type = map.getAnnotation(com.csm.rover.simulator.environments.annotations.Map.class).type();
            try {
                map.newInstance();
                maps.put(type, getClassPath(map));
            }
            catch (InstantiationException | IllegalAccessException e) {
                LOG.log(Level.WARN, "{} does not have a default constructor", map.toString());
            }
        }
        if (maps.size() == 0){
            LOG.log(Level.WARN, "No Maps were found to load");
        }
        else {
            LOG.log(Level.INFO, "Identified Maps: {}", maps.toString());
        }
    }

    private static void fillModifiers(Reflections reflect){
        for (String type : environments.keySet()){
            modifiers.put(type, new TreeMap<String, String>());
            modifierParameters.put(type, new TreeMap<String, String[]>());
        }

        Set<Class<? extends EnvironmentModifier>> classmods = reflect.getSubTypesOf(EnvironmentModifier.class);
        Set<Class<?>> labelmods = reflect.getTypesAnnotatedWith(com.csm.rover.simulator.environments.annotations.Modifier.class);
        List<Set<?>> sortedSets = sortSets(classmods, labelmods);
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset1 = (Set<Class<?>>)sortedSets.get(0);
        for (Class<?> clazz : warnset1){
            LOG.log(Level.WARN, "{} extends EnvironmentModifier but is not registered as an Modifier", clazz.toString());
        }
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset2 = (Set<Class<?>>)sortedSets.get(2);
        for (Class<?> clazz : warnset2){
            LOG.log(Level.WARN, "{} is a registered Modifier but does not extend EnvironmentModifier", clazz.toString());
        }

        @SuppressWarnings("unchecked")
        Set<Class<? extends EnvironmentModifier>> realmods = (Set<Class<? extends EnvironmentModifier>>)sortedSets.get(1);
        for (Class<? extends EnvironmentModifier> mod : realmods){
            Modifier annotation = mod.getAnnotation(com.csm.rover.simulator.environments.annotations.Modifier.class);
            String type = annotation.type();
            String name = annotation.name();
            String[] params = annotation.parameters();
            String clazz = getClassPath(mod);
            if (modifiers.keySet().contains(type)){
                try {
                    mod.newInstance();
                    modifiers.get(type).put(name, clazz);
                    modifierParameters.get(type).put(name, params);
                }
                catch (InstantiationException | IllegalAccessException e) {
                    LOG.log(Level.WARN, "{} does not have a default constructor", mod.toString());
                }
            }
            else {
                LOG.log(Level.WARN, "Modifier {} has type {} which is not a recognized platform", clazz, type);
            }
        }
        for (String type : modifiers.keySet()){
            if (modifiers.get(type).size() == 0){
                LOG.log(Level.WARN, "Found no Modifiers for platform type {}", type);
            }
            else {
                LOG.log(Level.INFO, "For platform type {} found Modifiers: {}", type, modifiers.get(type).toString());
            }
        }
    }

    private static void fillPopulators(Reflections reflect){
        for (String type : environments.keySet()){
            populators.put(type, new TreeMap<String, String>());
            populatorParameters.put(type, new TreeMap<String, String[]>());
            populatorDisplays.put(type, new TreeMap<>());
        }

        Set<Class<? extends EnvironmentPopulator>> classpops = reflect.getSubTypesOf(EnvironmentPopulator.class);
        Set<Class<?>> labelpops = reflect.getTypesAnnotatedWith(Populator.class);
        List<Set<?>> sortedSets = sortSets(classpops, labelpops);
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset1 = (Set<Class<?>>)sortedSets.get(0);
        for (Class<?> clazz : warnset1){
            LOG.log(Level.WARN, "{} extends EnvironmentPopulator but is not registered as a Populator", clazz.toString());
        }
        @SuppressWarnings("unchecked")
        Set<Class<?>> warnset2 = (Set<Class<?>>)sortedSets.get(2);
        for (Class<?> clazz : warnset2){
            LOG.log(Level.WARN, "{} is a registered Populator but does not extend EnvironmentPopulator", clazz.toString());
        }

        @SuppressWarnings("unchecked")
        Set<Class<? extends EnvironmentPopulator>> realpops = (Set<Class<? extends EnvironmentPopulator>>)sortedSets.get(1);
        for (Class<? extends EnvironmentPopulator> pop : realpops){
            Populator annotation = pop.getAnnotation(Populator.class);
            String type = annotation.type();
            String name = annotation.name();
            String[] params = annotation.parameters();
            String clazz = getClassPath(pop);
            if (populators.keySet().contains(type)){
                try {
                    EnvironmentPopulator inst = pop.newInstance();
                    populators.get(type).put(name, clazz);
                    populatorParameters.get(type).put(name, params);
                    if (inst.getDisplayFunction() == null){
                        LOG.log(Level.WARN, "Populator {} of type {} has no display function", name, type);
                    }
                    else {
                        populatorDisplays.get(type).put(name, inst.getDisplayFunction());
                    }
                }
                catch (InstantiationException | IllegalAccessException e) {
                    LOG.log(Level.WARN, "{} does not have a default constructor", pop.toString());
                }
            }
            else {
                LOG.log(Level.WARN, "Populator {} has type {} which is not a recognized platform", clazz, type);
            }
        }
        for (String type : populators.keySet()){
            if (populators.get(type).size() == 0){
                LOG.log(Level.WARN, "Found no Populators for platform type {}", type);
            }
            else {
                LOG.log(Level.INFO, "For platform type {} found Populators: {}", type, populators.get(type).toString());
            }
        }
    }

    public static List<String> getTypes(){
        return new ArrayList<>(environments.keySet());
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends PlatformEnvironment> getEnvironment(String type){
        if (environments.keySet().contains(type)){
            try {
                return (Class<? extends PlatformEnvironment>)Class.forName(environments.get(type));
            }
            catch (ClassNotFoundException | ClassCastException e){
                LOG.log(Level.ERROR, "Registry failed to properly load class " + environments.get(type) + " for Environment " + type, e);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "The Environment \"{}\" is not registered and cannot be returned", type);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends EnvironmentMap> getMap(String type){
        if (maps.keySet().contains(type)){
            try {
                return (Class<? extends EnvironmentMap>)Class.forName(maps.get(type));
            }
            catch (ClassNotFoundException | ClassCastException e){
                LOG.log(Level.ERROR, "Registry failed to properly load class " + maps.get(type) + " for " + type + " map", e);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "The Map \"{}\" is not registered and cannot be returned", type);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends EnvironmentModifier> getModifier(String type, String name){
        if (environments.keySet().contains(type)){
            Map<String, String> models = modifiers.get(type);
            if (models.keySet().contains(name)) {
                try {
                    return (Class<? extends EnvironmentModifier>) Class.forName(models.get(name));
                }
                catch (ClassNotFoundException | ClassCastException e) {
                    LOG.log(Level.ERROR, "Registry failed to properly load class " + modifiers.get(type) + " for Modifier " + type + "." + name, e);
                    return null;
                }
            }
            else {
                LOG.log(Level.ERROR, "The Modifier \"{}\" for environment \"{}\" is not registered and cannot be returned", name, type);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "The Environment \"{}\" is not registered and cannot be requested", type);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends EnvironmentPopulator> getPopulator(String type, String name){
        if (environments.keySet().contains(type)){
            Map<String, String> models = populators.get(type);
            if (models.keySet().contains(name)) {
                try {
                    return (Class<? extends EnvironmentPopulator>) Class.forName(models.get(name));
                }
                catch (ClassNotFoundException | ClassCastException e) {
                    LOG.log(Level.ERROR, "Registry failed to properly load class " + populators.get(type) + " for Populator " + type + "." + name, e);
                    return null;
                }
            }
            else {
                LOG.log(Level.ERROR, "The Populator \"{}\" for environment \"{}\" is not registered and cannot be returned", name, type);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "The Environment \"{}\" is not registered and cannot be requested", type);
            return null;
        }
    }

    public static PopulatorDisplayFunction getPopulatorDisplayFunction(String type, String name){
        if (environments.containsKey(type)){
            Map<String, PopulatorDisplayFunction> displays = populatorDisplays.get(type);
            if (displays.containsKey(name)){
                return displays.get(name);
            }
            else {
                LOG.log(Level.ERROR, "The Populator \"{}\" has no defined display and cannot be returned", name);
            }
        }
        else {
            LOG.log(Level.ERROR, "The Environment \"{}\" is not registered and cannot be requested", type);
        }
        return null;
    }

    public static List<String> listPopulators(String type){
        if (populators.keySet().contains(type)){
            return new ArrayList<>(populators.get(type).keySet());
        }
        else {
            LOG.log(Level.ERROR, "Requested type \"{}\" is not registered", type);
            return null;
        }
    }

    public static List<String> listModifiers(String type){
        if (modifiers.keySet().contains(type)){
            return new ArrayList<>(modifiers.get(type).keySet());
        }
        else {
            LOG.log(Level.ERROR, "Requested type \"{}\" is not registered", type);
            return null;
        }
    }

    public static List<String> getParametersForModifier(String type, String name){
        if (modifierParameters.keySet().contains(type)){
            if (modifierParameters.get(type).keySet().contains(name)){
                return Arrays.asList(modifierParameters.get(type).get(name));
            }
            else {
                LOG.log(Level.ERROR, "Requested Modifier \"{}\" of type {} is not registered", name, type);
                return null;
            }
        }
        else {
            LOG.log(Level.ERROR, "Requested type \"{}\" is not registered", type);
            return null;
        }
    }

    public static List<String> getParametersForPopulator(String type, String name){
        if (populatorParameters.keySet().contains(type)){
            if (populatorParameters.get(type).keySet().contains(name)){
                return Arrays.asList(populatorParameters.get(type).get(name));
            }
            else {
                LOG.log(Level.ERROR, "Requested Populator \"{}\" of type {} is not registered", name, type);
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
