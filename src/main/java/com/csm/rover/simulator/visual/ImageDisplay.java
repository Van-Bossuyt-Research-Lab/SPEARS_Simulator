package com.csm.rover.simulator.visual;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImageDisplay extends JLabel{

	private static final long serialVersionUID = 1L;
	
	private ImageIcon Image;
	private int Margin = 10;
	
	public void setImage(ImageIcon img){
		Image = img;
		try {
			super.setIcon(Image);
			updateImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Icon getImage(){
		return Image;
	}
	
	protected void updateImage(){
		try {
			super.setIcon(resize(getIcon(), this.getWidth() - Margin, this.getHeight() - Margin));
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	protected ImageIcon resize(Icon image, int width, int height) throws Exception {
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
	
	public int getLeft(){
		return (int) (super.getLocation().getX() + getWidth());
	}
	
	public int getBottom(){
		return (int) (super.getLocation().getY() + getHeight());
	}
	
	public void setMargin(int marg){
		if (marg >= 0){
			Margin = marg;
		}
	}
	
	public int getMargin(){
		return Margin;
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height){
		super.setBounds(x, y, width, height);
		updateImage();
	}
	
	@Override
	public void setIcon(Icon img){}
	
}
