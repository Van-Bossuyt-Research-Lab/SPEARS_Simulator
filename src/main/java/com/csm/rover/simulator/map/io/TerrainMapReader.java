package com.csm.rover.simulator.map.io;

import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.ArrayGrid;
import com.csm.rover.simulator.objects.FloatArrayArrayGrid;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TerrainMapReader {
    private static String fileID = "__^__";

    public static TerrainMap loadMap(File file) throws Exception {
        try {
            Scanner data = new Scanner(file);

            String ver = data.next();
            if (!ver.equals(fileID)){
                data.close();
                throw new Exception("Invalid File Version");
            }

            int width = data.nextInt();
            int height = data.nextInt();
            int detail = data.nextInt();

            TerrainMap map = new TerrainMap();

            ArrayGrid<Float> values = new FloatArrayArrayGrid();
            for (int y = 0; y < height; y++){
                for (int x = 0; x < width; x++){
                    values.fillToSize(x, y, data.nextFloat());
                }
            }
            map.setValues(width, detail, values);

            boolean monoTarget = data.next().equals("m");
            int targs = data.nextInt();
            if (targs > 0){
                Point[] targets = new Point[targs];
                for (int i = 0; i < targets.length; i++){
                    int x = data.nextInt();
                    int y = data.nextInt();
                    targets[i] = new Point(x, y);
                }
                if (monoTarget){
                    map.getTargets().setValues(targets);
                }
                else {
                    int[] targVals = new int[targs];
                    for (int i = 0; i < targVals.length; i++){
                        targVals[i] = data.nextInt();
                    }
                    map.getTargets().setValues(targets, targVals);
                }
            }

            boolean monoHazard = data.next().equals("m");
            int hzrds = data.nextInt();
            if (hzrds > 0){
                Point[] hazards = new Point[hzrds];
                for (int i = 0; i < hazards.length; i++){
                    int x = data.nextInt();
                    int y = data.nextInt();
                    hazards[i] = new Point(x, y);
                }
                if (monoHazard){
                    map.getHazards().setValues(hazards);
                }
                else {
                    int[] hzrdVals = new int[targs];
                    for (int i = 0; i < hzrdVals.length; i++){
                        hzrdVals[i] = data.nextInt();
                    }
                    map.getHazards().setValues(hazards, hzrdVals);
                }
            }

            data.close();
            return map;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
