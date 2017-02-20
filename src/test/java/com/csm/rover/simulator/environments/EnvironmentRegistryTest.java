package com.csm.rover.simulator.environments;

import com.csm.rover.simulator.environments.annotations.Environment;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.annotations.Populator;
import com.csm.rover.simulator.environments.rover.TerrainEnvironment;
import com.csm.rover.simulator.test.objects.environments.LandEnv;
import com.csm.rover.simulator.test.objects.environments.MislabeledEnv;
import com.csm.rover.simulator.test.objects.environments.ProtectedSkyEnv;
import com.csm.rover.simulator.test.objects.environments.UnlabeledSeaEnv;
import com.csm.rover.simulator.test.objects.maps.*;
import com.csm.rover.simulator.test.objects.modifiers.*;
import com.csm.rover.simulator.test.objects.populators.*;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.laughingpanda.beaninject.Inject;
import org.reflections.Reflections;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class EnvironmentRegistryTest {

    private static Method fillEnvironments, fillMaps, fillModifiers, fillPopulators;

    @BeforeClass
    public static void makeVisible(){
        try {
            fillEnvironments = EnvironmentRegistry.class.getDeclaredMethod("fillEnvironments", Reflections.class);
            fillEnvironments.setAccessible(true);
            fillMaps = EnvironmentRegistry.class.getDeclaredMethod("fillMaps", Reflections.class);
            fillMaps.setAccessible(true);
            fillModifiers = EnvironmentRegistry.class.getDeclaredMethod("fillModifiers", Reflections.class);
            fillModifiers.setAccessible(true);
            fillPopulators = EnvironmentRegistry.class.getDeclaredMethod("fillPopulators", Reflections.class);
            fillPopulators.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Reflections reflectionsMock;

    private static Map<String, String> environments;
    private static Map<String, String> maps;
    private static Map<String, Map<String, String>> modifiers;
    private static Map<String, Map<String, String[]>> modifierParameters;
    private static Map<String, Map<String, String>> populators;
    private static Map<String, Map<String, String[]>> populatorParameters;
    private static Map<String, Map<String, PopulatorDisplayFunction>> populatorDisplays;

    @Before
    public void clearRegistry(){
        environments = new TreeMap<>();
        maps = new TreeMap<>();
        modifiers = new TreeMap<>();
        populators = new TreeMap<>();
        modifierParameters = new TreeMap<>();
        populatorParameters = new TreeMap<>();
        populatorDisplays = new TreeMap<>();

        EnvironmentRegistry er = new EnvironmentRegistry();
        Inject.field("environments").of(er).with(environments);
        Inject.field("maps").of(er).with(maps);
        Inject.field("modifiers").of(er).with(modifiers);
        Inject.field("populators").of(er).with(populators);
        Inject.field("modifierParameters").of(er).with(modifierParameters);
        Inject.field("populatorParameters").of(er).with(populatorParameters);
        Inject.field("populatorDisplays").of(er).with(populatorDisplays);

        reflectionsMock = mock(Reflections.class);
    }

    private void setupFill(){
        Inject.field("reflect").of(new EnvironmentRegistry()).with(reflectionsMock);
        doReturn(Sets.newHashSet(LandEnv.class)).when(reflectionsMock).getSubTypesOf(PlatformEnvironment.class);
        doReturn(Sets.newHashSet(LandEnv.class)).when(reflectionsMock).getTypesAnnotatedWith(Environment.class);
        doReturn(Sets.newHashSet(LandMap.class)).when(reflectionsMock).getSubTypesOf(EnvironmentMap.class);
        doReturn(Sets.newHashSet(LandMap.class)).when(reflectionsMock).getTypesAnnotatedWith(com.csm.rover.simulator.environments.annotations.Map.class);
        doReturn(Sets.newHashSet(HillMod.class)).when(reflectionsMock).getSubTypesOf(EnvironmentModifier.class);
        doReturn(Sets.newHashSet(HillMod.class)).when(reflectionsMock).getTypesAnnotatedWith(Modifier.class);
        doReturn(Sets.newHashSet(RockPop.class)).when(reflectionsMock).getSubTypesOf(EnvironmentPopulator.class);
        doReturn(Sets.newHashSet(RockPop.class)).when(reflectionsMock).getTypesAnnotatedWith(Populator.class);
        EnvironmentRegistry.fillRegistry();
    }

    @Test
    public void testFill(){
        setupFill();

        Assert.assertEquals(LandEnv.class, EnvironmentRegistry.getEnvironment("Rover"));
        Assert.assertEquals(LandMap.class, EnvironmentRegistry.getMap("Rover"));
        Assert.assertEquals(HillMod.class, EnvironmentRegistry.getModifier("Rover", "Hill Mod"));
        Assert.assertEquals(Arrays.asList("param1", "param2"), EnvironmentRegistry.getParametersForModifier("Rover", "Hill Mod"));
        Assert.assertEquals(RockPop.class, EnvironmentRegistry.getPopulator("Rover", "Rock Pop"));
        Assert.assertEquals(Arrays.asList("paramA", "paramB"), EnvironmentRegistry.getParametersForPopulator("Rover", "Rock Pop"));
    }

//  ENVIRONMENTS -------------------------------------------------------------------------------------------------------
    private void setupFillEnvironments() throws InvocationTargetException, IllegalAccessException {
        doReturn(Sets.newHashSet(LandEnv.class, UnlabeledSeaEnv.class, ProtectedSkyEnv.class)).when(reflectionsMock).getSubTypesOf(PlatformEnvironment.class);
        doReturn(Sets.newHashSet(MislabeledEnv.class, ProtectedSkyEnv.class, LandEnv.class)).when(reflectionsMock).getTypesAnnotatedWith(Environment.class);
        fillEnvironments.invoke(null, reflectionsMock);
    }

    @Test
    public void testEnvironments_addComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillEnvironments();
        assert environments.containsValue(LandEnv.class.getName());
    }

    @Test
    public void testEnvironments_checkHasLabel() throws InvocationTargetException, IllegalAccessException {
        setupFillEnvironments();
        assert !environments.containsValue(UnlabeledSeaEnv.class.getName());
    }

    @Test
    public void testEnvironments_checkIsEnv() throws InvocationTargetException, IllegalAccessException {
        setupFillEnvironments();
        assert !environments.containsValue(MislabeledEnv.class.getName());
    }

    @Test
    public void testEnvironments_checkCanCreate() throws InvocationTargetException, IllegalAccessException {
        setupFillEnvironments();
        assert !environments.containsValue(ProtectedSkyEnv.class.getName());
    }

//  MAPS ---------------------------------------------------------------------------------------------------------------
    private void setupFillMaps() throws InvocationTargetException, IllegalAccessException {
        environments.put("Rover", "Terrain");
        environments.put("Sub", "Sea");
        environments.put("UAV", "Sky");
        environments.put("Horse", "Pasture");
        doReturn(Sets.newHashSet(UnlabeledSkyMap.class, UnknownCaveMap.class, LandMap.class, ProtectedSeaMap.class))
                .when(reflectionsMock).getSubTypesOf(EnvironmentMap.class);
        doReturn(Sets.newHashSet(LandMap.class, MislabeledMap.class, ProtectedSeaMap.class, UnknownCaveMap.class))
                .when(reflectionsMock).getTypesAnnotatedWith(com.csm.rover.simulator.environments.annotations.Map.class);
        fillMaps.invoke(null, reflectionsMock);
    }

    @Test
    public void testMaps_addsComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillMaps();
        assert maps.containsValue(LandMap.class.getName());
    }

    @Test
    public void testMaps_checkHasLabel() throws InvocationTargetException, IllegalAccessException {
        setupFillMaps();
        assert !maps.containsValue(UnlabeledSkyMap.class.getName());
    }

    @Test
    public void testMaps_checkIsMap() throws InvocationTargetException, IllegalAccessException {
        setupFillMaps();
        assert !maps.containsValue(MislabeledMap.class.getName());
    }

    @Test
    public void testMaps_checkCanCreate() throws InvocationTargetException, IllegalAccessException {
        setupFillMaps();
        assert !maps.containsValue(ProtectedSeaMap.class.getName());
    }

    @Test
    public void testMaps_checkKnown() throws InvocationTargetException, IllegalAccessException {
        setupFillMaps();
        assert !maps.containsValue(UnknownCaveMap.class.getName());
    }

//  MODIFIERS ----------------------------------------------------------------------------------------------------------
    private void setupFillModifiers() throws InvocationTargetException, IllegalAccessException {
        environments.put("Rover", "Terrain");
        environments.put("Sub", "Sea");
        environments.put("UAV", "Sky");
        environments.put("Horse", "Pasture");
        doReturn(Sets.newHashSet(ProtectedCurrentMod.class, UnlabeledWindMod.class, HillMod.class, UnknownTunnelMod.class))
                .when(reflectionsMock).getSubTypesOf(EnvironmentModifier.class);
        doReturn(Sets.newHashSet(HillMod.class, UnknownTunnelMod.class, MislabeledMod.class, ProtectedCurrentMod.class))
                .when(reflectionsMock).getTypesAnnotatedWith(Modifier.class);
        fillModifiers.invoke(null, reflectionsMock);
    }

    @Test
    public void testMods_addsComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillModifiers();
        assert modifiers.get("Rover").containsValue(HillMod.class.getName());
    }

    @Test
    public void testMods_checkHasLabel() throws InvocationTargetException, IllegalAccessException {
        setupFillModifiers();
        assert !modifiers.get("UAV").containsValue(UnlabeledWindMod.class.getName());
    }

    @Test
    public void testMods_checkIsMod() throws InvocationTargetException, IllegalAccessException {
        setupFillModifiers();
        assert !modifiers.get("Horse").containsValue(MislabeledMod.class.getName());
    }

    @Test
    public void testMods_checkCanCreate() throws InvocationTargetException, IllegalAccessException {
        setupFillModifiers();
        assert !modifiers.get("Sub").containsValue(ProtectedCurrentMod.class.getName());
    }

    @Test
    public void testMods_checkIsKnown() throws InvocationTargetException, IllegalAccessException {
        setupFillModifiers();
        assert !modifiers.containsKey("Mole");
    }

//  POPULATORS ---------------------------------------------------------------------------------------------------------
    private void setupFillPopulators() throws InvocationTargetException, IllegalAccessException {
        environments.put("Rover", "Terrain");
        environments.put("Sub", "Sea");
        environments.put("UAV", "Sky");
        environments.put("Horse", "Pasture");
        doReturn(Sets.newHashSet(RockPop.class, UnlabeledKelpPop.class, PrivateCloudPop.class, UnknownPop.class))
                .when(reflectionsMock).getSubTypesOf(EnvironmentPopulator.class);
        doReturn(Sets.newHashSet(PrivateCloudPop.class, UnknownPop.class, MislabeledPop.class, RockPop.class))
                .when(reflectionsMock).getTypesAnnotatedWith(Populator.class);
        fillPopulators.invoke(null, reflectionsMock);
    }

    @Test
    public void testPops_addsComplete() throws InvocationTargetException, IllegalAccessException {
        setupFillPopulators();
        assert populators.get("Rover").containsValue(RockPop.class.getName());
    }

    @Test
    public void testPops_checksHasLabel() throws InvocationTargetException, IllegalAccessException {
        setupFillPopulators();
        assert !populators.get("Sub").containsValue(UnlabeledKelpPop.class.getName());
    }

    @Test
    public void testPops_checksIsPop() throws InvocationTargetException, IllegalAccessException {
        setupFillPopulators();
        assert !populators.get("Horse").containsValue(MislabeledPop.class.getName());
    }

    @Test
    public void testPops_checksIsKnown() throws InvocationTargetException, IllegalAccessException {
        setupFillPopulators();
        assert !populators.containsKey("Mole");
    }

    @Test
    public void testPops_checksCanCreate() throws InvocationTargetException, IllegalAccessException {
        setupFillPopulators();
        assert !populators.get("UAV").containsValue(PrivateCloudPop.class.getName());
    }

    @Test
    public void testPops_testDisplayFunction() throws InvocationTargetException, IllegalAccessException {
        setupFillPopulators();
        assert populatorDisplays.get("Rover").containsKey("Rock Pop");
    }

//  GETTERS ------------------------------------------------------------------------------------------------------------

    @Test
    public void testListEnvironments(){
        setupFill();
        Assert.assertEquals(Arrays.asList("Rover"), EnvironmentRegistry.getTypes());
    }

    @Test
    public void testListModifiers(){
        setupFill();
        Assert.assertEquals(Arrays.asList("Hill Mod"), EnvironmentRegistry.listModifiers("Rover"));
    }

    @Test
    public void testListPopulators(){
        setupFill();
        Assert.assertEquals(Arrays.asList("Rock Pop"), EnvironmentRegistry.listPopulators("Rover"));
    }

    @Test
    public void testUnknownType(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getEnvironment("Sub"));
    }

    @Test
    public void testUnknownMapType(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getMap("Sub"));
    }

    @Test
    public void testUnknownModifierType(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getModifier("Sub", "Current Mod"));
    }

    @Test
    public void testUnknownModiferName(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getModifier("Rover", "Valley Mod"));
    }

    @Test
    public void testUnknownPopulatorType(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getPopulator("Sub", "Kelp Pop"));
    }

    @Test
    public void testUnknownPopulatorName(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getPopulator("Rover", "Bush Pop"));
    }

    @Test
    public void testUnknownModifierParamType(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getParametersForModifier("Sub", "Current Mod"));
    }

    @Test
    public void testUnknownModifiersParamsName(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getParametersForModifier("Rover", "Valley Mod"));
    }

    @Test
    public void testUnknownPopulatorParamsType(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getParametersForPopulator("Sub", "Kelp Pop"));
    }

    @Test
    public void testUnknownPolulatorParamName(){
        setupFill();
        Assert.assertEquals(null, EnvironmentRegistry.getParametersForPopulator("Rover", "Bush Pop"));
    }

    @Test
    public void testDisplayFunction(){
        setupFill();
        Assert.assertEquals(Color.GREEN, EnvironmentRegistry.getPopulatorDisplayFunction("Rover", "Rock Pop").displayFunction(4));
    }

    @Test
    public void testDisplayFunctionNull(){
        setupFill();
        assert null == EnvironmentRegistry.getPopulatorDisplayFunction("UAV", "Cloud");
    }

}
