package com.csm.rover.simulator.objects.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FloatArrayArrayArrayGridTest {
    private ArrayGrid3D<Float> grid;
    private static final double TOLERANCE = 0.00001;

    @Before
    public void setup(){
        grid = new FloatArrayArrayArrayGrid();
    }

    @Test
    public void testAddToBlank(){
        grid.put(5, 4, 3, 1.2f);

        assertEquals(6, grid.getWidth());
        assertEquals(5, grid.getHeight());
        assertEquals(4, grid.getDepth());
        assertEquals(1.2f, grid.get(5, 4, 3), TOLERANCE);
    }

    @Test
    public void testFill(){
        grid.fillToSize(3, 9, 12);

        assertEquals(3, grid.getWidth());
        assertEquals(9, grid.getHeight());
        assertEquals(12, grid.getDepth());
        assertEquals(0f, grid.get(2, 8, 6), TOLERANCE);
        assert !grid.isEmpty();
    }

    @Test
    public void testGrow(){
        grid.put(5, 4, 3, 1.2f);

        assertEquals(6, grid.getWidth());
        assertEquals(5, grid.getHeight());
        assertEquals(4, grid.getDepth());
        assertEquals(120, grid.size());

        grid.put(7, 5, 4, 2.2f);

        assertEquals(8, grid.getWidth());
        assertEquals(6, grid.getHeight());
        assertEquals(5, grid.getDepth());
    }

    @Test
    public void testEmpty(){
        assertEquals(0, grid.getWidth());
        assertEquals(0, grid.getHeight());
        assertEquals(0, grid.getDepth());
        assert grid.isEmpty();
    }

    @Test
    public void testAddXLayer(){
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.addXLayer(layer);

        assertEquals(1, grid.getWidth());
        assertEquals(2, grid.getHeight());
        assertEquals(3, grid.getDepth());
        assertEquals(1.9, grid.get(0, 1, 2), TOLERANCE);
    }

    @Test
    public void testAddYLayer(){
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.addYLayer(layer);

        assertEquals(2, grid.getWidth());
        assertEquals(1, grid.getHeight());
        assertEquals(3, grid.getDepth());
        assertEquals(1.9, grid.get(1, 0, 2), TOLERANCE);
    }

    @Test
    public void testAddZLayer(){
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.addZLayer(layer);

        assertEquals(2, grid.getWidth());
        assertEquals(3, grid.getHeight());
        assertEquals(1, grid.getDepth());
        assertEquals(1.9, grid.get(1, 2, 0), TOLERANCE);
    }

    @Test
    public void testInsertXLayer(){
        grid.put(5, 2, 1, 19f);
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.insertXLayerAt(3, layer);

        assertEquals(7, grid.getWidth());
        assertEquals(3, grid.getHeight());
        assertEquals(3, grid.getDepth());
        assertEquals(19, grid.get(6, 2, 1), TOLERANCE);
    }

    @Test
    public void testInsertYLayer(){
        grid.put(2, 10, 4, 3.14f);
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.insertYLayerAt(5, layer);

        assertEquals(3, grid.getWidth());
        assertEquals(12, grid.getHeight());
        assertEquals(5, grid.getDepth());
        assertEquals(3.14, grid.get(2, 11, 4), TOLERANCE);
    }

    @Test
    public void testInsertZLayer(){
        grid.put(3, 3, 5, -7.2f);
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.insertZLayerAt(1, layer);

        assertEquals(4, grid.getWidth());
        assertEquals(4, grid.getHeight());
        assertEquals(7, grid.getDepth());
        assertEquals(-7.2, grid.get(3, 3, 6), TOLERANCE);
    }

    @Test
    public void testAddXLayerPast(){
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.insertXLayerAt(3, layer);

        assertEquals(4, grid.getWidth());
        assertEquals(2, grid.getHeight());
        assertEquals(3, grid.getDepth());
        assertEquals(1.9, grid.get(3, 1, 2), TOLERANCE);
    }

    @Test
    public void testAddYLayerPast(){
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.insertYLayerAt(5, layer);

        assertEquals(2, grid.getWidth());
        assertEquals(6, grid.getHeight());
        assertEquals(3, grid.getDepth());
        assertEquals(1.9, grid.get(1, 5, 2), TOLERANCE);
    }

    @Test
    public void testAddZLayerPast(){
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.insertZLayerAt(1, layer);

        assertEquals(2, grid.getWidth());
        assertEquals(3, grid.getHeight());
        assertEquals(2, grid.getDepth());
        assertEquals(1.9, grid.get(1, 2, 1), TOLERANCE);
    }

    @Test
    public void testGetXLayer(){
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.insertXLayerAt(3, layer);

        assertEquals(layer, grid.getXLayer(3));
    }

    @Test
    public void testGetYLayer(){
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.insertYLayerAt(5, layer);

        assertEquals(layer, grid.getYLayer(5));
    }

    @Test
    public void testGetZLayer(){
        ArrayGrid<Float> layer = new FloatArrayArrayGrid(new Float[][] {
                new Float[] {5f, 0f, 2f},
                new Float[] {0f, -6f, 1.9f}
        });
        grid.insertZLayerAt(1, layer);

        assertEquals(layer, grid.getZLayer(1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_x(){
        grid.put(2, 4, 5, 3.2f);
        grid.get(3, 2, 4);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_y(){
        grid.put(2, 4, 3, 1.6f);
        grid.get(1, 5, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_z(){
        grid.put(2, 4, 3, 1.6f);
        grid.get(1, 3, 6);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBXLayer(){
        grid.put(2, 4, 4, 9.2f);
        grid.getZLayer(5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBYLayer(){
        grid.put(2, 4, 5, -100f);
        grid.getYLayer(7);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBZLayer(){
        grid.put(2, 4, 5, -100f);
        grid.getZLayer(9);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_xNeg(){
        grid.put(2, 4, 5, 3.2f);
        grid.get(-3, 2, 3);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_yNeg(){
        grid.put(2, 4, 5, 1.6f);
        grid.get(1, -5, 3);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBGet_zNeg(){
        grid.put(2, 4, 5, 1.6f);
        grid.get(1, 5, -3);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBXLayerNeg(){
        grid.put(2, 4, 5, 9.2f);
        grid.getXLayer(-5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBYLayerNeg(){
        grid.put(2, 4, 5, -100f);
        grid.getYLayer(-3);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOOBZLayerNeg(){
        grid.put(2, 4, 5, -100f);
        grid.getZLayer(-2);
    }

    @Test
    public void testEquals(){
        grid.put(3, 6, 9, 2f);
        grid.put(1, 2, 4, 0.21f);

        ArrayGrid3D<Float> other = new FloatArrayArrayArrayGrid();
        other.put(3, 6, 9, 2f);
        other.put(1, 2, 4, 0.21f);

        assert grid.equals(other);
    }

    @Test
    public void testNotEquals(){
        grid.put(3, 7, 6, 1f);

        ArrayGrid3D<Float> other = new FloatArrayArrayArrayGrid();
        other.put(4, 6, 2, 2f);

        assert !grid.equals(other);
    }


    @Test
    public void testReallyNotEquals(){
        assert !grid.equals(1.58f);
    }

    @Test
    public void testClone(){
        grid.put(2, 6, 3, 7f);
        grid.put(1, 5, 7, 1.2f);

        ArrayGrid3D<Float> clone = grid.clone();
        assert grid.equals(clone);

        clone.put(1, 5, 7, 2.1f);
        assert !grid.equals(clone);
    }

    @Test
    public void jsonTest(){
        ArrayGrid3D<Float> grid = new FloatArrayArrayArrayGrid();
        grid.put(0, 0, 0, 5f);
        grid.put(1, 1, 0, 1f);
        grid.put(1, 0, 1, 2f);
        grid.put(0, 1, 1, 3f);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(grid);
            ArrayGrid3D<Float> obj = mapper.readValue(json, ArrayGrid3D.class);

            assertEquals(grid, obj);
        }
        catch (IOException e) {
            fail();
        }
    }

}
