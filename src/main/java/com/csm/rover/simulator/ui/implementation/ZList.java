package com.csm.rover.simulator.ui.implementation;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

class ZList <T> extends JPanel implements Cloneable{

	private static final long serialVersionUID = 1L;
	
	private List<JLabel> items;
	private JScrollBar scroll;
	private List<T> values;
	private int selected = -1;
	
	private List<ListSelectionListener> ListSelectionListeners = new ArrayList<>();
	
	ZList(){
        values = new ArrayList<T>();
		items = new ArrayList<>();
        initialize();
	}
	
	ZList(T[] values){
		this.values = asList(values);
        setLabels();
        initialize();
	} 
    
    ZList(ArrayList<T> values){
        this.values = values;
        setLabels();
        initialize();
    }
    
    private void initialize(){
        this.setLayout(null);
        this.setOpaque(false);
        this.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
        scroll = new JScrollBar();
        scroll.addAdjustmentListener((AdjustmentEvent arg0) -> scroll());
        scroll.setOrientation(Adjustable.VERTICAL);
        this.add(scroll);
        placeComps();        
    }

    private void setLabels(){
        items = new ArrayList<>();
        int x = 0;
        while (x < values.size()){
            items.add(new JLabel("  " + values.get(x).toString()));
            items.get(x).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleMouseClick(e);
                }
            });
            items.get(x).setBackground(new Color(180, 200, 250));
            this.add(items.get(x));
            x++;
        }
    }
    
	private void placeComps(){
		int x = 0;
		if (23*items.size() > this.getHeight()){
			scroll.setBounds(this.getWidth() - (int)scroll.getPreferredSize().getWidth() - 2, 0, (int)scroll.getPreferredSize().getWidth(), this.getHeight());
			while (x < items.size()){
				items.get(x).setBounds(2, 3 + 23 * x, this.getWidth() - (int) scroll.getPreferredSize().getWidth() - 4, 23);
				x++;
			}
			scroll.setMinimum(0);
			scroll.setMaximum((23*x - this.getHeight()  + scroll.getBlockIncrement() + 6));
			scroll.setUnitIncrement(10);
			scroll.setVisible(true);
			scroll.setValue(0);
		}
		else {
			while (x < items.size()){
				items.get(x).setBounds(2, 3 + 23 * x, this.getWidth() - 4, 23);
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
		while (x < items.size()){
			items.get(x).setLocation(0, 3 + x * 23 - scroll.getValue());
			x++;
		}
	}

	private void handleMouseClick(MouseEvent e){
		if (this.isEnabled()){
			int x = 0;
			while (x < items.size()){
				if (e.getComponent().equals(items.get(x))){
					selectItem(x);
					break;
				}
				x++;
			}
		}
	}

	void setSelection(int which){
		if (which >= 0 && which < values.size()){
			selectItem(which);
		}
	}

	private void selectItem(int which){
		int x = 0;
		while (x < items.size()){
			items.get(x).setOpaque(false);
			x++;
		}
		try {
			items.get(which).setOpaque(true);
			selected = which;
		}
		catch (Exception e) {
			selected = -1;
		}
		this.repaint();
		fireListSelectionEvent(new ListSelectionEvent("Stuff", 0, 0, false));
	}

	void addValue(T val){
		addValue(val, val.toString(), values.size());
	}

    void addValue(T val, String label){
        addValue(val, label, values.size());
    }

    void addValue(T val, int loc){
        addValue(val, val.toString(), values.size());
    }

	void addValue(T val, String label, int loc){
		selectItem(-1);
        values.add(loc, val);
        items.add(loc, new JLabel("  " + label));
		items.get(loc).setFont(getFont());
		items.get(loc).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
		items.get(loc).setBackground(new Color(180, 200, 250));
		this.add(items.get(loc));
		placeComps();
	}

	void setValues(T[] values){
		selectItem(-1);
		while (this.values.size() > 0){
			removeValue(0);
		}
		int x = 0;
		while (x < values.length){
			addValue(values[x]);
			x++;
		}
	}

	void removeValue(int which){
		selectItem(-1);
		this.remove(items.get(which));
		items.remove(which);
		values.remove(which);
		placeComps();
	}

	@Override
	public void setFont(Font font){
		super.setFont(font);
		if (items != null) {
			for (JLabel item : items) {
				item.setFont(font);
			}
		}
	}
	
	void clearSelection(){
		selectItem(-1);
	}
	
	int getSelectedIndex(){
		return selected;
	}
	
	T getSelectedItem(){
		return getItemAt(selected);
	}
	
	String getLabelAt(int index){
        return this.items.get(index).getText().substring(2);
    }
	
	List<T> getItems(){
        return values;
	}
	
	String[] getListItems(){
		String[] out = new String[values.size()];
		int x = 0;
		while (x < out.length){
			out[x] = values.get(x).toString();
			x++;
		}
		return out;
	}
	
	void removeValue(T val){
		int hold = getLocationOfValue(val);
		if (hold != -1){
			removeValue(hold);
		}
	}
	
	void addListSelectionListener(ListSelectionListener listener){
		ListSelectionListeners.add(listener);
	}
	
	void removeListSelectionListener(ListSelectionListener listener){
		ListSelectionListeners.remove(listener);
	}
	
	protected void fireListSelectionEvent(ListSelectionEvent event){
		for (ListSelectionListener listener : ListSelectionListeners){
			listener.valueChanged(event);
		}
	}
	
	String getValueAt(int loc){
		try {
			return values.get(loc).toString();
		}
		catch (Exception e){
			return null;
		}
	}
	
	T getItemAt(int loc){
		try {
			try {
				return values.get(loc);
			}
			catch (Exception e){
				return values.get(loc);
			}
		}
		catch (Exception e){
			return null;
		}
	}
	
	int getLocationOfValue(T val){
		int x = 0;
		while (x < values.size()){
			if (values.get(x).equals(val)){
				return x;
			}
			x++;
		}
		return -1;
	}

    int getItemCount(){
        return items.size();
    }

}
