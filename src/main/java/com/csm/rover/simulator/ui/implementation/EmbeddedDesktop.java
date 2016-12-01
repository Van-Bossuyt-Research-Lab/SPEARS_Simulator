package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.wrapper.Globals;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JScrollBar;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

class EmbeddedDesktop extends JDesktopPane {
	private static final Logger LOG = LogManager.getLogger(EmbeddedDesktop.class);

	private static final long serialVersionUID = 1092626769481787681L;
	
	protected ImageIcon background;
	protected boolean hasImage = false;
	
	protected int left_divs = 1;
	protected int right_divs = 1;
	
	protected double dividing_line = 0.5;
	
	protected List<EmbeddedFrame> comps_in_wells;
	
	private boolean on_resize = false, resizing = false;
	
	EmbeddedDesktop(){
		setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		setUpWells();
		setUpEventListeners();
	}

	private void setUpWells(){
        left_divs = UiConfiguration.getDesktopDivsLeft();
        right_divs = UiConfiguration.getDesktopDivsRight();
        dividing_line = UiConfiguration.getDesktopCenterLine();

		comps_in_wells = new ArrayList<>();
		hasImage = true;
		background = ImageFunctions.getImage("/images/spears logo.png");
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
                    UiConfiguration.changeDesktopCenterLine(dividing_line);
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
	
	private void setUpEventListeners() {
		InternalEventHandler.registerInternalListener(this::frameEventHandler);
		InternalEventHandler.registerInternalListener(this::menuEventHandler);
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
			Globals.getInstance();
			g.drawString("Version: "+Globals.versionNumber, 10, getHeight()-60);
			g.drawString("Developed by:", 10, getHeight()-45);
			g.drawString("Van Bossuyt Group", 10, getHeight()-30);
			g.drawString("Colorado School of Mines", 10, getHeight()-15);
		}
		if (resizing){
			g.drawRect((int)(dividing_line*getWidth())-1, 0, 2, getHeight());
		}
	}
	
	private Component being_moved = null;
	
	public Component add(EmbeddedFrame frame){
		frame.setOpenState(frame.getSize());
		frame.setLocation(getOpeningLocation());
		frame.addComponentListener(new ComponentAdapter(){
			@Override
			public void componentMoved(ComponentEvent e) {
				if (e.getComponent().getY() < 0){
					e.getComponent().setLocation(e.getComponent().getX(), 0);
				}
				if (e.getComponent() != being_moved){
					being_moved = e.getComponent();
					placeInDivision(e.getComponent());
					being_moved = null;
				}
			}
		});
		InternalEventHandler.fireInternalEvent(EmbeddedFrameEvent.builder()
				.setAction(EmbeddedFrameEvent.Action.ADDED)
				.setComponent(frame)
				.build());
		frame.setVisible(true);
		Component out = super.add(frame);
		super.setComponentZOrder(frame, 0);
		setScrollsToTop(frame.getComponents());
		return out;
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
			if (!comps_in_wells.contains(frame)){
				frame.setOpenState(frame.getSize());
			}
			frame.setResizable(false);
			int div = (int)Math.ceil((frame.getY() > 0 ? frame.getY() : 1) /(double)getHeight()*left_divs) - 1;
			ignore_this_one = true;
			frame.setLocation(1, (int) (div/(double)left_divs*getHeight())+1);
			frame.setSize((int) (getWidth()*dividing_line)-2, (int) (getHeight()/(double)left_divs)-2);
		}
		else if (frame.getX()+frame.getWidth() > getWidth() && right_divs > 0){
			comps_in_wells.add(frame);
			if (!comps_in_wells.contains(frame)){
				frame.setOpenState(frame.getSize());
			}
			frame.setResizable(false);
			int div = (int)Math.ceil((frame.getY() > 0 ? frame.getY() : 1) /(double)getHeight()*right_divs) - 1;
			ignore_this_one = true;
			frame.setSize((int) (getWidth()*(1-dividing_line))-2, (int) (getHeight()/(double)right_divs)-2);
			frame.setLocation(getWidth()-frame.getWidth()+1, (int) (div/(double)right_divs*getHeight())+1);
		}
		else if (comps_in_wells.contains(frame) && frame.getX() > 2 && frame.getX()+frame.getWidth() < getWidth()-2){
			comps_in_wells.remove(frame);
			frame.setResizable(true);
			int x = (frame.getWidth()-frame.getOpenState().width)/2;
			frame.setSize(frame.getOpenState());
			frame.setLocation(frame.getX()+x, frame.getY());
		}
	}
	
	private void frameEventHandler(EmbeddedFrameEvent e){
		
	}
	
	@SuppressWarnings("incomplete-switch")
	private void menuEventHandler(MenuCommandEvent e) {
		switch (e.getAction()){
		case NEW_FRAME:
			try {
				@SuppressWarnings("unchecked")
				EmbeddedFrame frame = ((Class<? extends EmbeddedFrame>)e.getValue()).newInstance();
				this.add(frame);
			}
			catch (InstantiationException | IllegalAccessException | ClassCastException e1) {
				LOG.log(Level.ERROR, "The new frame could not be added", e1);
			}	
			break;
		case SHOW_FRAME:
			EmbeddedFrame frame = (EmbeddedFrame) e.getValue();
			frame.setVisible(true);
			if (frame.isIcon()){
				frame.deiconify();
			}
			frame.requestFocus();
			break;
		case GRID_CHANGE:
			String newval = (String) e.getValue();
			if (newval.charAt(0) == 'L'){
				this.left_divs = Integer.parseInt(newval.charAt(1)+"");
			}
			else if (newval.charAt(0) == 'R'){
				this.right_divs = Integer.parseInt(newval.charAt(1)+"");
			}
			else {
				LOG.log(Level.DEBUG, "The divisions value \"{}\" could not be parsed", newval);
			}
			//TODO reset frames already in divisions
			break;
		}
	}
	
	private Point startLocation = new Point(100, 100);
	
	private Point getOpeningLocation(){
		Point out = new Point(startLocation);
		startLocation = new Point(
				startLocation.getX()+50 < this.getWidth()-500 ? (int)startLocation.getX()+50 : 100,
				startLocation.getY()+50 < this.getHeight()-500 ? (int)startLocation.getY()+50 : 100
				);
		return out;
	}
	
	private void setScrollsToTop(Component[] comps){
		for (Component comp : comps){
			if (comp instanceof JScrollBar){
				((JScrollBar)comp).setValue(((JScrollBar)comp).getMinimum());
			}
//			else if (comp instanceof JScrollPane){
//				JScrollBar vert = ((JScrollPane)comp).getVerticalScrollBar();
//				vert.setValue(vert.getMinimum());
//				System.out.println("Set: "+vert.toString() + String.format("  Min: %d  Max: %d  Val: %d", vert.getMinimum(), vert.getMaximum(), vert.getValue()));
//				JScrollBar horz = ((JScrollPane)comp).getHorizontalScrollBar();
//				horz.setValue(horz.getMinimum());
//				System.out.println("Set: "+horz.toString() + String.format("  Min: %d  Max: %d  Val: %d", horz.getMinimum(), horz.getMaximum(), horz.getValue()));
//			}
			else if (comp instanceof Container){
				setScrollsToTop(((Container)comp).getComponents());
			}
		}
	}
	
}
