package com.csm.rover.simulator.ui.implementation;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TypeDisplayPanel extends JPanel {

	public TypeDisplayPanel(String platform){
//		setLayout(new MigLayout("", "[grow,fill][grow,fill][grow,fill]", "[][][][][grow,fill][]"));
//
//		JLabel lblNewLabel_3 = new JLabel("Platform");
//		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
//		add(lblNewLabel_3, "cell 0 0 3 1");
//
//		JLabel lblNewLabel = new JLabel("Environemnt:");
//		add(lblNewLabel, "cell 0 1 3 1");
//
//		JLabel lblNewLabel_1 = new JLabel("Set Environment");
//		add(lblNewLabel_1, "cell 0 2 3 1");
//
//		JLabel lblNewLabel_2 = new JLabel("Platforms:");
//		add(lblNewLabel_2, "cell 0 3 3 1");
//
//		JPanel panel = new JPanel();
//		add(panel, "cell 0 4 3 1,grow");
//
//		JButton btnNewButton = new JButton("Add");
//		add(btnNewButton, "cell 0 5");
//
//		JButton btnNewButton_1 = new JButton("Edit");
//		add(btnNewButton_1, "cell 1 5");
//
//		JButton btnNewButton_2 = new JButton("Remove");
//		add(btnNewButton_2, "cell 2 5");
//
	}
	
	private void a(String platform){

        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        JPanel envi = new JPanel();
        envi.setOpaque(false);
        envi.setLayout(new GridLayout(3, 1, 2, 0));
        this.add(envi, BorderLayout.NORTH);

        JLabel title = new JLabel(platform.toUpperCase());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, title.getFont().getSize()+2));
        envi.add(title, 0, 0);

        JLabel enviroTitle = new JLabel("Environment:");
        envi.add(enviroTitle, 0, 1);

        JLabel enviroSet = new JLabel("<html><u>Set Up Environment...</u></html>");
        enviroSet.setForeground(Color.BLUE);
        enviroSet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        envi.add(enviroSet, 0, 2);

        JPanel plat = new JPanel();
        plat.setOpaque(false);
        plat.setLayout(new BorderLayout());
        this.add(plat, BorderLayout.CENTER);

        JLabel platformTitle = new JLabel("Platforms:");
        plat.add(platformTitle, BorderLayout.NORTH);

        ZList<String> platformTable = new ZList<>();
        platformTable.setPreferredSize(new Dimension(0, 200));
        plat.add(platformTable, BorderLayout.CENTER);

        JPanel platBtns = new JPanel();
        platBtns.setOpaque(false);
        platBtns.setLayout(new GridLayout(1, 3));
        plat.add(platBtns, BorderLayout.SOUTH);

        JButton add = new JButton("Add");
        add.addActionListener((ActionEvent e) -> platformTable.addValue("hi"));
        platBtns.add(add, 0, 0);

        JButton edit = new JButton("Edit");
        platBtns.add(edit, 0, 1);

        JButton remove = new JButton("Remove");
        platBtns.add(remove, 0, 2);

    }
	
}
