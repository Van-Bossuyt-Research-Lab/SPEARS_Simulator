/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.*;

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
            fillPhysicsModels = PlatformRegistry.class.getDeclaredMethod("fillPhysicsModels", Reflections.class);
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

    private void setupFill(){
        Inject.field("reflect").of(new PlatformRegistry()).with(reflectionsMock);
        doReturn(Sets.newHashSet(RoverPlatform.class)).when(reflectionsMock).getSubTypesOf(Platform.class);
        doReturn(Sets.newHashSet(RoverPlatform.class)).when(reflectionsMock).getTypesAnnotatedWith(com.csm.rover.simulator.platforms.annotations.Platform.class);
        doReturn(Sets.newHashSet(RoverState.class)).when(reflectionsMock).getSubTypesOf(PlatformState.class);
        doReturn(Sets.newHashSet(RoverState.class)).when(reflectionsMock).getTypesAnnotatedWith(State.class);
        doReturn(Sets.newHashSet(RoverCode.class)).when(reflectionsMock).getSubTypesOf(PlatformAutonomousCodeModel.class);
        doReturn(Sets.newHashSet(RoverCode.class)).when(reflectionsMock).getTypesAnnotatedWith(AutonomousCodeModel.class);
        doReturn(Sets.newHashSet(RoverPhysics.class)).when(reflectionsMock).getSubTypesOf(PlatformPhysicsModel.class);
        doReturn(Sets.newHashSet(RoverPhysics.class)).when(reflectionsMock).getTypesAnnotatedWith(PhysicsModel.class);
        PlatformRegistry.fillRegistry();
    }

    @Test
    public void testFill(){
        setupFill();

        Assert.assertEquals(RoverPlatform.class, PlatformRegistry.getPlatform("Rover"));
        Assert.assertEquals(RoverState.class, PlatformRegistry.getPlatformState("Rover"));
        Assert.assertEquals(RoverCode.class, PlatformRegistry.getAutonomousCodeModel("Rover", "Test Rover"));
        Assert.assertEquals(Arrays.asList("param1", "param2"), PlatformRegistry.getParametersForAutonomousCodeModel("Rover", "Test Rover"));
        Assert.assertEquals(RoverPhysics.class, PlatformRegistry.getPhysicsModel("Rover", "Test Rover"));
        Assert.assertEquals(Arrays.asList("paramA", "paramB"), PlatformRegistry.getParametersForPhysicsModel("Rover", "Test Rover"));
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
        doReturn(Sets.newHashSet(MissingMoleCode.class, RoverCode.class, UnlabeledUAVCode.class, PrivateSubCode.class, AbstractRoverCode.class))
                .when(reflectionsMock).getSubTypesOf(PlatformAutonomousCodeModel.class);
        doReturn(Sets.newHashSet(MissingMoleCode.class, PrivateSubCode.class, IncorrectlyLabeledCode.class, RoverCode.class, AbstractRoverCode.class))
                .when(reflectionsMock).getTypesAnnotatedWith(AutonomousCodeModel.class);
        fillAutoModels.invoke(null, reflectionsMock);
    }

    @Test
    public void testCode_addsComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        assert autoModels.get("Rover").containsValue(RoverCode.class.getName());
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
        assert !autoModels.get("Rover").containsValue(PrivateSubCode.class.getName());
    }

    @Test
    public void testCode_checkKnownPlatform() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        assert !autoModels.containsKey("Mole");
    }

    @Test
    public void testCode_checkIsAbstract() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        assert !autoModels.get("Sub").containsValue(AbstractRoverCode.class.getName());
    }

    @Test
    public void testCode_importsParams() throws InvocationTargetException, IllegalAccessException {
        setupFillAutoModels();
        Assert.assertArrayEquals(new String[]{ "param1", "param2" }, autoModelParameters.get("Rover").get("Test Rover"));
    }

//  PHYSICS MODELS -----------------------------------------------------------------------------------------------------
    private void setupFillPhysicsModels() throws InvocationTargetException, IllegalAccessException {
        platforms.put("Rover", "RoverPlatform");
        platforms.put("Sub", "SubPlatform");
        platforms.put("UAV", "UAVPlatform");
        platforms.put("Horse", "Not");
        doReturn(Sets.newHashSet(AbstractRoverPhysics.class, ProtectedSubPhysics.class, RoverPhysics.class, MissingMolePhysics.class, UnlabeledUAVPhysics.class))
                .when(reflectionsMock).getSubTypesOf(PlatformPhysicsModel.class);
        doReturn(Sets.newHashSet(ProtectedSubPhysics.class, RoverPhysics.class, AbstractRoverPhysics.class, IncorrectlyLabeledPhysics.class, MissingMolePhysics.class))
                .when(reflectionsMock).getTypesAnnotatedWith(PhysicsModel.class);
        fillPhysicsModels.invoke(null, reflectionsMock);
    }

    @Test
    public void testPhysics_addsComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        assert physicsModels.get("Rover").containsValue(RoverPhysics.class.getName());
    }

    @Test
    public void testPhysics_checkHasLabel() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        assert !physicsModels.get("Rover").containsValue(UnlabeledUAVPhysics.class.getName());
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
        assert !physicsModels.get("UAV").containsValue(AbstractRoverPhysics.class.getName());
    }

    @Test
    public void testPhysics_importsParams() throws InvocationTargetException, IllegalAccessException {
        setupFillPhysicsModels();
        Assert.assertArrayEquals(new String[] {"paramA", "paramB"}, physicsModelParameters.get("Rover").get("Test Rover"));
    }

//  GETTERS ------------------------------------------------------------------------------------------------------------

    @Test
    public void testListPlatforms(){
        setupFill();
        Assert.assertEquals(Arrays.asList("Rover"), PlatformRegistry.getTypes());
    }

    @Test
    public void testListCode(){
        setupFill();
        Assert.assertEquals(Arrays.asList("Test Rover"), PlatformRegistry.listAutonomousCodeModels("Rover"));
    }

    @Test
    public void testListPhysics(){
        setupFill();
        Assert.assertEquals(Arrays.asList("Test Rover"), PlatformRegistry.listPhysicsModels("Rover"));
    }

    @Test
    public void testUnknownType(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getPlatform("Sub"));
    }

    @Test
    public void testUnknownState(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getPlatformState("Sub"));
    }

    @Test
    public void testUnknownCodeType(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getAutonomousCodeModel("Sub", "Test Sub"));
    }

    @Test
    public void testUnknownCodeName(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getAutonomousCodeModel("Rover", "GORADRO"));
    }

    @Test
    public void testUnknownPhysicsType(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getPhysicsModel("Sub", "Test Sub"));
    }

    @Test
    public void testUnknownPhysicsName(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getPhysicsModel("Rover", "RAIR"));
    }

    @Test
    public void testUnknownCodeParamType(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getParametersForAutonomousCodeModel("Sub", "Test Sub"));
    }

    @Test
    public void testUnknownCodeParamsName(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getParametersForAutonomousCodeModel("Rover", "GORADRO"));
    }

    @Test
    public void testUnknownPhysicsParamsType(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getParametersForPhysicsModel("Sub", "Test Sub"));
    }

    @Test
    public void testUnknownPhysicsParamName(){
        setupFill();
        Assert.assertEquals(null, PlatformRegistry.getParametersForPhysicsModel("Rover", "RAIR"));
    }

}
