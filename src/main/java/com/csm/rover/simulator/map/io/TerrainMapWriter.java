package com.csm.rover.simulator.map.io;


import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.util.ArrayGrid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TerrainMapWriter {
    private static final Logger LOG = LogManager.getLogger(TerrainMapWriter.class);

    private static String fileID = "__^__";

    public static void SaveImage(TerrainMap map, String filepath, boolean targets, boolean hazards, boolean gridlines){
        LOG.log(Level.INFO, "Saving map image to {}", filepath);
        LOG.log(Level.DEBUG, "Saving map with targets:{}, hazards:{}, gridlines:{}", targets, hazards, gridlines);
        int gridSize = 15;
        ArrayGrid<Float> fs = map.getValues();
        int detail = map.getDetail();
        BufferedImage image = new BufferedImage(gridSize*fs.getWidth()/detail, gridSize*fs.getHeight()/detail, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        double maxval = map.getMax();
        for (int x = 0; x < map.getMapSize().getWidth()*detail; x += detail){
            for (int y = 0; y < map.getMapSize().getHeight()*detail; y += detail){
                Color color = null;
                int value;
                if (targets){
                    value = map.getTargets().getValueAt(x/detail, y/detail);
                    color = value == 0 ?
                            null :
                            new Color(
                                    Color.MAGENTA.getRed()*(value+5)/15,
                                    Color.MAGENTA.getGreen()*(value+5)/15,
                                    Color.MAGENTA.getBlue()*(value+5)/15
                            );
                }
                if (hazards && color == null){
                    value = map.getHazards().getValueAt(x/detail, y/detail);
                    color = value <= 5 ?
                            null :
                            new Color((11-value)*20+100, (11-value)*20+100, (11-value)*20+100);
                }
                if (color == null){
                    double scaled = map.getValues().get(x, y) / maxval * 100;
                    int red, green = 0, blue = 0;
                    if (scaled < 25){
                        red = (int) ((scaled)/25*255);
                    }
                    else if (scaled < 50){
                        red = 255;
                        green = (int) ((scaled-25)/25*255);
                    } else if (scaled < 75) {
                        red = (int) ((25-(scaled-50))/25*255);
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
                    color = new Color(red, green, blue);
                }

                g.setColor(color);
                g.fillRect(x * gridSize / detail, y * gridSize / detail, gridSize, gridSize);

                if (gridlines) {
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect(x * gridSize / detail, y * gridSize / detail, gridSize, gridSize);
                }
            }
        }
        try {
            File output = new File(filepath + ".PNG");
            if (output.createNewFile()){
                ImageIO.write(image, "png", output);
            }
            else {
                LOG.log(Level.ERROR, "Failed to create image file on map images save");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        LOG.log(Level.INFO, "Map image save complete");
    }

    public static void saveMap(TerrainMap map, File file){
        LOG.log(Level.INFO, "Saving map file to {}", file.getAbsolutePath());
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
            write.write("\n");

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
            write.write("\n");

            if (map.getHazards().isMono()){
                write.write("m");
            }
            else {
                write.write("v");

            }
            write.write("\n" + map.getHazards().getValues().size() + "\n");
            write.write(map.getHazards().getValues().genorateList() + "\n");
            if (!map.getHazards().isMono()){
                for (Integer val : map.getHazards().getValues().getValues()){
                    write.write(val + "\n");
                }
            }

            write.flush();
            write.close();
        }
        catch (Exception e) {
            LOG.log(Level.ERROR, "Map file sve failed", e);
        }
        LOG.log(Level.INFO, "Finished saving map file");
    }

}
