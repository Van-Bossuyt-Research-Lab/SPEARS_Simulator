package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.io.EnvrioFileFilter;
import com.csm.rover.simulator.objects.io.PlatformConfig;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

class TypeDisplayPanel extends JPanel {

    private JPanel enviroPnl;

    private File environmentFile;

    private final String platform;
    private final JPopupMenu enviroEditPopup;
    private final ZList<PlatformConfig> platformTable;

    private Map<String, List<String>> codeModelParams, physicsModelParams,
            mapGeneratorParams, mapModifierParams, mapPopulatorParams;

	TypeDisplayPanel(String platform){
        this.platform = platform;
        codeModelParams = new HashMap<>();
        physicsModelParams = new HashMap<>();
        mapGeneratorParams = new HashMap<>();
        mapModifierParams = new HashMap<>();
        mapPopulatorParams = new HashMap<>();
        this.setOpaque(false);
		setLayout(new MigLayout("", "[grow,fill][grow,fill][grow,fill]", "[][][][][grow,fill][]"));

        JLabel title = new JLabel(platform.toUpperCase());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, title.getFont().getSize()+2));
        this.add(title, "cell 0 0 3 1");

        JLabel enviroTitle = new JLabel("Environment:");
        this.add(enviroTitle, "cell 0 1 3 1");

        enviroPnl = new JPanel();
        enviroPnl.setOpaque(false);
        enviroPnl.setLayout(new MigLayout("", "[grow,fill][grow,fill]", "[]"));
        this.add(enviroPnl, "cell 0 2 3 1");

        JLabel enviroSet = new JLabel(underline("Set Up..."));
        enviroSet.setForeground(Color.BLUE);
        enviroSet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        enviroSet.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                createNewEnvironment();
            }
        });
        enviroPnl.add(enviroSet, "cell 0 0");

        JLabel enviroLoad = new JLabel(underline("Load File..."));
        enviroLoad.setForeground(Color.BLUE);
        enviroLoad.setCursor(new Cursor(Cursor.HAND_CURSOR));
        enviroLoad.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Optional<File> path = loadFromFile();
                if (path.isPresent()) {
                    setEnvironment(path.get(), path.get().getName());
                }
            }
        });
        enviroPnl.add(enviroLoad, "cell 1 0");

        JLabel platformTitle = new JLabel("Platforms:");
        this.add(platformTitle, "cell 0 3 3 1");

        platformTable = new ZList<>();
        platformTable.setPreferredSize(new Dimension(0, 200));
        this.add(platformTable, "cell 0 4 3 1,grow");

        JButton add = new JButton("Add");
        add.addActionListener((ActionEvent e) -> {
            UiFactory.getDesktop().add(PlatformSetupWindow.newSetupPanel(platform)
                .setCodeModelMap(codeModelParams)
                .setPhysicsModelMap(physicsModelParams)
                .setReportAction((PlatformConfig config) ->
                        platformTable.addValue(config, config.getScreenName()))
            .build());
        });
        this.add(add, "cell 0 5");

        JButton edit = new JButton("Edit");
        edit.addActionListener((ActionEvent e) -> {
            final int currentloc = platformTable.getSelectedIndex();
            UiFactory.getDesktop().add(PlatformSetupWindow.newSetupPanel(platform)
                    .setCodeModelMap(codeModelParams)
                    .setPhysicsModelMap(physicsModelParams)
                    .setReportAction((PlatformConfig config) -> {
                        platformTable.removeValue(currentloc);
                        platformTable.addValue(config, config.getScreenName());
                    })
                    .loadExisting(platformTable.getItemAt(currentloc))
                    .build());
        });
        this.add(edit, "cell 1 5");

        JButton remove = new JButton("Remove");
        remove.addActionListener((ActionEvent e) -> platformTable.removeValue(platformTable.getSelectedIndex()));
        this.add(remove, "cell 2 5");

        enviroEditPopup = new JPopupMenu();
        enviroEditPopup.setVisible(false);
        enviroEditPopup.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (!enviroEditPopup.contains(e.getX(), e.getY())){
                    enviroEditPopup.setVisible(false);
                }
            }
        });

        JMenuItem configure = new JMenuItem("New Configuration");
        enviroEditPopup.add(configure);

        JMenuItem load = new JMenuItem("Select New File");
        load.addActionListener((ActionEvent e) -> {
            Optional<File> file = loadFromFile();
            if (file.isPresent()) {
                setEnvironment(file.get(), file.get().getName());
            }
        });
        enviroEditPopup.add(load);

	}

    void addCodeModel(String name, List<String> params){
        codeModelParams.put(name, params);
    }

    void addPhysicsModel(String name, List<String> params){
        physicsModelParams.put(name, params);
    }

    void addMapGenerator(String name, List<String> params){
        mapGeneratorParams.put(name, params);
    }

    void addMapModifier(String name, List<String> params){
        mapModifierParams.put(name, params);
    }

    void addMapPopulator(String name, List<String> params){
        mapPopulatorParams.put(name, params);
    }

    private void setEnvironment(File path, String name){
        enviroPnl.removeAll();
        environmentFile = path;

        JLabel enviroLbl = new JLabel(underline(name));
        enviroLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3){
                    enviroEditPopup.show(enviroLbl, e.getX()-2, e.getY()-2);
                }
            }
        });
        enviroPnl.add(enviroLbl, "cell 0 0 2 1");
        repaint();
    }

    private void createNewEnvironment(){
        UiFactory.getDesktop().add(EnvironmentSetupWindow.newEnvironmentSetupWindow(platform)
                .setGeneratorsMap(mapGeneratorParams)
                .setModifiersMap(mapModifierParams)
                .setPopulatorMap(mapPopulatorParams)
                .setReportAction((a) -> {})
                .build());
    }

    private Optional<File> loadFromFile(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose An Environment File");
        chooser.setCurrentDirectory(UiConfiguration.getFolder(platform));
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new EnvrioFileFilter(platform));
        if (chooser.showOpenDialog(UiFactory.getDesktop()) == JFileChooser.APPROVE_OPTION) {
            UiConfiguration.changeFolder(platform, chooser.getCurrentDirectory());
            return Optional.of(chooser.getSelectedFile());
        }
        else {
            return Optional.empty();
        }
    }

    private String underline(String s){
        return String.format("<html><u>%s</u></html>", s);
    }

}
