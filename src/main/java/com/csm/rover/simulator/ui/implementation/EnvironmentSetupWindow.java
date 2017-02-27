package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.environments.*;
import com.csm.rover.simulator.objects.FreeThread;
import com.csm.rover.simulator.ui.sound.SoundPlayer;
import com.csm.rover.simulator.ui.sound.SpearsSound;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.io.File;
import java.util.List;

class EnvironmentSetupWindow extends EmbeddedFrame {
    private static final Logger LOG = LogManager.getLogger(EnvironmentSetupWindow.class);

    private JPanel contentPane;
    private JComboBox<String> generatorCombo;
    private ZList<ModifierConfig> modList;
    private Map<String, JCheckBox> populatorSelectors;
    private JButton goBtn;

    private Set<EmbeddedFrame> spawnedFrames;
    private Optional<EnvironmentDisplay> display = Optional.empty();
    private FreeThread waitingAnimation;

    private String platform;
    private ReportEnvironment reportAction;
    private Map<String, List<String>> generatorParams, modifierParams, populatorParams;
    private Optional<ModifierConfig> generatorConfig = Optional.empty();
    private Optional<PlatformEnvironment> enviro = Optional.empty();
    private Map<String, PopulatorConfig> populatorConfigs;

    private Map<String, Boolean> isPopEditorOpen;

    private EnvironmentSetupWindow(){
        spawnedFrames = new HashSet<>();
        populatorConfigs = new HashMap<>();
        isPopEditorOpen = new HashMap<>();
    }

    private void initialize(){
        setTitle("New "+platform+" Environment");
        setVisible(true);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                for (EmbeddedFrame frame : spawnedFrames){
                    if (!frame.isClosed()){
                        frame.doDefaultCloseAction();
                    }
                }
            }
        });

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        JPanel mainPnl = new JPanel();
        mainPnl.setOpaque(false);
        mainPnl.setLayout(new MigLayout("", "[fill,grow][fill,grow][fill,grow]", "[fill][fill][fill,grow]2[fill]"));
        contentPane.add(mainPnl, BorderLayout.CENTER);

        JLabel modTitle = new JLabel("Modifiers");
        modTitle.setFont(FontFunctions.bold(FontFunctions.bigger(modTitle.getFont())));
        mainPnl.add(modTitle, "cell 0 0");

        generatorCombo = new JComboBox<>();
        generatorCombo.setModel(new DefaultComboBoxModel<>(generatorParams.keySet().toArray(new String[generatorParams.size()])));
        generatorCombo.setSelectedIndex(-1);
        generatorCombo.setEditable(false);
        generatorCombo.addActionListener((e) -> {
            if (generatorCombo.getSelectedIndex() != -1){
                String gen = (String)generatorCombo.getSelectedItem();
                ModifierCreationWindow window = ModifierCreationWindow.newModifierCreationWindow(platform)
                        .setSingleInstance(gen, generatorParams.get(gen))
                        .setReportAction((config) -> generatorConfig = Optional.of(config))
                        .build();
                window.addInternalFrameListener(new InternalFrameAdapter() {
                    @Override
                    public void internalFrameClosed(InternalFrameEvent e) {
                        if (!generatorConfig.isPresent()){
                            generatorCombo.setSelectedIndex(-1);
                        }
                        updateGoEnabled();
                    }
                });
                spawnframe(window);
            }
            updateGoEnabled();
        });
        mainPnl.add(generatorCombo, "cell 0 1");

        modList = new ZList<>();
        mainPnl.add(modList, "cell 0 2");

        JPanel modButtonPnl = new JPanel();
        modButtonPnl.setOpaque(false);
        modButtonPnl.setLayout(new MigLayout("", "2[fill,grow]2[fill]2[fill]2[fill,grow]2", "2[fill]2"));
        mainPnl.add(modButtonPnl, "cell 0 3");

        JButton modAdd = new JButton("Add");
        modAdd.addActionListener((a) ->
            spawnframe(ModifierCreationWindow.newModifierCreationWindow(platform)
                .setOptionMap(modifierParams)
                .setReportAction(modList::addValue)
                .build()));
        modAdd.addActionListener((a) -> resetSubmission());
        modButtonPnl.add(modAdd, "cell 0 0");

        JButton modUp = new JButton();
        modUp.setIcon(ImageFunctions.getImage("/gui/arrow_up.png", 15, 15));
        modUp.addActionListener((a) -> {
            int start = modList.getSelectedIndex();
            if (start == -1 || start == 0){
                return;
            }
            ModifierConfig mod = modList.getSelectedItem();
            modList.removeValue(mod);
            modList.addValue(mod, start-1);
            modList.setSelection(start-1);
            resetSubmission();
        });
        modButtonPnl.add(modUp, "cell 1 0");

        JButton modDown = new JButton();
        modDown.setIcon(ImageFunctions.getImage("/gui/arrow_down.png", 15, 15));
        modDown.addActionListener((a) -> {
            int start = modList.getSelectedIndex();
            if (start == -1 || start == modList.getItemCount()-1){
                return;
            }
            ModifierConfig mod = modList.getSelectedItem();
            modList.removeValue(mod);
            modList.addValue(mod, start+1);
            modList.setSelection(start+1);
            resetSubmission();
        });
        modButtonPnl.add(modDown, "cell 2 0");

        JButton modRemove = new JButton("Remove");
        modRemove.addActionListener((a) -> {
            modList.removeValue(modList.getSelectedItem());
            resetSubmission();
        });
        modButtonPnl.add(modRemove, "cell 3 0");

        JLabel popTitle = new JLabel("Populators");
        popTitle.setFont(FontFunctions.bigger(FontFunctions.bold(popTitle.getFont())));
        mainPnl.add(popTitle, "cell 1 0 2 1");

        JScrollPane populatorScroll = new JScrollPane();
        populatorScroll.setOpaque(false);
        mainPnl.add(populatorScroll, "cell 1 1 2 3");

        JPanel populatorList = new JPanel();
        populatorList.setBackground(Color.WHITE);
        populatorList.setLayout(new MigLayout("", "[fill,grow]", "fill"));
        populatorScroll.setViewportView(populatorList);

        populatorSelectors = new HashMap<>();
        int i = 0;
        for (String pop : populatorParams.keySet()){
            JCheckBox check = new JCheckBox(pop);
            check.setOpaque(false);
            check.addActionListener((a) -> {
                if (isPopEditorOpen.get(pop)){
                    check.setSelected(false);
                    return;
                }
                if (check.isSelected()){
                    populatorCreation(pop); //might be weird if pop is updating
                }
                else {
                    removePopulator(pop);
                }
                resetSubmission();
            });
            populatorList.add(check, "cell 0 "+i);
            populatorSelectors.put(pop, check);
            isPopEditorOpen.put(pop, false);
            i++;
        }

        goBtn = new JButton("Generate");
        goBtn.addActionListener((e) -> new FreeThread(0, this::processGo, 1, "genEnv"));
        goBtn.setEnabled(false);
        contentPane.add(goBtn, BorderLayout.SOUTH);

        setSize(700, 250);
        setMinimumSize(new Dimension(350, 150));
    }

    private void populatorCreation(String pop){
        isPopEditorOpen.put(pop, true);
        populatorSelectors.get(pop).setSelected(false);
        PopulatorCreationWindow window = PopulatorCreationWindow.newPopulatorCreationWindow(platform)
                .setName(pop)
                .setParamterList(populatorParams.get(pop))
                .setReportAction((config) -> populatorConfigs.put(pop, config))
                .build();
        window.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                isPopEditorOpen.put(pop, false);
                populatorSelectors.get(pop).setSelected(populatorConfigs.containsKey(pop));
            }
        });
        spawnframe(window);
    }

    private void removePopulator(String pop){
        if (populatorConfigs.containsKey(pop)){
            populatorConfigs.remove(pop);
        }
    }

    private void updateGoEnabled(){
        goBtn.setEnabled(generatorConfig.isPresent());
    }

    private void resetSubmission(){
        enviro = Optional.empty();
        goBtn.setText("Generate");
        if (display.isPresent()){
            display.get().doDefaultCloseAction();
            display = Optional.empty();
        }
    }

    private void processGo(){
        if (goBtn.getText().contains(".")){
            return;
        }
        if (enviro.isPresent()){
            startAnimation();
            File tempFile = saveTemp(enviro.get());
            stopAnimation();
            reportAction.registerEnvironment(tempFile);
            doDefaultCloseAction();
        }
        else {
            try {
                startAnimation();
                enviro = Optional.of(generateMap());
                stopAnimation();
                EnvironmentDisplay display = FrameRegistry.getEnvironmentDisplay(platform).newInstance();
                display.setEnvironment(enviro.get());
                this.display = Optional.of(display);
                spawnframe(display);
                goBtn.setText("Use Environment");
            }
            catch (IllegalAccessException | InstantiationException e) {
                LOG.log(Level.ERROR, "Failed to create new Environment", e);
            }
        }
    }

    private void startAnimation(){
        goBtn.setText(".");
        waitingAnimation = new FreeThread(1000, this::animateWaiting, FreeThread.FOREVER, "env_animate", true);
    }

    private void stopAnimation(){
        waitingAnimation.Stop();
        SoundPlayer.playSound(SpearsSound.NEW);
    }

    private void animateWaiting(){
        String cur = goBtn.getText();
        if (cur.length() > 18){
            goBtn.setText(".");
        }
        else {
            goBtn.setText(cur + " . .");
        }
    }

    private PlatformEnvironment generateMap() throws IllegalAccessException, InstantiationException {
        LOG.log(Level.INFO, "Generating new Environment for " + platform);
        UiFactory.getDesktop().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        PlatformEnvironment environment = EnvironmentRegistry.getEnvironment(platform).newInstance();
        PlatformEnvironment.MapBuilder builder = environment.generateNewMap(EnvironmentRegistry.getModifier(platform, generatorConfig.get().name).newInstance(), generatorConfig.get().params);
        for (ModifierConfig config : modList.getItems()){
            builder.addMapModifier(EnvironmentRegistry.getModifier(platform, config.name).newInstance(), config.params);
        }
        for (String pop : populatorConfigs.keySet()){
            PopulatorConfig config = populatorConfigs.get(pop);
            builder.addPopulator(EnvironmentRegistry.getPopulator(platform, pop).newInstance(), config.params);
        }
        builder.generate();
        UiFactory.getDesktop().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        return environment;
    }

    private File saveTemp(PlatformEnvironment environment){
        Random rnd = new Random();
        File tempFile;
        do {
            tempFile = new File("Temp/temp_"+rnd.nextInt(9999));
        } while (tempFile.exists());
        tempFile = EnvironmentIO.appendSuffix(platform, tempFile);
        EnvironmentIO.saveEnvironment(environment, tempFile);
        return tempFile;
    }

    private void spawnframe(EmbeddedFrame frame){
        spawnedFrames.add(frame);
        UiFactory.getDesktop().add(frame);
    }

    static Builder newEnvironmentSetupWindow(String platform){
        return new Builder(platform);
    }

    static class Builder {
        private EnvironmentSetupWindow window;

        private Builder(String platform){
            window = new EnvironmentSetupWindow();
            window.platform = platform;
        }

        Builder setGeneratorsMap(Map<String, List<String>> map){
            window.generatorParams = map;
            return this;
        }

        Builder setModifiersMap(Map<String, List<String>> map){
            window.modifierParams = map;
            return this;
        }

        Builder setPopulatorMap(Map<String, List<String>> map){
            window.populatorParams = map;
            return this;
        }

        Builder setReportAction(ReportEnvironment report){
            window.reportAction = report;
            return this;
        }

        EnvironmentSetupWindow build(){
            if (window.generatorParams == null
                    || window.modifierParams == null
                    || window.populatorParams == null
                    || window.reportAction == null){
                throw new IllegalStateException("Builder is not fully initialized");
            }
            window.initialize();
            return window;
        }

    }

}

