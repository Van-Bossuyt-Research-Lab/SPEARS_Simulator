package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.ui.visual.StartupEvent;
import com.csm.rover.simulator.ui.visual.StartupWindow;

import javax.swing.JPanel;
import java.util.List;

class StartupWindowImpl extends EmbeddedFrame implements StartupWindow {

    private JPanel content;

    StartupWindowImpl(){
        initialize();
    }

    private void initialize() {
        setTitle("Start New Sim");
        this.setBounds(100, 30, 500, 300);

        content = new JPanel();
        content.setLayout(null);
        this.setContentPane(content);
    }

    @Override
    public void registerPlatform(String platform) {

    }

    @Override
    public void registerAutonomousCodeModel(String platform, String name, List<String> parameters) {

    }

    @Override
    public void registerPhysicsModel(String platform, String name, List<String> parameters) {

    }

    @Override
    public void registerEnvironment(String platform, List<String> parameters) {

    }

    @Override
    public void display(){
        ((Form2)UiFactory.getApplication()).desktop.add(this);
        this.setVisible(true);
    }

    @Override
    public void setStartUpAction(StartupEvent e) {

    }

}
