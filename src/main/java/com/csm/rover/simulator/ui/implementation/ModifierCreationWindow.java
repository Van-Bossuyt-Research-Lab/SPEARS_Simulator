package com.csm.rover.simulator.ui.implementation;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ModifierCreationWindow extends EmbeddedFrame {

    private JPanel contentPane;
    private JComboBox<String> optionCombo;
    private ParameterInput paramTable;
    private JButton submitBtn;

    private String platform;
    private Optional<Map<String, List<String>>> options;
    private Optional<String> singleName;
    private Optional<List<String>> singleParams;
    private Optional<ReportModifier> reportAction = Optional.empty();

    private ModifierCreationWindow(){}

    private void initialize(){
        setTitle("New Modifier");
        setVisible(true);
        setSize(200, 200);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        paramTable = new ParameterInput();
        paramTable.addInputChangeListener((i) -> updateGoEnabled());
        contentPane.add(paramTable, BorderLayout.CENTER);

        if (options.isPresent()){
            optionCombo = new JComboBox<>();
            optionCombo.setModel(new DefaultComboBoxModel<>(options.get().keySet().toArray(new String[options.get().size()])));
            optionCombo.setSelectedIndex(-1);
            optionCombo.setEditable(false);
            optionCombo.addItemListener((i) -> {
                paramTable.setOptions(options.get().get((String)optionCombo.getSelectedItem()));
                updateGoEnabled();
            });
            contentPane.add(optionCombo, BorderLayout.NORTH);
        }
        else if (singleName.isPresent() && singleParams.isPresent()) {
            JLabel title = new JLabel(singleName.get());
            title.setPreferredSize(new Dimension(0, (int)title.getPreferredSize().getHeight()+10));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            contentPane.add(title, BorderLayout.NORTH);
            paramTable.setOptions(singleParams.get());
        }

        submitBtn = new JButton("Submit");
        submitBtn.setEnabled(false);
        submitBtn.addActionListener((a) -> report());
        contentPane.add(submitBtn, BorderLayout.SOUTH);
    }

    private void updateGoEnabled(){
        submitBtn.setEnabled(paramTable.isComplete());
    }

    private void report(){
        if (reportAction.isPresent()){
            reportAction.get().reportModifier(new ModifierConfig(platform, singleName.isPresent() ? singleName.get() : (String)optionCombo.getSelectedItem(), paramTable.getParameters()));
        }
        doDefaultCloseAction();
    }

    static Builder newModifierCreationWindow(String platform){
        return new Builder(platform);
    }

    static class Builder {
        private ModifierCreationWindow window;

        private Builder(String platform){
            window = new ModifierCreationWindow();
            window.platform = platform;
        }

        Builder2 setSingleInstance(String name, List<String> params){
            window.singleName = Optional.of(name);
            window.singleParams = Optional.of(params);
            window.options = Optional.empty();
            return new Builder2(window);
        }

        Builder2 setOptionMap(Map<String, List<String>> map){
            window.options = Optional.of(map);
            window.singleName = Optional.empty();
            window.singleParams = Optional.empty();
            return new Builder2(window);
        }
    }

    static class Builder2 {
        private ModifierCreationWindow window;

        private Builder2(ModifierCreationWindow window){
            this.window = window;
        }

        Builder2 setReportAction(ReportModifier report){
            window.reportAction = Optional.of(report);
            return this;
        }

        ModifierCreationWindow build(){
            window.initialize();
            return window;
        }
    }

}

class ModifierConfig {
    final String type;
    final String name;
    final Map<String, Double> params;

    ModifierConfig(String type, String name, Map<String, Double> params){
        this.type = type;
        this.name = name;
        this.params = params;
    }

    @Override
    public String toString(){
        return name;
    }
}

interface ReportModifier {
    void reportModifier(ModifierConfig mod);
}
