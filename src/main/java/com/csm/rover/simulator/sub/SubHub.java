package com.csm.rover.simulator.sub;

import com.csm.rover.simulator.control.InterfaceCode;
import com.csm.rover.simulator.map.SubMap;
import com.csm.rover.simulator.map.TerrainMap;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.visual.Panel;
import com.csm.rover.simulator.wrapper.SerialBuffers;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class SubHub extends Panel {

    private static final long serialVersionUID = 1L;

    private SerialBuffers serialBuffers;
    private ArrayList<SubObject> rovers;
    private boolean inHUDmode = false;
    private Map<Integer, Map<Integer, Integer>> standardDisplayLinks;

    private int numberOfPages = 1;
    private int currentPage = 0;

    private Map<Integer, Integer> HUDDisplayLinks;
    private int numberOfHUDDisplays;

    private int numberOfDisplays = 9;
    private ArrayList<SubDisplayWindow> displayWindows = new ArrayList<SubDisplayWindow>(numberOfDisplays);

    private JComboBox<String> satSelect;
    private JComboBox<String> rovSelect;
    private JTextField commandInput;

    public SubHub(Dimension size, SerialBuffers serialBuffers, ArrayList<SubObject> subs, SubMap map){
        super(size, "Sub Hub");
        this.serialBuffers = serialBuffers;

        standardDisplayLinks = new TreeMap<Integer, Map<Integer, Integer>>();
        for (int x = 0; x < numberOfPages; x++){
            standardDisplayLinks.put(x, new TreeMap<Integer, Integer>());
            for (int y = 0; y < numberOfDisplays; y++){
                standardDisplayLinks.get(x).put(y, -1);
            }
        }

        numberOfHUDDisplays = 4;
        if (numberOfHUDDisplays > numberOfDisplays){
            numberOfHUDDisplays = numberOfDisplays;
        }
        HUDDisplayLinks = new TreeMap<Integer, Integer>();
        for (int x = 0; x < numberOfHUDDisplays; x++){
            HUDDisplayLinks.put(x, -1);
        }

        this.rovers = rovers;

        initialize(map);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (!inHUDmode) {
                    if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
                        if (currentPage > 0) {
                            currentPage--;
                            updateDisplays();
                        }
                    } else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
                        if (currentPage < standardDisplayLinks.size() - 1) {
                            currentPage++;
                            updateDisplays();
                        }
                    }
                }
                else {
                    if (getForm().isPresent()){
                        getForm().get().focusOnTerrain();
                    }
                }
            }
        });

        setVisible(false);
    }

    private void initialize(SubMap map){
        for (int x = 0; x < numberOfDisplays; x++){
            final int a = x;
            SubDisplayWindow displayWindow = new SubDisplayWindow(map);
            // displayWindow.setSubList(subs);
            displayWindow.addPageForwardAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    changeLinkedRover(a, 1);
                }
            });
            displayWindow.addPageBackAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    changeLinkedRover(a, -1);
                }
            });
            displayWindows.add(x, displayWindow);
            this.add(displayWindow);
        }

        commandInput = new JTextField();
        commandInput.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        commandInput.setSize(300, 28);
        commandInput.setLocation(super.getWidth()-commandInput.getWidth()-10, super.getTopOfPage()+super.getWorkingHeight()*7/8);
        commandInput.setVisible(false);
        commandInput.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    char[] out = (satSelect.getSelectedItem() + " " + rovSelect.getSelectedItem() + " " + commandInput.getText()).toCharArray();
                    commandInput.setText("");
                    for (char c : out){
                        serialBuffers.writeToSerial(c, InterfaceCode.IDcode);
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    focusOnHub();
                }
            }
        });
        this.add(commandInput);

        satSelect = new JComboBox<String>();
        satSelect.setSize(commandInput.getWidth()/2, 28);
        satSelect.setLocation(commandInput.getX(), commandInput.getY()-satSelect.getHeight());
        satSelect.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        satSelect.setVisible(false);
        this.add(satSelect);

        rovSelect = new JComboBox<String>();
        rovSelect.setSize(commandInput.getWidth()/2, 28);
        rovSelect.setLocation(commandInput.getX()+commandInput.getWidth()-rovSelect.getWidth(), commandInput.getY()-rovSelect.getHeight());
        rovSelect.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        rovSelect.setVisible(false);
        this.add(rovSelect);

    }

    public void start(){
        new SynchronousThread(500, new Runnable(){
            public void run(){
                updateDisplays();
            }
        }, SynchronousThread.FOREVER, "Rover Hub Update");
    }

    public void setIdentifiers(ArrayList<String> rovs, ArrayList<String> sats){
        rovSelect.removeAllItems();
        for (Object rov : rovs){
            rovSelect.addItem((String)rov);
        }
        satSelect.removeAllItems();
        for (Object sat : sats){
            satSelect.addItem((String)sat);
        }
    }

    private void focusOnHub(){
        requestFocus();
        try {
            this.getKeyListeners()[0].keyPressed(null);
        } catch (ArrayIndexOutOfBoundsException e) { e.printStackTrace(); }
    }

    //adds the rover objects to the hub
    public void setRovers(ArrayList<SubObject> subs){
        for (SubObject sub : subs){
             // this.subs.add(sub);
        }

        int x = 0;
        while (x < subs.size()){
            //set the first n displays to the the nth rover
            standardDisplayLinks.get(x/numberOfDisplays).put(x%numberOfDisplays, x);
            x++;
        }
        while (x < numberOfDisplays*numberOfPages){
            standardDisplayLinks.get(x/numberOfDisplays).put(x%numberOfDisplays, -1);
            x++;
        }

        updateDisplays();
    }

    public void setFocusedRover(int which){
        if (which >= 0 && which < rovers.size()){
            HUDDisplayLinks.put(0, which);
            displayWindows.get(0).lockOnSub(which);
            updateDisplays();
            rovSelect.setSelectedIndex(which);
        }
    }

    public void updateDisplays(){
        int x = 0;
        while (x < numberOfDisplays){
            if (inHUDmode){
                displayWindows.get(x).update(HUDDisplayLinks.get(x));
                if (x+1 == numberOfHUDDisplays){
                    break;
                }
            }
            else {
                displayWindows.get(x).update(standardDisplayLinks.get(currentPage).get(x));
            }
            x++;
        }
    }

    //change which rover is connected to a certain display
    private void changeLinkedRover(int display, int by){
        if (rovers.size() == 0){
            return;
        }
        if (inHUDmode){
            HUDDisplayLinks.put(display, HUDDisplayLinks.get(display)+by);
        }
        else {
            standardDisplayLinks.get(currentPage).put(display, standardDisplayLinks.get(currentPage).get(display)+by);
        }
        updateDisplays();
    }

    //whether or not to show the panel as an overlay on the map
    public void setInHUDMode(boolean b){
        for (SubDisplayWindow display : displayWindows){
            display.setHUDMode(b);
        }
        if (b){
            // displayWindows.get(0).lockOnRover(HUDDisplayLinks.get(0));
        }
        else {
            displayWindows.get(0).unlock();
        }
        inHUDmode = b;
        updateDisplays();
        commandInput.setVisible(b);
        satSelect.setVisible(b);
        rovSelect.setVisible(b);
    }

    public boolean isInHUDMode(){
        return inHUDmode;
    }

    @Override
    public void setVisible(boolean b){
        if (inHUDmode){ //organize everything as an overlaid display
            this.getParent().setComponentZOrder(this, 0);
            super.titleLbl.setVisible(false);
            super.postScript.setVisible(false);
            super.setOpaque(false);
            int spacing = 20;
            displayWindows.get(0).setLocation(spacing, super.getTopOfPage()+spacing);
            displayWindows.get(0).setSize(displayWindows.get(0).getPreferredSize());
            displayWindows.get(1).setLocation(spacing, displayWindows.get(0).getY()+displayWindows.get(0).getHeight()+spacing);
            displayWindows.get(1).setSize(displayWindows.get(1).getPreferredSize());
            displayWindows.get(2).setSize(displayWindows.get(2).getPreferredSize());
            displayWindows.get(2).setLocation(this.getWidth()-displayWindows.get(2).getWidth()-spacing, displayWindows.get(0).getY());
            displayWindows.get(3).setLocation(displayWindows.get(2).getX(), displayWindows.get(1).getY());
            displayWindows.get(3).setSize(displayWindows.get(3).getPreferredSize());
            int x = numberOfHUDDisplays;
            while (x < numberOfDisplays){
                displayWindows.get(x).setVisible(false);
                x++;
            }
        }
        else { //organize everything as an independent panel
            super.titleLbl.setVisible(true);
            super.postScript.setVisible(true);
            super.setOpaque(true);
            int numbAcross = (numberOfDisplays/3+numberOfDisplays%3);
            int spacing = 10;
            int width = (this.getWidth()-spacing*(numbAcross-1))/numbAcross;
            int height = (this.getWorkingHeight()-spacing*(numberOfDisplays/numbAcross))/3;
            int x = 0;
            while (x < numberOfDisplays){
                displayWindows.get(x).setBounds(
                        (width+spacing)*(x%numbAcross),
                        this.getTopOfPage() + (height+spacing)*(x/numbAcross),
                        width,
                        height
                );
                displayWindows.get(x).setVisible(true);
                x++;
            }
        }
        super.setVisible(b);
    }

}