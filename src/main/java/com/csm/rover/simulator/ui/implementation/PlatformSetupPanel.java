package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.io.PlatformConfig;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class PlatformSetupPanel extends EmbeddedFrame {

    private JPanel contentPane;
    private JComboBox<String> physicsCombo, codeCombo;
    private JButton addBtn;
    private ParameterInput physicsParams, codeParams;

    private String platform;
    private JTextField nameTxt;
    private Map<String, List<String>> codeModels, physicsModels;
    private ReportPlatform reportAction;

    private PlatformSetupPanel(){}

    private void initialize(){
        setTitle("New " + platform);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        nameTxt = new JTextField("[Name]");
        nameTxt.setEditable(false);
        nameTxt.setHorizontalAlignment(SwingConstants.CENTER);
        nameTxt.setFont(new Font(nameTxt.getFont().getName(), Font.BOLD, nameTxt.getFont().getSize()+2));
        contentPane.add(nameTxt, BorderLayout.NORTH);

        JPanel insertPnl = new JPanel();
        insertPnl.setOpaque(false);
        insertPnl.setLayout(new MigLayout("", "[fill,grow][fill,grow]", "[fill,grow][fill,grow]"));
        contentPane.add(insertPnl, BorderLayout.CENTER);

        physicsCombo = new JComboBox<>();
        physicsCombo.setEditable(false);
        physicsCombo.setModel(new DefaultComboBoxModel<>(physicsModels.keySet().toArray(new String[physicsModels.size()])));
        physicsCombo.setSelectedIndex(-1);
        physicsCombo.setMaximumSize(new Dimension(9000, (int)physicsCombo.getPreferredSize().getHeight()));
        physicsCombo.addItemListener((ItemEvent e) -> physicsParams.setOptions(physicsModels.get((String)physicsCombo.getSelectedItem())));
        insertPnl.add(physicsCombo, "cell 0 0");

        codeCombo = new JComboBox<>();
        codeCombo.setEditable(false);
        codeCombo.setModel(new DefaultComboBoxModel<>(codeModels.keySet().toArray(new String[codeModels.size()])));
        codeCombo.setSelectedIndex(-1);
        codeCombo.setMaximumSize(new Dimension(9000, (int)codeCombo.getPreferredSize().getHeight()));
        codeCombo.addItemListener((ItemEvent e) -> codeParams.setOptions(codeModels.get((String)codeCombo.getSelectedItem())));
        insertPnl.add(codeCombo, "cell 0 1");

        physicsParams = new ParameterInput();
        insertPnl.add(physicsParams, "cell 1 0");

        codeParams = new ParameterInput();
        insertPnl.add(codeParams, "cell 1 1");

        addBtn = new JButton("Add");
        addBtn.addActionListener((ActionEvent e) -> reportPlatform());
        contentPane.add(addBtn, BorderLayout.SOUTH);

        setSize(getWidth(), 100);
        this.setVisible(true);
    }

    private void loadExistingPlatform(PlatformConfig platform){
        physicsCombo.setSelectedItem(platform.getPhysicsModelName());
        codeCombo.setSelectedItem(platform.getAutonomousModelName());
        physicsParams.fillInFields(platform.getPhysicsModelParameters());
        codeParams.fillInFields(platform.getAutonomousModelParameters());
        addBtn.setText("Update");
    }

    private void reportPlatform(){
        PlatformConfig config = PlatformConfig.builder()
                .setAutonomousModel((String)codeCombo.getSelectedItem(), codeParams.getParameters())
                .setPhysicsModel((String)physicsCombo.getSelectedItem(), physicsParams.getParameters())
                .setScreenName(nameTxt.getText())
                .build();
        reportAction.registerNewPlatform(config);
    }

    static Builder newSetupPanel(String platform){
        return new Builder(platform);
    }

    static class Builder {
        private PlatformSetupPanel panel;
        private Optional<PlatformConfig> existing;

        private Builder(String platform){
            panel = new PlatformSetupPanel();
            panel.platform = platform;
            existing = Optional.empty();
        }

        Builder setCodeModelMap(Map<String, List<String>> map){
            panel.codeModels = map;
            return this;
        }

        Builder setPhysicsModelMap(Map<String, List<String>> map){
            panel.physicsModels = map;
            return this;
        }

        Builder setReportAction(ReportPlatform report){
            panel.reportAction = report;
            return this;
        }

        Builder loadExisting(PlatformConfig edit){
            existing = Optional.of(edit);
            return this;
        }

        PlatformSetupPanel build(){
            if (panel.physicsModels == null || panel.codeModels == null || panel.reportAction == null){
                throw new IllegalStateException("Builder was not fully initialized");
            }
            panel.initialize();
            if (existing.isPresent()){
                panel.loadExistingPlatform(existing.get());
            }
            return panel;
        }
    }

}

interface ReportPlatform {
    void registerNewPlatform(PlatformConfig plat);
}
