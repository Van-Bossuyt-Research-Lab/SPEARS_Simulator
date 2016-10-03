package com.csm.rover.simulator.ui.implementation;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

class TypeDisplayPanel extends JPanel {

    private JPanel enviroPnl;

    private String environmentPath;

	TypeDisplayPanel(String platform){
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
                File path = loadFromFile();
                setEnvironment(path.getAbsolutePath(), path.getName().substring(0, path.getName().lastIndexOf('.')));
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

    private void setEnvironment(String path, String name){
        enviroPnl.removeAll();
        environmentPath = path;

        JLabel enviroLbl = new JLabel(name);
        enviroPnl.add(enviroLbl, "cell 0 0 2 1");
    }

    private File loadFromFile(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose An Environment File");
        //chooser.setCurrentDirectory(create a field in the save file);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().substring(f.getName().indexOf('.')).equals("map");
            }

            @Override
            public String getDescription() {
                return "SPEARS Environment File";
            }
        });
        chooser.showOpenDialog(UiFactory.getDesktop());
        return chooser.getSelectedFile();
    }

}
