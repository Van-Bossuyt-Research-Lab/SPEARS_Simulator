package com.csm.rover.simulator.ui.implementation;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ModifierCreationWindow extends EmbeddedFrame {

    private JPanel contentPane;
    private ParameterInput paramTable;
    private JButton submitBtn;

    private Optional<Map<String, List<String>>> options;
    private Optional<String> singleName;
    private Optional<List<String>> singleParams;

    ModifierCreationWindow(String label, List<String> params){
        options = Optional.empty();
        singleName = Optional.of(label);
        singleParams = Optional.of(params);
        initialize();
    }

    ModifierCreationWindow(Map<String, List<String>> map){
        options = Optional.of(map);
        singleName = Optional.empty();
        singleParams = Optional.empty();
        initialize();
    }

    private void initialize(){
        setTitle("New Modifier");
        setVisible(true);
        setSize(100, 100);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        paramTable = new ParameterInput();
        paramTable.addInputChangeListener((i) -> updateGoEnabled());
        contentPane.add(paramTable, BorderLayout.CENTER);

        if (options.isPresent()){
            JComboBox<String> optionCombo = new JComboBox<>();
            optionCombo.setModel(new DefaultComboBoxModel<>(options.get().keySet().toArray(new String[options.get().size()])));
            optionCombo.setSelectedIndex(-1);
            optionCombo.setEditable(false);
            optionCombo.addItemListener((i) -> {
                paramTable.setOptions(options.get().get((String)optionCombo.getSelectedItem()));
                submitBtn.setEnabled(false);
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
        contentPane.add(submitBtn, BorderLayout.SOUTH);
    }

    private void updateGoEnabled(){
        submitBtn.setEnabled(paramTable.isComplete());
    }

}
