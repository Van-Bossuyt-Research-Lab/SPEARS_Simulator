package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.io.PlatformConfig;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class PlatformSetupWindow extends EmbeddedFrame {

    private JPanel contentPane;
    private JComboBox<String> physicsCombo, codeCombo;
    private JButton addBtn;
    private ParameterInput physicsParams, codeParams;

    private String platform;
    private JTextField nameTxt;
    private Map<String, List<String>> codeModels, physicsModels;
    private ReportPlatform reportAction;
    private boolean manual_name = false;

    private PlatformSetupWindow(){}

    private void initialize(){
        setTitle("New " + platform);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        JTextField dump = new JTextField();
        dump.setSize(0, 0);
        dump.setMaximumSize(dump.getSize());
        dump.setVisible(false);
        contentPane.add(dump, BorderLayout.WEST);

        nameTxt = new JTextField("[Name]");
        nameTxt.setEditable(false);
        nameTxt.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        nameTxt.setHorizontalAlignment(SwingConstants.CENTER);
        nameTxt.setFont(FontFunctions.bigger(FontFunctions.bold(nameTxt.getFont())));
        nameTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE
                        || e.getKeyChar() == KeyEvent.VK_ENTER
                        || e.getKeyChar() == KeyEvent.VK_TAB){
                    revokeNameFocus();
                }
            }
        });
        nameTxt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                nameTxt.setEditable(true);
                nameTxt.setSelectionStart(0);
                nameTxt.setSelectionEnd(nameTxt.getText().length());
                manual_name = true;
            }
        });
        nameTxt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                revokeNameFocus();
            }
        });
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
        physicsCombo.addItemListener((ItemEvent e) -> {
            physicsParams.setOptions(physicsModels.get((String)physicsCombo.getSelectedItem()));
            updateIsReady();
        });
        insertPnl.add(physicsCombo, "cell 0 0");

        codeCombo = new JComboBox<>();
        codeCombo.setEditable(false);
        codeCombo.setModel(new DefaultComboBoxModel<>(codeModels.keySet().toArray(new String[codeModels.size()])));
        codeCombo.setSelectedIndex(-1);
        codeCombo.setMaximumSize(new Dimension(9000, (int)codeCombo.getPreferredSize().getHeight()));
        codeCombo.addItemListener((ItemEvent e) -> {
            codeParams.setOptions(codeModels.get((String)codeCombo.getSelectedItem()));
            if (!manual_name){
                nameTxt.setText((String)codeCombo.getSelectedItem());
            }
            updateIsReady();
        });
        insertPnl.add(codeCombo, "cell 0 1");

        physicsParams = new ParameterInput();
        physicsParams.addInputChangeListener((e) -> updateIsReady());
        insertPnl.add(physicsParams, "cell 1 0");

        codeParams = new ParameterInput();
        codeParams.addInputChangeListener((e) -> updateIsReady());
        insertPnl.add(codeParams, "cell 1 1");

        addBtn = new JButton("Add");
        addBtn.setEnabled(false);
        addBtn.addActionListener((ActionEvent e) -> reportPlatform());
        contentPane.add(addBtn, BorderLayout.SOUTH);

        setSize(350, 250);
        setMinimumSize(getSize());
        this.setVisible(true);
    }

    private void revokeNameFocus(){
        nameTxt.setEditable(false);
        nameTxt.setSelectionStart(0);
        nameTxt.setSelectionEnd(0);
        nameTxt.setCursor(new Cursor(Cursor.TEXT_CURSOR));
    }

    private void updateIsReady(){
        addBtn.setEnabled(
                physicsCombo.getSelectedIndex() >= 0
                && codeCombo.getSelectedIndex() >= 0
                && physicsParams.isComplete()
                && codeParams.isComplete());
    }

    private void loadExistingPlatform(PlatformConfig platform){
        physicsCombo.setSelectedItem(platform.getPhysicsModelName());
        codeCombo.setSelectedItem(platform.getAutonomousModelName());
        physicsParams.fillInFields(platform.getPhysicsModelParameters());
        codeParams.fillInFields(platform.getAutonomousModelParameters());
        addBtn.setText("Update");
        nameTxt.setText(platform.getScreenName().substring(0, platform.getScreenName().lastIndexOf(' ')));
        manual_name = !nameTxt.getText().equals(codeCombo.getSelectedItem());
    }

    private void reportPlatform(){
        PlatformConfig config = PlatformConfig.builder()
                .setType(platform)
                .setAutonomousModel((String)codeCombo.getSelectedItem(), codeParams.getParameters())
                .setPhysicsModel((String)physicsCombo.getSelectedItem(), physicsParams.getParameters())
                .setScreenName(nameTxt.getText() + " #")
                .setID(platform.charAt(0) + " #")
                .build();
        reportAction.registerNewPlatform(config);
        doDefaultCloseAction();
    }

    static Builder newSetupPanel(String platform){
        return new Builder(platform);
    }

    static class Builder {
        private PlatformSetupWindow panel;
        private Optional<PlatformConfig> existing;

        private Builder(String platform){
            panel = new PlatformSetupWindow();
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

        PlatformSetupWindow build(){
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
