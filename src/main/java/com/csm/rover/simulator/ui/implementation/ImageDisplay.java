package com.csm.rover.simulator.ui.implementation;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Optional;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

class ImageDisplay extends JLabel{
	
	private Optional<ImageIcon> image;
	private int margin = 10;

	ImageDisplay(){
		image = Optional.empty();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateImage();
			}
		});
	}

	void setImage(ImageIcon img){
		image = Optional.of(img);
		updateImage();
	}
	
	Icon getImage(){
		if (image.isPresent()){
			return image.get();
		}
		return null;
	}
	
	protected void updateImage(){
		if (image.isPresent()){
			super.setIcon(ImageFunctions.resize(image.get(), this.getWidth() - margin, this.getHeight() - margin));
		}
	}
	
	void setMargin(int marg){
		if (marg >= 0){
			margin = marg;
		}
	}
	
	int getMargin(){
		return margin;
	}
	
	@Override
	public void setIcon(Icon img){}
	
}
