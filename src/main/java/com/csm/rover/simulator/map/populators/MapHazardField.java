package com.csm.rover.simulator.map.populators;

import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.DecimalPoint;

import java.awt.*;

public class MapHazardField {

    //Generate random hazards
    public void genorateHazards(double density){
        this.hazards = new int[this.values.length/detail][this.values[0].length/detail];
        monoHazards = true;
        Hazard[] hazards = new Hazard[(int)(values.length*values[0].length/(detail*detail)*density)];
        for (int x = 0; x < hazards.length; x++){
            hazards[x] = new Hazard(new DecimalPoint(values.length/detail*rnd.nextDouble()-values.length/detail/2, values.length/detail*rnd.nextDouble()-values.length/detail/2), 5*rnd.nextDouble()+1, 10);
        }
        for (int i = 0; i < values.length/detail; i++){
            for (int j = 0; j < values[0].length/detail; j++){
                int x = i - values.length/detail/2;
                int y = values.length/detail/2 - j;
                for (Hazard hazard : hazards) {
                    if (hazard.isPointWithin(new DecimalPoint(x, y))) {
                        this.hazards[i][j] = hazard.getValue();
                        break;
                    }
                }
            }
        }
    }

    public void genorateValuedHazards(double density){
        monoHazards = false;
        TerrainMap hazMap = new TerrainMap();
        hazMap.generateLandscape(layerSize, detail, 2.6);
        this.hazards = new int[this.values.length/detail][this.values[0].length/detail];
        double max = hazMap.getMax()+0.001;
        for (int i = 0; i < hazards.length; i++){
            for (int j = 0; j < hazards[0].length; j++){
                hazards[i][j] = (int)(hazMap.values[i][j]/max*5);
            }
        }
        Hazard[] hazards = new Hazard[(int)(values.length*values[0].length/(detail*detail)*density*2.5)];
        for (int x = 0; x < hazards.length; x++){
            hazards[x] = new Hazard(new DecimalPoint(values.length/detail*rnd.nextDouble()-values.length/detail/2, values.length/detail*rnd.nextDouble()-values.length/detail/2), 5*rnd.nextDouble()+1, rnd.nextInt(5)+1);
        }
        for (int i = 0; i < values.length/detail; i++){
            for (int j = 0; j < values[0].length/detail; j++){
                int x = i - values.length/detail/2;
                int y = values.length/detail/2 - j;
                for (Hazard hazard : hazards) {
                    if (hazard.isPointWithin(new DecimalPoint(x, y))) {
                        this.hazards[i][j] += hazard.getValue();
                        break;
                    }
                }
            }
        }
    }

    public void setHazards(Point[] hzds){
        hazards = new int[this.values.length/detail][this.values[0].length/detail];
        monoHazards = true;
        for (Point p : hzds){
            hazards[p.x][p.y] = 10;
        }
        this.repaint();
    }

    public void setHazards(int[][] values){
        hazards = values;
        monoHazards = false;
    }

    //get the hazard list
    public int[][] getHazards(){
        return hazards;
    }

    //is the given point in a hazard
    public boolean isPointInHazard(DecimalPoint loc){
        int x = (int) getMapSquare(loc).getX() / detail;
        int y = (int) getMapSquare(loc).getY() / detail;
        try {
            return hazards[x][y] > 0;
        }
        catch (NullPointerException e){
            return false;
        }
        catch (ArrayIndexOutOfBoundsException e){
            return true;
        }
    }

    public int getHazardValue(DecimalPoint loc){
        int x = (int) getMapSquare(loc).getX() / detail;
        int y = (int) getMapSquare(loc).getY() / detail;
        try {
            return hazards[x][y];
        }
        catch (ArrayIndexOutOfBoundsException e){
            return 10;
        }
    }

}
