package com.csm.rover.simulator.environments.rover.modifiers;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import com.csm.rover.simulator.environments.annotations.Modifier;
import com.csm.rover.simulator.environments.rover.TerrainMap;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import com.csm.rover.simulator.objects.util.FloatArrayArrayGrid;

import java.util.Map;
import java.util.Random;

@Modifier(name="Plasma Fractal", type="Rover", parameters={"size", "detail", "range", "rough"}, generator=true)
public class PlasmaFractalGen extends EnvironmentModifier<TerrainMap> {

    public PlasmaFractalGen() {
        super("Rover", true);
    }

    @Override
    protected TerrainMap doModify(TerrainMap map, Map<String, Double> params) {
        int size = params.get("size").intValue();
        int detail = params.get("detail").intValue();
        int true_size = size*detail;
        ArrayGrid<Float> values = new FloatArrayArrayGrid();

        Random rnd = new Random();
        double rough = params.get("rough");
        double range = params.get("range");

        double seed = rnd.nextInt(30) * rnd.nextDouble();
        values.put(0, 0, (float) Math.abs(seed + rnd.nextDouble()/5.));
        values.put(0, 1, (float) Math.abs(seed + rnd.nextDouble()/5.));
        values.put(1, 0, (float) Math.abs(seed + rnd.nextDouble()/5.));
        values.put(1, 1, (float) Math.abs(seed + rnd.nextDouble()/5.));
        while (values.getWidth() < true_size){
            expand(values);
            for (int x = 0; x < values.getWidth(); x++){
                for (int y = 0; y < values.getHeight(); y++){
                    if ((x+1) % 2 == 0){
                        if ((y+1) % 2 == 0){
                            values.put(x, y, (float)((values.get(x - 1, y - 1)+values.get(x - 1, y + 1)+values.get(x + 1, y - 1)+values.get(x + 1, y + 1))/4. + rnd.nextDouble()*rough));
                        }
                        else {
                            values.put(x, y, (float)((values.get(x - 1, y)+values.get(x + 1, y))/2. + rnd.nextDouble()*rough));
                        }
                    }
                    else {
                        if ((y+1) % 2 == 0){
                            values.put(x, y, (float)((values.get(x, y - 1)+values.get(x, y + 1))/2. + rnd.nextDouble()*rough));
                        }
                    }
                }
            }
            rough *= 0.7;
            if (rough < 0){
                rough = 0;
            }
        }

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int x = 0; x < values.getWidth(); x++){
            for (int y = 0; y < values.getHeight(); y++){
                double val = values.get(x, y);
                if (val < min){
                    min = val;
                }
                else if (val > max){
                    max = val;
                }
            }
        }
        max -= min;

        ArrayGrid<Float> heightmap = new FloatArrayArrayGrid();
        int xstart = (values.getWidth() - true_size)/2;
        int ystart = (values.getHeight() - true_size)/2;
        for (int x = 0; x < true_size; x++) {
            for (int y = 0; y < true_size; y++) {
                heightmap.put(x, y, (float) ((values.get(x + xstart, y + ystart) - min) * (max / range)));
            }
        }

        System.out.println("Creating terrain map");
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
