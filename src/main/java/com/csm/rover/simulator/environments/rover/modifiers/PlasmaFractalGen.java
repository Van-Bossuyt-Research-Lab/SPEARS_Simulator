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
        int build_size = getBuildSize(true_size);
        ArrayGrid<Float> grid = new FloatArrayArrayGrid();

        Random rnd = new Random();
        double range = params.get("range");


        return new TerrainMap(size, detail, trim(grid, true_size));
    }

    private int getBuildSize(int map_size){
        int base = 3;
        while (base <= map_size){
            base += base-1;
        }
        return base;
    }

    private ArrayGrid<Float> trim(ArrayGrid<Float> orig, int size){
        if (orig.getWidth() < size || orig.getHeight() < size){
            throw new IllegalArgumentException("Grid is smaller than crop size");
        }
        int xstart = (orig.getWidth()-size)/2;
        int ystart = (orig.getHeight()-size)/2;
        ArrayGrid<Float> out = new FloatArrayArrayGrid();
        out.fillToSize(size, size);
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                out.put(i, j, orig.get(i+xstart, j+ystart));
            }
        }
        return out;
    }

}
