package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.environments.EnvironmentRegistry;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.rover.TerrainEnvironment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

@FrameMarker(name = "Terrain Monitor", platform = "Rover")
class TerrainEnvironmentMonitor extends EnvironmentDisplay {
    private static final Logger LOG = LogManager.getLogger(TerrainEnvironmentMonitor.class);

    private JPanel content;
    private TerrainMapDisplay display;

    protected TerrainEnvironmentMonitor() {
        super("Rover");
        initialize();
    }

    private void initialize(){
        content = new JPanel();
        setBackground(Color.BLACK);
        setLayout(null);
        setContentPane(content);
        setVisible(false);
        setSize(500, 500);
    }

    @Override
    protected void doSetEnvironment(PlatformEnvironment environment) {
        display = new TerrainMapDisplay((TerrainEnvironment)environment);
        display.setResolution(20);
        content.add(display);
    }

    @Override
    protected void update() {
        display.repaint();
    }
}

class TerrainMapDisplay extends JPanel {
    private static final Logger LOG = LogManager.getLogger(TerrainMapDisplay.class);

    private final TerrainEnvironment terrainMap;

    private int squareResolution = 50;

    private Map<String, Boolean> viewPopulators;
    private Map<String, PopulatorDisplayFunction> popDisplays;

    TerrainMapDisplay(TerrainEnvironment environment){
        this.terrainMap = environment;
        viewPopulators = new HashMap<>();
        popDisplays = new HashMap<>();
        for (String pop : terrainMap.getPopulators()){
            viewPopulators.put(pop, true);
            popDisplays.put(pop, EnvironmentRegistry.getPopulatorDisplayFunction(terrainMap.getType(), pop));
        }
        this.setBounds(0, 0, 100, 100);
        this.setBackground(Color.BLACK);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent arg0) {
                try {
                    setSize(terrainMap.getSize() * squareResolution, terrainMap.getSize() * squareResolution);
                }
                catch (Exception e) {
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
        try {
            int xstart = (-this.getLocation().x / squareResolution - 1);
            int xend = xstart + (this.getParent().getWidth() / squareResolution + 3);
            if (xstart < 0){
                xstart = 0;
            }
            if (xend > terrainMap.getSize()){
                xend = terrainMap.getSize();
            }
            int ystart = (-this.getLocation().y / squareResolution - 1);
            int yend = ystart + (this.getParent().getHeight() / squareResolution + 4);
            if (ystart < 0){
                ystart = 0;
            }
            if (yend > terrainMap.getSize()){
                yend = terrainMap.getSize();
            }
            for (int x = xstart; x < xend; x++){
                for (int y = ystart; y < yend; y++){
                    DecimalPoint loc = new DecimalPoint(x - terrainMap.getSize()/2, terrainMap.getSize()/2 - y);
                    Color color = null;
                    for (String pop : viewPopulators.keySet()){
                        if (viewPopulators.get(pop)){
                            double value = terrainMap.getPopulatorValue(pop, loc);
                            color = value == 0 ? null : popDisplays.get(pop).displayFunction(value);
                        }
                    }
                    if (color == null){
                        double scaled = terrainMap.getHeightAt(loc) / terrainMap.getMaxHeight() * 100;
                        int red, green = 0, blue = 0;
                        if (scaled < 25){
                            red = (int) ((scaled)/25*255);
                        }
                        else if (scaled < 50){
                            red = 255;
                            green = (int) ((scaled-25)/25*255);
                        }
                        else if (scaled < 75) {
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
        catch (Exception e) {
            LOG.log(Level.ERROR, "Failed to draw map", e);
        }
    }

    void setPopulatorVisible(String pop, boolean b){
        if (viewPopulators.containsKey(pop)){
            viewPopulators.put(pop, b);
        }
        repaint();
    }

    boolean isPopulatorVisible(String pop){
        return viewPopulators.get(pop);
    }

    void setResolution(int res){
        if (res > 0){
            squareResolution = res;
            setSize(terrainMap.getSize() * squareResolution, terrainMap.getSize() * squareResolution);
            this.repaint();
        }
    }

    int getResolution(){
        return squareResolution;
    }

}
