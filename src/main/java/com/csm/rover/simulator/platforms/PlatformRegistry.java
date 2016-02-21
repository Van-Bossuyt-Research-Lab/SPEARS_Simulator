package com.csm.rover.simulator.platforms;

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
        platforms = new ArrayList<>();
        autoModels = new TreeMap<>();
        physicsModels = new TreeMap<>();
    }

    private ArrayList<String> platforms;
    private Map<String, ArrayList<String>> autoModels;
    private Map<String, ArrayList<String>> physicsModels;

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
        Set<Class<? extends Platform>> platforms = reflect.getSubTypesOf(Platform.class);
        for (Class<? extends Platform> platform : platforms){
            this.platforms.add(getClassPath(platform));
        }
        LOG.log(Level.INFO, "Identified Platforms: {}", platforms.toString());
    }

    private void fillAutoModels(Reflections reflect){
        Set<Class<? extends PlatformAutonomousCodeModel>> allautos = reflect.getSubTypesOf(PlatformAutonomousCodeModel.class);
        for (Class<? extends PlatformAutonomousCodeModel> auto : allautos){
            if (reflect.getSubTypesOf(auto).size() > 0){
                this.autoModels.put(getClassPath(auto), new ArrayList<String>());
            }
        }
        LOG.log(Level.INFO, "Identified AutonomousModel Types: {}", autoModels.keySet().toString());
        for (String platformModel : autoModels.keySet()){
            try {
                Set<?> models = reflect.getSubTypesOf(Class.forName(platformModel));
                for (Object model : models){
                    try {
                        autoModels.get(platformModel).add(getClassPath((Class<?>)model));
                    }
                    catch (ClassCastException e){
                        LOG.log(Level.ERROR, model+" could not be parsed to a Class<?> object", e);
                    }
                }
            }
            catch (Exception e){
                LOG.log(Level.ERROR, "Platform of class " + platformModel + " was found but could not be accessed", e);
            }
            LOG.log(Level.INFO, "For AutonomousModel Type \"{}\" found Models: {}", platformModel, autoModels.get(platformModel).toString());
        }
    }

    private void fillPhysicsModels(Reflections reflect){
        Set<Class<? extends PlatformPhysicsModel>> allphysics = reflect.getSubTypesOf(PlatformPhysicsModel.class);
        for (Class<? extends PlatformPhysicsModel> physics : allphysics){
            if (reflect.getSubTypesOf(physics).size() > 0){
                this.physicsModels.put(getClassPath(physics), new ArrayList<String>());
            }
        }
        LOG.log(Level.INFO, "Identified PhysicsModel Types: {}", physicsModels.keySet().toString());
        for (String platformModel : physicsModels.keySet()) {
            try {
                Set<?> models = reflect.getSubTypesOf(Class.forName(platformModel));
                for (Object model : models) {
                    try {
                        physicsModels.get(platformModel).add(getClassPath((Class<?>) model));
                    } catch (ClassCastException e) {
                        LOG.log(Level.ERROR, model + " could not be parsed to a Class<?> object", e);
                    }
                }
            } catch (Exception e) {
                LOG.log(Level.ERROR, "Platform of class " + platformModel + " was found but could not be accessed", e);
            }
            LOG.log(Level.INFO, "For PhysicsModel Type \"{}\" found Models: {}", platformModel, physicsModels.get(platformModel).toString());
        }
    }

    private static String getClassPath(Class<?> clazz){
        return clazz.toString().substring(clazz.toString().indexOf(' ')+1);
    }

}
