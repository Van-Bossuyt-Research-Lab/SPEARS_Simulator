package com.csm.rover.simulator.map.display;

import com.csm.rover.simulator.map.io.TerrainMapWriter;
import com.csm.rover.simulator.objects.io.ImageFileFilter;
import com.csm.rover.simulator.objects.io.MapFileFilter;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class MapOptionsMenu extends JPopupMenu{

    private LandMapPanel mapPanel;

    private JRadioButtonMenuItem rdbtnmntmShowTargets;
    private JRadioButtonMenuItem rdbtnmntmShowHazards;

    public MapOptionsMenu(LandMapPanel mapPanel){
        this.mapPanel = mapPanel;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setVisible(false);
            }
        });
        initialize();
    }

    private void initialize() {
        JMenuItem mntmShowFocusedRover = new JMenuItem("Show Focused Rover");
        mntmShowFocusedRover.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                mapPanel.centerOnFocusedRover();
            }
        });
        add(mntmShowFocusedRover);

        JMenuItem mntmSaveMapFile = new JMenuItem("Save Map File");
        mntmSaveMapFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser finder = new JFileChooser();
                finder.setFileFilter(new MapFileFilter());
                finder.setApproveButtonText("Save");
                int option = finder.showSaveDialog(getParent());
                if (option == JFileChooser.APPROVE_OPTION) {
                    TerrainMapWriter.saveMap(mapPanel.heightMap, new File(finder.getSelectedFile().getAbsolutePath() + ".map"));
                }
            }
        });
        add(mntmSaveMapFile);

        JMenuItem mntmSaveMapImage = new JMenuItem("Save Map Image");
        mntmSaveMapImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser finder = new JFileChooser();
                finder.setFileFilter(new ImageFileFilter());
                finder.setApproveButtonText("Save");
                int option = finder.showSaveDialog(getParent());
                if (option == JFileChooser.APPROVE_OPTION) {
                    TerrainMapWriter.SaveImage(mapPanel.heightMap, finder.getSelectedFile().getAbsolutePath(), rdbtnmntmShowTargets.isSelected(), rdbtnmntmShowHazards.isSelected(), false);
                }
            }
        });
        add(mntmSaveMapImage);

        rdbtnmntmShowTargets = new JRadioButtonMenuItem("Show Targets");
        rdbtnmntmShowTargets.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPanel.mapPanel.setTargetsVisible(rdbtnmntmShowTargets.isSelected());
            }
        });
        rdbtnmntmShowTargets.setSelected(true);
        add(rdbtnmntmShowTargets);

        rdbtnmntmShowHazards = new JRadioButtonMenuItem("Show Hazards");
        rdbtnmntmShowHazards.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPanel.mapPanel.setHazardsVisible(rdbtnmntmShowHazards.isSelected());
            }
        });
        rdbtnmntmShowHazards.setSelected(true);
        add(rdbtnmntmShowHazards);
    }

}
