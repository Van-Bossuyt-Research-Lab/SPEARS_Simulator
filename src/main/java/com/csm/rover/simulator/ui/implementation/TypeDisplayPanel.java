package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.io.EnvrioFileFilter;
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
import java.util.Optional;

class TypeDisplayPanel extends JPanel {

    private JPanel enviroPnl;

    private File environmentFile;

    private final String platform;

	TypeDisplayPanel(String platform){
        this.platform = platform;
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

        JLabel enviroSet = new JLabel("<html><u>Set Up...</u></html>");
        enviroSet.setForeground(Color.BLUE);
        enviroSet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        enviroPnl.add(enviroSet, "cell 0 0");

        JLabel enviroLoad = new JLabel("<html><u>Load File...</u></html>");
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

        ZList<String> platformTable = new ZList<>();
        platformTable.setPreferredSize(new Dimension(0, 200));
        this.add(platformTable, "cell 0 4 3 1,grow");

        JButton add = new JButton("Add");
        add.addActionListener((ActionEvent e) -> platformTable.addValue("hi"));
        this.add(add, "cell 0 5");

        JButton edit = new JButton("Edit");
        this.add(edit, "cell 1 5");

        JButton remove = new JButton("Remove");
        this.add(remove, "cell 2 5");

	}

    private void setEnvironment(File path, String name){
        enviroPnl.removeAll();
        environmentFile = path;

        JLabel enviroLbl = new JLabel(name);
        enviroPnl.add(enviroLbl, "cell 0 0 2 1");
        repaint();
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

}
