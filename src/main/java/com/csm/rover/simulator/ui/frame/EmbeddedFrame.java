package com.csm.rover.simulator.ui.frame;

import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class EmbeddedFrame extends JInternalFrame {
	
	public EmbeddedFrame(){
		setResizable(true);
		setMaximizable(true);
		setClosable(true);
		setIconifiable(true);
		setFrameIcon(new ImageIcon(getClass().getResource("/gui/frame_icon.png")));
		
		addInternalFrameListener(new InternalFrameAdapter(){
			@Override
			public void internalFrameIconified(InternalFrameEvent e) {
				e.getInternalFrame().setVisible(false);
			}
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {
				e.getInternalFrame().setVisible(true);
			}
		});
	}
	
	public void iconify(){
		try {
			this.setIcon(true);
		} catch (PropertyVetoException e) {}
	}
	
	public void deiconify(){
		try {
			this.setIcon(false);
		} catch (PropertyVetoException e) {}
	}
	
	public void maximize(){
		try {
			this.setMaximum(true);
		} catch (PropertyVetoException e) {}
	}
	
	public void unmaximize(){
		try {
			this.setMaximum(false);
		} catch (PropertyVetoException e) {}
	}

}
