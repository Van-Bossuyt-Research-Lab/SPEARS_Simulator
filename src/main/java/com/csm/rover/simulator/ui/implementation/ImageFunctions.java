package com.csm.rover.simulator.ui.implementation;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

class ImageFunctions {
	
	private static final int menu_icon_size = 24;
	
	static ImageIcon getImage(String image){
		return new ImageIcon(ImageFunctions.class.getResource(image));
	}
	
	static ImageIcon getMenuIcon(String image){
		return getImage(image, menu_icon_size, menu_icon_size);
	}
	
	static ImageIcon getImage(String image, int width, int height){
		return resize(getImage(image), width, height);
	}
	
	static ImageIcon resize(Icon image, int width, int height) {
		try {
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		    Graphics2D g = bi.createGraphics();
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		            RenderingHints.VALUE_ANTIALIAS_ON);
	        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
	        g.setComposite(comp);
	        g.drawImage(((ImageIcon) (image)).getImage(), 0, 0, width, height, null);
		    g.dispose();
		    return new ImageIcon(bi);
		}
		catch (Exception i) {
			return (ImageIcon) image;
		}
	}

}
