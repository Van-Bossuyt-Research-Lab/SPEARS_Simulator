package com.csm.rover.simulator.ui.implementation;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class PopulatorCreationWindow extends EmbeddedFrame {

    private JPanel contentPane;
    private ParameterInput paramTable;
    private JButton submitBtn;

    private String platform;
    private String name;
    private List<String> params;
    private Optional<ReportPopulator> reportAction = Optional.empty();

    private PopulatorCreationWindow(){}

    private void initialize(){
        setTitle("New Populator");
        setVisible(true);
        setSize(200, 200);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        paramTable = new ParameterInput();
        paramTable.addInputChangeListener((i) -> updateGoEnabled());
        contentPane.add(paramTable, BorderLayout.CENTER);

        JLabel title = new JLabel(name);
        title.setPreferredSize(new Dimension(0, (int)title.getPreferredSize().getHeight()+10));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(title, BorderLayout.NORTH);
        paramTable.setOptions(params);

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
            reportAction.get().reportPopulator(new PopulatorConfig(platform, name, paramTable.getParameters()));
        }
        doDefaultCloseAction();
    }

    static Builder newPopulatorCreationWindow(String platform){
        return new Builder(platform);
    }

    static class Builder {
        private PopulatorCreationWindow window;

        private Builder(String platform){
            window = new PopulatorCreationWindow();
            window.platform = platform;
        }

        Builder setName(String name){
            window.name = name;
            return this;
        }

        Builder setParamterList(List<String> list){
            window.params = list;
            return this;
        }

        Builder setReportAction(ReportPopulator report){
            window.reportAction = Optional.of(report);
            return this;
        }

        PopulatorCreationWindow build(){
            if (window.name == null || window.params == null){
                throw new IllegalStateException("Builder was not fully initialized");
            }
            window.initialize();
            return window;
        }
    }

}

class PopulatorConfig {
    final String type;
    final String name;
    final Map<String, Double> params;

    PopulatorConfig(String type, String name, Map<String, Double> params){
        this.type = type;
        this.name = name;
        this.params = params;
    }

    @Override
    public String toString(){
        return name;
    }
}

interface ReportPopulator {
    void reportPopulator(PopulatorConfig mod);
}
