package com.csm.rover.simulator.map.io;


import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.map.display.MapDisplayPanel;
import com.csm.rover.simulator.objects.ArrayGrid;
import com.csm.rover.simulator.wrapper.Globals;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TerrainMapWriter {
    private static String fileID = "__^__";

    public static void SaveImage(TerrainMap map, int scheme, String filepath){
        int gridSize = 15;
        ArrayGrid<Float> fs = map.getValues();
        int detail = map.getDetail();
        BufferedImage image = new BufferedImage(gridSize*fs.getWidth()/detail, gridSize*fs.getHeight()/detail, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        double maxval = map.getMax();
        double minval = map.getMin();
        for (int x = 0; x < fs.getWidth(); x += detail){
            for (int y = 0; y < fs.getHeight(); y += detail){
                g.setColor(getColor(fs.get(x, y), scheme, minval, maxval));
                g.fillRect(x * gridSize / detail, y * gridSize / detail, gridSize, gridSize);
            }
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

    private static Color getColor(double numb, int currentColorScheme, double minval, double maxval) {
        switch (currentColorScheme){
            case MapDisplayPanel.REDtoGREEN:
                double scaled = numb / maxval * 100;
                int red, green = 0, blue = 0;
                if (scaled < 25){
                    red = (int) ((scaled)/25*255);
                }
                else if (scaled < 50){
                    red = 255;
                    green = (int) ((scaled-25)/25*255);
                }
                else if (scaled < 75){
                    red =  (int) ((25-(scaled-50))/25*255);
                    green = 255;
                }
                else if (scaled < 100){
                    red = (int) ((scaled-75)/25*255);
                    green = 255;
                    blue = red;
                }
                else {
                    red = 255;
                    green = 255;
                    blue = 255;
                }
                try{
                    return new Color(red, green, blue);
                }
                catch (Exception e){
                    return Color.CYAN;
                }
            case MapDisplayPanel.BLACKtoWHITE:
                int x = (int) Math.round((numb - minval) / maxval * 255);
                return new Color(x, x, x);
            case MapDisplayPanel.BLUEtoWHITE:
                int y = (int) Math.round((numb - minval) / maxval * 255);
                return new Color(y, y, 255);
            default:
                return null;
        }
    }

    public static void saveMap(TerrainMap map, File file){
        try {
            BufferedWriter write = new BufferedWriter(new FileWriter(file, true));

            write.write(fileID + "\n");

            write.write(map.getValues().getWidth() + "\n" + map.getValues().getHeight() + "\n" + map.getDetail() + "\n\n");
            for (int x = 0; x < map.getValues().getWidth(); x++) {
                for (int y = 0; y < map.getValues().getHeight(); y++) {
                    write.write(map.getValues().get(x, y) + "\t");
                }
                write.write('\n');
            }

            if (map.getTargets().isMono()){
                write.write("m");
            }
            else {
                write.write("v");

            }
            write.write("\n" + map.getTargets().getValues().size() + "\n");
            write.write(map.getTargets().getValues().genorateList() + "\n");
            if (!map.getTargets().isMono()){
                for (Integer val : map.getTargets().getValues().getValues()){
                    write.write(val + "\n");
                }
            }

            if (map.getHazards().isMono()){
                write.write("m");
            }
            else {
                write.write("v");

            }
            write.write("\n" + map.getHazards().getValues().size() + "\n");
            write.write(map.getHazards().getValues().genorateList() + "\n");
            if (!map.getHazards().isMono()){
                for (Integer val : map.getTargets().getValues().getValues()){
                    write.write(val + "\n");
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
