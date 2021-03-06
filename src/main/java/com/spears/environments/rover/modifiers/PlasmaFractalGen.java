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

package com.spears.environments.rover.modifiers;

import com.spears.environments.EnvironmentModifier;
import com.spears.environments.annotations.Modifier;
import com.spears.environments.rover.TerrainMap;
import com.spears.objects.util.ArrayGrid;
import com.spears.objects.util.FloatArrayArrayGrid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.Map;
import java.util.Random;

@Modifier(name="Plasma Fractal", type="Rover", parameters={"size", "detail", "rough"}, generator=true)
public class PlasmaFractalGen extends EnvironmentModifier<TerrainMap> {

    public PlasmaFractalGen() {
        super("Rover", true);
    }

    @Override
    protected TerrainMap doModify(TerrainMap map, Map<String, Double> params) {
        int size = params.get("size").intValue();
        if (size%2 == 1){
            size++;
            params.put("size", (double)size);
            LogManager.getLogger(PlasmaFractalGen.class).log(Level.WARN, "Terrain map cannot have an odd size - increasing size to " + size);
        }
        int detail = params.get("detail").intValue();
        int true_size = size*detail + 1;
        ArrayGrid<Float> values = new FloatArrayArrayGrid();

        Random rnd = new Random();
        double rough = params.get("rough");

        float min = Float.MIN_VALUE;

        double seed = rnd.nextInt(30) * rnd.nextDouble();
        values.put(0, 0, (float) Math.abs(seed + rnd.nextDouble()/5.));
        values.put(0, 1, (float) Math.abs(seed + rnd.nextDouble()/5.));
        values.put(1, 0, (float) Math.abs(seed + rnd.nextDouble()/5.));
        values.put(1, 1, (float) Math.abs(seed + rnd.nextDouble()/5.));
        while (values.getWidth() < true_size){
            expand(values);
            for (int x = 0; x < values.getWidth(); x++){
                for (int y = 0; y < values.getHeight(); y++){
                    float value;
                    if ((x+1) % 2 == 0){
                        if ((y+1) % 2 == 0){
                            value = (float)((values.get(x - 1, y - 1)+values.get(x - 1, y + 1)+values.get(x + 1, y - 1)+values.get(x + 1, y + 1))/4. + rnd.nextDouble()*rough*(rnd.nextBoolean() ? 1 : -1));
                        }
                        else {
                            value = (float)((values.get(x - 1, y)+values.get(x + 1, y))/2. + rnd.nextDouble()*rough*(rnd.nextBoolean() ? 1 : -1));
                        }
                    }
                    else {
                        if ((y+1) % 2 == 0){
                            value = (float)((values.get(x, y - 1)+values.get(x, y + 1))/2. + rnd.nextDouble()*rough*(rnd.nextBoolean() ? 1 : -1));
                        }
                        else {
                            continue;
                        }
                    }
                    if (value < min){
                        min = value;
                    }
                    values.put(x, y, value);
                }
            }
            rough *= 0.7;
            if (rough < 0){
                rough = 0;
            }
        }

        ArrayGrid<Float> heightmap = new FloatArrayArrayGrid();
        int xstart = (values.getWidth() - true_size)/2;
        int ystart = (values.getHeight() - true_size)/2;
        for (int x = 0; x < true_size; x++) {
            for (int y = 0; y < true_size; y++) {
                heightmap.put(x, y, values.get(x + xstart, y + ystart)-min);
            }
        }
        return new TerrainMap(size, detail, heightmap);
    }

    //part of the plasma fractal generation, pushes the array from |x|x|x| to |x|_|x|_|x|
    private void expand(ArrayGrid<Float> vals){
        int width = vals.getWidth();
        int height = vals.getHeight();
        for (int x = width-1; x >=0; x--){
            for (int y = height-1; y >= 0; y--){
                vals.put(x*2, y*2, vals.get(x, y));
            }
        }
    }

}
