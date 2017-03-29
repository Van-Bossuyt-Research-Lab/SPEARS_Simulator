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

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;

public class EmbeddedFrame extends JInternalFrame {
	
	private static final long serialVersionUID = -8256034256255313580L;

	private Dimension openState;
	
	EmbeddedFrame(){
		setResizable(true);
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
		openState = new Dimension(0, 0);
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
	
	public void setOpenState(Dimension openState){
		this.openState = openState;
	}
	
	public void setOpenState(int width, int height){
		setOpenState(new Dimension(width, height));
	}
	
	public Dimension getOpenState(){
		return new Dimension(openState);
	}

}
