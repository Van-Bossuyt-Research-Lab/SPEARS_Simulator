package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class EnvironmentSetupWindow extends EmbeddedFrame {

    private JPanel contentPane;
    private JComboBox<String> generatorCombo;
    private ZList<EnvironmentModifier> modList;
    private Map<String, JCheckBox> populatorSelectors;
    private JButton goBtn;

    private String platform;
    private ReportEnvironment reportAction;
    private Map<String, List<String>> generatorParams, modifierParams, populatorParams;
    private Optional<File> mapFile = Optional.empty();

    private EnvironmentSetupWindow(){}

    private void initialize(){
        setTitle("New "+platform+" Environment");
        setVisible(true);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        JPanel mainPnl = new JPanel();
        mainPnl.setOpaque(false);
        mainPnl.setLayout(new MigLayout("", "[fill,grow][fill,grow][fill,grow]", "[fill][fill][fill,grow]2[fill]"));
        contentPane.add(mainPnl, BorderLayout.CENTER);

        JLabel modTitle = new JLabel("Modifiers");
        modTitle.setFont(new Font(modTitle.getFont().getName(), Font.BOLD, modTitle.getFont().getSize()+2));
        mainPnl.add(modTitle, "cell 0 0");

        generatorCombo = new JComboBox<>();
        generatorCombo.setModel(new DefaultComboBoxModel<>(generatorParams.keySet().toArray(new String[generatorParams.size()])));
        generatorCombo.setSelectedIndex(-1);
        generatorCombo.setEditable(false);
        generatorCombo.addItemListener((e) -> {
            if (generatorCombo.getSelectedIndex() != -1){
                String gen = (String)generatorCombo.getSelectedItem();
                UiFactory.getDesktop().add(new ModifierCreationWindow(gen, generatorParams.get(gen)));
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
        modAdd.addActionListener((a) -> {
            UiFactory.getDesktop().add(new ModifierCreationWindow(modifierParams));
        });
        modButtonPnl.add(modAdd, "cell 0 0");

        JButton modUp = new JButton();
        modUp.setIcon(ImageFunctions.getImage("/gui/arrow_up.png", 15, 15));
        modUp.addActionListener((a) -> {
            int start = modList.getSelectedIndex();
            if (start == 0){
                return;
            }
            String label = modList.getLabelAt(start);
            EnvironmentModifier mod = modList.getSelectedItem();
            modList.removeValue(mod);
            modList.addValue(mod, label, start-1);
        });
        modButtonPnl.add(modUp, "cell 1 0");

        JButton modDown = new JButton();
        modDown.setIcon(ImageFunctions.getImage("/gui/arrow_down.png", 15, 15));
        modDown.addActionListener((a) -> {
            int start = modList.getSelectedIndex();
            if (start == modList.getItemCount()-1){
                return;
            }
            String label = modList.getLabelAt(start);
            EnvironmentModifier mod = modList.getSelectedItem();
            modList.removeValue(mod);
            modList.addValue(mod, label, start+1);
        });
        modButtonPnl.add(modDown, "cell 2 0");

        JButton modRemove = new JButton("Remove");
        modRemove.addActionListener((a) -> {
            modList.removeValue(modList.getSelectedItem());
            resetSubmission();
        });
        modButtonPnl.add(modRemove, "cell 3 0");

        JLabel popTitle = new JLabel("Populators");
        popTitle.setFont(new Font(popTitle.getFont().getName(), Font.BOLD, popTitle.getFont().getSize()+2));
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
                if (check.isSelected()){
                    populatorCreation(pop); //might be weird if pop is updating
                }
                else {
                    removePopulator(pop);
                }
            });
            populatorList.add(check, "cell 0 "+i);
            populatorSelectors.put(pop, check);
            i++;
        }

        goBtn = new JButton("Generate");
        goBtn.addActionListener((e) -> processGo());
        goBtn.setEnabled(false);
        contentPane.add(goBtn, BorderLayout.SOUTH);

        setSize(700, 250);
        setMinimumSize(new Dimension(350, 150));
    }

    private void populatorCreation(String pop){
        //TODO poulator creation

        //if actually changed
        resetSubmission();
    }

    private void removePopulator(String pop){
        //TODO
        resetSubmission();
    }

    private void updateGoEnabled(){
        goBtn.setEnabled(generatorCombo.getSelectedIndex() != -1);
    }

    private void resetSubmission(){
        mapFile = Optional.empty();
        goBtn.setText("Generate");
    }

    private void processGo(){
        if (mapFile.isPresent()){
            reportAction.registerEnvironment(mapFile.get());
            //TODO close preview
            doDefaultCloseAction();
        }
        else {
            mapFile.of(generateMap());
            goBtn.setText("Use Environment");
        }
    }

    private File generateMap(){
        //TODO generate and preview map
        return new File("");
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

interface ReportEnvironment {
    void registerEnvironment(File environmentFile);
}
