package com.csm.rover.simulator.map.display;

import com.csm.rover.simulator.map.TerrainMap;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapDisplayPanel extends JPanel {

    private TerrainMap terrainMap;

    private int squareResolution = 50;
    private int currentColorScheme = 0;
    public static final int REDtoGREEN = 0, BLACKtoWHITE = 1, BLUEtoWHITE = 2;

    public MapDisplayPanel(){
        this.setBounds(0, 0, 100, 100);
        this.setBackground(Color.BLACK);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent arg0) {
                try {
                    setSize(values.getWidth()*squareResolution/detail, values.getHeight()*squareResolution/detail);
                }
                catch (Exception e){
                    e.printStackTrace();
                    setSize(100, 100);
                }
            }
        });
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                double shift = values.getWidth() / (double)detail * squareResolution / 2.0;
                double x = (e.getX()-shift) / squareResolution;
                double y = (shift-e.getY()) / squareResolution;
            }
        });
    }

    //paint
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            int xstart = (-this.getLocation().x / squareResolution - 1) * detail;
            if (xstart < 0){
                xstart = 0;
            }
            int xend = xstart + (this.getParent().getWidth() / squareResolution + 3) * detail;
            if (xend > values.length){
                xend = values.length;
            }
            int ystart = (-this.getLocation().y / squareResolution - 1) * detail;
            if (ystart < 0){
                ystart = 0;
            }
            int yend = ystart + (this.getParent().getHeight() / squareResolution + 4) * detail;
            if (yend > values[0].length){
                yend = values[0].length;
            }
            if (values.length > 0){
                //this.setSize(values.length*squareResolution/detail, values[0].length*squareResolution/detail);
                int x = xstart;
                while (x < xend){
                    int y = ystart;
                    while (y < yend){
                        try {
                            try {
                                if (viewTargets){
                                    if (targets.get(x/detail, y/detail) > 0){
                                        int value = targets.get(x/detail, y/detail);
                                        g.setColor(new Color(Color.MAGENTA.getRed()*(value+5)/15, Color.MAGENTA.getGreen()*(value+5)/15, Color.MAGENTA.getBlue()*(value+5)/15));
                                    }
                                    else {
                                        throw new NullPointerException();
                                    }
                                }
                                else {
                                    throw new NullPointerException();
                                }
                            }
                            catch (NullPointerException e){
                                if (viewHazards){
                                    if (hazards[x/detail][y/detail] > 5){
                                        int value = (11-hazards[x/detail][y/detail])*20+100;
                                        g.setColor(new Color(value, value, value));
                                    }
                                    else {
                                        throw new Exception();
                                    }
                                }
                                else {
                                    throw new Exception();
                                }
                            }
                        }
                        catch (Exception e){
                            g.setColor(getColor(values[x/detail][y/detail]));
                        }
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
                        y += detail;
                    }
                    x += detail;
                }
            }
			/*if (trailVis){
				int i = 0;
				while (i < trailPoints.length){
					g.setColor(new Color(255-i*255/trailPoints.length, 255-i*255/trailPoints.length, 255-i*255/trailPoints.length));
					g.fillRect((int)(trailPoints[i].getX()*squareResolution+this.getWidth()/2)-3, (int)(this.getHeight()/2-trailPoints[i].getY()*squareResolution)-3, 6, 6);
					i++;
				}
			}*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Color getColor(double numb) {
        switch (currentColorScheme){
            case REDtoGREEN:
                double scaled = numb / maxHeight * 100;
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
            this.setSize(values.length*squareResolution/detail, values[0].length*squareResolution/detail);
            this.repaint();
        }
    }

    public int getResolution(){
        return squareResolution;
    }

}
