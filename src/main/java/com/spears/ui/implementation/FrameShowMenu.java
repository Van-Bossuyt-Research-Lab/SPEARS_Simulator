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

package com.spears.ui.implementation;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class FrameShowMenu extends JMenuItem {
	
	private static final long serialVersionUID = 2126009229663749152L;
	
	public EmbeddedFrame frame;
	
	FrameShowMenu(EmbeddedFrame frm){
		super(frm.getTitle());
		this.frame = frm;
		
		InternalEventHandler.registerInternalListener(new EmbeddedFrameListener(){
			@Override
			public void frameChanged(EmbeddedFrameEvent e) {
				if (e.getComponent() == frame){
					if (e.getAction().equals(EmbeddedFrameEvent.Action.ICONIFIED)){
						frame.setVisible(!(Boolean)e.getNewState());
					}
				}
			}			
		});
		
		addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				InternalEventHandler.fireInternalEvent(
						MenuCommandEvent.builder()
								.setAction(MenuCommandEvent.Action.SHOW_FRAME)
								.setOrigin(e)
								.setValue(frame)
								.build()
						);
			}
		});
		
		frame.addPropertyChangeListener(JInternalFrame.TITLE_PROPERTY, (PropertyChangeEvent) -> setText(frame.getTitle()));
	}
	
	public EmbeddedFrame getFrame(){
		return frame;
	}

}
