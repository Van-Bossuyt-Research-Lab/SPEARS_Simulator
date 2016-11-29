package com.csm.rover.simulator.objects.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ParamMapTest {

    private static final double TOLERANCE = .0001;

    @Test
    public void testEmpty(){
        assert ParamMap.emptyParamMap().isEmpty();
    }

    @Test
    public void testAdditions(){
        Map<String, Double> params = ParamMap.newParamMap()
                .addParameter("first", 1)
                .addParameter("second", -2.5)
                .build();
        assert params.containsKey("first");
        assert params.containsKey("second");
        Assert.assertEquals(1, params.get("first"), TOLERANCE);
        Assert.assertEquals(-2.5, params.get("second"), TOLERANCE);
    }

    @Test
    public void testUnmodifiable(){
        Map<String, Double> params = ParamMap.newParamMap()
                .addParameter("first", 1)
                .addParameter("second", -2.5)
                .build();
        try {
            params.put("new", 0.);
        } catch (Exception e) {}
        assert !params.containsKey("new");
        try {
            params.remove("first");
        } catch (Exception e) {}
        assert params.containsKey("first");
        try {
            params.put("second", 5.);
        } catch (Exception e) {}
        Assert.assertEquals(-2.5, params.get("second"), TOLERANCE);
    }

}
