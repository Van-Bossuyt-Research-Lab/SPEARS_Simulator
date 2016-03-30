package com.csm.rover.simulator.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

import com.csm.rover.simulator.ui.events.EmbeddedFrameEvent;
import com.csm.rover.simulator.ui.events.EmbeddedFrameListener;
import com.csm.rover.simulator.ui.events.InternalEventHandler;
import com.csm.rover.simulator.ui.events.MenuCommandEvent;
import com.csm.rover.simulator.ui.frame.EmbeddedFrame;

public class FrameShowMenu extends JMenuItem {
	
	private static final long serialVersionUID = 2126009229663749152L;
	
	public EmbeddedFrame frame;
	
	public FrameShowMenu(EmbeddedFrame frm){
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
