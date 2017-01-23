package com.csm.rover.simulator.platforms;

import com.csm.rover.simulator.platforms.annotations.AutonomousCodeModel;
import com.csm.rover.simulator.platforms.annotations.PhysicsModel;
import com.csm.rover.simulator.platforms.annotations.State;
import com.csm.rover.simulator.test.objects.AutoModels.*;
import com.csm.rover.simulator.test.objects.PhysicsModels.*;
import com.csm.rover.simulator.test.objects.platforms.*;
import com.csm.rover.simulator.test.objects.states.*;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlatformRegistryTest {

    private static Method fillPlatforms, fillStates, fillAutoModels, fillPhysicsModels;

    @BeforeClass
    public static void makeVisible(){
        try {
            fillPlatforms = PlatformRegistry.class.getDeclaredMethod("fillPlatforms", Reflections.class);
            fillPlatforms.setAccessible(true);
            fillStates = PlatformRegistry.class.getDeclaredMethod("fillStates", Reflections.class);
            fillStates.setAccessible(true);
            fillAutoModels = PlatformRegistry.class.getDeclaredMethod("fillAutoModels", Reflections.class);
            fillAutoModels.setAccessible(true);
            fillPhysicsModels= PlatformRegistry.class.getDeclaredMethod("fillPhysicsModels", Reflections.class);
            fillPhysicsModels.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Reflections reflectionsMock;

    private static Map<String, String> platforms;
    private static Map<String, String> platformStates;
    private static Map<String, Map<String, String>> autoModels;
    private static Map<String, Map<String, String>> physicsModels;
    private static Map<String, Map<String, String[]>> autoModelParameters;
    private static Map<String, Map<String, String[]>> physicsModelParameters;

    @Before
    public void clearRegistry(){
        platforms = new TreeMap<>();
        platformStates = new TreeMap<>();
        autoModels = new TreeMap<>();
        physicsModels = new TreeMap<>();
        autoModelParameters = new TreeMap<>();
        physicsModelParameters = new TreeMap<>();

        PlatformRegistry pr = new PlatformRegistry();
        Inject.field("platforms").of(pr).with(platforms);
        Inject.field("platformStates").of(pr).with(platformStates);
        Inject.field("autoModels").of(pr).with(autoModels);
        Inject.field("physicsModels").of(pr).with(physicsModels);
        Inject.field("autoModelParameters").of(pr).with(autoModelParameters);
        Inject.field("physicsModelParameters").of(pr).with(physicsModelParameters);

        reflectionsMock = mock(Reflections.class);
    }

//  PLATFORMS ----------------------------------------------------------------------------------------------------------
    private void setupFillPlatforms() throws InvocationTargetException, IllegalAccessException {
        doReturn(Sets.newHashSet(RoverPlatform.class, UnlabeledPlatform.class, NoncreatableSub.class, UAVPlatform.class))
                .when(reflectionsMock).getSubTypesOf(Platform.class);
        when(reflectionsMock.getTypesAnnotatedWith(com.csm.rover.simulator.platforms.annotations.Platform.class)).thenReturn(
                Sets.newHashSet(RoverPlatform.class, IncorrectlyLabeledPlatform.class, NoncreatableSub.class, UAVPlatform.class));
        fillPlatforms.invoke(null, reflectionsMock);
    }

    @Test
    public void testPlatforms_addsComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillPlatforms();
        assert platforms.containsValue(RoverPlatform.class.getName());
        assert platforms.containsValue(UAVPlatform.class.getName());
    }

    @Test
    public void testPlatforms_checkHasLabel() throws InvocationTargetException, IllegalAccessException {
        setupFillPlatforms();
        assert !platforms.containsValue(UnlabeledPlatform.class.getName());
    }

    @Test
    public void testPlatforms_checkIsPlatform() throws InvocationTargetException, IllegalAccessException {
        setupFillPlatforms();
        assert !platforms.containsValue(IncorrectlyLabeledPlatform.class.getName());
    }

    @Test
    public void testPlatforms_checkCanCreate() throws InvocationTargetException, IllegalAccessException {
        setupFillPlatforms();
        assert !platforms.containsValue(NoncreatableSub.class.getName());
    }

//  STATES -------------------------------------------------------------------------------------------------------------
    private void setupFillStates() throws InvocationTargetException, IllegalAccessException {
        platforms.put("Rover", "RoverPlatform");
        platforms.put("Sub", "SubPlatform");
        platforms.put("UAV", "UAVPlatform");
        platforms.put("Horse", "Because");
        doReturn(Sets.newHashSet(UnlabeledSubState.class, NoncreatableUAVState.class, RoverState.class, MoleState.class))
                .when(reflectionsMock).getSubTypesOf(PlatformState.class);
        doReturn(Sets.newHashSet(RoverState.class, NoncreatableUAVState.class, IncorrectlyLabeledState.class, MoleState.class))
                .when(reflectionsMock).getTypesAnnotatedWith(State.class);
        fillStates.invoke(null, reflectionsMock);
    }

    @Test
    public void testStates_addsComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillStates();
        assert platformStates.containsValue(RoverState.class.getName());
    }

    @Test
    public void testStates_checksHasLabel() throws InvocationTargetException, IllegalAccessException {
        setupFillStates();
        assert !platformStates.containsValue(UnlabeledSubState.class.getName());
    }

    @Test
    public void testStates_checksIsState() throws InvocationTargetException, IllegalAccessException {
        setupFillStates();
        assert !platformStates.containsValue(IncorrectlyLabeledState.class.getName());
    }

    @Test
    public void testStates_checksCanCreate() throws InvocationTargetException, IllegalAccessException {
        setupFillStates();
        assert !platformStates.containsValue(NoncreatableUAVState.class.getName());
    }

    @Test
    public void testStates_checksKnownPlatform() throws InvocationTargetException, IllegalAccessException {
        setupFillStates();
        assert !platformStates.containsValue(MoleState.class.getName());
    }

//  AUTO MODELS --------------------------------------------------------------------------------------------------------
    private void setupFillAutoModels() throws InvocationTargetException, IllegalAccessException {
        platforms.put("Rover", "RoverPlatform");
        platforms.put("Sub", "SubPlatform");
        platforms.put("UAV", "UAVPlatform");
        platforms.put("Horse", "Why");
        doReturn(Sets.newHashSet(MissingMoleCode.class, SubCode.class, UnlabeledUAVCode.class, PrivateRoverCode.class, AbstractSubCode.class))
                .when(reflectionsMock).getSubTypesOf(PlatformAutonomousCodeModel.class);
        doReturn(Sets.newHashSet(MissingMoleCode.class, PrivateRoverCode.class, IncorrectlyLabeledCode.class, SubCode.class, AbstractSubCode.class))
                .when(reflectionsMock).getTypesAnnotatedWith(AutonomousCodeModel.class);
        fillAutoModels.invoke(null, reflectionsMock);
    }

    @Test
    public void testCode_addsComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        assert autoModels.get("Sub").containsValue(SubCode.class.getName());
    }

    @Test
    public void testCode_checkHasLabel() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        assert !autoModels.get("UAV").containsValue(UnlabeledUAVCode.class.getName());
    }

    @Test
    public void testCode_checkIsCode() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        assert !autoModels.get("Horse").containsValue(IncorrectlyLabeledCode.class.getName());
    }

    @Test
    public void testCode_checkCanCreate() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        assert !autoModels.get("Rover").containsValue(PrivateRoverCode.class.getName());
    }

    @Test
    public void testCode_checkKnownPlatform() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        assert !autoModels.containsKey("Mole");
    }

    @Test
    public void testCode_checkIsAbstract() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        assert !autoModels.get("Sub").containsValue(AbstractSubCode.class.getName());
    }

    @Test
    public void testCode_importsParams() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        Assert.assertArrayEquals(new String[]{ "param1", "param2" }, autoModelParameters.get("Sub").get("Test Sub"));
    }

//  PHYSICS MODELS -----------------------------------------------------------------------------------------------------
    private void setupFillPhysicsModels() throws InvocationTargetException, IllegalAccessException {
        platforms.put("Rover", "RoverPlatform");
        platforms.put("Sub", "SubPlatform");
        platforms.put("UAV", "UAVPlatform");
        platforms.put("Horse", "Not");
        doReturn(Sets.newHashSet(AbstractUAVPhysics.class, ProtectedSubPhysics.class, UAVPhysics.class, MissingMolePhysics.class, UnlabeledRoverPhysics.class))
                .when(reflectionsMock).getSubTypesOf(PlatformPhysicsModel.class);
        doReturn(Sets.newHashSet(ProtectedSubPhysics.class, UAVPhysics.class, AbstractUAVPhysics.class, IncorrectlyLabeledPhysics.class, MissingMolePhysics.class))
                .when(reflectionsMock).getTypesAnnotatedWith(PhysicsModel.class);
        fillPhysicsModels.invoke(null, reflectionsMock);
    }

    @Test
    public void testPhysics_addsComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        assert physicsModels.get("UAV").containsValue(UAVPhysics.class.getName());
    }

    @Test
    public void testPhysics_checkHasLabel() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        assert !physicsModels.get("Rover").containsValue(UnlabeledRoverPhysics.class.getName());
    }

    @Test
    public void testPhysics_checkIsPhysics() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        assert !physicsModels.get("Horse").containsValue(IncorrectlyLabeledPhysics.class.getName());
    }

    @Test
    public void testPhysics_checkCanCreate() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        assert !physicsModels.get("Sub").containsValue(ProtectedSubPhysics.class.getName());
    }

    @Test
    public void testPhysics_checkIsKnownPlatform() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        assert !physicsModels.containsKey("Mole");
    }

    @Test
    public void testPhysics_checkIsAbstract() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        assert !physicsModels.get("UAV").containsValue(AbstractUAVPhysics.class.getName());
    }

    @Test
    public void testPhysics_importsParams() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        Assert.assertArrayEquals(new String[] {"paramA", "paramB"}, physicsModelParameters.get("UAV").get("Test UAV"));
    }

}
