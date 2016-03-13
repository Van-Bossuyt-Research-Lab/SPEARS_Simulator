package com.csm.rover.simulator.ui.frame;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.csm.rover.simulator.ui.ImageFunctions;
import com.csm.rover.simulator.ui.events.EmbeddedFrameEvent;
import com.csm.rover.simulator.ui.events.InternalEventHandler;



public class EmbeddedFrame extends JInternalFrame {
	
	private static final long serialVersionUID = -8256034256255313580L;

	public EmbeddedFrame(){
		setResizable(true);
		setMaximizable(true);
		setClosable(true);
		setIconifiable(true);
		setFrameIcon(ImageFunctions.getImage("/gui/frame_icon.png"));
		
		addInternalFrameListener(new InternalFrameAdapter(){
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				InternalEventHandler.fireInternalEvent(EmbeddedFrameEvent.builder()
						.setAction(EmbeddedFrameEvent.Action.CLOSED)
						.setComponent(getThis())
						.build());
			}
		});
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e) {
				InternalEventHandler.fireInternalEvent(EmbeddedFrameEvent.builder()
						.setAction(EmbeddedFrameEvent.Action.RESIZED)
						.setComponent(getThis())
						.setNewState(getThis().getSize())
						.build());
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				InternalEventHandler.fireInternalEvent(EmbeddedFrameEvent.builder()
						.setAction(EmbeddedFrameEvent.Action.MOVED)
						.setComponent(getThis())
						.setNewState(getThis().getLocation())
						.build());
			}			
		});
	}
	
	private EmbeddedFrame getThis(){
		return this;
	}
	
	@Override
	public void setIcon(boolean b) throws PropertyVetoException {
		boolean old = super.isIcon();
		super.setIcon(b);
		InternalEventHandler.fireInternalEvent(EmbeddedFrameEvent.builder()
				.setAction(EmbeddedFrameEvent.Action.ICONIFIED)
				.setComponent(this)
				.setOldState(old)
				.setNewState(b)
				.build());
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
	
	@Override
	public void setMaximum(boolean b) throws PropertyVetoException{
		boolean old = super.isMaximum();
		super.setMaximum(b);
		InternalEventHandler.fireInternalEvent(EmbeddedFrameEvent.builder()
				.setAction(EmbeddedFrameEvent.Action.MAXIMIZED)
				.setComponent(this)
				.setOldState(old)
				.setNewState(b)
				.build());
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
