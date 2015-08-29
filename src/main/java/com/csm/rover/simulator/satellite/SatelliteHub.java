package com.csm.rover.simulator.satellite;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.csm.rover.simulator.objects.SyncronousThread;
import com.csm.rover.simulator.visual.LEDIndicator;
import com.csm.rover.simulator.visual.Panel;

public class SatelliteHub extends Panel{
	
	private static final long serialVersionUID = 2063498616789417121L;
	
	private SatelliteObject[] satellites;
	private int[][] standardDisplayLinks;
	int currentPage = 0;
	
	private int numberOfDisplays = 9;
	private JPanel[] SatelliteDisplayWindow = new JPanel[numberOfDisplays];
	private JLabel[] SatelliteNameLbl = new JLabel[numberOfDisplays];
	private JButton[] PageLeftBtn = new JButton[numberOfDisplays];
	private JButton[] PageRightBtn = new JButton[numberOfDisplays];
	private JTabbedPane[] tabbedPane = new JTabbedPane[numberOfDisplays];
	private JScrollPane[] scrollPane = new JScrollPane[numberOfDisplays];
	private JTextArea[] SerialHistoryLbl = new JTextArea[numberOfDisplays];
	private JScrollPane[] scrollPane_1 = new JScrollPane[numberOfDisplays];
	private JTextArea[] MovementStatsLbl = new JTextArea[numberOfDisplays];
	private JScrollPane[] scrollPane_2 = new JScrollPane[numberOfDisplays];
	private JTextArea[] ElectricalStatsLbl = new JTextArea[numberOfDisplays];
	private JScrollPane[] TemperatureScroll = new JScrollPane[numberOfDisplays];
	private JTextArea[] TemperatureStatsLbl = new JTextArea[numberOfDisplays];
	private JPanel[] LEDPnl = new JPanel[numberOfDisplays];
	private LEDIndicator[] MuteLED = new LEDIndicator[numberOfDisplays];
	private JLabel[] MuteLbl = new JLabel[numberOfDisplays];
	private LEDIndicator[] InstructionsLED = new LEDIndicator[numberOfDisplays];
	private JLabel[] InstructionsLbl = new JLabel[numberOfDisplays];
	private LEDIndicator[] AutonomusLED = new LEDIndicator[numberOfDisplays];
	private JLabel[] AutonomusLbl = new JLabel[numberOfDisplays];
	private LEDIndicator[] UnusedLED1 = new LEDIndicator[numberOfDisplays];
	private JLabel[] UnusedLbl1 = new JLabel[numberOfDisplays];
	private LEDIndicator[] UnusedLED2 = new LEDIndicator[numberOfDisplays];
	private JLabel[] UnusedLbl2 = new JLabel[numberOfDisplays];
	private LEDIndicator[] UnusedLED3 = new LEDIndicator[numberOfDisplays];
	private JLabel[] UnusedLbl3 = new JLabel[numberOfDisplays];
	private LEDIndicator[] UnusedLED4 = new LEDIndicator[numberOfDisplays];
	private JLabel[] UnusedLbl4 = new JLabel[numberOfDisplays];
	private LEDIndicator[] UnusedLED5 = new LEDIndicator[numberOfDisplays];
	private JLabel[] UnusedLbl5 = new JLabel[numberOfDisplays];
	private LEDIndicator[] UnusedLED6 = new LEDIndicator[numberOfDisplays];
	private JLabel[] UnusedLbl6 = new JLabel[numberOfDisplays];
	private LEDIndicator[] UnusedLED7 = new LEDIndicator[numberOfDisplays];
	private JLabel[] UnusedLbl7 = new JLabel[numberOfDisplays];

	public SatelliteHub(Dimension size){
		super(size, "Satellite Hub");
		
		standardDisplayLinks = new int[1][numberOfDisplays];
		int x = 0;
		while (x < numberOfDisplays){
			standardDisplayLinks[0][x] = -1;
			x++;
		}
		
		initialize();
		this.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT){
					if (currentPage > 0){
						currentPage--;
						updateDisplays();
					}
				}
				else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT){
					if (currentPage < standardDisplayLinks.length-1){
						currentPage++;
						updateDisplays();
					}
				}
			}		
		});
		
		setVisible(false);
	}
	
	private void initialize(){
		int x = 0;
		while (x < numberOfDisplays){
			SatelliteDisplayWindow[x] = new JPanel();
			SatelliteDisplayWindow[x].setLayout(new BorderLayout());
			SatelliteDisplayWindow[x].setPreferredSize(new Dimension(450, 325));
			SatelliteDisplayWindow[x].setBounds(0, 0, 450, 325);
			this.add(SatelliteDisplayWindow[x]);
			
			JPanel rack = new JPanel();
			rack.setLayout(new BorderLayout());
			rack.setOpaque(false);
			SatelliteDisplayWindow[x].add(rack, BorderLayout.NORTH);
			
			SatelliteNameLbl[x] = new JLabel("Undefined");
			SatelliteNameLbl[x].setFont(new Font("Trebuchet MS", Font.BOLD, 17));
			SatelliteNameLbl[x].setHorizontalAlignment(JLabel.CENTER);
			rack.add(SatelliteNameLbl[x], BorderLayout.CENTER);
			
			final int a = x;
			
			PageLeftBtn[x] = new JButton("<");
			PageLeftBtn[x].addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					changeLinkedSatellite(a, -1);
				}
			});
			PageLeftBtn[x].setEnabled(false);
			rack.add(PageLeftBtn[x], BorderLayout.WEST);
			
			PageRightBtn[x] = new JButton(">");
			PageRightBtn[x].addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					changeLinkedSatellite(a, 1);
				}
			});
			PageRightBtn[x].setEnabled(false);
			rack.add(PageRightBtn[x], BorderLayout.EAST);
			
			tabbedPane[x] = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane[x].setOpaque(false);
			tabbedPane[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
			SatelliteDisplayWindow[x].add(tabbedPane[x], BorderLayout.CENTER);
			
			scrollPane[x] = new JScrollPane();
			scrollPane[x].setOpaque(false);
			tabbedPane[x].addTab("Serial", null, scrollPane[x], null);
			
			SerialHistoryLbl[x] = new JTextArea();
			scrollPane[x].setViewportView(SerialHistoryLbl[x]);
			SerialHistoryLbl[x].setOpaque(false);
			SerialHistoryLbl[x].setEditable(false);
			SerialHistoryLbl[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			
			scrollPane_1[x] = new JScrollPane();
			tabbedPane[x].addTab("Movement", null, scrollPane_1[x], null);
			
			MovementStatsLbl[x] = new JTextArea();
			MovementStatsLbl[x].setTabSize(14);
			MovementStatsLbl[x].setText("X:  m\tAngular Spin FL:  deg/s\r\nY:  m\tAngluar Spin FR:  deg/s\r\nAngle:  deg\tAngular Spin BL:  deg/s\r\n\tAngular Spin BR:  deg/s\r\nSpeed:  m/s\r\nVelocity X:  m/s\r\nVelocity Y:  m/s\r\nAngular Velocity:  deg/s\r\nSlip Velocity:  m/s\r\n\r\nAcceleration:  m/s^2\r\nAngular Acceleration:  deg/s^2\r\nSlip Acceleration:  m/s^2");
			MovementStatsLbl[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			MovementStatsLbl[x].setOpaque(false);
			MovementStatsLbl[x].setWrapStyleWord(true);
			MovementStatsLbl[x].setLineWrap(true);
			MovementStatsLbl[x].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			MovementStatsLbl[x].setEditable(false);
			scrollPane_1[x].setViewportView(MovementStatsLbl[x]);
			
			scrollPane_2[x] = new JScrollPane();
			tabbedPane[x].addTab("Electrical", null, scrollPane_2[x], null);
			
			ElectricalStatsLbl[x] = new JTextArea();
			ElectricalStatsLbl[x].setText("SOC:  %\r\nBattery Charge:  C\r\nBattery CP Charge:  C\r\nBattery Voltage:  V\r\nBattery Current:  mA\r\n\r\nMotor Current FL:  mA\r\nMotor Current FR:  mA\r\nMotor Current BL:  mA\r\nMotor Current BR:  mA\r\n\r\nMotor Voltage FL:  V\r\nMotor Voltage FR:  V\r\nMotor Voltage BL:  V\r\nMotor Voltage BR:  V");
			ElectricalStatsLbl[x].setTabSize(14);
			ElectricalStatsLbl[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			ElectricalStatsLbl[x].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			ElectricalStatsLbl[x].setEditable(false);
			ElectricalStatsLbl[x].setLineWrap(true);
			ElectricalStatsLbl[x].setOpaque(false);
			ElectricalStatsLbl[x].setWrapStyleWord(true);
			scrollPane_2[x].setViewportView(ElectricalStatsLbl[x]);
			
			TemperatureScroll[x] = new JScrollPane();
			tabbedPane[x].addTab("Temperature", null, TemperatureScroll[x], null);
			
			TemperatureStatsLbl[x] = new JTextArea();
			TemperatureStatsLbl[x].setText("Motor Temperature FL:  *c\r\nMotor Temperature FR:  *c\r\nMotor Temperature BL:  *c\r\nMotor Temperature BR:  *c\r\n\r\nBattery Temperature:  *c\n\n" + "Air Temperature:  *c");
			TemperatureStatsLbl[x].setOpaque(false);
			TemperatureStatsLbl[x].setWrapStyleWord(true);
			TemperatureStatsLbl[x].setEditable(false);
			TemperatureStatsLbl[x].setTabSize(14);
			TemperatureStatsLbl[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			TemperatureStatsLbl[x].setLineWrap(true);
			TemperatureScroll[x].setViewportView(TemperatureStatsLbl[x]);
			
			LEDPnl[x] = new JPanel();
			tabbedPane[x].addTab("LED's", null, LEDPnl[x], null);
			LEDPnl[x].setLayout(null);
			
			MuteLED[x] = new LEDIndicator();
			MuteLED[x].setLEDColor(LEDIndicator.ORANGE);
			MuteLED[x].setBounds(10, 11, 30, 30);
			LEDPnl[x].add(MuteLED[x]);
			
			MuteLbl[x] = new JLabel("Muted");
			MuteLbl[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			MuteLbl[x].setBounds(50, 19, 107, 14);
			LEDPnl[x].add(MuteLbl[x]);
			
			InstructionsLED[x] = new LEDIndicator();
			InstructionsLED[x].setLEDColor(LEDIndicator.BLUE);
			InstructionsLED[x].setBounds(10, 52, 30, 30);
			LEDPnl[x].add(InstructionsLED[x]);
			
			InstructionsLbl[x] = new JLabel("Instructions");
			InstructionsLbl[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			InstructionsLbl[x].setBounds(50, 60, 107, 14);
			LEDPnl[x].add(InstructionsLbl[x]);
			
			AutonomusLED[x] = new LEDIndicator();
			AutonomusLED[x].setLEDColor(LEDIndicator.PURPLE);
			AutonomusLED[x].setBounds(10, 93, 30, 30);
			LEDPnl[x].add(AutonomusLED[x]);
			
			AutonomusLbl[x] = new JLabel("Autonomus");
			AutonomusLbl[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			AutonomusLbl[x].setBounds(50, 101, 107, 14);
			LEDPnl[x].add(AutonomusLbl[x]);
			
			UnusedLED1[x] = new LEDIndicator();
			UnusedLED1[x].setEnabled(false);
			UnusedLED1[x].setLEDColor(4);
			UnusedLED1[x].setBounds(10, 134, 30, 30);
			LEDPnl[x].add(UnusedLED1[x]);
			
			UnusedLbl1[x] = new JLabel("unused");
			UnusedLbl1[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			UnusedLbl1[x].setBounds(50, 142, 107, 14);
			LEDPnl[x].add(UnusedLbl1[x]);
			
			UnusedLED2[x] = new LEDIndicator();
			UnusedLED2[x].setEnabled(false);
			UnusedLED2[x].setLEDColor(4);
			UnusedLED2[x].setBounds(10, 175, 30, 30);
			LEDPnl[x].add(UnusedLED2[x]);
			
			UnusedLbl2[x] = new JLabel("unused");
			UnusedLbl2[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			UnusedLbl2[x].setBounds(50, 183, 107, 14);
			LEDPnl[x].add(UnusedLbl2[x]);
			
			UnusedLED3[x] = new LEDIndicator();
			UnusedLED3[x].setEnabled(false);
			UnusedLED3[x].setLEDColor(4);
			UnusedLED3[x].setBounds(217, 11, 30, 30);
			LEDPnl[x].add(UnusedLED3[x]);
			
			UnusedLbl3[x] = new JLabel("unused");
			UnusedLbl3[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			UnusedLbl3[x].setBounds(257, 19, 107, 14);
			LEDPnl[x].add(UnusedLbl3[x]);
			
			UnusedLED4[x] = new LEDIndicator();
			UnusedLED4[x].setEnabled(false);
			UnusedLED4[x].setLEDColor(4);
			UnusedLED4[x].setBounds(217, 52, 30, 30);
			LEDPnl[x].add(UnusedLED4[x]);
			
			UnusedLbl4[x] = new JLabel("unused");
			UnusedLbl4[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			UnusedLbl4[x].setBounds(257, 60, 107, 14);
			LEDPnl[x].add(UnusedLbl4[x]);
			
			UnusedLED5[x] = new LEDIndicator();
			UnusedLED5[x].setEnabled(false);
			UnusedLED5[x].setLEDColor(4);
			UnusedLED5[x].setBounds(217, 93, 30, 30);
			LEDPnl[x].add(UnusedLED5[x]);
			
			UnusedLbl5[x] = new JLabel("unused");
			UnusedLbl5[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			UnusedLbl5[x].setBounds(257, 101, 107, 14);
			LEDPnl[x].add(UnusedLbl5[x]);
			
			UnusedLED6[x] = new LEDIndicator();
			UnusedLED6[x].setEnabled(false);
			UnusedLED6[x].setLEDColor(4);
			UnusedLED6[x].setBounds(217, 134, 30, 30);
			LEDPnl[x].add(UnusedLED6[x]);
			
			UnusedLbl6[x] = new JLabel("unused");
			UnusedLbl6[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			UnusedLbl6[x].setBounds(257, 142, 107, 14);
			LEDPnl[x].add(UnusedLbl6[x]);
			
			UnusedLED7[x] = new LEDIndicator();
			UnusedLED7[x].setEnabled(false);
			UnusedLED7[x].setLEDColor(4);
			UnusedLED7[x].setBounds(217, 175, 30, 30);
			LEDPnl[x].add(UnusedLED7[x]);
			
			UnusedLbl7[x] = new JLabel("unused");
			UnusedLbl7[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
			UnusedLbl7[x].setBounds(257, 183, 107, 14);
			LEDPnl[x].add(UnusedLbl7[x]);
			
			x++;
		}
	}
	
	public void start(){
		new SyncronousThread(500, new Runnable(){
			public void run(){
				updateDisplays();
			}
		}, SyncronousThread.FOREVER, "Satellite Hub Update");
		int x = 0;
		while (x < satellites.length){
			satellites[x].start();
			x++;
		}
	}
	
	//adds the rover objects to the hub
	public void setSatellites(SatelliteObject[] sats){
		this.satellites = sats;

		standardDisplayLinks = new int[sats.length][numberOfDisplays];
		int x = 0;
		while (x < sats.length){
			//set the first n displays to the the nth rover
			standardDisplayLinks[x/numberOfDisplays][x%numberOfDisplays] = x;
			x++;
		}
		while (x < numberOfDisplays*standardDisplayLinks.length){
			standardDisplayLinks[x/numberOfDisplays][x%numberOfDisplays] = -1;
			x++;
		}
		
		updateDisplays();
	}
	
	public void updateDisplays(){
		//TODO Satellite Value Displays
		int x = 0;
		while (x < numberOfDisplays){
			if (standardDisplayLinks[currentPage][x] == -1){
				SatelliteNameLbl[x].setText("Undefined");
				PageLeftBtn[x].setEnabled(false);
			}
			else {
				PageLeftBtn[x].setVisible(true);
				PageLeftBtn[x].setEnabled(true);
				tabbedPane[x].setVisible(true);
				SatelliteNameLbl[x].setText(satellites[standardDisplayLinks[currentPage][x]].getName());
				SerialHistoryLbl[x].setText(satellites[standardDisplayLinks[currentPage][x]].getSerialHistory());
				/*MovementStatsLbl[x].setText("X: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getLocation().getX()) 
						+ " m\tAngular Spin FL: " + formatDouble(Math.toDegrees(satellites[standardDisplayLinks[currentPage][x]].getWheelSpeed(RoverObject.FL))) 
						+ " deg/s\nY: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getLocation().getY()) 
						+ " m\tAngluar Spin FR: " + formatDouble(Math.toDegrees(satellites[standardDisplayLinks[currentPage][x]].getWheelSpeed(RoverObject.FR))) 
						+ " deg/s\nAngle: " + formatDouble(Math.toDegrees(satellites[standardDisplayLinks[currentPage][x]].getDirection())) 
						+ " deg\tAngular Spin BL: " + formatDouble(Math.toDegrees(satellites[standardDisplayLinks[currentPage][x]].getWheelSpeed(RoverObject.BL))) 
						+ " deg/s\n\tAngular Spin BR: " + formatDouble(Math.toDegrees(satellites[standardDisplayLinks[currentPage][x]].getWheelSpeed(RoverObject.BR))) 
						+ " deg/s\nSpeed: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getSpeed()) 
						+ " m/s\nVelocity X: " + formatDouble(Math.cos(satellites[standardDisplayLinks[currentPage][x]].getDirection()) * satellites[standardDisplayLinks[currentPage][x]].getSpeed()) 
						+ " m/s\nVelocity Y: " + formatDouble(Math.sin(satellites[standardDisplayLinks[currentPage][x]].getDirection()) * satellites[standardDisplayLinks[currentPage][x]].getSpeed()) 
						+ " m/s\nAngular Velocity: " + formatDouble(Math.toDegrees(satellites[standardDisplayLinks[currentPage][x]].getAngularVelocity())) 
						+ " deg/s\nSlip Velocity: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getSlipVelocity())
						+ " m/s\n\nAcceleration: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getAcceleration()) 
						+ " m/s^2\nAngular Acceleration: " + formatDouble(Math.toDegrees(satellites[standardDisplayLinks[currentPage][x]].getAngularAcceleration()))
						+ " deg/s^2\nSlip Acceleration: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getSlipAcceleration()) + " m/s^s");
				ElectricalStatsLbl[x].setText("SOC: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getSOC())
						+ "\nBattery Charge: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getBatteryCharge()) 
						+ " C\nBattery CP Charge: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getBatterCPCharge())
						+ " C\nBattery Voltage: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getBatteryVoltage()) 
						+ " V\nBattery Current: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getBatteryCurrent()) 
						+ " A\n\nMotor Current FL: " + formatDouble(Math.abs(satellites[standardDisplayLinks[currentPage][x]].getMotorCurrent(RoverObject.FL))) 
						+ " A\nMotor Current FR: " + formatDouble(Math.abs(satellites[standardDisplayLinks[currentPage][x]].getMotorCurrent(RoverObject.FR))) 
						+ " A\nMotor Current BL: " + formatDouble(Math.abs(satellites[standardDisplayLinks[currentPage][x]].getMotorCurrent(RoverObject.BL))) 
						+ " A\nMotor Current BR: " + formatDouble(Math.abs(satellites[standardDisplayLinks[currentPage][x]].getMotorCurrent(RoverObject.BR)))
						+ " A\n\nMotor Voltage FL: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getMotorVoltage(RoverObject.FL))
						+ " V\nMotor Voltage FR: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getMotorVoltage(RoverObject.FR))
						+ " V\nMotor Voltage BL: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getMotorVoltage(RoverObject.BL))
						+ " V\nMotor Voltage BR: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getMotorVoltage(RoverObject.BR)) + " V");
				TemperatureStatsLbl[x].setText("Motor Temperature FL: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getMotorTemp(RoverObject.FL)) + " *c\n" +
						"Motor Temperature FR: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getMotorTemp(RoverObject.FR)) +" *c\n" +
						"Motor Temperature BL: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getMotorTemp(RoverObject.BL)) + " *c\n" +
						"Motor Temperature BR: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getMotorTemp(RoverObject.BR)) + " *c\n\n" +
						"Battery Temperature: " + formatDouble(satellites[standardDisplayLinks[currentPage][x]].getBatteryTemperature()) + " *c\n\n" +
						"Air Temperature: " + formatDouble(Access.getMapTemperatureAtPoint(satellites[standardDisplayLinks[currentPage][x]].getLocation())) + "*c");
				MuteLED[x].setSelected(satellites[standardDisplayLinks[currentPage][x]].getLEDisLit("Mute"));
				InstructionsLED[x].setSelected(satellites[standardDisplayLinks[currentPage][x]].getLEDisLit("Instructions"));
				AutonomusLED[x].setSelected(satellites[standardDisplayLinks[currentPage][x]].getLEDisLit("Autonomus"));	*/
			}
			PageLeftBtn[x].setEnabled(standardDisplayLinks[currentPage][x] >= 0);
			try {
				PageRightBtn[x].setEnabled(standardDisplayLinks[currentPage][x] < satellites.length-1);
			}
			catch (Exception e){
				PageRightBtn[x].setEnabled(false);
			}
			x++;
		}
	}
	
	//change which rover is connected to a certain display
	private void changeLinkedSatellite(int display, int by){
		try {
			satellites[0].equals("exists");
		}
		catch (Exception e){
			return;
		}
		finally {
			standardDisplayLinks[currentPage][display] += by;
			updateDisplays();
		}
	}
	
	// round doubles so they're pretty to display
		@SuppressWarnings("unused")
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
