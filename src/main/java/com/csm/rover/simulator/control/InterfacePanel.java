package com.csm.rover.simulator.control;

import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.visual.ImageButton;
import com.csm.rover.simulator.visual.Panel;
import com.csm.rover.simulator.visual.ZList;
import com.csm.rover.simulator.wrapper.Globals;
import com.csm.rover.simulator.wrapper.SerialBuffers;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//TODO pop out option
//TODO more adaptable 'plug in' interface like communication style
public class InterfacePanel extends Panel{

	private static final long serialVersionUID = -2554090286918354434L;

	public InterfaceCode CODE;
	
	JPanel ProgramBtnsPnl;
		ImageButton LinkBtn;
		ImageButton PingBtn;
		ImageButton MailBtn;
		ImageButton MessageBtn;
		ImageButton CommentBtn;
		ImageButton OptionsBtn;
		ImageButton FolderBtn;
		JComboBox<String> PortSelectCombo;
		JLabel MuteIcon;
	JPanel StatusPnl;
		JLabel StatusSatPowerLbl;
		JProgressBar StatusSatPower;
		JLabel StatusRoverPowerLbl;
		JProgressBar StatusRoverPower;
		JLabel StatusMotorPowerLbl;
		JProgressBar StatusMotorPower;
		JLabel StatusArmPowerLbl;
		JProgressBar StatusArmPower;
	JPanel RoverBtnsPnl;
		JLabel RoverSelectionLbl;
		JComboBox<String> RoverSelectionCombo;
		ImageButton[] RoverBtns = new ImageButton[18];
		JLabel RoverSendLbl;
		JTextField RoverSendTxt;
		JButton RoverSendBtn;
		JLabel RoverDeleteLink;
		JLabel RoverEditLink;
		JLabel RoverAddLink;
		JLabel RoverPageLeftBtn;
		JLabel RoverPageRightBtn;
		JLabel RoverPageLbl;
	JPanel SatelliteBtnsPnl;
		JLabel SatSelectionLbl;
		JComboBox<String> SatSelectionCombo;
		ImageButton[] SatBtns = new ImageButton[RoverBtns.length];
		JLabel SatSendLbl;
		JTextField SatSendTxt;
		JButton SatSendBtn;
		JLabel SatAddLink;
		JLabel SatEditLink;
		JLabel SatDeleteLink;
		JLabel SatPageLeftBtn;
		JLabel SatPageRightBtn;
		JLabel SatPageLbl;
	JPanel InstructionsPnl;
		JButton CancelInstructsBtn;
		JLabel ParametersLbl;
		JLabel InstructionsLbl;
		JLabel RoverCommandsLbl;
		JLabel SatelliteCommandsLbl;
		ZList RoverCommandsList;
		ZList SatelliteCommandList;
		ZList ParameterList;
		public ZList InstructionsList;		
		public JButton InstructionsSubmitBtn;
		public JButton InstructionsDeleteBtn;
		public JButton InstructionsEditBtn;
		public JButton InstructionsUpBtn;
		public JButton InstructionsDownBtn;
		JLabel Divider;
		JButton InstructionAddBtn;
		JTextField ParameterTxt;
		JLabel AddInstructionLink;
		JLabel EditInstructionLink;
	JScrollPane SerialDisplayScroll;
	JTextPane SerialDisplayLbl;
	JLabel ConnectionLbl;
	JLabel VersionLbl;
	JMenuItem OverrideMuteOptn;
	private boolean CursorInMuteOptn = false;

	public InterfacePanel(Dimension size, SerialBuffers serialBuffers) {
		super(size, "Control Interface");
		CODE = InterfaceAccess.setCode(this, serialBuffers);
		super.titleLbl.setVisible(false);
		super.postScript.setVisible(false);
		super.background = new ImageIcon(getClass().getResource("/images/Background.png"));
		initialize();
		alignComponents();
	}
	
	private void initialize(){
		OverrideMuteOptn = new JMenuItem("Override Mute");
		OverrideMuteOptn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				CursorInMuteOptn = false;
				new SynchronousThread(400, new Runnable(){
					public void run(){
						OverrideMuteOptn.setVisible(CursorInMuteOptn);
					}
				}, 1, "mute watch");
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				CursorInMuteOptn = true;
			}
		});
		OverrideMuteOptn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OverrideMuteOptn.setVisible(false);
				InterfaceAccess.CODE.muted = false;
				MuteIcon.setVisible(false);
			}
		});
		OverrideMuteOptn.setVisible(false);
		OverrideMuteOptn.setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
		OverrideMuteOptn.setOpaque(true);
		OverrideMuteOptn.setBounds(314, 100, 139, 22);
		this.add(OverrideMuteOptn);
		
		ProgramBtnsPnl = new JPanel();
		ProgramBtnsPnl.setOpaque(false);
		ProgramBtnsPnl.setBackground(Color.DARK_GRAY);
		ProgramBtnsPnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(192, 192, 192)), "Program Controls", TitledBorder.LEADING, TitledBorder.TOP, new Font("Iskoola Pota", Font.BOLD, 16), Color.WHITE));
		ProgramBtnsPnl.setBounds(10, 11, 665, 85);
		this.add(ProgramBtnsPnl);
		ProgramBtnsPnl.setLayout(null);
		
		LinkBtn = new ImageButton();
		LinkBtn.setToolTipText("Refresh Link");
		LinkBtn.setBounds(10, 20, 55, 55);
		LinkBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceAccess.CODE.resetConnection();
			}
		});
		LinkBtn.setImage(new ImageIcon(getClass().getResource("/icons/Earth_Up.png")));
		ProgramBtnsPnl.add(LinkBtn);
		
		PingBtn = new ImageButton();
		PingBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceAccess.CODE.pingRover();
			}
		});
		PingBtn.setImage(new ImageIcon(getClass().getResource("/icons/Wi-Fi.png")));
		PingBtn.setToolTipText("Ping Rover");
		PingBtn.setBounds(75, 20, 55, 55);
		ProgramBtnsPnl.add(PingBtn);
		
		MailBtn = new ImageButton();
		MailBtn.setBounds(140, 20, 55, 55);
		MailBtn.setImage(new ImageIcon(getClass().getResource("/icons/Mail.png")));
		MailBtn.setToolTipText("View Data");
		MailBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceAccess.CODE.OpenRecievedFiles();
			}
		});
		ProgramBtnsPnl.add(MailBtn);
		
		MessageBtn = new ImageButton();
		MessageBtn.setBounds(400, 20, 55, 55);
		MessageBtn.setImage(new ImageIcon(getClass().getResource("/icons/Comment_Up.png")));
		MessageBtn.setToolTipText("Send Command");
		MessageBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!MessageBtn.getText().equals("")){
					InterfaceAccess.sendMsg(JOptionPane.showInputDialog(getParent().getParent(), "Enter a Command:", "Write to Serial", JOptionPane.PLAIN_MESSAGE));
				}
			}
		});
		ProgramBtnsPnl.add(MessageBtn);
		
		CommentBtn = new ImageButton();
		CommentBtn.setBounds(270, 20, 55, 55);
		CommentBtn.setImage(new ImageIcon(getClass().getResource("/icons/New_Post.png")));
		CommentBtn.setToolTipText("Note on Log");
		CommentBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceAccess.addNoteToLog("User", JOptionPane.showInputDialog(getParent().getParent(), "Note for log:", "Data Log Edit", JOptionPane.PLAIN_MESSAGE));
			}
		});
		ProgramBtnsPnl.add(CommentBtn);
		
		OptionsBtn = new ImageButton();
		OptionsBtn.setImage(new ImageIcon(getClass().getResource("/icons/Gear.png")));
		OptionsBtn.setToolTipText("View Activity Log");
		OptionsBtn.setBounds(335, 20, 55, 55);
		OptionsBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// Show Options Pnl
			}
		});
		ProgramBtnsPnl.add(OptionsBtn);
		
		FolderBtn = new ImageButton();
		FolderBtn.setImage(new ImageIcon(getClass().getResource("/icons/Folder.png")));
		FolderBtn.setToolTipText("View Files");
		FolderBtn.setBounds(205, 20, 55, 55);
		FolderBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().open(new File("").getAbsoluteFile());
				} catch (IOException e) {
					Globals.getInstance().reportError("InterfacePanel", "folderBtn_Clicked", e);
				}
			}
		});
		ProgramBtnsPnl.add(FolderBtn);
		
		PortSelectCombo = new JComboBox<String>();
		PortSelectCombo.setMaximumRowCount(20);
		PortSelectCombo.setFont(new Font("OCR B MT", Font.BOLD, 13));
		PortSelectCombo.setModel(new DefaultComboBoxModel<String>(new String[] {"COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "COM10", "COM11", "COM12", "COM13", "COM14", "COM15", "COM16", "COM17", "COM18", "COM19", "COM20"}));
		PortSelectCombo.setSelectedIndex(12);
		PortSelectCombo.setBounds(570, 20, 85, 20);
		PortSelectCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InterfaceAccess.COMPortChanged();
			}
		});
		ProgramBtnsPnl.add(PortSelectCombo);
		
		JLabel lblSelectedPort = new JLabel("Selected Port:");
		lblSelectedPort.setForeground(Color.WHITE);
		lblSelectedPort.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		lblSelectedPort.setBounds(477, 22, 83, 14);
		ProgramBtnsPnl.add(lblSelectedPort);
		
		ConnectionLbl = new JLabel("Connected for 0 min.");
		ConnectionLbl.setBounds(497, 53, 158, 25);
		ProgramBtnsPnl.add(ConnectionLbl);
		ConnectionLbl.setForeground(Color.WHITE);
		ConnectionLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		ConnectionLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		
		MuteIcon = new JLabel(new ImageIcon(getClass().getResource("/icons/Mute.png")));
		MuteIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() == 3){
					OverrideMuteOptn.setLocation((int)arg0.getLocationOnScreen().getX() - 3, (int)arg0.getLocationOnScreen().getY() - 3);
					OverrideMuteOptn.setVisible(true);
				}
			}
		});
		MuteIcon.setToolTipText("Cannot Send Messages");
		MuteIcon.setBounds(477, 51, 24, 24);
		MuteIcon.setVisible(false);
		ProgramBtnsPnl.add(MuteIcon);
		
		SerialDisplayScroll = new JScrollPane();
		SerialDisplayScroll.setBounds(685, 107, 322, 460);
		this.add(SerialDisplayScroll);
		
		SerialDisplayLbl = new JTextPane();
		SerialDisplayScroll.setViewportView(SerialDisplayLbl);
		SerialDisplayLbl.setFont(new Font("Miriam Fixed", Font.PLAIN, 14));
		SerialDisplayLbl.setEditable(false);
		
		RoverBtnsPnl = new JPanel();
		RoverBtnsPnl.setOpaque(false);
		RoverBtnsPnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(192, 192, 192)), "Rover Controls", TitledBorder.LEADING, TitledBorder.TOP, new Font("Iskoola Pota", Font.BOLD, 16), Color.WHITE));
		RoverBtnsPnl.setBounds(10, 107, 665, 106);
		this.add(RoverBtnsPnl);
		RoverBtnsPnl.setLayout(null);
		
		RoverSelectionLbl = new JLabel("Select Active Rover:");
		RoverSelectionLbl.setForeground(Color.WHITE);
		RoverSelectionLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverSelectionLbl.setSize((int) RoverSelectionLbl.getPreferredSize().getWidth(), 20);
		RoverBtnsPnl.add(RoverSelectionLbl);
		
		RoverSelectionCombo = new JComboBox<String>();
		RoverSelectionCombo.setMaximumRowCount(20);
		RoverSelectionCombo.setFont(new Font("OCR B MT", Font.BOLD, 13));
		RoverSelectionCombo.setBounds(570, 20, 175, 20);
		RoverBtnsPnl.add(RoverSelectionCombo);
		
		RoverSendLbl = new JLabel("Send Command:");
		RoverSendLbl.setForeground(Color.WHITE);
		RoverSendLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverSendLbl.setBounds(10, 81, 93, 14);
		RoverBtnsPnl.add(RoverSendLbl);
		
		RoverSendTxt = new JTextField();
		RoverSendTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10){
					InterfaceAccess.sendRoverMsg();
				}
			}
		});
		RoverSendTxt.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverSendTxt.setBounds(113, 77, 200, 23);
		RoverBtnsPnl.add(RoverSendTxt);
		RoverSendTxt.setColumns(10);
		
		RoverSendBtn = new JButton("Send");
		RoverSendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InterfaceAccess.sendRoverMsg();
			}
		});
		RoverSendBtn.setOpaque(false);
		RoverSendBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		RoverSendBtn.setBounds(314, 78, 86, 23);
		RoverBtnsPnl.add(RoverSendBtn);
		
		RoverDeleteLink = new JLabel("<HTML><U>Delete</U></HTML>");
		RoverDeleteLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceAccess.roverLinkClicked(2);
			}
		});
		RoverDeleteLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		RoverDeleteLink.setForeground(Color.LIGHT_GRAY);
		RoverDeleteLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverDeleteLink.setBounds(612, 81, 43, 14);
		RoverBtnsPnl.add(RoverDeleteLink);
		
		RoverEditLink = new JLabel("<HTML><U>Edit</U></HTML>");
		RoverEditLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceAccess.roverLinkClicked(1);
			}
		});
		RoverEditLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		RoverEditLink.setForeground(Color.LIGHT_GRAY);
		RoverEditLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverEditLink.setBounds(580, 81, 22, 14);
		RoverBtnsPnl.add(RoverEditLink);
		
		RoverAddLink = new JLabel("<HTML><U>Add</U></HTML>");
		RoverAddLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceAccess.roverLinkClicked(0);
			}
		});
		RoverAddLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		RoverAddLink.setForeground(Color.LIGHT_GRAY);
		RoverAddLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverAddLink.setBounds(548, 81, 22, 14);
		RoverBtnsPnl.add(RoverAddLink);
		
		RoverPageLeftBtn = new JLabel("");
		RoverPageLeftBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (RoverPageLeftBtn.isEnabled()){
					InterfaceAccess.roverActionsPageChanged(-1);
				}
			}
		});
		RoverPageLeftBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		RoverPageLeftBtn.setHorizontalAlignment(SwingConstants.CENTER);
		RoverPageLeftBtn.setHorizontalTextPosition(SwingConstants.CENTER);
		RoverPageLeftBtn.setIcon(new ImageIcon(getClass().getResource("/icons/Left_Shaded.png")));
		RoverPageLeftBtn.setEnabled(false);
		RoverPageLeftBtn.setBounds(434, 81, 34, 18);
		RoverBtnsPnl.add(RoverPageLeftBtn);
		
		RoverPageRightBtn = new JLabel("");
		RoverPageRightBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (RoverPageRightBtn.isEnabled()){
					InterfaceAccess.roverActionsPageChanged(1);
				}
			}
		});
		RoverPageRightBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		RoverPageRightBtn.setIcon(new ImageIcon(getClass().getResource("/icons/Right_Shaded.png")));
		RoverPageRightBtn.setHorizontalTextPosition(SwingConstants.CENTER);
		RoverPageRightBtn.setHorizontalAlignment(SwingConstants.CENTER);
		RoverPageRightBtn.setBounds(478, 82, 34, 18);
		RoverBtnsPnl.add(RoverPageRightBtn);
		
		RoverPageLbl = new JLabel("1 / 4");
		RoverPageLbl.setHorizontalAlignment(SwingConstants.CENTER);
		RoverPageLbl.setForeground(Color.WHITE);
		RoverPageLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverPageLbl.setBounds(487, 79, 51, 18);
		RoverBtnsPnl.add(RoverPageLbl);
		
		int x = 0;
		while (x < RoverBtns.length){
			RoverBtns[x] = new ImageButton();
			RoverBtns[x].setBounds(10 + 65 * x, 20, 70, 70);
			RoverBtns[x].setEnabled(false);
			RoverBtns[x].setHorizontalTextPosition(SwingConstants.CENTER);
			RoverBtns[x].setIcon(null);
			RoverBtns[x].setMargin(3);
			final int hold = x;
			RoverBtns[x].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e){
					if (RoverBtns[hold].isEnabled()){
						InterfaceAccess.actionButtonClicked(0, hold);
					}
				}
			});
			RoverBtnsPnl.add(RoverBtns[x]);
			x++;
		}
		
		SatelliteBtnsPnl = new JPanel();
		SatelliteBtnsPnl.setOpaque(false);
		SatelliteBtnsPnl.setBounds(10, 224, 665, 123);
		SatelliteBtnsPnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(192, 192, 192)), "Satellite Controls", TitledBorder.LEADING, TitledBorder.TOP, new Font("Iskoola Pota", Font.BOLD, 16), Color.WHITE));
		SatelliteBtnsPnl.setLayout(null);
		this.add(SatelliteBtnsPnl);
		
		SatSelectionLbl = new JLabel("Select Active Satellite:");
		SatSelectionLbl.setForeground(Color.WHITE);
		SatSelectionLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatSelectionLbl.setSize((int) SatSelectionLbl.getPreferredSize().getWidth(), 20);
		SatelliteBtnsPnl.add(SatSelectionLbl);
		
		SatSelectionCombo = new JComboBox<String>();
		SatSelectionCombo.setMaximumRowCount(20);
		SatSelectionCombo.setFont(new Font("OCR B MT", Font.BOLD, 13));
		SatSelectionCombo.setBounds(570, 20, 175, 20);
		SatelliteBtnsPnl.add(SatSelectionCombo);
		
		SatSendLbl = new JLabel("Send Command:");
		SatSendLbl.setForeground(Color.WHITE);
		SatSendLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatSendLbl.setBounds(10, 92, 93, 14);
		SatelliteBtnsPnl.add(SatSendLbl);
		
		SatSendTxt = new JTextField();
		SatSendTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10){
					InterfaceAccess.sendSatMessage();
				}
			}
		});
		SatSendTxt.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatSendTxt.setColumns(10);
		SatSendTxt.setBounds(113, 88, 200, 23);
		SatelliteBtnsPnl.add(SatSendTxt);
		
		SatSendBtn = new JButton("Send");
		SatSendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InterfaceAccess.sendSatMessage();
			}
		});
		SatSendBtn.setOpaque(false);
		SatSendBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		SatSendBtn.setBounds(314, 89, 86, 23);
		SatelliteBtnsPnl.add(SatSendBtn);
		
		SatAddLink = new JLabel("<HTML><U>Add</U></HTML>");
		SatAddLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceAccess.satLinkClicked(0);
			}
		});
		SatAddLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		SatAddLink.setForeground(Color.LIGHT_GRAY);
		SatAddLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatAddLink.setBounds(548, 97, 22, 14);
		SatelliteBtnsPnl.add(SatAddLink);
		
		SatEditLink = new JLabel("<HTML><U>Edit</U></HTML>");
		SatEditLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceAccess.satLinkClicked(1);
			}
		});
		SatEditLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		SatEditLink.setForeground(Color.LIGHT_GRAY);
		SatEditLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatEditLink.setBounds(580, 97, 22, 14);
		SatelliteBtnsPnl.add(SatEditLink);
		
		SatDeleteLink = new JLabel("<HTML><U>Delete</U></HTML>");
		SatDeleteLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceAccess.satLinkClicked(2);
			}
		});
		SatDeleteLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		SatDeleteLink.setForeground(Color.LIGHT_GRAY);
		SatDeleteLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatDeleteLink.setBounds(612, 97, 43, 14);
		SatelliteBtnsPnl.add(SatDeleteLink);
		
		SatPageLeftBtn = new JLabel("");
		SatPageLeftBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SatPageLeftBtn.isEnabled()){
					InterfaceAccess.satelliteActionsPageChanged(-1);
				}
			}
		});
		SatPageLeftBtn.setIcon(new ImageIcon(getClass().getResource("/icons/Left_Shaded.png")));
		SatPageLeftBtn.setHorizontalTextPosition(SwingConstants.CENTER);
		SatPageLeftBtn.setHorizontalAlignment(SwingConstants.CENTER);
		SatPageLeftBtn.setEnabled(false);
		SatPageLeftBtn.setBounds(435, 92, 34, 18);
		SatelliteBtnsPnl.add(SatPageLeftBtn);
		
		SatPageRightBtn = new JLabel("");
		SatPageRightBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SatPageRightBtn.isEnabled()){
					InterfaceAccess.satelliteActionsPageChanged(1);
				}
			}
		});
		SatPageRightBtn.setIcon(new ImageIcon(getClass().getResource("/icons/Right_Shaded.png")));
		SatPageRightBtn.setHorizontalTextPosition(SwingConstants.CENTER);
		SatPageRightBtn.setHorizontalAlignment(SwingConstants.CENTER);
		SatPageRightBtn.setBounds(479, 93, 34, 18);
		SatelliteBtnsPnl.add(SatPageRightBtn);
		
		SatPageLbl = new JLabel("1 / 4");
		SatPageLbl.setHorizontalAlignment(SwingConstants.CENTER);
		SatPageLbl.setForeground(Color.WHITE);
		SatPageLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatPageLbl.setBounds(491, 88, 51, 18);
		SatelliteBtnsPnl.add(SatPageLbl);
		
		x = 0;
		while (x < SatBtns.length){
			SatBtns[x] = new ImageButton();
			SatBtns[x].setBounds(10 + 80 * x, 20, 70, 70);
			SatBtns[x].setEnabled(false);
			SatBtns[x].setHorizontalTextPosition(SwingConstants.CENTER);
			SatBtns[x].setIcon(null);
			SatBtns[x].setMargin(3);
			final int hold = x;
			SatBtns[x].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e){
					if (SatBtns[hold].isEnabled()){
						InterfaceAccess.actionButtonClicked(1, hold);
					}
				}
			});
			SatelliteBtnsPnl.add(SatBtns[x]);
			x++;
		}
		
		VersionLbl = new JLabel("Health Prognostics Rover Project    CSM    v 2.2");
		VersionLbl.setHorizontalAlignment(SwingConstants.CENTER);
		VersionLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 12));
		VersionLbl.setForeground(Color.LIGHT_GRAY);
		VersionLbl.setBounds(354, 553, 251, 14);
		this.add(VersionLbl);
		
		InstructionsPnl = new JPanel();
		InstructionsPnl.setLayout(null);
		InstructionsPnl.setOpaque(false);
		InstructionsPnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(192, 192, 192)), "System Instructions", TitledBorder.LEADING, TitledBorder.TOP, new Font("Iskoola Pota", Font.BOLD, 16), Color.WHITE));
		InstructionsPnl.setBounds(10, 358, 665, 184);
		this.add(InstructionsPnl);
		
		RoverCommandsList = new ZList();
		RoverCommandsList.setSize(137, 113);
		RoverCommandsList.setLocation(10, 39);
		RoverCommandsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				InterfaceAccess.RoverListChanged();
			}
		});
		RoverCommandsList.setBackground(new Color(240, 240, 240));
		InstructionsPnl.add(RoverCommandsList);
		
		SatelliteCommandList = new ZList();
		SatelliteCommandList.setSize(137, 105);
		SatelliteCommandList.setLocation(157, 47);
		SatelliteCommandList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				InterfaceAccess.SatelliteListChanged();
			}
		});
		SatelliteCommandList.setFont(new Font("Iskoola Pota", Font.PLAIN, 15));
		SatelliteCommandList.setBackground(SystemColor.menu);
		InstructionsPnl.add(SatelliteCommandList);
		
		ParameterTxt = new JTextField();
		ParameterTxt.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent arg0) {
				ParameterTxt.setText("");
				ParameterList.setSize(ParameterList.getWidth(), ParameterList.getHeight() + 27);
			}
			@Override
			public void componentShown(ComponentEvent e) {
				ParameterList.setSize(ParameterList.getWidth(), ParameterList.getHeight() - 27);
			}
		});
		ParameterTxt.setVisible(false);
		ParameterTxt.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		ParameterTxt.setBounds(304, 127, 137, 25);
		InstructionsPnl.add(ParameterTxt);
		
		ParameterList = new ZList();
		ParameterList.setSize(137, 69);
		ParameterList.setLocation(304, 47);
		ParameterList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				InterfaceAccess.ParametersListChanged();
			}
		});
		ParameterList.setFont(new Font("Iskoola Pota", Font.PLAIN, 15));
		ParameterList.setBackground(SystemColor.menu);
		InstructionsPnl.add(ParameterList);
		
		InstructionsList = new ZList();
		InstructionsList.setSize(117, 113);
		InstructionsList.setLocation(451, 39);
		InstructionsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				InterfaceAccess.InstructionListChanged();
			}
		});
		InstructionsList.setFont(new Font("Iskoola Pota", Font.PLAIN, 15));
		InstructionsList.setBackground(Color.LIGHT_GRAY);
		InstructionsPnl.add(InstructionsList);
		
		InstructionsSubmitBtn = new JButton("Submit");
		InstructionsSubmitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InterfaceAccess.SubmitInstructionClicked();
			}
		});
		InstructionsSubmitBtn.setEnabled(false);
		InstructionsSubmitBtn.setOpaque(false);
		InstructionsSubmitBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		InstructionsSubmitBtn.setBounds(451, 150, 117, 23);
		InstructionsPnl.add(InstructionsSubmitBtn);
		
		InstructionsDeleteBtn = new JButton("Delete");
		InstructionsDeleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InterfaceAccess.InstructionModClicked(0);
			}
		});
		InstructionsDeleteBtn.setEnabled(false);
		InstructionsDeleteBtn.setOpaque(false);
		InstructionsDeleteBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		InstructionsDeleteBtn.setBounds(569, 39, 100, 23);
		InstructionsPnl.add(InstructionsDeleteBtn);
		
		InstructionsEditBtn = new JButton("Edit");
		InstructionsEditBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InterfaceAccess.InstructionModClicked(1);
			}
		});
		InstructionsEditBtn.setEnabled(false);
		InstructionsEditBtn.setOpaque(false);
		InstructionsEditBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		InstructionsEditBtn.setBounds(569, 58, 100, 23);
		InstructionsPnl.add(InstructionsEditBtn);
		
		InstructionsUpBtn = new JButton("Move Up");
		InstructionsUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InterfaceAccess.InstructionModClicked(2);
			}
		});
		InstructionsUpBtn.setEnabled(false);
		InstructionsUpBtn.setOpaque(false);
		InstructionsUpBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		InstructionsUpBtn.setBounds(569, 73, 100, 23);
		InstructionsPnl.add(InstructionsUpBtn);
		
		InstructionsDownBtn = new JButton("Move Down");
		InstructionsDownBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InterfaceAccess.InstructionModClicked(3);
			}
		});
		InstructionsDownBtn.setEnabled(false);
		InstructionsDownBtn.setOpaque(false);
		InstructionsDownBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		InstructionsDownBtn.setBounds(569, 92, 100, 23);
		InstructionsPnl.add(InstructionsDownBtn);
		
		RoverCommandsLbl = new JLabel("Rover Commands:");
		RoverCommandsLbl.setForeground(Color.WHITE);
		RoverCommandsLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 16));
		RoverCommandsLbl.setBounds(10, 20, 117, 14);
		InstructionsPnl.add(RoverCommandsLbl);
		
		SatelliteCommandsLbl = new JLabel("Satellite Commands:");
		SatelliteCommandsLbl.setForeground(Color.WHITE);
		SatelliteCommandsLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 16));
		SatelliteCommandsLbl.setBounds(157, 20, 137, 14);
		InstructionsPnl.add(SatelliteCommandsLbl);
		
		ParametersLbl = new JLabel("Parameters:");
		ParametersLbl.setForeground(Color.WHITE);
		ParametersLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 16));
		ParametersLbl.setBounds(304, 21, 137, 14);
		InstructionsPnl.add(ParametersLbl);
		
		InstructionsLbl = new JLabel("Instructions:");
		InstructionsLbl.setForeground(Color.WHITE);
		InstructionsLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 16));
		InstructionsLbl.setBounds(451, 22, 117, 14);
		InstructionsPnl.add(InstructionsLbl);
		
		Divider = new JLabel();
		Divider.setOpaque(true);
		Divider.setBackground(Color.GRAY);
		Divider.setBounds(445, 20, 3, 200);
		InstructionsPnl.add(Divider);
		
		InstructionAddBtn = new JButton("Add");
		InstructionAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InterfaceAccess.AddInstructionClicked();
			}
		});
		InstructionAddBtn.setOpaque(false);
		InstructionAddBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		InstructionAddBtn.setEnabled(false);
		InstructionAddBtn.setBounds(304, 150, 137, 23);
		InstructionsPnl.add(InstructionAddBtn);
		
		AddInstructionLink = new JLabel("<HTML><p align=\"right\"><U>Add<br>Instruction</U></p></HTML>");
		AddInstructionLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		AddInstructionLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceAccess.CODE.addInstructionToList();
			}
		});
		AddInstructionLink.setHorizontalAlignment(SwingConstants.RIGHT);
		AddInstructionLink.setForeground(Color.LIGHT_GRAY);
		AddInstructionLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		AddInstructionLink.setBounds(579, 107, 65, 30);
		InstructionsPnl.add(AddInstructionLink);
		
		EditInstructionLink = new JLabel("<HTML><p align=\"right\"><U>Edit<br>Instruction</U></p></HTML>");
		EditInstructionLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		EditInstructionLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceAccess.CODE.editInstructionInList();
			}
		});
		EditInstructionLink.setEnabled(false);
		EditInstructionLink.setHorizontalAlignment(SwingConstants.RIGHT);
		EditInstructionLink.setForeground(Color.LIGHT_GRAY);
		EditInstructionLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		EditInstructionLink.setBounds(578, 143, 65, 30);
		InstructionsPnl.add(EditInstructionLink);
		
		CancelInstructsBtn = new JButton("Cancel All");
		CancelInstructsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InterfaceAccess.cancelInstructs_Clicked();
			}
		});
		CancelInstructsBtn.setOpaque(false);
		CancelInstructsBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		CancelInstructsBtn.setBounds(10, 150, 284, 23);
		InstructionsPnl.add(CancelInstructsBtn);
				
		StatusPnl = new JPanel();
		StatusPnl.setLayout(null);
		StatusPnl.setOpaque(false);
		StatusPnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(192, 192, 192)), "Status", TitledBorder.LEADING, TitledBorder.TOP, new Font("Iskoola Pota", Font.BOLD, 16), Color.WHITE));
		StatusPnl.setBounds(685, 11, 236, 85);
		this.add(StatusPnl);
		
		JLabel label_1 = new JLabel("<HTML><U>Delete</U></HTML>");
		label_1.setForeground(Color.LIGHT_GRAY);
		label_1.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		label_1.setBounds(612, 81, 43, 14);
		StatusPnl.add(label_1);
		
		JLabel label_2 = new JLabel("<HTML><U>Edit</U></HTML>");
		label_2.setForeground(Color.LIGHT_GRAY);
		label_2.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		label_2.setBounds(580, 81, 22, 14);
		StatusPnl.add(label_2);
		
		JLabel label_3 = new JLabel("<HTML><U>Add</U></HTML>");
		label_3.setForeground(Color.LIGHT_GRAY);
		label_3.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		label_3.setBounds(548, 81, 22, 14);
		StatusPnl.add(label_3);
		
		StatusSatPowerLbl = new JLabel("Satelite Power:");
		StatusSatPowerLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		StatusSatPowerLbl.setForeground(Color.WHITE);
		StatusSatPowerLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		StatusSatPowerLbl.setBounds(10, 49, 84, 25);
		StatusPnl.add(StatusSatPowerLbl);
		
		StatusSatPower = new JProgressBar();
		StatusSatPower.setBounds(104, 54, 50, 20);
		StatusPnl.add(StatusSatPower);
		
		StatusRoverPowerLbl = new JLabel("Rover Power:");
		StatusRoverPowerLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		StatusRoverPowerLbl.setForeground(Color.WHITE);
		StatusRoverPowerLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		StatusRoverPowerLbl.setBounds(10, 20, 84, 25);
		StatusPnl.add(StatusRoverPowerLbl);
		
		StatusRoverPower = new JProgressBar();
		StatusRoverPower.setForeground(Color.RED);
		StatusRoverPower.setBounds(104, 24, 50, 20);
		StatusPnl.add(StatusRoverPower);
		
		StatusMotorPowerLbl = new JLabel("Motor Power:");
		StatusMotorPowerLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		StatusMotorPowerLbl.setForeground(Color.WHITE);
		StatusMotorPowerLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		StatusMotorPowerLbl.setBounds(164, 20, 84, 25);
		StatusPnl.add(StatusMotorPowerLbl);
		
		StatusMotorPower = new JProgressBar();
		StatusMotorPower.setForeground(Color.RED);
		StatusMotorPower.setBounds(258, 24, 50, 20);
		StatusPnl.add(StatusMotorPower);
		
		StatusArmPowerLbl = new JLabel("Arm Power:");
		StatusArmPowerLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		StatusArmPowerLbl.setForeground(Color.WHITE);
		StatusArmPowerLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		StatusArmPowerLbl.setBounds(164, 49, 84, 25);
		StatusPnl.add(StatusArmPowerLbl);
		
		StatusArmPower = new JProgressBar();
		StatusArmPower.setForeground(Color.RED);
		StatusArmPower.setBounds(258, 53, 50, 20);
		StatusPnl.add(StatusArmPower);
	}
	
	public void setNamesLists(ArrayList<String> rovers, ArrayList<String> sats){
		RoverSelectionCombo.setModel(new DefaultComboBoxModel<String>(rovers.toArray(new String[rovers.size()])));
		SatSelectionCombo.setModel(new DefaultComboBoxModel<String>(sats.toArray(new String[sats.size()])));
	}
	
	private void alignComponents(){
		SerialDisplayScroll.setSize(getWidth() / 4, getHeight() - 10 - super.getTopOfPage());
		SerialDisplayScroll.setLocation(getWidth() - 10 - SerialDisplayScroll.getWidth(), super.getTopOfPage());
		VersionLbl.setLocation((getWidth() - VersionLbl.getWidth()) / 2, getHeight() - VersionLbl.getHeight() - 3);
		
		RoverBtnsPnl.setBounds(10, ProgramBtnsPnl.getHeight() + 20, (SerialDisplayScroll.getX() - 20), 20 + RoverSelectionCombo.getHeight() + 10 + RoverBtns[0].getHeight() + 10 + RoverSendTxt.getHeight() + 10);
		RoverSelectionCombo.setLocation(150, 20);
		RoverSelectionLbl.setLocation(RoverSelectionCombo.getX() - 5 - RoverSelectionLbl.getWidth(), RoverSelectionCombo.getY());
		RoverSendLbl.setLocation(10, RoverBtnsPnl.getHeight() - 10 - RoverSendTxt.getHeight() + 4);
		RoverSendTxt.setLocation(10 + RoverSendLbl.getWidth() + 5, (RoverSendLbl.getY() - 4));
		RoverSendBtn.setLocation((RoverSendTxt.getX() + RoverSendTxt.getWidth() + 3), RoverSendTxt.getY());
		RoverDeleteLink.setLocation(RoverBtnsPnl.getWidth() - 10 - RoverDeleteLink.getWidth(), RoverSendLbl.getY());
		RoverEditLink.setLocation((RoverDeleteLink.getX() - 10 - RoverEditLink.getWidth()), RoverSendLbl.getY());
		RoverAddLink.setLocation((RoverEditLink.getX() - 10 - RoverAddLink.getWidth()), RoverSendLbl.getY());
		RoverPageRightBtn.setLocation(((RoverAddLink.getX() - RoverSendBtn.getX() - RoverSendBtn.getWidth()) / 3 + RoverSendBtn.getX() + RoverSendBtn.getWidth()), RoverAddLink.getY());
		RoverPageLeftBtn.setLocation((RoverPageRightBtn.getX() - RoverPageLeftBtn.getWidth()), RoverPageRightBtn.getY());
		RoverPageLbl.setLocation(((RoverAddLink.getX() - RoverSendBtn.getX() - RoverSendBtn.getWidth()) * 2 / 3 + RoverSendBtn.getX() + RoverSendBtn.getWidth()), RoverPageRightBtn.getY());
		
		SatelliteBtnsPnl.setBounds(10, 10 + ProgramBtnsPnl.getHeight() + 10 + RoverBtnsPnl.getHeight() + 10, RoverBtnsPnl.getWidth(), 20 + SatSelectionCombo.getHeight() + 10 + SatBtns[0].getHeight() + 10 + SatSendTxt.getHeight() + 10);
		SatSelectionCombo.setLocation(150, 20);
		SatSelectionLbl.setLocation(SatSelectionCombo.getX() - 5 - SatSelectionLbl.getWidth(), SatSelectionCombo.getY());
		SatSendLbl.setLocation(10, SatelliteBtnsPnl.getHeight() - 10 - SatSendTxt.getHeight() + 4);
		SatSendTxt.setLocation(10 + SatSendLbl.getWidth() + 5, (SatSendLbl.getY() - 4));
		SatSendBtn.setLocation((SatSendTxt.getX() + SatSendTxt.getWidth() + 3), SatSendTxt.getY());
		SatDeleteLink.setLocation(SatelliteBtnsPnl.getWidth() - 10 - SatDeleteLink.getWidth(), SatSendLbl.getY());
		SatEditLink.setLocation((SatDeleteLink.getX() - 10 - SatEditLink.getWidth()), SatSendLbl.getY());
		SatAddLink.setLocation((SatEditLink.getX() - 10 - SatAddLink.getWidth()), SatSendLbl.getY());
		SatPageRightBtn.setLocation(((SatAddLink.getX() - SatSendBtn.getX() - SatSendBtn.getWidth()) / 3 + SatSendBtn.getX() + SatSendBtn.getWidth()), SatAddLink.getY());
		SatPageLeftBtn.setLocation((SatPageRightBtn.getX() - SatPageLeftBtn.getWidth()), SatPageRightBtn.getY());
		SatPageLbl.setLocation(((SatAddLink.getX() - SatSendBtn.getX() - SatSendBtn.getWidth()) * 2 / 3 + SatSendBtn.getX() + SatSendBtn.getWidth()), SatPageRightBtn.getY());
		
		int spacing = (RoverBtnsPnl.getWidth() - 20 - 70 * (SatBtns.length)) / (SatBtns.length - 1);
		int x = 0;
		while (x < RoverBtns.length){
			RoverBtns[x].setLocation(10 + (70 + spacing) * ((x + (RoverBtns.length)) % (RoverBtns.length)), 20 + RoverSelectionCombo.getHeight() + 10 + 80 * (x / (RoverBtns.length)));
			SatBtns[x].setLocation(10 + (70 + spacing) * ((x + (RoverBtns.length)) % (RoverBtns.length)), 20 + SatSelectionCombo.getHeight() + 10 + 80 * (x / (RoverBtns.length)));
			x++;
		}
		
		InstructionsPnl.setLocation(10, SatelliteBtnsPnl.getY() + SatelliteBtnsPnl.getHeight() + 10);
		InstructionsPnl.setSize(RoverBtnsPnl.getWidth(), VersionLbl.getY() - InstructionsPnl.getY() - 10);
		RoverCommandsLbl.setLocation(10, 20);
		InstructionsDeleteBtn.setLocation((InstructionsPnl.getWidth() - InstructionsDeleteBtn.getWidth() - 10), (RoverCommandsLbl.getY() + RoverCommandsLbl.getHeight() + 5));
		InstructionsEditBtn.setLocation(InstructionsDeleteBtn.getX(), (InstructionsDeleteBtn.getY() + InstructionsDeleteBtn.getHeight()));
		InstructionsUpBtn.setLocation(InstructionsDeleteBtn.getX(), (InstructionsEditBtn.getY() + InstructionsEditBtn.getHeight()));
		InstructionsDownBtn.setLocation(InstructionsDeleteBtn.getX(), (InstructionsUpBtn.getY() + InstructionsUpBtn.getHeight()));
		spacing = (InstructionsDeleteBtn.getX() - 10 - 10 * 3 - 10) / 4;
		RoverCommandsList.setBounds(10, (RoverCommandsLbl.getY() + RoverCommandsLbl.getHeight() + 5), spacing, (InstructionsPnl.getHeight() - 20 - RoverCommandsLbl.getHeight() - 5 - CancelInstructsBtn.getHeight() - 10));
		SatelliteCommandsLbl.setLocation((10 + spacing + 10), 20);
		SatelliteCommandList.setBounds(SatelliteCommandsLbl.getX(), RoverCommandsList.getY(), spacing, RoverCommandsList.getHeight());
		CancelInstructsBtn.setBounds(RoverCommandsList.getX(), (RoverCommandsList.getY() + RoverCommandsList.getHeight()), (SatelliteCommandList.getX() + SatelliteCommandList.getWidth() - RoverCommandsList.getX()), CancelInstructsBtn.getHeight());
		ParametersLbl.setLocation((10 + (spacing + 10)*2), 20);
		ParameterList.setBounds(ParametersLbl.getX(), RoverCommandsList.getY(), spacing, RoverCommandsList.getHeight());
		InstructionAddBtn.setBounds(ParametersLbl.getX(), (ParameterList.getY() + ParameterList.getHeight()), spacing, InstructionAddBtn.getHeight());
		ParameterTxt.setBounds(InstructionAddBtn.getX(), (InstructionAddBtn.getY() - 26), InstructionAddBtn.getWidth(), 25);
		InstructionsLbl.setLocation((10 + (spacing + 10)*3 + 10), 20);
		InstructionsList.setBounds(InstructionsLbl.getX(), RoverCommandsList.getY(), spacing, RoverCommandsList.getHeight());
		InstructionsSubmitBtn.setBounds(InstructionsList.getX(), (InstructionsList.getY() + InstructionsList.getHeight()), spacing, InstructionsSubmitBtn.getHeight()); 
		Divider.setBounds((InstructionsLbl.getX() - 11), 25, 3, (InstructionsPnl.getHeight() - 25 - 15));
		EditInstructionLink.setLocation(InstructionsDeleteBtn.getX() + InstructionsDeleteBtn.getWidth() - EditInstructionLink.getWidth(), InstructionsPnl.getHeight() - 20 - EditInstructionLink.getHeight());
		AddInstructionLink.setLocation(EditInstructionLink.getX(), EditInstructionLink.getY() - 10 - AddInstructionLink.getHeight());
		
		StatusPnl.setBounds((ProgramBtnsPnl.getX() + ProgramBtnsPnl.getWidth() + 10), ProgramBtnsPnl.getY(), (RoverBtnsPnl.getWidth() - ProgramBtnsPnl.getWidth() - 10), ProgramBtnsPnl.getHeight());
	}
}