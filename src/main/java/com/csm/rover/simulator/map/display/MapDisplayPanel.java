package com.csm.rover.simulator.map.display;

import com.csm.rover.simulator.map.TerrainMap;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MapDisplayPanel extends JPanel {

    private TerrainMap terrainMap;

    private int squareResolution = 50;

    private boolean viewTargets = true;
    private boolean viewHazards = true;

    public MapDisplayPanel(){
        this.setBounds(0, 0, 100, 100);
        this.setBackground(Color.BLACK);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent arg0) {
                try {
                    setSize((int) terrainMap.getMapSize().getWidth() * squareResolution, (int) terrainMap.getMapSize().getHeight() * squareResolution);
                } catch (Exception e) {
                    e.printStackTrace();
                    setSize(100, 100);
                }
            }
        });
    }

    //paint the map
    //kind long and ugly, I know, but necessary for performance
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int detail = terrainMap.getDetail();
        try {
            if (!terrainMap.getValues().isEmpty()){
                int xstart = (-this.getLocation().x / squareResolution - 1);
                int xend = xstart + (this.getParent().getWidth() / squareResolution + 3);
                if (xstart < 0){
                    xstart = 0;
                }
                if (xend > terrainMap.getMapSize().getWidth()){
                    xend = (int)terrainMap.getMapSize().getWidth();
                }
                int ystart = (-this.getLocation().y / squareResolution - 1);
                int yend = ystart + (this.getParent().getHeight() / squareResolution + 4);
                if (ystart < 0){
                    ystart = 0;
                }
                if (yend > terrainMap.getMapSize().getHeight()){
                    yend = (int)terrainMap.getMapSize().getHeight();
                }
                for (int x = xstart; x < xend; x++){
                    for (int y = ystart; y < yend; y++){
                        Color color = null;
                        int value;
                        if (viewTargets){
                            value = terrainMap.getTargets().getValueAt(x, y);
                            color = value == 0 ?
                                    null :
                                    new Color(
                                    Color.MAGENTA.getRed()*(value+5)/15,
                                    Color.MAGENTA.getGreen()*(value+5)/15,
                                    Color.MAGENTA.getBlue()*(value+5)/15
                            );
                        }
                        if (viewHazards && color == null){
                            value = terrainMap.getHazards().getValueAt(x, y);
                            color = value <= 5 ?
                                    null :
                                    new Color((11-value)*20+100, (11-value)*20+100, (11-value)*20+100);
                        }
                        if (color == null){
                            double scaled = terrainMap.getValues().get(x * detail, y * detail) / terrainMap.getMax() * 100;
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
                        g.fillRect(x * squareResolution, y * squareResolution, squareResolution, squareResolution);

                        g.setColor(Color.DARK_GRAY);
                        g.drawRect(x * squareResolution, y * squareResolution, squareResolution, squareResolution);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTerrainMap(TerrainMap map){
        this.terrainMap = map;
    }

    public TerrainMap getTerrainMap(){
        return this.terrainMap;
    }

    public void setTargetsVisible(boolean b){
        viewTargets = b;
        repaint();
    }

    public boolean areTargetsVisible(){
        return viewTargets;
    }

    public void setHazardsVisible(boolean b){
        viewHazards = b;
        repaint();
    }

    public boolean areHazardsVisible(){
        return viewHazards;
    }

    public void setResolution(int res){
        if (res > 0){
            squareResolution = res;
            setSize((int) terrainMap.getMapSize().getWidth() * squareResolution, (int) terrainMap.getMapSize().getHeight() * squareResolution);
            this.repaint();
        }
    }

    public int getResolution(){
        return squareResolution;
    }

}
