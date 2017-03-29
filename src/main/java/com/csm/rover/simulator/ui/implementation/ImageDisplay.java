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
