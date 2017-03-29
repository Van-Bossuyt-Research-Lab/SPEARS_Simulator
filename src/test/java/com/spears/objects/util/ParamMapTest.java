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

package com.spears.objects.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ParamMapTest {

    private static final double TOLERANCE = 0.00001;

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
