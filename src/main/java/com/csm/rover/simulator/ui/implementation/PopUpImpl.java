/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.ui.sound.SoundPlayer;
import com.csm.rover.simulator.ui.sound.SpearsSound;
import com.csm.rover.simulator.ui.visual.PopUp;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;

import static com.csm.rover.simulator.ui.visual.PopUp.Buttons.OK_CANCEL_OPTIONS;
import static com.csm.rover.simulator.ui.visual.PopUp.Options.*;

class PopUpImpl extends JDialog implements PopUp {

	private JLabel defaultText;
	private JButton[] optionBtns = new JButton[3];

	private Buttons currentConfig;
	private Options buttonClicked;

    private Optional<String> text = Optional.empty(), title = Optional.empty();

    PopUpImpl(){
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.getContentPane().setLayout(null);
		initialize();
	}
	
	private void initialize(){
		defaultText = new JLabel();
		defaultText.setHorizontalAlignment(SwingConstants.CENTER);
		defaultText.setVerticalAlignment(SwingConstants.CENTER);
		this.add(defaultText);
		int x = 0;
		while (x < optionBtns.length){
			optionBtns[x] = new JButton();
			optionBtns[x].setSize(80, 23);
			final int hold = x;
			optionBtns[x].addActionListener((ActionEvent e) -> buttonClick(hold));
			optionBtns[x].addKeyListener(new KeyAdapter(){
				@Override
				public void keyPressed(KeyEvent arg0) {
					if (arg0.getKeyCode() == 10){
						keyClicked();
					}
				}
			});
            optionBtns[x].addActionListener((ActionEvent e) -> SoundPlayer.playSound(SpearsSound.BEEP_LOW));
			this.add(optionBtns[x]);
			x++;
		}
	}
	
	private void keyClicked(){
		int x = 0;
		while (x < 3){
			if (optionBtns[x].isVisible()){
				optionBtns[x].doClick();
			}
			x++;
		}
	}

    @Override
    public PopUp setMessage(String text){
        this.text = Optional.of(text);
        return this;
    }

    @Override
    public PopUp setSubject(String title){
        this.title = Optional.of(title);
        return this;
    }

    @Override
	public Options showConfirmDialog(Buttons options){
		buttonClicked = null;
		this.setTitle(title.isPresent() ? title.get() : "");
		currentConfig = options;
		defaultText.setText(String.format("<html><center>%s</center></html>",  text.isPresent() ? text.get() : "").replaceAll("\n", "<br>"));
		this.setSize((int)(defaultText.getPreferredSize().getWidth() + 50), (int)(defaultText.getPreferredSize().getHeight() + 100));
		defaultText.setVisible(true);
		defaultText.setBounds(0, 0, this.getWidth(), (this.getHeight() - 30 - 2*optionBtns[0].getHeight()));
		placeButtons();
		openWindow();
		while (buttonClicked == null){
			try{
				Thread.sleep(300);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
		this.setVisible(false);
		return buttonClicked;
	}
	
	public int showOptionDialog(String[] options){
		buttonClicked = null;
		this.setTitle(title.isPresent() ? title.get() : "");
		this.remove(defaultText);
		this.setSize(250, 130);
		JLabel title1 = new JLabel(text.isPresent() ? text.get() : "Choose" + ":");
		title1.setBounds(20, 10, (int)title1.getPreferredSize().getWidth(), (int)title1.getPreferredSize().getHeight());
		this.add(title1);
		JComboBox<String> input3 = new JComboBox<>(options);
		input3.setBounds(20, title1.getY() + title1.getHeight() + 5, this.getWidth() - 40, 25);
		this.add(input3);
		currentConfig = OK_CANCEL_OPTIONS;
		placeButtons();
		openWindow();
		while (buttonClicked == null){
			try{
				Thread.sleep(300);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
		this.setVisible(false);
		this.remove(title1);
		this.remove(input3);
		this.add(defaultText);
		if (buttonClicked == OK_OPTION){
			return input3.getSelectedIndex();
		}
		else {
			return -1;
		}
	}
	
	public String showInputDialog(){
		buttonClicked = null;
		this.setTitle(title.isPresent() ? title.get() : "");
		currentConfig = OK_CANCEL_OPTIONS;
		defaultText.setText(String.format("<html><center>%s</center></html>", text.isPresent() ? text : "").replaceAll("\n", "<br>"));
		int width = (int)(defaultText.getPreferredSize().getWidth() + 50);
		if (width < 250){
			width = 250;
		}
		this.setSize(width, (int)(defaultText.getPreferredSize().getHeight() + 130));
		defaultText.setVisible(true);
		defaultText.setBounds(0, 0, this.getWidth(), (this.getHeight() - 30 - 2*optionBtns[0].getHeight() - 30));
		JTextField input = new JTextField();
		input.setFont(defaultText.getFont());
		input.setBounds(10, defaultText.getHeight() + 10, this.getWidth() - 27, 25);
		input.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == 10){
					int x = 0;
					while (x < 3){
						if (optionBtns[x].isVisible()){
							optionBtns[x].doClick();
							break;
						}
						x++;
					}
				}
			}
		});
		this.add(input);
		placeButtons();
		openWindow();
		while (buttonClicked == null){
			try{
				Thread.sleep(300);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
		this.setVisible(false);
		if (buttonClicked == OK_OPTION){
			return input.getText();
		}
		else {
			return "";
		}
	}
	
	private void placeButtons(){
		switch (currentConfig){
		case DEFAULT_OPTIONS:
			optionBtns[0].setVisible(false);
			optionBtns[1].setVisible(false);
			optionBtns[2].setText("OK");
			optionBtns[2].setLocation(getButtonX(1, 1), getButtonY());
			optionBtns[2].setVisible(true);
			break;
		case OK_CANCEL_OPTIONS:
			optionBtns[0].setVisible(false);
			optionBtns[1].setText("OK");
			optionBtns[1].setLocation(getButtonX(2, 1), getButtonY());
			optionBtns[1].setVisible(true);
			optionBtns[2].setText("Cancel");
			optionBtns[2].setLocation(getButtonX(2, 2), getButtonY());
			optionBtns[2].setVisible(true);
			break;
		case YES_NO_OPTIONS:
			optionBtns[0].setVisible(false);
			optionBtns[1].setText("Yes");
			optionBtns[1].setLocation(getButtonX(2, 1), getButtonY());
			optionBtns[1].setVisible(true);
			optionBtns[2].setText("No");
			optionBtns[2].setLocation(getButtonX(2, 2), getButtonY());
			optionBtns[2].setVisible(true);
			break;
		case YES_NO_CANCEL_OPTIONS:
			optionBtns[0].setText("Yes");
			optionBtns[0].setLocation(getButtonX(3, 1), getButtonY());
			optionBtns[0].setVisible(true);
			optionBtns[1].setText("No");
			optionBtns[1].setLocation(getButtonX(3, 2), getButtonY());
			optionBtns[1].setVisible(true);
			optionBtns[2].setText("Cancel");
			optionBtns[2].setLocation(getButtonX(3, 3), getButtonY());
			optionBtns[2].setVisible(true);
			break;
		}
	}
	
	private void buttonClick(int which){
		switch (which){
		case 0:
			switch (currentConfig){
			case YES_NO_CANCEL_OPTIONS:
				buttonClicked = CANCEL_OPTION;
				break;
			}
			break;
		case 1:
			switch (currentConfig){
			case OK_CANCEL_OPTIONS:
				buttonClicked = OK_OPTION;
				break;
			case YES_NO_OPTIONS:
				buttonClicked = YES_OPTION;
				break;
			case YES_NO_CANCEL_OPTIONS:
				buttonClicked = NO_OPTION;
				break;
			}
			break;
		case 2:
			switch (currentConfig){
			case DEFAULT_OPTIONS:
				buttonClicked = OK_OPTION;
				break;
			case OK_CANCEL_OPTIONS:
				buttonClicked = CANCEL_OPTION;
				break;
			case YES_NO_OPTIONS:
				buttonClicked = NO_OPTION;
				break;
			case YES_NO_CANCEL_OPTIONS:
				buttonClicked = CANCEL_OPTION;
				break;
			}
			break;
		}
	}
	
	private void openWindow(){
		this.repaint();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)((screenSize.getWidth() - this.getSize().getWidth()) / 2), (int)((screenSize.getHeight() - this.getSize().getHeight()) / 2));
		this.setVisible(true);
        SoundPlayer.playSound(SpearsSound.HELLO);
	}
	
	private int getButtonY(){
		return (this.getHeight() - 15 - 2 * optionBtns[0].getHeight());
	}
	
	private int getButtonX(int buttons, int which){
		switch (buttons){
		case 1:
			return ((this.getWidth() - optionBtns[0].getWidth()) / 2);
		case 2:
			switch (which){
			case 1:
				return (((this.getWidth() - (optionBtns[0].getWidth() * 2 + 10)) / 2));
			case 2:
				return (((this.getWidth() - (optionBtns[0].getWidth() * 2 + 10)) / 2) + optionBtns[0].getWidth() + 10);
			}
		case 3:
			switch (which){
			case 1:
				return (((this.getWidth() - (3*optionBtns[0].getWidth() + 20)) / 2));
			case 2:
				return (((this.getWidth() - (3*optionBtns[0].getWidth() + 20)) / 2) + (optionBtns[0].getWidth() + 10));
			case 3:
				return (((this.getWidth() - (3*optionBtns[0].getWidth() + 20)) / 2) + (optionBtns[0].getWidth() * 2 + 20));
			}
		}
		return -100;
	}
	
}
