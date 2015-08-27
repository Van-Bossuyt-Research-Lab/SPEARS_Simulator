package control;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class PopUp extends JDialog {

	public static final int OK_OPTION = 0, CANCEL_OPTION = 1, YES_OPTION = 2, NO_OPTION = 3;
	public static final int DEFAULT_OPTIONS = 0, OK_CANCEL_OPTIONS = 1, YES_NO_OPTIONS = 2, YES_NO_CANCEL_OPTIONS = 3, CUSTOM_OPTIONS = 4;
	
	private JLabel defaultText;
	private JButton[] optionBtns = new JButton[3];
	
	private int currentConfig = -1;
	private int buttonClicked = -1;
	
	private String[] customTitles;
	private int[] customReturns;
	private boolean customSet = false;
	
	public PopUp(){
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		// this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.getContentPane().setLayout(null);
		initalize();
	}
	
	private void initalize(){
		defaultText = new JLabel();
		defaultText.setHorizontalAlignment(SwingConstants.CENTER);
		defaultText.setVerticalAlignment(SwingConstants.CENTER);
		defaultText.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		this.add(defaultText);		
		int x = 0;
		while (x < optionBtns.length){
			optionBtns[x] = new JButton();
			optionBtns[x].setFont(new Font("Miriam Fixed", Font.PLAIN, 12));
			optionBtns[x].setSize(80, 23);
			final int hold = x;
			optionBtns[x].addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					buttonClick(hold);
				}
			});
			optionBtns[x].addKeyListener(new KeyAdapter(){
				@Override
				public void keyPressed(KeyEvent arg0) {
					if (arg0.getKeyCode() == 10){
						keyClicked();
					}
				}
			});
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
	
	public int showConfirmDialog(String message, String title, int options){
		buttonClicked = -1;
		this.setTitle(title);
		currentConfig = options;
		defaultText.setText("<html><center>" + message.replaceAll("\n", "<br>") + "</center></html>");
		this.setSize((int)(defaultText.getPreferredSize().getWidth() + 50), (int)(defaultText.getPreferredSize().getHeight() + 100));
		defaultText.setVisible(true);
		defaultText.setBounds(0, 0, this.getWidth(), (this.getHeight() - 30 - 2*optionBtns[0].getHeight()));
		placeButtons(options);
		openWindow();
		while (buttonClicked == -1){
			try{
				Thread.sleep(300);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
		this.setVisible(false);
		return buttonClicked;
	}
	
	public int showPictureDialog(ImageIcon image, String title, int options){
		buttonClicked = -1;
		this.setTitle(title);
		currentConfig = options;
		defaultText.setText("");
		defaultText.setIcon(image);
		this.setSize((int)(defaultText.getPreferredSize().getWidth() + 50), (int)(defaultText.getPreferredSize().getHeight() + 100));
		defaultText.setVisible(true);
		defaultText.setBounds(0, 0, this.getWidth(), (this.getHeight() - 30 - 2*optionBtns[0].getHeight()));
		placeButtons(options);
		openWindow();
		while (buttonClicked == -1){
			try{
				Thread.sleep(300);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
		this.setVisible(false);
		defaultText.setIcon(null);
		return buttonClicked;
	}
	
	public String[] showPromptDialog(String prompt1, String inital1, String prompt2, String inital2, String prompt3, String[] options3, String initial3, String formTitle){
		buttonClicked = -1;
		this.setTitle(formTitle);
		this.remove(defaultText);
		this.setSize(250, 250);
		JLabel title1 = new JLabel(prompt1 + ":");
		title1.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		title1.setBounds(20, 10, (int)title1.getPreferredSize().getWidth(), (int)title1.getPreferredSize().getHeight());
		this.add(title1);
		JTextField input1 = new JTextField();
		input1.setText(inital1);
		input1.setFont(new Font("Iskoola Pota", Font.PLAIN, 15));
		input1.setBounds(20, title1.getY() + title1.getHeight() + 5, this.getWidth() - 40, 25);
		this.add(input1);
		JLabel title2 = new JLabel(prompt2 + ":");
		title2.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		title2.setBounds(20, input1.getY() + input1.getHeight() + 10, (int)title2.getPreferredSize().getWidth(), (int)title2.getPreferredSize().getHeight());
		this.add(title2);
		JTextField input2 = new JTextField();
		input2.setText(inital2);
		input2.setFont(new Font("Iskoola Pota", Font.PLAIN, 15));
		input2.setBounds(20, title2.getY() + title2.getHeight() + 5, this.getWidth() - 40, 25);
		this.add(input2);
		JLabel title3 = new JLabel(prompt3 + ":");
		title3.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		title3.setBounds(20, input2.getY() + input2.getHeight() + 10, (int)title3.getPreferredSize().getWidth(), (int)title3.getPreferredSize().getHeight());
		this.add(title3);
		JComboBox input3 = new JComboBox(options3);		
		input3.setSelectedItem(initial3);
		input3.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		input3.setBounds(20, title3.getY() + title3.getHeight() + 5, this.getWidth() - 40, 25);
		this.add(input3);
		currentConfig = OK_CANCEL_OPTIONS;
		placeButtons(OK_CANCEL_OPTIONS);
		openWindow();
		while (buttonClicked == -1){
			try{
				Thread.sleep(300);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
		this.setVisible(false);
		this.remove(title1);
		this.remove(title2);
		this.remove(title3);
		this.remove(input1);
		this.remove(input2);
		this.remove(input3);
		this.add(defaultText);
		return new String[] { buttonClicked + "", input1.getText(), input2.getText(), (String)input3.getSelectedItem() };
	}
	
	public int showOptionDialog(String promptTitle, String formTitle, String[] options){
		buttonClicked = -1;
		this.setTitle(formTitle);
		this.remove(defaultText);
		this.setSize(250, 130);
		JLabel title1 = new JLabel(promptTitle + ":");
		title1.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		title1.setBounds(20, 10, (int)title1.getPreferredSize().getWidth(), (int)title1.getPreferredSize().getHeight());
		this.add(title1);
		JComboBox input3 = new JComboBox(options);
		input3.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		input3.setBounds(20, title1.getY() + title1.getHeight() + 5, this.getWidth() - 40, 25);
		this.add(input3);
		currentConfig = OK_CANCEL_OPTIONS;
		placeButtons(OK_CANCEL_OPTIONS);
		openWindow();
		while (buttonClicked == -1){
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
	
	public String showInputDialog(String text, String title, int options){
		buttonClicked = -1;
		this.setTitle(title);
		currentConfig = options;
		defaultText.setText("<html><center>" + text.replaceAll("\n", "<br>") + "</center></html>");
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
		placeButtons(options);
		openWindow();
		while (buttonClicked == -1){
			try{
				Thread.sleep(300);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
		this.setVisible(false);
		if (buttonClicked == OK_OPTION || buttonClicked == YES_OPTION){
			return input.getText();
		}
		else {
			return "";
		}
	}
	
	private void placeButtons(int option){
		switch (option){
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
		case CUSTOM_OPTIONS:
			if (customSet){
				int x = 0;
				while (x < customTitles.length){
					optionBtns[x].setText(customTitles[x]);
					optionBtns[x].setLocation(getButtonX(customTitles.length, (x + 1)), getButtonY());
					optionBtns[x].setVisible(true);
					x++;
				}
				while (x < 3){
					optionBtns[x].setVisible(false);
					x++;
				}
			}
			else {
				currentConfig = DEFAULT_OPTIONS;
				optionBtns[0].setVisible(false);
				optionBtns[1].setVisible(false);
				optionBtns[2].setText("OK");
				optionBtns[2].setLocation(getButtonX(1, 1), getButtonY());
				optionBtns[2].setVisible(true);
			}
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
			case CUSTOM_OPTIONS:
				buttonClicked = customReturns[0];
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
			case CUSTOM_OPTIONS:
				buttonClicked = customReturns[1];
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
			case CUSTOM_OPTIONS:
				buttonClicked = customReturns[2];
				break;
			}
			break;
		}
	}
	
	public void setCustomButtonOptions(String[] titles, int[] returns){
		if (titles.length == returns.length && titles.length <= 3 && returns.length > 0){
			customTitles = titles;
			customReturns = returns;
			customSet = true;
		}
	}
	
	private void openWindow(){
		this.repaint();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)((screenSize.getWidth() - this.getSize().getWidth()) / 2), (int)((screenSize.getHeight() - this.getSize().getHeight()) / 2));
		this.setVisible(true);
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
