package com.csm.rover.simulator.map.io;


import com.csm.rover.simulator.wrapper.Globals;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TerrainMapWriter {
    private String fileID = "%&$#";

    public void SaveImage(float[][] fs, int detail, int scheme, String filepath){
        BufferedImage image = new BufferedImage(15*fs.length/detail, 15*fs.length/detail, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        double maxval = Double.MIN_VALUE;
        double minval = Double.MAX_VALUE;
        int x = 0;
        while (x < fs.length){
            int y = 0;
            while (y < fs.length){
                if (fs[x][y] > maxval){
                    maxval = fs[x][y];
                }
                if (fs[x][y] < minval){
                    minval = fs[x][y];
                }
                y++;
            }
            x++;
        }
        x = 0;
        while (x < fs.length){
            int y = 0;
            while (y < fs.length){
                try {
                    switch (scheme){
                        case REDtoGREEN:
                            int scaled = (int) (fs[x+detail/2][y+detail/2] * 100 / maxHeight);
                            g.setColor(new Color(2.0f * scaled, 2.0f * (1 - scaled), 0));
                            break;
                        case BLACKtoWHITE:
                            int i = (int) Math.round((fs[x+detail/2][y+detail/2] - minval) / (maxval-minval) * 255);
                            g.setColor(new Color(i, i, i));
                            break;
                        case BLUEtoWHITE:
                            int j = (int) Math.round((fs[x+detail/2][y+detail/2] - minval) / (maxval-minval) * 255);
                            g.setColor(new Color(255-j, 255-j, 255));
                            break;
                    }
                } catch (Exception i){
                    g.setColor(getColor(fs[x][y]));
                }
                g.fillRect(x * 15 / detail, y * 15 / detail, 15, 15);
                y += detail;
            }
            x += detail;
        }
        try {
            File output = new File(filepath + ".PNG");
            if (output.createNewFile()){
                ImageIO.write(image, "png", output);
            }
            else {
                Globals.reportError("TerrainMap", "Failed to create image file on map images save", null);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMap(File file){
        try {
            BufferedWriter write = new BufferedWriter(new FileWriter(file, true));

            write.write(fileID + "\n");

            write.write(values[0].length + "\n" + values.length + "\n" + detail + "\n\n");
            for (float[] value : values) {
                for (float aValue : value) {
                    write.write(aValue + "\t");
                }
                write.write('\n');
            }

            if (monoTargets){
                write.write("m");
            }
            else {
                write.write("v");

            }
            write.write("\n" + targets.size() + "\n");
            write.write(targets.genorateList() + "\n");
            if (!monoTargets){
                for (Integer val : targets.getValues()){
                    write.write(val + "\n");
                }
            }

            if (monoHazards){
                write.write("m");
                ArrayList<String> valueLocs = new ArrayList<String>();
                for (int i = 0; i < hazards.length; i++){
                    for (int j = 0; j < hazards[0].length; j++){
                        try {
                            if (hazards[i][j] > 0){
                                valueLocs.add(i + "\t" + j);
                            }
                        } catch (NullPointerException e) { e.printStackTrace(); }
                    }
                }
                write.write("\n" + valueLocs.size() + "\n");
                for (String str : valueLocs){
                    write.write(str + "\n");
                }
                write.write("\n");
            }
            else {
                write.write("v");
                write.write("\n" + hazards[0].length + "\n" + hazards.length + "\n");
                for (int[] hazard : hazards) {
                    for (int hazardVal : hazard) {
                        try {
                            if (hazardVal > 0) {
                                write.write(hazardVal + "\t");
                            } else {
                                write.write(0 + "\t");
                            }
                        } catch (NullPointerException e) {
                            write.write(0 + "\t");
                        }
                    }
                    write.write("\n");
                }
            }

            write.flush();
            write.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
