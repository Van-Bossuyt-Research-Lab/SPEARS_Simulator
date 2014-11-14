package rover;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import objects.ThreadTimer;
import visual.LEDIndicator;
import visual.Panel;
import wrapper.Access;

public class RoverHub extends Panel {
	
	private RoverObj[] rovers;
	private boolean inHUDmode = false;
	private int[][] standardDisplayLinks;
	int currentPage = 0;
	
	private int[] HUDDisplayLinks;
	private int numberOfHUDDisplays;
	
	private int numberOfDisplays = 9;
	private JPanel[] RoverDisplayWindow = new JPanel[numberOfDisplays];
	private JLabel[] RoverNameLbl = new JLabel[numberOfDisplays];
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

	public RoverHub(Dimension size){
		super(size, "Rover Hub");
		
		standardDisplayLinks = new int[1][numberOfDisplays];
		int x = 0;
		while (x < numberOfDisplays){
			standardDisplayLinks[0][x] = -1;
			x++;
		}
		
		numberOfHUDDisplays = 4;
		if (numberOfHUDDisplays > numberOfDisplays){
			numberOfHUDDisplays = numberOfDisplays;
		}
		HUDDisplayLinks = new int[numberOfHUDDisplays];
		x = 0;
		while (x < numberOfHUDDisplays){
			HUDDisplayLinks[x] = -1;
			x++;
		}
		
		initialize();
		this.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (!inHUDmode){
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
				else {
					Access.requestFocusToMap();
				}
			}		
		});
		
		setVisible(false);
	}
	
	private void initialize(){
		int x = 0;
		while (x < numberOfDisplays){
			RoverDisplayWindow[x] = new JPanel();
			RoverDisplayWindow[x].setLayout(new BorderLayout());
			RoverDisplayWindow[x].setPreferredSize(new Dimension(450, 325));
			RoverDisplayWindow[x].setBounds(0, 0, 450, 325);
			this.add(RoverDisplayWindow[x]);
			
			JPanel rack = new JPanel();
			rack.setLayout(new BorderLayout());
			rack.setOpaque(false);
			RoverDisplayWindow[x].add(rack, BorderLayout.NORTH);
			
			RoverNameLbl[x] = new JLabel("Undefined");
			RoverNameLbl[x].setFont(new Font("Trebuchet MS", Font.BOLD, 17));
			RoverNameLbl[x].setHorizontalAlignment(JLabel.CENTER);
			rack.add(RoverNameLbl[x], BorderLayout.CENTER);
			
			final int a = x;
			
			PageLeftBtn[x] = new JButton("<");
			PageLeftBtn[x].addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					changeLinkedRover(a, -1);
				}
			});
			PageLeftBtn[x].setEnabled(false);
			rack.add(PageLeftBtn[x], BorderLayout.WEST);
			
			PageRightBtn[x] = new JButton(">");
			PageRightBtn[x].addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					changeLinkedRover(a, 1);
				}
			});
			PageRightBtn[x].setEnabled(false);
			rack.add(PageRightBtn[x], BorderLayout.EAST);
			
			tabbedPane[x] = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane[x].setOpaque(false);
			tabbedPane[x].setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
			RoverDisplayWindow[x].add(tabbedPane[x], BorderLayout.CENTER);
			
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
		ThreadTimer updateTimer = new ThreadTimer(500, new Runnable(){
			public void run(){
				updateDisplays();
			}
		}, ThreadTimer.FOREVER);
		int x = 0;
		while (x < rovers.length){
			rovers[x].start();
			x++;
		}
	}
	
	//adds the rover objects to the hub
	public void setRovers(RoverObj[] rovers){
		this.rovers = rovers;
		Access.addRoversToMap(rovers);

		standardDisplayLinks = new int[rovers.length][numberOfDisplays];
		int x = 0;
		while (x < rovers.length){
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
	
	public void setfocusedRover(int which){
		if (which >= 0 && which < rovers.length){
			HUDDisplayLinks[0] = which;
			updateDisplays();
		}
	}
	
	public void updateDisplays(){
		int x = 0;
		while (x < numberOfDisplays){
			if (inHUDmode){
				if (HUDDisplayLinks[x] == -1){
					PageLeftBtn[x].setVisible(false);
					tabbedPane[x].setVisible(false);
					RoverDisplayWindow[x].setOpaque(false);
					RoverNameLbl[x].setText("Undefined");
				}
				else {
					PageLeftBtn[x].setVisible(true);
					tabbedPane[x].setVisible(true);
					RoverDisplayWindow[x].setOpaque(true);
					RoverNameLbl[x].setText(rovers[HUDDisplayLinks[x]].getName());
					MovementStatsLbl[x].setText("X: " + formatDouble(rovers[HUDDisplayLinks[x]].getLocation().getX()) 
							+ " m\tAngular Spin FL: " + formatDouble(Math.toDegrees(rovers[HUDDisplayLinks[x]].getWheelSpeed(RoverObj.FL))) 
							+ " deg/s\nY: " + formatDouble(rovers[HUDDisplayLinks[x]].getLocation().getY()) 
							+ " m\tAngluar Spin FR: " + formatDouble(Math.toDegrees(rovers[HUDDisplayLinks[x]].getWheelSpeed(RoverObj.FR))) 
							+ " deg/s\nAngle: " + formatDouble(Math.toDegrees(rovers[HUDDisplayLinks[x]].getDirection())) 
							+ " deg\tAngular Spin BL: " + formatDouble(Math.toDegrees(rovers[HUDDisplayLinks[x]].getWheelSpeed(RoverObj.BL))) 
							+ " deg/s\n\tAngular Spin BR: " + formatDouble(Math.toDegrees(rovers[HUDDisplayLinks[x]].getWheelSpeed(RoverObj.BR))) 
							+ " deg/s\nSpeed: " + formatDouble(rovers[HUDDisplayLinks[x]].getSpeed()) 
							+ " m/s\nVelocity X: " + formatDouble(Math.cos(rovers[HUDDisplayLinks[x]].getDirection()) * rovers[HUDDisplayLinks[x]].getSpeed()) 
							+ " m/s\nVelocity Y: " + formatDouble(Math.sin(rovers[HUDDisplayLinks[x]].getDirection()) * rovers[HUDDisplayLinks[x]].getSpeed()) 
							+ " m/s\nAngular Velocity: " + formatDouble(Math.toDegrees(rovers[HUDDisplayLinks[x]].getAngularVelocity())) 
							+ " deg/s\nSlip Velocity: " + formatDouble(rovers[HUDDisplayLinks[x]].getSlipVelocity())
							+ " m/s\n\nAcceleration: " + formatDouble(rovers[HUDDisplayLinks[x]].getAcceleration()) 
							+ " m/s^2\nAngular Acceleration: " + formatDouble(Math.toDegrees(rovers[HUDDisplayLinks[x]].getAngularAcceleration()))
							+ " deg/s^2\nSlip Acceleration: " + formatDouble(rovers[HUDDisplayLinks[x]].getSlipAcceleration()) + " m/s^s");
					ElectricalStatsLbl[x].setText("SOC: " + formatDouble(rovers[HUDDisplayLinks[x]].getSOC())
							+ "\nBattery Charge: " + formatDouble(rovers[HUDDisplayLinks[x]].getBatteryCharge()) 
							+ " C\nBattery CP Charge: " + formatDouble(rovers[HUDDisplayLinks[x]].getBatterCPCharge())
							+ " C\nBattery Voltage: " + formatDouble(rovers[HUDDisplayLinks[x]].getBatteryVoltage()) 
							+ " V\nBattery Current: " + formatDouble(rovers[HUDDisplayLinks[x]].getBatteryCurrent()) 
							+ " A\n\nMotor Current FL: " + formatDouble(Math.abs(rovers[HUDDisplayLinks[x]].getMotorCurrent(RoverObj.FL))) 
							+ " A\nMotor Current FR: " + formatDouble(Math.abs(rovers[HUDDisplayLinks[x]].getMotorCurrent(RoverObj.FR))) 
							+ " A\nMotor Current BL: " + formatDouble(Math.abs(rovers[HUDDisplayLinks[x]].getMotorCurrent(RoverObj.BL))) 
							+ " A\nMotor Current BR: " + formatDouble(Math.abs(rovers[HUDDisplayLinks[x]].getMotorCurrent(RoverObj.BR)))
							+ " A\n\nMotor Voltage FL: " + formatDouble(rovers[HUDDisplayLinks[x]].getMotorVoltage(RoverObj.FL))
							+ " V\nMotor Voltage FR: " + formatDouble(rovers[HUDDisplayLinks[x]].getMotorVoltage(RoverObj.FR))
							+ " V\nMotor Voltage BL: " + formatDouble(rovers[HUDDisplayLinks[x]].getMotorVoltage(RoverObj.BL))
							+ " V\nMotor Voltage BR: " + formatDouble(rovers[HUDDisplayLinks[x]].getMotorVoltage(RoverObj.BR)) + " V");
					TemperatureStatsLbl[x].setText("Motor Temperature FL: " + formatDouble(rovers[HUDDisplayLinks[x]].getMotorTemp(RoverObj.FL)) + " *c\n" +
							"Motor Temperature FR: " + formatDouble(rovers[HUDDisplayLinks[x]].getMotorTemp(RoverObj.FR)) +" *c\n" +
							"Motor Temperature BL: " + formatDouble(rovers[HUDDisplayLinks[x]].getMotorTemp(RoverObj.BL)) + " *c\n" +
							"Motor Temperature BR: " + formatDouble(rovers[HUDDisplayLinks[x]].getMotorTemp(RoverObj.BR)) + " *c\n\n" +
							"Battery Temperature: " + formatDouble(rovers[HUDDisplayLinks[x]].getBatteryTemperature()) + " *c\n\n" +
							"Air Temperature: " + formatDouble(Access.getMapTemperatureAtPoint(rovers[HUDDisplayLinks[x]].getLocation())) + "*c");
				}	
				PageLeftBtn[x].setEnabled(HUDDisplayLinks[x] >= 0 && x != 0);
				try {
					PageRightBtn[x].setEnabled(HUDDisplayLinks[x] < rovers.length-1 && x != 0);
				}
				catch (Exception e){
					PageRightBtn[x].setEnabled(false);
				}
				if (x+1 == numberOfHUDDisplays){
					break;
				}
			}
			else {
				if (standardDisplayLinks[currentPage][x] == -1){
					RoverNameLbl[x].setText("Undefined");
					PageLeftBtn[x].setEnabled(false);
				}
				else {
					PageLeftBtn[x].setVisible(true);
					PageLeftBtn[x].setEnabled(true);
					tabbedPane[x].setVisible(true);
					RoverNameLbl[x].setText(rovers[standardDisplayLinks[currentPage][x]].getName());
					MovementStatsLbl[x].setText("X: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getLocation().getX()) 
							+ " m\tAngular Spin FL: " + formatDouble(Math.toDegrees(rovers[standardDisplayLinks[currentPage][x]].getWheelSpeed(RoverObj.FL))) 
							+ " deg/s\nY: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getLocation().getY()) 
							+ " m\tAngluar Spin FR: " + formatDouble(Math.toDegrees(rovers[standardDisplayLinks[currentPage][x]].getWheelSpeed(RoverObj.FR))) 
							+ " deg/s\nAngle: " + formatDouble(Math.toDegrees(rovers[standardDisplayLinks[currentPage][x]].getDirection())) 
							+ " deg\tAngular Spin BL: " + formatDouble(Math.toDegrees(rovers[standardDisplayLinks[currentPage][x]].getWheelSpeed(RoverObj.BL))) 
							+ " deg/s\n\tAngular Spin BR: " + formatDouble(Math.toDegrees(rovers[standardDisplayLinks[currentPage][x]].getWheelSpeed(RoverObj.BR))) 
							+ " deg/s\nSpeed: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getSpeed()) 
							+ " m/s\nVelocity X: " + formatDouble(Math.cos(rovers[standardDisplayLinks[currentPage][x]].getDirection()) * rovers[standardDisplayLinks[currentPage][x]].getSpeed()) 
							+ " m/s\nVelocity Y: " + formatDouble(Math.sin(rovers[standardDisplayLinks[currentPage][x]].getDirection()) * rovers[standardDisplayLinks[currentPage][x]].getSpeed()) 
							+ " m/s\nAngular Velocity: " + formatDouble(Math.toDegrees(rovers[standardDisplayLinks[currentPage][x]].getAngularVelocity())) 
							+ " deg/s\nSlip Velocity: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getSlipVelocity())
							+ " m/s\n\nAcceleration: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getAcceleration()) 
							+ " m/s^2\nAngular Acceleration: " + formatDouble(Math.toDegrees(rovers[standardDisplayLinks[currentPage][x]].getAngularAcceleration()))
							+ " deg/s^2\nSlip Acceleration: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getSlipAcceleration()) + " m/s^s");
					ElectricalStatsLbl[x].setText("SOC: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getSOC())
							+ "\nBattery Charge: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getBatteryCharge()) 
							+ " C\nBattery CP Charge: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getBatterCPCharge())
							+ " C\nBattery Voltage: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getBatteryVoltage()) 
							+ " V\nBattery Current: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getBatteryCurrent()) 
							+ " A\n\nMotor Current FL: " + formatDouble(Math.abs(rovers[standardDisplayLinks[currentPage][x]].getMotorCurrent(RoverObj.FL))) 
							+ " A\nMotor Current FR: " + formatDouble(Math.abs(rovers[standardDisplayLinks[currentPage][x]].getMotorCurrent(RoverObj.FR))) 
							+ " A\nMotor Current BL: " + formatDouble(Math.abs(rovers[standardDisplayLinks[currentPage][x]].getMotorCurrent(RoverObj.BL))) 
							+ " A\nMotor Current BR: " + formatDouble(Math.abs(rovers[standardDisplayLinks[currentPage][x]].getMotorCurrent(RoverObj.BR)))
							+ " A\n\nMotor Voltage FL: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getMotorVoltage(RoverObj.FL))
							+ " V\nMotor Voltage FR: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getMotorVoltage(RoverObj.FR))
							+ " V\nMotor Voltage BL: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getMotorVoltage(RoverObj.BL))
							+ " V\nMotor Voltage BR: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getMotorVoltage(RoverObj.BR)) + " V");
					TemperatureStatsLbl[x].setText("Motor Temperature FL: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getMotorTemp(RoverObj.FL)) + " *c\n" +
							"Motor Temperature FR: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getMotorTemp(RoverObj.FR)) +" *c\n" +
							"Motor Temperature BL: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getMotorTemp(RoverObj.BL)) + " *c\n" +
							"Motor Temperature BR: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getMotorTemp(RoverObj.BR)) + " *c\n\n" +
							"Battery Temperature: " + formatDouble(rovers[standardDisplayLinks[currentPage][x]].getBatteryTemperature()) + " *c\n\n" +
							"Air Temperature: " + formatDouble(Access.getMapTemperatureAtPoint(rovers[standardDisplayLinks[currentPage][x]].getLocation())) + "*c");
				}
				PageLeftBtn[x].setEnabled(standardDisplayLinks[currentPage][x] >= 0);
				try {
					PageRightBtn[x].setEnabled(standardDisplayLinks[currentPage][x] < rovers.length-1);
				}
				catch (Exception e){
					PageRightBtn[x].setEnabled(false);
				}
			}
			x++;
		}
	}
	
	//change which rover is connected to a certain display
	private void changeLinkedRover(int display, int by){
		try {
			rovers[0].equals("exists");
		}
		catch (Exception e){
			return;
		}
		finally {
			if (inHUDmode){
				if (display != 0){
					HUDDisplayLinks[display] += by;
				}
			}
			else {
				standardDisplayLinks[currentPage][display] += by;
			}
			updateDisplays();
		}
	}
	
	//whether or not to show the panel as an overlay on the map
	public void setInHUDMode(boolean b){
		inHUDmode = b;
		updateDisplays();
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
			RoverDisplayWindow[0].setLocation(spacing, super.getTopOfPage()+spacing);
			RoverDisplayWindow[0].setSize(RoverDisplayWindow[0].getPreferredSize());
			RoverDisplayWindow[1].setLocation(spacing, RoverDisplayWindow[0].getY()+RoverDisplayWindow[0].getHeight()+spacing);
			RoverDisplayWindow[1].setSize(RoverDisplayWindow[1].getPreferredSize());
			RoverDisplayWindow[2].setSize(RoverDisplayWindow[2].getPreferredSize());
			RoverDisplayWindow[2].setLocation(this.getWidth()-RoverDisplayWindow[2].getWidth()-spacing, RoverDisplayWindow[0].getY());
			RoverDisplayWindow[3].setLocation(RoverDisplayWindow[2].getX(), RoverDisplayWindow[1].getY());
			RoverDisplayWindow[3].setSize(RoverDisplayWindow[3].getPreferredSize());
			int x = numberOfHUDDisplays;
			while (x < numberOfDisplays){
				RoverDisplayWindow[x].setVisible(false);
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
				RoverDisplayWindow[x].setBounds(
						(width+spacing)*(x%numbAcross),
						this.getTopOfPage() + (height+spacing)*(x/numbAcross),
						width,
						height
				);
				RoverDisplayWindow[x].setVisible(true);
				RoverDisplayWindow[x].setOpaque(true);
				x++;
			}
		}
		super.setVisible(b);
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
