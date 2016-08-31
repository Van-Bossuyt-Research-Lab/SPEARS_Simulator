package com.csm.rover.simulator.platforms.rover;

import com.csm.rover.simulator.environments.rover.TerrainEnvironment;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.visual.LEDIndicator;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class RoverDisplayWindow extends JPanel {

    private TerrainEnvironment map;

    private ArrayList<RoverObject> rovers = new ArrayList<>();
    private int roverLock = -1;

    private boolean inHUD = false;

    private JLabel RoverNameLbl = new JLabel();
    private JButton PageLeftBtn = new JButton();
    private JButton PageRightBtn = new JButton();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextArea SerialHistoryLbl = new JTextArea();
    private JTextArea MovementStatsLbl = new JTextArea();
    private JTextArea ElectricalStatsLbl = new JTextArea();
    private JTextArea TemperatureStatsLbl = new JTextArea();
    private LEDIndicator MuteLED = new LEDIndicator();
    private LEDIndicator InstructionsLED = new LEDIndicator();
    private LEDIndicator AutonomousLED = new LEDIndicator();
    private JLabel UnusedLbl1 = new JLabel();
    private LEDIndicator UnusedLED1 = new LEDIndicator();
    private JLabel UnusedLbl2 = new JLabel();
    private LEDIndicator UnusedLED2 = new LEDIndicator();
    private JLabel UnusedLbl3 = new JLabel();
    private LEDIndicator UnusedLED3 = new LEDIndicator();
    private JLabel UnusedLbl4 = new JLabel();
    private LEDIndicator UnusedLED4 = new LEDIndicator();
    private JLabel UnusedLbl5 = new JLabel();
    private LEDIndicator UnusedLED5 = new LEDIndicator();
    private JLabel UnusedLbl6 = new JLabel();
    private LEDIndicator UnusedLED6 = new LEDIndicator();
    private JLabel UnusedLbl7 = new JLabel();
    private LEDIndicator UnusedLED7 = new LEDIndicator();
    
    private Font font = new Font("Bookman Old Style", Font.PLAIN, 13);

    private String movementPattern = "X: %s m\tAngular Spin FL: %s \u00B0/s" +
            "\nY: %s m" + "\tAngular Spin FR: %s \u00B0/s" +
            "\nHeading: %s\u00B0" + "\tAngular Spin BL: %s \u00B0/s" +
            "\n" + "\tAngular Spin BR: %s \u00B0/s" +
            "\nSpeed: %s m/s" +
            "\nVelocity X: %s m/s" + "\tIncline: %s\u00B0" +
            "\nVelocity Y: %s m/s" + "\tCross Slope: %s\u00B0" +
            "\nAngular Velocity: %s \u00B0/s" +
            "\nSlip Velocity: %s m/s" +
            "\n\nAcceleration: %s m/s\u00B2" +
            "\nAngular Acceleration: %s \u00B0/s\u00B2" +
            "\nSlip Acceleration: %s m/s\u00B2";
    private String electricalPattern = "Battery Charge: %s C" +
            "\nBattery Voltage: %s V" +
            "\nBattery Current: %s A" +
            "\n\nMotor Current FL: %s A" +
            "\nMotor Current FR: %s A" +
            "\nMotor Current BL: %s A" +
            "\nMotor Current BR: %s A" +
            "\n\nMotor Voltage FL: %s V" +
            "\nMotor Voltage FR: %s V" +
            "\nMotor Voltage BL: %s V" +
            "\nMotor Voltage BR: %s V";
    private String temperaturePattern = "Motor Temperature FL: %s \u00B0c" +
            "\nMotor Temperature FR: %s \u00B0c" +
            "\nMotor Temperature BL: %s \u00B0c" +
            "\nMotor Temperature BR: %s \u00B0c" +
            "\n\nBattery Temperature: %s \u00B0c" +
            "\n\nAir Temperature: %s \u00B0c";

    public RoverDisplayWindow(TerrainEnvironment map){
        initialize();
        setToBlank();
        this.map = map;
    }
    
    private void initialize() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(450, 325));
        this.setBounds(0, 0, 450, 325);

        JPanel rack = new JPanel();
        rack.setLayout(new BorderLayout());
        rack.setOpaque(false);
        this.add(rack, BorderLayout.NORTH);

        RoverNameLbl = new JLabel("Undefined");
        RoverNameLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 17));
        RoverNameLbl.setHorizontalAlignment(JLabel.CENTER);
        rack.add(RoverNameLbl, BorderLayout.CENTER);

        PageLeftBtn = new JButton("<");
        PageLeftBtn.setEnabled(false);
        rack.add(PageLeftBtn, BorderLayout.WEST);

        PageRightBtn = new JButton(">");
        PageRightBtn.setEnabled(false);
        rack.add(PageRightBtn, BorderLayout.EAST);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setOpaque(false);
        tabbedPane.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
        this.add(tabbedPane, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setOpaque(false);
        tabbedPane.addTab("Serial", null, scrollPane, null);

        SerialHistoryLbl = new JTextArea();
        scrollPane.setViewportView(SerialHistoryLbl);
        SerialHistoryLbl.setOpaque(false);
        SerialHistoryLbl.setEditable(false);
        SerialHistoryLbl.setFont(font);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setOpaque(false);
        tabbedPane.addTab("Movement", null, scrollPane_1, null);

        MovementStatsLbl = new JTextArea();
        MovementStatsLbl.setTabSize(20);
        MovementStatsLbl.setFont(font);
        MovementStatsLbl.setOpaque(false);
        MovementStatsLbl.setWrapStyleWord(true);
        MovementStatsLbl.setLineWrap(true);
        MovementStatsLbl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        MovementStatsLbl.setEditable(false);
        scrollPane_1.setViewportView(MovementStatsLbl);

        JScrollPane scrollPane_2 = new JScrollPane();
        tabbedPane.addTab("Electrical", null, scrollPane_2, null);

        ElectricalStatsLbl = new JTextArea();
        ElectricalStatsLbl.setTabSize(20);
        ElectricalStatsLbl.setFont(font);
        ElectricalStatsLbl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        ElectricalStatsLbl.setEditable(false);
        ElectricalStatsLbl.setLineWrap(true);
        ElectricalStatsLbl.setOpaque(false);
        ElectricalStatsLbl.setWrapStyleWord(true);
        scrollPane_2.setViewportView(ElectricalStatsLbl);

        JScrollPane temperatureScroll = new JScrollPane();
        tabbedPane.addTab("Temperature", null, temperatureScroll, null);

        TemperatureStatsLbl = new JTextArea();
        TemperatureStatsLbl.setOpaque(false);
        TemperatureStatsLbl.setWrapStyleWord(true);
        TemperatureStatsLbl.setEditable(false);
        TemperatureStatsLbl.setTabSize(14);
        TemperatureStatsLbl.setFont(font);
        TemperatureStatsLbl.setLineWrap(true);
        temperatureScroll.setViewportView(TemperatureStatsLbl);

        JPanel LEDPnl = new JPanel();
        tabbedPane.addTab("LED's", null, LEDPnl, null);
        LEDPnl.setLayout(null);

        MuteLED = new LEDIndicator();
        MuteLED.setLEDColor(LEDIndicator.ORANGE);
        MuteLED.setBounds(10, 11, 30, 30);
        LEDPnl.add(MuteLED);

        JLabel muteLbl = new JLabel("Muted");
        muteLbl.setFont(font);
        muteLbl.setBounds(50, 19, 107, 14);
        LEDPnl.add(muteLbl);

        InstructionsLED = new LEDIndicator();
        InstructionsLED.setLEDColor(LEDIndicator.BLUE);
        InstructionsLED.setBounds(10, 52, 30, 30);
        LEDPnl.add(InstructionsLED);

        JLabel instructionsLbl = new JLabel("Instructions");
        instructionsLbl.setFont(font);
        instructionsLbl.setBounds(50, 60, 107, 14);
        LEDPnl.add(instructionsLbl);

        AutonomousLED = new LEDIndicator();
        AutonomousLED.setLEDColor(LEDIndicator.PURPLE);
        AutonomousLED.setBounds(10, 93, 30, 30);
        LEDPnl.add(AutonomousLED);

        JLabel autonomusLbl = new JLabel("Autonomus");
        autonomusLbl.setFont(font);
        autonomusLbl.setBounds(50, 101, 107, 14);
        LEDPnl.add(autonomusLbl);

        UnusedLED1 = new LEDIndicator();
        UnusedLED1.setEnabled(false);
        UnusedLED1.setLEDColor(4);
        UnusedLED1.setBounds(10, 134, 30, 30);
        LEDPnl.add(UnusedLED1);

        UnusedLbl1 = new JLabel("unused");
        UnusedLbl1.setFont(font);
        UnusedLbl1.setBounds(50, 142, 107, 14);
        LEDPnl.add(UnusedLbl1);

        UnusedLED2 = new LEDIndicator();
        UnusedLED2.setEnabled(false);
        UnusedLED2.setLEDColor(4);
        UnusedLED2.setBounds(10, 175, 30, 30);
        LEDPnl.add(UnusedLED2);

        UnusedLbl2 = new JLabel("unused");
        UnusedLbl2.setFont(font);
        UnusedLbl2.setBounds(50, 183, 107, 14);
        LEDPnl.add(UnusedLbl2);

        UnusedLED3 = new LEDIndicator();
        UnusedLED3.setEnabled(false);
        UnusedLED3.setLEDColor(4);
        UnusedLED3.setBounds(217, 11, 30, 30);
        LEDPnl.add(UnusedLED3);

        UnusedLbl3 = new JLabel("unused");
        UnusedLbl3.setFont(font);
        UnusedLbl3.setBounds(257, 19, 107, 14);
        LEDPnl.add(UnusedLbl3);

        UnusedLED4 = new LEDIndicator();
        UnusedLED4.setEnabled(false);
        UnusedLED4.setLEDColor(4);
        UnusedLED4.setBounds(217, 52, 30, 30);
        LEDPnl.add(UnusedLED4);

        UnusedLbl4 = new JLabel("unused");
        UnusedLbl4.setFont(font);
        UnusedLbl4.setBounds(257, 60, 107, 14);
        LEDPnl.add(UnusedLbl4);

        UnusedLED5 = new LEDIndicator();
        UnusedLED5.setEnabled(false);
        UnusedLED5.setLEDColor(4);
        UnusedLED5.setBounds(217, 93, 30, 30);
        LEDPnl.add(UnusedLED5);

        UnusedLbl5 = new JLabel("unused");
        UnusedLbl5.setFont(font);
        UnusedLbl5.setBounds(257, 101, 107, 14);
        LEDPnl.add(UnusedLbl5);

        UnusedLED6 = new LEDIndicator();
        UnusedLED6.setEnabled(false);
        UnusedLED6.setLEDColor(4);
        UnusedLED6.setBounds(217, 134, 30, 30);
        LEDPnl.add(UnusedLED6);

        UnusedLbl6 = new JLabel("unused");
        UnusedLbl6.setFont(font);
        UnusedLbl6.setBounds(257, 142, 107, 14);
        LEDPnl.add(UnusedLbl6);

        UnusedLED7 = new LEDIndicator();
        UnusedLED7.setEnabled(false);
        UnusedLED7.setLEDColor(4);
        UnusedLED7.setBounds(217, 175, 30, 30);
        LEDPnl.add(UnusedLED7);

        UnusedLbl7 = new JLabel("unused");
        UnusedLbl7.setFont(font);
        UnusedLbl7.setBounds(257, 183, 107, 14);
        LEDPnl.add(UnusedLbl7);
    }
    
    public void update(int whichRover) {
        if (roverLock != -1) {
            whichRover = roverLock;
        }
        if (whichRover == -1 || rovers.size() == 0) {
            setToBlank();
            PageLeftBtn.setEnabled(false);
            PageRightBtn.setEnabled(rovers.size() != 0);
            tabbedPane.setVisible(!inHUD);
            return;
        }
        doUpdate(whichRover);
    }

    private void doUpdate(int whichRover){
        PageLeftBtn.setEnabled(roverLock == -1);
        PageRightBtn.setEnabled(whichRover != rovers.size()-1 && roverLock == -1);
        tabbedPane.setVisible(true);

        RoverObject rover = rovers.get(whichRover);
        PlatformState state = rover.getState();
        RoverNameLbl.setText(rover.getName());
        SerialHistoryLbl.setText(rover.getSerialHistory());
        MovementStatsLbl.setText(String.format(movementPattern,
                formatDouble(state.<Double>get("x")),
                formatDouble(Math.toDegrees(state.<Double[]>get("wheel_speed")[RoverWheels.FL.getValue()])),
                formatDouble(state.<Double>get("y")),
                formatDouble(Math.toDegrees(state.<Double[]>get("wheel_speed")[RoverWheels.FR.getValue()])),
                formatDouble(Math.toDegrees(state.<Double>get("direction"))),
                formatDouble(Math.toDegrees(state.<Double[]>get("wheel_speed")[RoverWheels.BL.getValue()])),
                formatDouble(Math.toDegrees(state.<Double[]>get("wheel_speed")[RoverWheels.BR.getValue()])),
                formatDouble(state.<Double>get("speed")),
                formatDouble(Math.cos(state.<Double>get("direction") * state.<Double>get("speed"))),
                formatDouble(Math.toDegrees(map.getSlopeAt(new DecimalPoint(state.<Double>get("x"), state.<Double>get("y")), state.<Double>get("direction")))),
                formatDouble(Math.sin(state.<Double>get("direction") * state.<Double>get("speed"))),
                formatDouble(Math.toDegrees(map.getCrossSlopeAt(new DecimalPoint(state.<Double>get("x"), state.<Double>get("y")), state.<Double>get("direction")))),
                formatDouble(Math.toDegrees(state.<Double>get("angular_velocity"))),
                formatDouble(state.<Double>get("slip_velocity")),
                formatDouble(state.<Double>get("acceleration")),
                formatDouble(Math.toDegrees(state.<Double>get("angular_acceleration"))),
                formatDouble(state.<Double>get("slip_acceleration"))
        ));
        ElectricalStatsLbl.setText(String.format(electricalPattern,
                formatDouble(state.<Double>get("battery_charge")),
                formatDouble(state.<Double>get("battery_voltage")),
                formatDouble(state.<Double>get("battery_current")),
                formatDouble(Math.abs(state.<Double[]>get("motor_current")[RoverWheels.FL.getValue()])),
                formatDouble(Math.abs(state.<Double[]>get("motor_current")[RoverWheels.FR.getValue()])),
                formatDouble(Math.abs(state.<Double[]>get("motor_current")[RoverWheels.BL.getValue()])),
                formatDouble(Math.abs(state.<Double[]>get("motor_current")[RoverWheels.BR.getValue()])),
                formatDouble(state.<Double[]>get("motor_voltage")[RoverWheels.FL.getValue()]),
                formatDouble(state.<Double[]>get("motor_voltage")[RoverWheels.FR.getValue()]),
                formatDouble(state.<Double[]>get("motor_voltage")[RoverWheels.BL.getValue()]),
                formatDouble(state.<Double[]>get("motor_voltage")[RoverWheels.BR.getValue()])
        ));
        TemperatureStatsLbl.setText(String.format(temperaturePattern,
                formatDouble(state.<Double[]>get("motor_temp")[RoverWheels.FL.getValue()]),
                formatDouble(state.<Double[]>get("motor_temp")[RoverWheels.FR.getValue()]),
                formatDouble(state.<Double[]>get("motor_temp")[RoverWheels.BL.getValue()]),
                formatDouble(state.<Double[]>get("motor_temp")[RoverWheels.BR.getValue()]),
                formatDouble(state.<Double>get("battery_temp")),
                formatDouble(-30) //TODO add temp map
        ));
        MuteLED.setSelected(rover.getLEDisLit("Mute"));
        InstructionsLED.setSelected(rover.getLEDisLit("Instructions"));
        AutonomousLED.setSelected(rover.getLEDisLit("Autonomus"));
    }

    private void setToBlank() {
        RoverNameLbl.setText("Undefined");
        SerialHistoryLbl.setText("");
        MovementStatsLbl.setText(String.format(movementPattern, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        ElectricalStatsLbl.setText(String.format(electricalPattern, null, null, null, null, null, null, null, null, null, null, null, null, null));
        TemperatureStatsLbl.setText(String.format(temperaturePattern, null, null, null, null, null, null));
        MuteLED.setSelected(false);
        InstructionsLED.setSelected(false);
        AutonomousLED.setSelected(false);
    }

    public void setRoverList(ArrayList<RoverObject> rovers){
        this.rovers = rovers;
    }

    public void setHUDMode(boolean inHUD){
        this.inHUD = inHUD;
    }

    public void lockOnRover(int which){
        roverLock = which;
        update(which);
    }

    public void unlock(){
        int rover = roverLock;
        roverLock = -1;
        update(rover);
    }

    public void addPageForwardAction(ActionListener listener){
        PageRightBtn.addActionListener(listener);
    }
    
    public void addPageBackAction(ActionListener listener){
        PageLeftBtn.addActionListener(listener);
    }

    // round doubles so they're pretty to display
    private String formatDouble(double in){
        String out = "";
        if (Math.abs(in) < Integer.MAX_VALUE/1000){
            if (in < 0){
                in *= -1;
                out = "-";
            }
            int whole = (int)in;
            out += whole;
            int part = (int)((in * 1000) - whole*1000);
            if (part == 0){
                out += ".000";
            }
            else if (part < 10){
                out += "." + part + "00";
            }
            else if (part < 100){
                out += "." + part + "0";
            }
            else {
                out += "." + part;
            }
        }
        else {
            out = (int)in + "";
        }
        return out;
    }
    
}
