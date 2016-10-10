package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.environments.EnvironmentModifier;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

class EnvironmentSetupWindow extends EmbeddedFrame {

    private JPanel contentPane;
    private ZList<EnvironmentModifier> modList;
    private JButton goBtn;

    private String platform;
    private Map<String, List<String>> modifierParams, populatorParams;

    private EnvironmentSetupWindow(){}

    private void initialize(){
        setTitle("New "+platform+" Environment");
        setSize(100, 100);
        setVisible(true);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        JPanel mainPnl = new JPanel();
        mainPnl.setOpaque(false);
        mainPnl.setLayout(new MigLayout("", "[fill,grow][fill,grow]", "[fill][fill,grow][fill]"));
        contentPane.add(mainPnl, BorderLayout.CENTER);

        JLabel modTitle = new JLabel("Modifiers");
        modTitle.setFont(new Font(modTitle.getFont().getName(), Font.BOLD, modTitle.getFont().getSize()+2));
        mainPnl.add(modTitle, "cell 0 0");

        modList = new ZList<>();
        mainPnl.add(modList, "cell 0 1");

        JPanel modButtonPnl = new JPanel();
        modButtonPnl.setOpaque(false);
        modButtonPnl.setLayout(new MigLayout("", "[fill,grow][fill][fill][fill,grow]", "[fill]"));
        mainPnl.add(modButtonPnl, "cell 0 2");

        JButton modAdd = new JButton("Add");
        modButtonPnl.add(modAdd, "cell 0 0");

        JButton modUp = new JButton();
        modUp.setIcon(ImageFunctions.getMenuIcon("/gui/arrow_up.png"));
        modButtonPnl.add(modUp, "cell 1 0");

        JButton modDown = new JButton();
        modDown.setIcon(ImageFunctions.getMenuIcon("/gui/arrow_down.png"));
        modButtonPnl.add(modDown, "cell 2 0");

        JButton modRemove = new JButton("Remove");
        modButtonPnl.add(modRemove, "cell 3 0");

        JLabel popTitle = new JLabel("Modifiers");
        popTitle.setFont(new Font(popTitle.getFont().getName(), Font.BOLD, popTitle.getFont().getSize()+2));
        mainPnl.add(popTitle, "cell 1 0");

        JScrollPane populatorScroll = new JScrollPane();
        mainPnl.add(populatorScroll, "cell 1 1 1 2");

        JPanel populatorList = new JPanel();
        populatorList.setOpaque(false);
        populatorList.setLayout(new MigLayout("", "[fill,grow]", "fill"));
        populatorScroll.setViewportView(populatorList);

        for (String pop : populatorParams.keySet()){
            JCheckBox check = new JCheckBox(pop);
            populatorList.add(check);
        }
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

        Builder setModifiersMap(Map<String, List<String>> map){
            window.modifierParams = map;
            return this;
        }

        Builder setPopulatorMap(Map<String, List<String>> map){
            window.populatorParams = map;
            return this;
        }

        EnvironmentSetupWindow build(){
            window.initialize();
            return window;
        }

    }

}
