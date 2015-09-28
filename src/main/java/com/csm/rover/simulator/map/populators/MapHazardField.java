package com.csm.rover.simulator.map.populators;

import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.map.modifiers.PlasmaGeneratorMod;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.GridList;

import java.awt.Dimension;

public class MapHazardField extends MapPopularsField {

    @Override
    public void generate(boolean mono, Dimension mapSize, double density) {
        if (mono){
            generateHazards(mapSize, density);
        }
        else {
            generateValuedHazards(mapSize, density);
        }
    }

    private void generateHazards(Dimension mapSize, double density){
        values = new GridList<Integer>();
        mono = true;
        Hazard[] hazards = new Hazard[(int)(mapSize.getWidth()*mapSize.getHeight()*density)];
        for (int x = 0; x < hazards.length; x++){
            hazards[x] = new Hazard(new DecimalPoint(mapSize.getWidth()*rnd.nextDouble(), mapSize.getHeight()*rnd.nextDouble()), 5*rnd.nextDouble()+1, 10);
        }
        for (int x = 0; x < mapSize.getWidth(); x++){
            for (int y = 0; y < mapSize.getHeight(); y++){
                for (Hazard hazard : hazards) {
                    if (hazard.isPointWithin(new DecimalPoint(x, y))) {
                        values.put(hazard.getValue(), x, y);
                        break;
                    }
                }
            }
        }
    }

    private void generateValuedHazards(Dimension mapSize, double density){
        mono = false;
        TerrainMap hazMap = new TerrainMap();
        hazMap.addMapModifier(new PlasmaGeneratorMod(2.6));
        hazMap.generateLandscape((int)mapSize.getWidth(), 1);
        values = new GridList<Integer>();
        double max = hazMap.getTrueMax()+0.001;
        for (int x = 0; x < hazMap.getValues().getWidth(); x++){
            for (int y = 0; y < hazMap.getValues().getHeight(); y++){
                values.put((int)(hazMap.getValueAtLocation(x, y)/max*5), x, y);
            }
        }
        Hazard[] hazards = new Hazard[(int)(mapSize.getWidth()*mapSize.getHeight()*density)];
        for (int x = 0; x < hazards.length; x++){
            hazards[x] = new Hazard(new DecimalPoint(mapSize.getWidth()*rnd.nextDouble(), mapSize.getHeight()*rnd.nextDouble()), 5*rnd.nextDouble()+1, rnd.nextInt(5)+1);
        }
        for (int x = 0; x < mapSize.getWidth(); x++){
            for (int y = 0; y < mapSize.getHeight(); y++){
                int sum = 0;
                int count = 0;
                for (Hazard hazard : hazards) {
                    if (hazard.isPointWithin(new DecimalPoint(x, y))) {
                        sum += hazard.getValue();
                        count++;
                    }
                }
                if (count > 0){
                    values.put(((int)Math.round(sum/(double)count) + values.get(x, y)), x, y);
                }
            }
        }
    }
}

class Hazard {

    private DecimalPoint position;
    private double radius;
    private int scale;

    public Hazard(DecimalPoint pos, double r, int scale){
        position = pos;
        radius = r;
        this.scale = scale;
    }

    public boolean isPointWithin(DecimalPoint pnt){
        return Math.sqrt(Math.pow(position.getX()-pnt.getX(), 2) + Math.pow(position.getY()-pnt.getY(), 2)) <= radius;
    }

    public int getValue(){
        return scale;
    }

}
