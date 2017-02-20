package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.io.ConfigurationFileFilter;
import com.csm.rover.simulator.objects.io.RunConfiguration;
import com.csm.rover.simulator.objects.FreeThread;
import com.csm.rover.simulator.ui.visual.PopUp;
import com.csm.rover.simulator.ui.visual.StartupListener;
import com.csm.rover.simulator.ui.visual.StartupWindow;
import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

class StartupWindowImpl extends EmbeddedFrame implements StartupWindow {
    private static final Logger LOG = LogManager.getLogger(StartupWindowImpl.class);

    private JPanel content, platformsPnl;
    private JCheckBox accelChk;
    private JSpinner durationSpin;
    private Map<String, TypeDisplayPanel> platformDisplays;

    private Optional<StartupListener> startupAction = Optional.empty();

    public StartupWindowImpl(){
        platformDisplays = new TreeMap<>();
        initialize();
    }

    private void initialize() {
        setTitle("Start New Sim");
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setSize(500, 300);

        content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BorderLayout());
        this.setContentPane(content);

        JPanel globalPnl = new JPanel();
        globalPnl.setOpaque(false);
        globalPnl.setLayout(new MigLayout("", "[fill][fill][fill][fill,grow][fill]", "[fill]"));
        content.add(globalPnl, BorderLayout.NORTH);

        durationSpin = new JSpinner();
        durationSpin.setModel(new SpinnerNumberModel(60, 5, 130000, 1));
        durationSpin.setVisible(false);
        globalPnl.add(durationSpin, "cell 1 0");

        JLabel unitsLbl = new JLabel("min");
        unitsLbl.setVisible(false);
        globalPnl.add(unitsLbl, "cell 2 0");

        accelChk = new JCheckBox("Accelerate Simulation");
        accelChk.setOpaque(false);
        accelChk.addActionListener((a) -> {
            durationSpin.setVisible(accelChk.isSelected());
            unitsLbl.setVisible(accelChk.isSelected());
        });
        globalPnl.add(accelChk, "cell 0 0");

        JButton saveBtn = new JButton("Save Config");
        saveBtn.addActionListener((a) -> saveConfiguration());
        globalPnl.add(saveBtn, "cell 4 0");

        platformsPnl = new JPanel();
        platformsPnl.setOpaque(false);
        platformsPnl.setLayout(new MigLayout("", "grow,fill", "[grow,fill]"));
        content.add(platformsPnl, BorderLayout.CENTER);

        JButton goBtn = new JButton("START");
        goBtn.addActionListener((ActionEvent e) -> {
            try {
                verifyPlatformConfig();
                if (startupAction.isPresent()) {
                    startupAction.get().startSimulation(getConfiguration());
                }
                setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
                doDefaultCloseAction();
            }
            catch (StartUpConfigurationException ex){
                new FreeThread(0, () -> {
                    UiFactory.newPopUp().setMessage(ex.getMessage()).setSubject("Invalid Configuration").showConfirmDialog(PopUp.Buttons.OK_CANCEL_OPTIONS);
                    if (ex.getSource() != null){
                        ex.getSource().requestFocus();
                    }
                }, 1, "configErrorPopUp");

            }
        });
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
    public void registerEnvironmentGenerator(String platform, String name, List<String> parameters){
        platformDisplays.get(platform).addMapGenerator(name, parameters);
    }

    @Override
    public void registerEnvironmentModifier(String platform, String name, List<String> parameters){
        platformDisplays.get(platform).addMapModifier(name, parameters);
    }

    @Override
    public void registerEnvironmentPopulator(String platform, String name, List<String> parameters){
        platformDisplays.get(platform).addMapPopulator(name, parameters);
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

    private void verifyPlatformConfig() throws StartUpConfigurationException {
        boolean filled = false;
        for (String platform : platformDisplays.keySet()){
            try {
                boolean fill = platformDisplays.get(platform).verifyConfig();
                filled = fill || filled;
            }
            catch (StartUpConfigurationException e){
                throw new StartUpConfigurationException(platform + ": " + e.getMessage(), e.getSource());
            }
        }
        if (!filled){
            throw new StartUpConfigurationException("At least one platform must be set up");
        }
    }

    private RunConfiguration getConfiguration() throws StartUpConfigurationException {
        RunConfiguration.Builder builder = RunConfiguration.newConfig();
        if (accelChk.isSelected()){
            builder.setRuntime((int)durationSpin.getModel().getValue());
        }
        else {
            builder.setAccelerated(false);
        }
        try {
            for (String type : platformDisplays.keySet()) {
                if (platformDisplays.get(type).verifyConfig()) {
                    builder = builder.addType(platformDisplays.get(type).getConfiguration());
                }
            }
            return builder.build();
        }
        catch (StartUpConfigurationException e){
            LOG.log(Level.ERROR, "Invalid TypeDisplayPanel after verification", e);
            throw e;
        }
    }

    private void saveConfiguration(){
        try {
            verifyPlatformConfig();
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new ConfigurationFileFilter());
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setCurrentDirectory(UiConfiguration.getConfigurationFolder());
            if (chooser.showSaveDialog(UiFactory.getDesktop()) == JFileChooser.APPROVE_OPTION){
                UiConfiguration.changeConfigurationFolder(chooser.getCurrentDirectory());
                try {
                    String filename = chooser.getSelectedFile().getAbsolutePath();
                    if (!filename.endsWith(".cfg")){
                        filename += ".cfg";
                    }
                    getConfiguration().save(new File(filename));
                }
                catch (Exception e){
                    new FreeThread(0, () -> UiFactory.newPopUp().setMessage(e.getMessage()).setSubject("Save Failed").showConfirmDialog(PopUp.Buttons.DEFAULT_OPTIONS), 1, "saveFail", true);
                }
            }
        }
        catch (StartUpConfigurationException ex) {
            new FreeThread(0, () -> {
                UiFactory.newPopUp().setMessage(ex.getMessage()).setSubject("Invalid Configuration").showConfirmDialog(PopUp.Buttons.OK_CANCEL_OPTIONS);
                if (ex.getSource() != null){
                    ex.getSource().requestFocus();
                }
            }, 1, "configErrorPopUp");
        }
    }

}

class StartUpConfigurationException extends Exception {

    private final Component source;

    StartUpConfigurationException(String message){
        this(message, null);
    }

    StartUpConfigurationException(String message, Component source){
        super(message);
        this.source = source;
    }

    Component getSource(){
        return source;
    }

}
