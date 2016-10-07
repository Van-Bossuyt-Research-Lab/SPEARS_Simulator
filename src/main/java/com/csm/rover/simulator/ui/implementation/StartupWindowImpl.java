package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.io.PlatformConfig;
import com.csm.rover.simulator.ui.visual.StartupListener;
import com.csm.rover.simulator.ui.visual.StartupWindow;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

class StartupWindowImpl extends EmbeddedFrame implements StartupWindow {

    private JPanel content, platformsPnl;
    private Map<String, TypeDisplayPanel> platformDisplays;

    private Optional<StartupListener> startupAction = Optional.empty();

    public StartupWindowImpl(){
        platformDisplays = new TreeMap<>();
        initialize();
    }

    private void initialize() {
        setTitle("Start New Sim");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(500, 300);

        content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BorderLayout());
        this.setContentPane(content);

        platformsPnl = new JPanel();
        platformsPnl.setOpaque(false);
        platformsPnl.setLayout(new MigLayout("", "grow,fill", "[grow,fill]"));
        content.add(platformsPnl, BorderLayout.CENTER);

        JButton goBtn = new JButton("START");
        goBtn.addActionListener((ActionEvent e) -> { if (startupAction.isPresent()) startupAction.get().startSimulation(getConfiguration()); });
        content.add(goBtn, BorderLayout.SOUTH);
    }

    @Override
    public void registerPlatform(String platform) {
        TypeDisplayPanel display = new TypeDisplayPanel(platform);
        platformsPnl.add(display, "cell "+platformDisplays.size()+" 0");
        platformDisplays.put(platform, display);
        setSize((int)platformsPnl.getPreferredSize().getWidth()+15, (int)platformsPnl.getPreferredSize().getHeight());
        setMinimumSize(new Dimension((int)platformsPnl.getPreferredSize().getWidth()+15, (int)platformsPnl.getPreferredSize().getHeight()));
    }

    @Override
    public void registerAutonomousCodeModel(String platform, String name, List<String> parameters) {
        this.platformDisplays.get(platform).addCodeModel(name, parameters);
    }

    @Override
    public void registerPhysicsModel(String platform, String name, List<String> parameters) {
        platformDisplays.get(platform).addPhysicsModel(name, parameters);
    }

    @Override
    public void registerEnvironment(String platform, List<String> parameters) {

    }

    @Override
    public void display(){
        UiFactory.getDesktop().add(this);
        this.setVisible(true);
    }

    @Override
    public void setStartUpAction(StartupListener e) {
        startupAction = Optional.of(e);
    }

    private PlatformConfig getConfiguration(){
        //TODO
        return null;
    }

}
