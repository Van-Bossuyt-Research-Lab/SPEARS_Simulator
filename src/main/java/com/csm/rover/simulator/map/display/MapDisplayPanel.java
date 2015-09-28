package com.csm.rover.simulator.map.display;

import com.csm.rover.simulator.map.TerrainMap;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MapDisplayPanel extends JPanel {

    private TerrainMap terrainMap;

    private int squareResolution = 50;

    private boolean viewTargets = true;
    private boolean viewHazards = true;

    private int currentColorScheme = 0;
    public static final int REDtoGREEN = 0, BLACKtoWHITE = 1, BLUEtoWHITE = 2;

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

    //paint
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int detail = terrainMap.getDetail();
        try {
            if (!terrainMap.getValues().isEmpty()){
                int xstart = (-this.getLocation().x / squareResolution - 1) * detail;
                if (xstart < 0){
                    xstart = 0;
                }
                int xend = xstart + (this.getParent().getWidth() / squareResolution + 3) * detail;
                if (xend > terrainMap.getValues().getWidth()){
                    xend = terrainMap.getValues().getWidth();
                }
                int ystart = (-this.getLocation().y / squareResolution - 1) * detail;
                if (ystart < 0){
                    ystart = 0;
                }
                int yend = ystart + (this.getParent().getHeight() / squareResolution + 4) * detail;
                if (yend > terrainMap.getValues().getHeight()){
                    yend = terrainMap.getValues().getHeight();
                }
                for (int x = xstart; x < xend; x += detail){
                    for (int y = ystart; y < yend; y += detail){
                        Color color = null;
                        if (viewTargets && terrainMap.getTargets().isPointMarked(new Point(x, y))){
                            color = getTargetColor(terrainMap.getTargets().getValueAt(new Point(x, y)));
                        }
                        else if (viewHazards && terrainMap.getHazards().isPointMarked(new Point(x, y))){
                            color = getHazardColor(terrainMap.getHazards().getValueAt(new Point(x, y)));
                        }
                        if (color == null){
                            color = getColor(terrainMap.getValueAtLocation(x/detail, y/detail));
                        }
                        g.setColor(color);
                        g.fillRect(x * squareResolution / detail, y * squareResolution / detail, squareResolution, squareResolution);
                        switch (currentColorScheme){
                            case REDtoGREEN:
                            case BLUEtoWHITE:
                                g.setColor(Color.DARK_GRAY);
                                break;
                            case BLACKtoWHITE:
                                g.setColor(new Color(240, 250, 0));
                                break;
                        }
                        g.drawRect(x * squareResolution / detail, y * squareResolution / detail, squareResolution, squareResolution);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Color getColor(double numb) {
        double maxval = terrainMap.getMax();
        double minval = terrainMap.getMin();
        switch (currentColorScheme){
            case REDtoGREEN:
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
            case BLACKtoWHITE:
                int x = (int) Math.round((numb - minval) / maxval * 255);
                return new Color(x, x, x);
            case BLUEtoWHITE:
                int y = (int) Math.round((numb - minval) / maxval * 255);
                return new Color(y, y, 255);
            default:
                return null;
        }
    }

    public Color getTargetColor(int value){
        return new Color(
                Color.MAGENTA.getRed()*(value+5)/15,
                Color.MAGENTA.getGreen()*(value+5)/15,
                Color.MAGENTA.getBlue()*(value+5)/15
        );
    }

    public Color getHazardColor(int value){
        int value2 = (11-value)*20+100;
        return new Color(value2, value2, value2);
    }

    public void setTerrainMap(TerrainMap map){
        this.terrainMap = map;
    }

    public TerrainMap getTerrainMap(){
        return this.terrainMap;
    }

    public void setColorScheme(int which){
        currentColorScheme = which;
        this.repaint();
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
