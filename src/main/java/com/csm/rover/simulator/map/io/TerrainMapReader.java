package com.csm.rover.simulator.map.io;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TerrainMapReader {
    private String fileID = "%&$#";

    public void loadMap(File file) throws Exception {
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
            setDetail(detail);

            float[][] values = new float[height][width];
            for (int i = 0; i < height; i++){
                float[] row = new float[width];
                for (int j = 0; j < width; j++){
                    row[j] = data.nextFloat();
                }
                values[i] = row;
            }
            this.setValues(values);

            boolean mono = data.next().equals("m");
            int targs = data.nextInt();
            if (targs > 0){
                Point[] targets = new Point[targs];
                for (int i = 0; i < targets.length; i++){
                    int x = data.nextInt();
                    int y = data.nextInt();
                    targets[i] = new Point(x, y);
                }
                if (mono){
                    this.setTargets(targets);
                }
                else {
                    int[] targVals = new int[targs];
                    for (int i = 0; i < targVals.length; i++){
                        targVals[i] = data.nextInt();
                    }
                    this.setTargets(targets, targVals);
                }
            }

            if (data.next().equals("m")){
                int hzdrs = data.nextInt();
                if (hzdrs > 0){
                    Point[] hazards = new Point[hzdrs];
                    for (int i = 0; i < hazards.length; i++){
                        int x = data.nextInt();
                        int y = data.nextInt();
                        hazards[i] = new Point(x, y);
                    }
                    this.setHazards(hazards);
                }
            }
            else {
                width = data.nextInt();
                height = data.nextInt();
                int[][] hazVals = new int[width][height];
                for (int i = 0; i < height; i++){
                    int[] row = new int[width];
                    for (int j = 0; j < width; j++){
                        row[j] = data.nextInt();
                    }
                    hazVals[i] = row;
                }
                this.setHazards(hazVals);
            }

            data.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
