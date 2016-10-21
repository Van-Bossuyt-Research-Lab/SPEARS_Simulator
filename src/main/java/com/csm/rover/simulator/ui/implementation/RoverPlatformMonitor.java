package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.platforms.rover.RoverState;

import javax.swing.*;
import java.awt.*;

@FrameMarker(name = "Rover Monitor", platform = "Rover")
class RoverPlatformMonitor extends PlatformDisplay {

    private JPanel contentPane;
    private JLabel label;

    private RoverObject rover;

    RoverPlatformMonitor(){
        super("Rover");
        initialize();
    }

    private void initialize(){
        setTitle("");
        setSize(100, 100);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Color.WHITE);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        label = new JLabel();
        scrollPane.setViewportView(label);
    }

    @Override
    protected void doSetPlatform(Platform platform) {
        this.rover = (RoverObject)platform;
        setTitle("Rover: " + rover.getName());
        update();
    }

    @Override
    protected void update(){
        PlatformState state = rover.getState();
        String out = "";
        for (String param : state.expectedValues()){
            out += param + ": ";
            try {
                out += state.get(param);
            }
            catch (Exception e){
                out += e.getMessage();
            }
            out += "\n";
        }
        label.setText(String.format("<html>%s</html>", out.replaceAll("\n", "<br>")));
    }

}
