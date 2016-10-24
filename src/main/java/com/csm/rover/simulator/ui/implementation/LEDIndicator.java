package com.csm.rover.simulator.ui.implementation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

class LEDIndicator extends ImageDisplay {

	private static final long serialVersionUID = 1L;
	
	private boolean on = false;
	private String color = "LED_White.png";
	enum Colors { GREEN, RED, LIGHT_BLUE, LIGHT_GREEN, ORANGE, PURPLE, BLUE, YELLOW, WHITE };
	
	LEDIndicator(){
		this.setBorder(null);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		this.setVerticalAlignment(CENTER);
		this.setHorizontalAlignment(CENTER);
		this.setOpaque(false);
		this.setVisible(true);
		this.setPreferredSize(new Dimension(25, 25));
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (getWidth() != getHeight()){
					SwingUtilities.invokeLater(() -> setSize(getHeight(), getHeight()));
				}
			}
		});
		setMargin(5);
		setImage(ImageFunctions.getImage("/icons/LED_Off.png"));
	}
	
	void setSelected(boolean b){
		if (on != b){
			if (b){
				setImage(ImageFunctions.getImage("/icons/" + color));
			}
			else{
				setImage(ImageFunctions.getImage("/icons/LED_Off.png"));
			}
			on = b;
		}
	}
	
	void toggle(){
		setSelected(!on);
	}
	
	boolean isSelected(){
		return on;
	}
	
	void setLEDColor(Colors color){
		switch (color){
		case GREEN:
			this.color = "LED_Green.png";
			break;
		case RED:
			this.color = "LED_Red.png";
			break;
		case LIGHT_BLUE:
			this.color = "LED_Light_Blue.png";
			break;
		case LIGHT_GREEN:
			this.color = "LED_Light_Green.png";
			break;
		case ORANGE:
			this.color = "LED_Orange.png";
			break;
		case PURPLE:
			this.color = "LED_Purple.png";
			break;
		case BLUE:
			this.color = "LED_Blue.png";
			break;
		case YELLOW:
			this.color = "LED_Yellow.png";
			break;
		case WHITE:
			this.color = "LED_White.png";
			break;
		default:
			this.color = "LED_White.png";
			break;
		}
		setSelected(on);
	}
	
}
