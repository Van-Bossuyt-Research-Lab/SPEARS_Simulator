package com.csm.rover.simulator.map.populators;

import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.objects.GridList;

import java.awt.*;

public class MapTargetField {

    //force a target distribution
    public void setTargets(Point[] targs){
        targets = new GridList<Integer>();
        monoTargets = true;
        for (Point p : targs){
            targets.put(10, p.x, p.y);
        }
        this.repaint();
    }

    public void setTargets(Point[] targs, int[] values){
        monoTargets = false;
        targets = new GridList<Integer>();
        for (int i = 0; i < targs.length; i++){
            targets.put(values[i], targs[i].x, targs[i].y);
        }
        this.repaint();
    }

    //get the target distribution
    public GridList<Integer> getTargets(){
        return targets;
    }

    //Generate a target distribution
    public void genorateTargets(double density){
        targets = new GridList<Integer>();
        monoTargets = true;
        int size = (int)(values.length*values[0].length/(detail*detail)*density);
        int x = 0;
        while (x < size){
            targets.put(10, rnd.nextInt(values.length/detail), rnd.nextInt(values.length/detail));
            x++;
        }
    }

    public void genorateValuedTargets(double density){
        targets = new GridList<Integer>();
        monoTargets = false;
        int size = (int)(values.length*values[0].length/(detail*detail)*density);
        int x = 0;
        while (x < size){
            int value;
            double rand = rnd.nextDouble();
            if (rand < 0.3){
                value = 1;
            }
            else if (rand < 0.45){
                value = 2;
            }
            else if (rand < 0.6){
                value = 3;
            }
            else if (rand < 0.7){
                value = 4;
            }
            else if (rand < 0.8){
                value = 5;
            }
            else if (rand < 0.86){
                value = 6;
            }
            else if (rand < 0.91){
                value = 7;
            }
            else if (rand < 0.95){
                value = 8;
            }
            else if (rand < 0.98){
                value = 9;
            }
            else {
                value = 10;
            }
            targets.put(value, rnd.nextInt(values.length/detail), rnd.nextInt(values.length/detail));
            x++;
        }
    }

    //is the given point a target
    public boolean isPointOnTarget(DecimalPoint loc){
        int x = (int) getMapSquare(loc).getX() / detail;
        int y = (int) getMapSquare(loc).getY() / detail;
        try {
            return targets.get(x, y) > 0;
        }
        catch (NullPointerException e){
            return false;
        }
        catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
    }

    public int getTargetValue(DecimalPoint loc){
        int x = (int) getMapSquare(loc).getX() / detail;
        int y = (int) getMapSquare(loc).getY() / detail;
        try {
            return targets.get(x, y);
        }
        catch (NullPointerException e){
            return 0;
        }
        catch (ArrayIndexOutOfBoundsException e){
            return 0;
        }
    }

}
