package com.csm.rover.simulator.ui.desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.csm.rover.simulator.ui.frame.EmbeddedFrame;

public class EnbeddedDesktop extends JDesktopPane {

	protected ImageIcon background;
	protected boolean hasImage = false;
	
	protected int left_divs = 1;
	protected int right_divs = 1;
	
	protected double dividing_line = 0.5;
	
	protected List<EmbeddedFrame> comps_in_wells;
	
	private boolean on_resize = false, resizing = false;
	
	public EnbeddedDesktop(){
		comps_in_wells = new ArrayList<>();
		hasImage = true;
		background = new ImageIcon("C:\\Users\\PHM-Lab1\\Eclipse Workspace\\PHM_System_Simulator_V2\\src\\main\\resources\\images\\spears logo.png");
		this.addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseMoved(MouseEvent e) {
				if (Math.abs(e.getX()-dividing_line*getWidth()) < 10){
					setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
					on_resize = true;
				}
				else {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					on_resize = false;
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (resizing){
					dividing_line = e.getX()/(double)getWidth();
					if (dividing_line < 0.2){
						dividing_line = 0.2;
					}
					if (dividing_line > 0.8){
						dividing_line = 0.8;
					}
					for (Component c : comps_in_wells){
						if (c.getX() <= 1 ){
							c.setSize((int) (getWidth()*dividing_line)-2, c.getHeight());
						}
						else {
							c.setSize((int) (getWidth()*(1-dividing_line))-2, c.getHeight());
							ignore_this_one = true;
							c.setLocation((int) (getWidth()*dividing_line+1), c.getY());
						}
					}
					repaint();
				}		
			}
		});
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				if (on_resize){
					resizing = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (resizing){
					resizing = false;
					repaint();
				}
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
			g.setColor(Color.GRAY);
		if (isOpaque()){
			if (hasImage){
				try {
					g.drawImage(background.getImage(), (this.getWidth()-background.getIconWidth())/2, (this.getHeight()-background.getIconHeight())/2, null);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			g.drawString("Version: 1.8.3", 10, getHeight()-60);
			g.drawString("Developed by:", 10, getHeight()-45);
			g.drawString("Van Bossuyt Group", 10, getHeight()-30);
			g.drawString("Colorado School of Mines", 10, getHeight()-15);
		}
		if (resizing){
			g.drawRect((int)(dividing_line*getWidth())-1, 0, 2, getHeight());
		}
	}
	
	private Component being_moved = null;
	
	public Component add(JInternalFrame frame){
		frame.setPreferredSize(frame.getSize());
		frame.addComponentListener(new ComponentAdapter(){
			@Override
			public void componentMoved(ComponentEvent e) {
				if (e.getComponent() != being_moved){
					being_moved = e.getComponent();
					placeInDivision(e.getComponent());
					being_moved = null;
				}
			}
		});
		frame.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("let go");
			}
		});
		return super.add(frame);
	}
	
	private boolean ignore_this_one = false;
	private void placeInDivision(Component comp){
		if (ignore_this_one){
			ignore_this_one = false;
			return;
		}
		EmbeddedFrame frame;
		try {
			frame = (EmbeddedFrame)comp;
		}
		catch (ClassCastException e){
			return;
		}
		if (frame.getX() < 0 && left_divs > 0){
			comps_in_wells.add(frame);
			if (!comps_in_wells.contains(frame))
				frame.setPreferredSize(frame.getSize());
			frame.setResizable(false);
			int div = (int)Math.ceil((frame.getY() > 0 ? frame.getY() : 1) /(double)getHeight()*left_divs) - 1;
			ignore_this_one = true;
			frame.setLocation(1, (int) (div/(double)left_divs*getHeight())+1);
			frame.setSize((int) (getWidth()*dividing_line)-2, (int) (getHeight()/(double)left_divs)-2);
		}
		else if (frame.getX()+frame.getWidth() > getWidth() && right_divs > 0){
			comps_in_wells.add(frame);
			if (!comps_in_wells.contains(frame))
				frame.setPreferredSize(frame.getSize());
			frame.setResizable(false);
			int div = (int)Math.ceil((frame.getY() > 0 ? frame.getY() : 1) /(double)getHeight()*right_divs) - 1;
			ignore_this_one = true;
			frame.setSize((int) (getWidth()*(1-dividing_line))-2, (int) (getHeight()/(double)right_divs)-2);
			frame.setLocation(getWidth()-frame.getWidth()+1, (int) (div/(double)right_divs*getHeight())+1);
		}
		else if (comps_in_wells.contains(frame) && frame.getX() > 2 && frame.getX()+frame.getWidth() < getWidth()-2){
			comps_in_wells.remove(frame);
			frame.setResizable(true);
			int x = (frame.getWidth()-frame.getPreferredSize().width)/2;
			frame.setSize(frame.getPreferredSize());
			frame.setLocation(frame.getX()+x, frame.getY());
		}
	}

	public void setImage(ImageIcon icon){
		background = icon;
		hasImage = icon != null;
	}
	
	public ImageIcon getImage(){
		return background;
	}
	
}
