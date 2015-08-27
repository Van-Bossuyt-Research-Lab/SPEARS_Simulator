package com.csm.rover.simulator.visual;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.border.LineBorder;
import com.csm.rover.simulator.objects.FreeThread;

public class ImageButton extends ImageDisplay {

	private static final long serialVersionUID = 1L;
	protected boolean working = false;
	private ImageIcon Image;
	private ImageIcon hoverImage;
	
	public ImageButton(){
		this.setBorder(new LineBorder(Color.GRAY));
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.setVerticalAlignment(CENTER);
		this.setHorizontalAlignment(CENTER);
		this.setOpaque(true);
		this.addMouseListener(new MouseAdapter() {			
			@Override
			public void mouseClicked(MouseEvent arg0){
				try{
					hoverImage.equals(Image);
				} catch (Exception e) {
					if (isEnabled()){
						RunAnimation();
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg1){
				startHover();
			}
			@Override
			public void mouseExited(MouseEvent arg2){
				endHover();
			}
		});
	}
	
	@Override
	public void setImage(ImageIcon img){
		Image = img;
		super.setImage(Image);
	}
	
	@Override
	public Icon getImage(){
		return Image;
	}
	
	private void RunAnimation(){
		setBackground(new Color(200, 200, 200));
		new FreeThread(500, new Runnable(){
			public void run(){
				setBackground(new Color(240, 240, 240));
			}
		}, 1, "button-animation");
	}
	
	public void setHoverImage(ImageIcon img){
		hoverImage = img;
	}
	
	public ImageIcon getHoverImage(){
		return hoverImage;
	}
	
	private void startHover(){
		try {
			hoverImage.equals(Image);
			super.setImage(hoverImage);
		} catch (Exception e) {}
	}
	
	private void endHover(){
		try {
			super.setImage(Image);
		} catch (Exception e) {}
	}
}
