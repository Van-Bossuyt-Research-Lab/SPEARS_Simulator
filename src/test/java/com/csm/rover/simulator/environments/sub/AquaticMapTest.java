package com.csm.rover.simulator.environments.sub;

import com.csm.rover.simulator.environments.EnvironmentMap;
import com.csm.rover.simulator.objects.util.ArrayGrid3D;
import com.csm.rover.simulator.objects.util.DecimalPoint3D;
import com.csm.rover.simulator.objects.util.FloatArrayArrayArrayGrid;
import com.csm.rover.simulator.test.objects.maps.UnlabeledSkyMap;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AquaticMapTest {

    private static final double TOLERANCE = 0.000001;

    @Test(expected = IllegalArgumentException.class)
    public void testCheckSizeOnMake(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.loadFromArray(new Float[][][] {
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.5f, 2f, 1.5f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 0.7f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {0.75f, 0.5f, 0.75f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
        });
        EnvironmentMap map = new AquaticMap(3, 1, grid);
        assertNotEquals(getAMap(), map);
    }

    @Test
    public void testGetMax(){
        assertEquals(2f, getAMap().getMaxValue(), TOLERANCE);
    }

    @Test
    public void testGetMin(){
        assertEquals(0.5f, getAMap().getMinValue(), TOLERANCE);
    }

    @Test
    public void testGetDensity_Easy(){
        assertEquals(1.3, getAMap().getDensityAt(new DecimalPoint3D()), TOLERANCE);
    }

    @Test
    public void testGetDensity(){
        assertEquals(1.2875, getAMap().getDensityAt(new DecimalPoint3D(-0.75, -0.75, 0.75)), TOLERANCE);
    }

    @Test
    public void testGetSize(){
        assertEquals(2, getAMap().getSize());
    }

    @Test
    public void testGetDetail(){
        assertEquals(2, getAMap().getDetail(), TOLERANCE);
    }

    @Test
    public void testIsEqual(){
        assertEquals(getAMap(), getAMap());
    }

    @Test
    public void testNotEqual_Size(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.loadFromArray(new Float[][][] {
                new Float[][] {
                        new Float[] {1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f},
                        new Float[] {1f, 1f, 1.3f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1.1f},
                        new Float[] {1f, 1f, 1.3f},
                        new Float[] {1f, 1f, 1.5f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f},
                        new Float[] {1f, 1f, 1.3f}}
        });
        EnvironmentMap map = new AquaticMap(2, 1, grid);
        assertNotEquals(getAMap(), map);
    }

    @Test
    public void testNotEqual_Value(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.loadFromArray(new Float[][][] {
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.5f, 2f, 1.5f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 0.7f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {0.75f, 0.5f, 0.75f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
        });
        EnvironmentMap map = new AquaticMap(2, 2, grid);
        assertNotEquals(getAMap(), map);
    }

    @Test
    public void testNotEqual_Type(){
        assertNotEquals(getAMap(), new UnlabeledSkyMap());
    }

    @Test
    public void testGetRaw(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.loadFromArray(new Float[][][] {
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.5f, 2f, 1.5f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {0.75f, 0.5f, 0.75f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
        });
        Assert.assertEquals(grid, getAMap().rawValues());
    }

    private AquaticMap getAMap(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.loadFromArray(new Float[][][] {
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.5f, 2f, 1.5f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1.3f, 1.5f, 1.3f},
                        new Float[] {1f, 1f, 1.1f, 1.3f, 1.1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
                new Float[][] {
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {0.75f, 0.5f, 0.75f, 1f, 1f},
                        new Float[] {1f, 0.75f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f},
                        new Float[] {1f, 1f, 1f, 1f, 1f}},
        });
        return new AquaticMap(2, 2, grid);
    }

}
