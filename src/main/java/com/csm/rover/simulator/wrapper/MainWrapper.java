package com.csm.rover.simulator.wrapper;

import com.csm.rover.simulator.visual.Panel;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Queue;

public class MainWrapper extends Panel {

	private static final long serialVersionUID = 2633938480331316518L;

    private SerialBuffers serialBuffers;
    private NamesAndTags namesAndTags;
    private ArrayList<ArrayList<String>> serialHistory = new ArrayList<ArrayList<String>>();
    
    private JLabel SerialDisplayTitle;
    private JLabel SerialAvialableLbl;
    private JScrollPane SerialDisplayScroll;
    private JPanel SerialDisplayPnl;
    JSlider SerialHistorySlider;
    JLabel[] SerialLbls;
    JLabel[] SerialAvailableLbls;
	
	public MainWrapper(Dimension size, SerialBuffers serialBuffers, NamesAndTags namesAndTags) {
		super(size /*new Dimension(1024, 800)*/, "Wrapper Display");
        this.serialBuffers = serialBuffers;
        this.namesAndTags = namesAndTags;
		initialize();
		align();
        createSerialDisplays();
	}
	
	private void initialize(){
		SerialDisplayScroll = new JScrollPane();
		SerialDisplayScroll.setOpaque(false);
		SerialDisplayScroll.setBorder(null);
		SerialDisplayScroll.setBounds(10, 41, 620, 444);
		SerialDisplayScroll.getViewport().setOpaque(false);
		this.add(SerialDisplayScroll);
		
		SerialDisplayPnl = new JPanel();
		SerialDisplayPnl.setBorder(null);
		SerialDisplayPnl.setOpaque(false);
		SerialDisplayScroll.setViewportView(SerialDisplayPnl);
		
		SerialDisplayTitle = new JLabel("Serial Buffers");
		SerialDisplayTitle.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		SerialDisplayTitle.setBounds(10, 11, 118, 21);
		this.add(SerialDisplayTitle);
		
		SerialAvialableLbl = new JLabel("Available");
		SerialAvialableLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		SerialAvialableLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		SerialAvialableLbl.setBounds(461, 12, 118, 21);
		this.add(SerialAvialableLbl);
		
		SerialHistorySlider = new JSlider();
		SerialHistorySlider.setOpaque(false);
		SerialHistorySlider.setMinorTickSpacing(1);
		SerialHistorySlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				drawSerialBuffers(SerialHistorySlider.getValue());
			}
		});
		SerialHistorySlider.setMajorTickSpacing(10);
		SerialHistorySlider.setSnapToTicks(true);
		SerialHistorySlider.setValue(0);
		SerialHistorySlider.setMaximum(10);
		SerialHistorySlider.setPaintTicks(true);
		SerialHistorySlider.setBounds(10, 491, 620, 25);
		this.add(SerialHistorySlider);
	}
	
	private void align(){
        int spacing = 10;
		this.SerialDisplayTitle.setLocation(spacing*3, super.getTopOfPage()+spacing);
		this.SerialDisplayScroll.setBounds(SerialDisplayTitle.getX(), SerialDisplayTitle.getY()+SerialDisplayTitle.getHeight()+spacing, (getWidth()-spacing*4)/3, (getHeight()-spacing*3-SerialDisplayTitle.getHeight()-40)/2);
		this.SerialAvialableLbl.setLocation(SerialDisplayScroll.getX()+SerialDisplayScroll.getWidth()-SerialAvialableLbl.getWidth(), SerialDisplayTitle.getY());
        this.SerialHistorySlider.setLocation(SerialDisplayTitle.getX(), SerialDisplayScroll.getY()+SerialDisplayScroll.getHeight());
	}

    private void createSerialDisplays(){
        //TODO consolidate to 1 array for labels
        serialHistory = new ArrayList<ArrayList<String>>();
        for ( int x = 0; x < serialBuffers.size(); x++){
            serialHistory.add(new ArrayList<String>());
            serialHistory.get(x).add("");
        }
        SerialHistorySlider.setValue(0);
        SerialHistorySlider.setMaximum(0);

        RowSpec[] rows = new RowSpec[serialBuffers.size()*2+1];
        rows[0] = FormFactory.RELATED_GAP_ROWSPEC;

        for (int x = 1; x < rows.length; x += 2){
            rows[x] = FormFactory.DEFAULT_ROWSPEC;
            rows[x+1] = FormFactory.RELATED_GAP_ROWSPEC;
        }
        SerialDisplayPnl.setLayout(new FormLayout(
                new ColumnSpec[] {
                        FormFactory.RELATED_GAP_COLSPEC,
                        new ColumnSpec("center:default:grow"),
                        FormFactory.RELATED_GAP_COLSPEC,
                        new ColumnSpec("left:default:grow"),
                        FormFactory.RELATED_GAP_COLSPEC,
                        new ColumnSpec("right:default"),
                        FormFactory.RELATED_GAP_COLSPEC,},
                rows ));
        JLabel[] titles = new JLabel[serialBuffers.size()];
        SerialLbls = new JLabel[serialBuffers.size()];
        SerialAvailableLbls = new JLabel[serialBuffers.size()];
        for (int i = 0; i < serialBuffers.size(); i++){
            titles[i] = new JLabel(namesAndTags.getNames().get(i));
            titles[i].setFont(new Font("Trebuchet MS", Font.BOLD, 13));
            SerialDisplayPnl.add(titles[i], "2, " + (i*2+2) + ", left, default");
            SerialLbls[i] = new JLabel();
            SerialLbls[i].setFont(new Font("Lucida Console", Font.PLAIN, 12));
            SerialDisplayPnl.add(SerialLbls[i], "4, " + (i*2+2) + ", left, default");
            SerialAvailableLbls[i] = new JLabel("0");
            SerialAvailableLbls[i].setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
            SerialDisplayPnl.add(SerialAvailableLbls[i], "6, " + (i*2+2) + ", right, default");
        }
    }

    public void updateSerialDisplays(){
        ArrayList<Queue<Byte>> buffers = serialBuffers.getSerialQueues();
        String[] stored = new String[buffers.size()];
        int x = 0;
        while (x < buffers.size()){
            stored[x] = "";
            while (!buffers.get(x).isEmpty()){
                stored[x] += (char) buffers.get(x).poll().byteValue();
            }
            x++;
        }
        x = 0;
        while (x < stored.length){
            serialHistory.get(x).add(stored[x]);
            x++;
        }
        SerialHistorySlider.setMaximum(SerialHistorySlider.getMaximum()+1);
        if (SerialHistorySlider.getValue() == SerialHistorySlider.getMaximum()-1){
            SerialHistorySlider.setValue(SerialHistorySlider.getMaximum());
        }
        drawSerialBuffers(SerialHistorySlider.getValue());
    }

    public void drawSerialBuffers(int hist){
        for (int x = 0; x < SerialLbls.length; x++){
            SerialLbls[x].setText(serialHistory.get(x).get(hist));
            SerialAvailableLbls[x].setText(serialHistory.get(x).get(hist).length()+"");
        }
    }
}
