package control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ZList extends JPanel implements Cloneable{

	private JLabel[] items;
	private JScrollBar scroll;
	private Object[] values = new String[0];
	private int selected = -1;
	
	private EventListenerList ListSelectionListeners = new EventListenerList();
	
	public ZList(){
		this.setLayout(null);
		super.setBounds(0, 0, 100, 100);
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		items = new JLabel[0];
		scroll = new JScrollBar();
		scroll.setOrientation(SwingConstants.VERTICAL);
		scroll.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				scroll();
			}
		});
		this.add(scroll);
		placeComps();
	}
	
	public ZList(Object[] values){
		this.values = values;
		this.setLayout(null);
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		super.setBounds(0, 0, 100, 100);
		items = new JLabel[values.length];
		int x = 0;
		while (x < items.length){
			items[x] = new JLabel("  " + values[x].toString());
			items[x].setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
			final int hold = x;
			items[x].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e){
					handleMouseClick(e);
				}
			});
			items[x].setBackground(new Color(180, 200, 250));
			this.add(items[x]);
			x++;
		}
		scroll = new JScrollBar();
		scroll.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				scroll();
			}
		});
		scroll.setOrientation(SwingConstants.VERTICAL);
		this.add(scroll);
		placeComps();
	}
	
	private void placeComps(){
		int x = 0;
		if (23*items.length > this.getHeight()){
			scroll.setBounds(this.getWidth() - (int)scroll.getPreferredSize().getWidth() - 2, 0, (int)scroll.getPreferredSize().getWidth(), this.getHeight());
			while (x < items.length){
				items[x].setBounds(2, 3+23*x, this.getWidth() - (int)scroll.getPreferredSize().getWidth() - 4, 23);
				x++;
			}
			scroll.setMinimum(0);
			scroll.setMaximum((23*x - this.getHeight()  + scroll.getBlockIncrement() + 6));
			scroll.setUnitIncrement(10);
			scroll.setVisible(true);
			scroll.setValue(0);
		}
		else {
			while (x < items.length){
				items[x].setBounds(2, 3+23*x, this.getWidth() - 4, 23);
				x++;
			}
			scroll.setVisible(false);
		}
		
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height){
		super.setBounds(x, y, width, height);
		placeComps();
	}
	
	private void scroll(){
		int x = 0;
		while (x < items.length){
			items[x].setLocation(0, 3+x*23-scroll.getValue());
			x++;
		}
	}
	
	private void handleMouseClick(MouseEvent e){
		if (this.isEnabled()){
			int x = 0;
			while (x < items.length){
				if (e.getComponent().equals(items[x])){
					selectItem(x);
					break;
				}
				x++;
			}
		}
	}
	
	public void setSelection(int which){
		if (which >= 0 && which < values.length){
			selectItem(which);
		}
	}
	
	private void selectItem(int which){
		int x = 0;
		while (x < items.length){
			items[x].setOpaque(false);
			x++;
		}
		try {
			items[which].setOpaque(true);
			selected = which;
		} 
		catch (Exception e) {
			selected = -1;
		}
		this.repaint();
		fireListSelectionEvent(new ListSelectionEvent("Stuff", 0, 0, false));
	}
	
	public void addValue(Object val){
		addValue(val, values.length);
	}
	
	public void addValue(Object val, int loc){
		selectItem(-1);
		items = Augment(items, new JLabel("  " + val.toString()), loc);
		values = Augment(values, val, loc);
		items[loc].setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		final int hold = loc;
		items[loc].addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				handleMouseClick(e);
			}
		});
		items[loc].setBackground(new Color(180, 200, 250));
		this.add(items[loc]);
		placeComps();
	}
	
	public void setValues(Object[] values){
		selectItem(-1);
		while (this.values.length > 0){
			removeValue(0);
		}
		int x = 0;
		while (x < values.length){
			addValue(values[x]);
			x++;
		}
	}
	
	public void removeValue(int which){
		selectItem(-1);
		this.remove(items[which]);
		items = Remove(items, which);
		values = Remove(values, which);
		placeComps();
	}
	
	public void clearSelection(){
		selectItem(-1);
	}
	
	public int getSelectedIndex(){
		return selected;
	}
	
	public Object getSelectedItem(){
		return getItemAt(selected);
	}
	
	public Object[] getItems(){
		return values;
	}
	
	public String[] getListItems(){
		String[] out = new String[values.length];
		int x = 0;
		while (x < out.length){
			out[x] = values[x].toString();
			x++;
		}
		return out;
	}
	
	public void removeValue(Object val){
		int hold = getLocationOfValue(val);
		if (hold != -1){
			removeValue(hold);
		}
	}
	
	public void addListSelectionListener(ListSelectionListener listener){
		ListSelectionListeners.add(ListSelectionListener.class, listener);
	}
	
	public void removeListSelectionListener(ListSelectionListener listener){
		ListSelectionListeners.remove(ListSelectionListener.class, listener);
	}
	
	protected void fireListSelectionEvent(ListSelectionEvent event){
		Object[] listeners = ListSelectionListeners.getListenerList();
		for (Object listener : listeners){
			try {
				((ListSelectionListener) listener).valueChanged(event);
			} catch (Exception e) {}
		}
	}
	
	public String getValueAt(int loc){
		try {
			return values[loc].toString();
		}
		catch (Exception e){
			return null;
		}
	}
	
	public Object getItemAt(int loc){
		try {
			try {
				return ((InstructionObj) values[loc]).clone();
			}
			catch (Exception e){
				return values[loc];
			}
		}
		catch (Exception e){
			return null;
		}
	}
	
	public int getLocationOfValue(Object val){
		int x = 0;
		while (x < values.length){
			if (values[x].equals(val)){
				return x;
			}
			x++;
		}
		return -1;
	}
	
	private Object[] Augment(Object[] array, Object val, int loc){
		Object[] out = new Object[array.length + 1];
		int x = 0;
		while (x < loc){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		while (x < array.length){
			out[x + 1] = array[x];
			x++;
		}
		return out;
	}
	
	private JLabel[] Augment(JLabel[] array, JLabel val, int loc){
		JLabel[] out = new JLabel[array.length + 1];
		int x = 0;
		while (x < loc){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		while (x < array.length){
			out[x + 1] = array[x];
			x++;
		}
		return out;
	}
	
	private Object[] Remove(Object[] array, int which){
		Object[] out = new Object[array.length - 1];
		int x = 0;
		while (x < which){
			out[x] = array[x];
			x++;
		}
		while (x < out.length){
			out[x] = array[x + 1];
			x++;
		}
		return out;
	}
	
	private JLabel[] Remove(JLabel[] array, int which){
		JLabel[] out = new JLabel[array.length - 1];
		int x = 0;
		while (x < which){
			out[x] = array[x];
			x++;
		}
		while (x < out.length){
			out[x] = array[x + 1];
			x++;
		}
		return out;
	}
}
