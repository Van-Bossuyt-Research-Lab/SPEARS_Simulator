package com.csm.rover.simulator.control;

import com.csm.rover.simulator.objects.util.FreeThread;
import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.wrapper.Globals;
import com.csm.rover.simulator.wrapper.NamesAndTags;
import com.csm.rover.simulator.wrapper.SerialBuffers;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

//TODO clean this up
//TODO figure out how it actually works
//TODO change implementation to allow for 'plug in' communications
//TODO update the actual console code to look like this
public class InterfaceCode {
	private static final Logger LOG = LogManager.getLogger(InterfaceCode.class);

	private InterfacePanel GUI;
    private Globals GLOBAL = Globals.getInstance();
    private SerialBuffers serialBuffers;

	private String connectedPort = "COM13";
	public static String IDcode = "g";
	
	private int connectionTime = 0;
	private int countSec = 0;
	private boolean Connected = false;
	private NamesAndTags namesAndTags;
	public boolean muted = false;
	private Runnable confirmMessage;
	private Runnable failMessage;
	
	private boolean editingRover = false;
	private boolean deletingRover = false;
	private boolean editingSat = false;
	private boolean deletingSat = false;
	private boolean receivingFile = false;
	private boolean editingInstruction = false;
	private int editingCommand = -1;
	
	private boolean listening = false;
	private String listenFor;
	private SynchronousThread listenTimer;
	private Runnable listenAction;
	private Runnable listenFail;
	private String[] receivedFiles = new String[0];
	
	private int[] currentActionPages = { 0, 0 };
	private int pageLength;
	private int numberOfPages = 4;
	private String[][] actionCommands = new String[2][pageLength*numberOfPages];
	private String[][] actionTips = new String[2][pageLength*numberOfPages];
	private String[][] actionIcons = new String[2][pageLength*numberOfPages];
	
	private InstructionObj[][] RoverInstructions;
	private InstructionObj[][] SatelliteInstructions;
	
	// SETUP
	
	public InterfaceCode(InterfacePanel gui, SerialBuffers serialBuffers){
		GUI = gui;
        pageLength = GUI.RoverBtns.length;
        this.serialBuffers = serialBuffers;
		GLOBAL.addClockIncrementEvent(new Runnable(){
			public void run(){
				if (Connected){
					countSec++;
					if (countSec == 0){
						connectionTime++;
						countSec = 0;
						GUI.ConnectionLbl.setText("Connected for " + connectionTime + " min.");
					}
				}
				else {
					GUI.ConnectionLbl.setText("Not Connected");
				}
			}
		});
		initalize();
	}
	
	public void start(){
		@SuppressWarnings("unused")
        SynchronousThread serialCheck = new SynchronousThread(400, new Runnable(){
			public void run(){
				InterfaceAccess.CODE.updateSerialCom();
			}
		}, SynchronousThread.FOREVER, "Interface serial");
	}
	
	public void initalize(){
		try {
			File logsFldr = new File("Logs");
			if (!logsFldr.exists()){
				logsFldr.mkdir();
			}
			File dataFldr = new File("Data");
			if (!dataFldr.exists()){
				dataFldr.mkdir();
			}
			File photosFldr = new File("Photos");
			if (!photosFldr.exists()){
				photosFldr.mkdir();
			}
		}
		catch (Exception e){
			LOG.log(Level.ERROR, "Tried and failed to create directory structure", e);
		}
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream("CommandString.dll");
			in = new ObjectInputStream(fis);
			SaveFile input = (SaveFile) in.readObject();
			actionCommands = input.getCommands();
			actionTips = input.getTooltips();
			actionIcons = input.getIcons();
			UpdateActionBtns();
			try {
				input.getRoverOptions().clone();
				GUI.RoverCommandsList.setValues(input.getRoverOptions());
				RoverInstructions = input.getRoverInstructions();
			}
			catch (Exception e){
				RoverInstructions = new InstructionObj[0][0];
			}
			try {
				input.getSatelliteOptions().clone();
				GUI.SatelliteCommandList.setValues(input.getSatelliteOptions());
				SatelliteInstructions = input.getSatelliteInstructions();
			}
			catch (Exception e){
				SatelliteInstructions = new InstructionObj[0][0];
			}
			in.close();
		} 
		catch (Exception e){
			int x = 0;
			while (x < actionCommands[0].length){
				actionCommands[0][x] = "";
				actionCommands[1][x] = "";
				actionTips[0][x] = "";
				actionTips[1][x] = "";
				actionIcons[0][x] = "";
				actionIcons[1][x] = "";
				RoverInstructions = new InstructionObj[0][0];
				SatelliteInstructions = new InstructionObj[0][0];
				x++;
			}
		}
		confirmMessage = new Runnable(){
			public void run(){
				(new PopUp()).showConfirmDialog("Message was successfully sent.", "Message Confirmed", PopUp.DEFAULT_OPTIONS);
			}
		};
		failMessage = new Runnable(){
			public void run(){
				(new PopUp()).showConfirmDialog("No message confirmation was recieved.", "Message Failed", PopUp.DEFAULT_OPTIONS);
			}
		};
	}
	
	public void pingRover(){
		GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Pinging Rover...\n");
		writeToSerial(tagMessage("^", 'r'), true);
		listenForSignal("g ^",
				new Runnable() {
					public void run() {
						runThreadOutOfSync(new Runnable() {
							public void run() {
								Connected = true;
								GUI.ConnectionLbl.setText("Connected for 0 min.");
								GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Rover Connected: " + GLOBAL.dateTime.toString("hh:mm:ss") + "\n");
								(new PopUp()).showConfirmDialog("Rover connected.", "Ping Confirm", PopUp.DEFAULT_OPTIONS);
							}
						}, "ping-return");
					}
				}, new Runnable() {
					public void run() {
						runThreadOutOfSync(new Runnable() {
							public void run() {
								Connected = false;
								GUI.ConnectionLbl.setText("Not Connected.");
								GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Rover did not respond.\n");
								(new PopUp()).showConfirmDialog("No Rover found.", "Ping Failed", PopUp.DEFAULT_OPTIONS);
							}
						}, "ping-return");
					}
				},
				10);
	}
	
	public void setCallTags(NamesAndTags namesAndTags){
		this.namesAndTags = namesAndTags;
		GUI.setNamesLists(namesAndTags.getRoverNames(), namesAndTags.getSatelliteNames());
	}
	
	// COM CONNECTION STUFF
	
	public void resetConnection(){
		if (Connected){
			pingRover();
		}
	}
	
	public void changeCOMPort(){
		connectionTime = 0;
		connectedPort = (String)GUI.PortSelectCombo.getSelectedItem();
		GUI.ConnectionLbl.setText("Connected for " + connectionTime + " min.");
		LOG.log(Level.INFO, "COM port connection changed to {}", connectedPort);
		resetConnection();
	}

	public void writeToSerial(String msg){
		if (Connected && !muted){
			LOG.log(Level.INFO, "Sent serial message \"{}\"", msg);
			char[] output = msg.toCharArray();
			int x = 0;
			while (x < output.length){
                serialBuffers.writeToSerial(output[x], IDcode); // Write to Serial one char at a time
				delay(20); // Pause for sending
				x++;
			}
		}
	}
	
	public void writeToSerial(String msg, boolean override){
		if ((Connected || override) && !muted){
			LOG.log(Level.INFO, "Sent serial message \"{}\" with override: {}", msg, override);
			char[] output = msg.toCharArray();
			int x = 0;
			while (x < output.length){
                serialBuffers.writeToSerial(output[x], IDcode); // Write to Serial one char at a time
				delay(20); // Pause for sending
				x++;
			}
		}
	}
	
	private String tagMessage(String mess, char which){
		if (which == 'r'){
			return namesAndTags.getTagByName((String)GUI.SatSelectionCombo.getSelectedItem()) + " " + namesAndTags.getTagByName((String)GUI.RoverSelectionCombo.getSelectedItem()) + " " + mess;
		}
		else if (which == 's'){
			return namesAndTags.getTagByName((String) GUI.SatSelectionCombo.getSelectedItem()) + " c " + mess;
		}
		else {
			return mess;
		}
	}
	
	public void updateSerialCom(){
		if (!receivingFile){
			char[] input = readFromSerial().toCharArray();
			if (input.length > 2){
				if ((input[0]+"").equals(IDcode)){
					if (input[2] == 'n'){
						String data = "";
						int x = 4;
						while (x < input.length){
							data += input[x];
							x++;
						}
						GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Recieved: " + data + "\n");
						LOG.log(Level.INFO, "Received serial note \"{}\"", data);
					}
					else if (input[2] == 'i'){
						receivingFile = true;
						int filelength = Integer.parseInt(buildString(input, 4, input.length - 1));
						if (filelength < 0){
							filelength += 65536;
						}
						LOG.log(Level.INFO, "Received photo notice");
						ReadPhoto(filelength);
					}
					else if (input[2] == '}'){
						muted = true;
						GUI.MuteIcon.setVisible(true);
						LOG.log(Level.INFO, "Ground station muted");
					}
					else if (input[2] == '{'){
						muted = false;
						GUI.MuteIcon.setVisible(false);
						LOG.log(Level.INFO, "Ground station un-muted");
					}
					else if (input[2] == '*'){
						GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "The rover has pinged the ground station.\n");
						LOG.log(Level.INFO, "Rover pinged the ground station");
						writeToSerial(tagMessage("*", 'r'));
					}
				}
				else {
					if (Arrays.equals(input, "Data Could Not be Parsed\n".toCharArray())){
						GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Data Could Not be Parsed\n");
						LOG.log(Level.INFO, "Received serail message could not be parsed");
					}
				}
			}
		}
	}
	
	private String readFromSerial(){
		if (!connectedPort.equals("")){
			//if (inputStream.available() > 0){
			if (serialBuffers.RFAvailable(IDcode) > 0){
				// System.out.println("Available");
				int hold = 0;
				while (hold != serialBuffers.RFAvailable(IDcode)){
					hold = serialBuffers.RFAvailable(IDcode);
					delay(7);
				}
				String out = "";
					//while(inputStream.available() > 0) {
				while (serialBuffers.RFAvailable(IDcode) > 0) {
						//out += (char)(inputStream.read());
					out += (char) serialBuffers.ReadSerial(IDcode);
				}
				if (listening){
					if (out.equals(listenFor)){
						new FreeThread(0, listenAction, 1, "interface listening");
					}
				}
				return out;
			}
			else {
				return "";
			}
		}
		else {
			return "";
		}
	}
	
	private void listenForSignal(String msg, final Runnable passaction, final Runnable failaction, int secs){
		listening = true;
		listenFor = msg;
		listenAction = new Runnable(){
			public void run(){
				listenTimer.Stop();
				listening = false;
				passaction.run();
			}
		};
		listenFail = new Runnable(){
			public void run(){
				listening = false;
				failaction.run();
			}
		};
		listenTimer = new SynchronousThread((secs*1000), listenFail, 1, "listening timer");
	}
	
	
	// ACTION BUTTONS
	
	public void ActionButtonClicked(int section, int which){
		which += currentActionPages[section]*pageLength;
		if (editingRover){
			if (section == 0){
				editRover2(which);
			}
			else {
				cancelProgrammer();
			}
		}
		else if (deletingRover){
			if (section == 0){
				deleteRover2(which);
			}
			else {
				cancelProgrammer();
			}
		}
		else if (editingSat){
			if (section == 1){
				editSat2(which);
			}
			else {
				cancelProgrammer();
			}
		}
		else if (deletingSat){
			if (section == 1){
				deleteSat2(which);
			}
			else {
				cancelProgrammer();
			}
		}
		else {
			if (!actionCommands[section][which].equals("")){
	            GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Sent: \"" + actionCommands[section][which] + "\"\n");
				if (section == 0){
					writeToSerial(tagMessage(actionCommands[section][which], 'r'));
					listenForSignal("g #", new Runnable(){
						public void run(){
							listenForSignal("g %", new Runnable(){
								public void run(){
									runThreadOutOfSync(confirmMessage, "confirm 1");
								}
							}, new Runnable(){
								public void run(){
									runThreadOutOfSync(failMessage, "fail 1");
								}
							}, 5);
						}
					}, new Runnable(){
						public void run(){
							runThreadOutOfSync(failMessage, "fail 1.1");
						}
					}, 4);
				}
				else {
					writeToSerial(tagMessage(actionCommands[section][which], 's'));
					listenForSignal("g #", new Runnable(){
						public void run(){
							runThreadOutOfSync(confirmMessage, "confirm 2");
						}
					}, new Runnable(){
						public void run(){
							runThreadOutOfSync(failMessage, "fail 2");
						}
					}, 4);
				}
			}
		}
	}
	
	public void sendRoverCommand(){
		if (!GUI.RoverSendTxt.getText().equals("")){
			if (!Connected){
				GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "You are not Connected.\n");
			}
			//writeToSerial(tagMessage(Access.CODE.GUI.interfacePnl.RoverSendTxt.getText(), 'r'));
			writeToSerial(GUI.RoverSendTxt.getText(), true);
            GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Sent: \"" + GUI.RoverSendTxt.getText() + "\"\n");
            listenForSignal("g #", new Runnable(){
				public void run(){
					listenForSignal("g %", new Runnable() {
						public void run() {
							runThreadOutOfSync(confirmMessage, "confirm 3");
						}
					}, new Runnable() {
						public void run() {
							runThreadOutOfSync(failMessage, "fail 3");
						}
					}, 5);
				}
			}, new Runnable(){
				public void run(){
					runThreadOutOfSync(failMessage, "fail 3.1");
				}
			}, 4);
			GUI.RoverSendTxt.setText("");
		}
		else {
			new SynchronousThread(0, new Runnable(){
				public void run(){
					(new PopUp()).showConfirmDialog("You must enter a message into the field.", "Message Failed", PopUp.DEFAULT_OPTIONS);
				}
			}, 1, "invalid message 1");
		}
	}
	
	public void sendSatCommand(){
		if (!GUI.SatSendTxt.getText().equals("")){
			writeToSerial(tagMessage(GUI.SatSendTxt.getText(), 's'));
            GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Sent: \"" + GUI.SatSendTxt.getText() + "\"\n");
			listenForSignal("g #", new Runnable(){
				public void run(){
					runThreadOutOfSync(confirmMessage, "confirm 4");
				}
			}, new Runnable(){
				public void run(){
					runThreadOutOfSync(failMessage, "fail 4");
				}
			}, 4);
			GUI.SatSendTxt.setText("");
		}
		else {
			runThreadOutOfSync(new Runnable() {
				public void run() {
					(new PopUp()).showConfirmDialog("You must enter a message into the field.", "Message Failed", PopUp.DEFAULT_OPTIONS);
				}
			}, "invalid message 2");
		}
	}
	
	public void advanceActionPage(int direction, int section){
		currentActionPages[section] += direction;
		if (section == 0){
			if (currentActionPages[section] == 0){
				GUI.RoverPageLeftBtn.setEnabled(false);
			}
			else if (currentActionPages[section] == numberOfPages-1){
				GUI.RoverPageRightBtn.setEnabled(false);
			}
			else{
				GUI.RoverPageLeftBtn.setEnabled(true);
				GUI.RoverPageRightBtn.setEnabled(true);
			}
			GUI.RoverPageLbl.setText((currentActionPages[section]+1) + " / " + numberOfPages);
		}
		else {
			if (currentActionPages[section] == 0){
				GUI.SatPageLeftBtn.setEnabled(false);
			}
			else if (currentActionPages[section] == numberOfPages-1){
				GUI.SatPageRightBtn.setEnabled(false);
			}
			else{
				GUI.SatPageLeftBtn.setEnabled(true);
				GUI.SatPageRightBtn.setEnabled(true);
			}
			GUI.SatPageLbl.setText((currentActionPages[section]+1) + " / " + numberOfPages);			
		}
		UpdateActionBtns();
	}
	
	
	// ACTION BUTTON EDITING
	
	public void addRoverBtn(){
		new SynchronousThread(0, new Runnable(){
			public void run(){
				String[] data;
				boolean go = true;
				while (go){		
					data = (new PopUp()).showPromptDialog("Button Command", "", "Tool Tip", "", "Icon", new String[] { "", "About_Page.png", "Add.png", "Anchor.png", "Bacteria.png", "Bacteria_2.png", "Band_Aide.png", "Battery.png", "Bottom_Left.png", "Bottom_Left_Shaded.png", "Bottom_Right.png", "Bottom_Right_Shaded.png", "Caduceus.png", "Calandar.png", "Calculator.png", "Camer_2.png", "Camera.png", "Cancel.png", "Cancel_2.png", "Chain.png", "Circle_CCW.png", "Circle_CCW_Shaded.png", "Circle_CW.png", "Circle_CW_Shaded.png", "Comment.png", "Comment_Up.png", "Cone.png", "Controler.png", "Controller_2.png", "Dish.png", "Double_Arrow_CCW.png", "Double_Arrow_CW.png", "Down.png", "Down_Left.png", "Down_Left_Shaded.png", "Down_Right.png", "Down_Right_Shaded.png", "Down_Shaded.png", "Earth.png", "Earth_Up.png", "Expand.png", "Expand_Shaded.png", "Eye.png", "File_AVI.png", "File_DAT.png", "File_DOC.png", "File_GIF.png", "File_HTML.png", "File_JPG.png", "File_MP4.png", "File_PDF.png", "File_PNG.png", "File_PPT.png", "File_TXT.png", "File_XLS.png", "File_ZIP.png", "Finder.png", "Fire.png", "Flashlight.png", "Folder.png", "Folder_Up.png", "Gear.png", "GPS.png", "GPS_Pin.png", "Green_Check.png", "Handicap.png", "Handicap_Shaded.png", "Hourglass.png", "Key.png", "Lamp.png", "Left.png", "Left_Down.png", "Left_Down_Shaded.png", "Left_Shaded.png", "Left_Up.png", "Left_Up_Shaded.png", "Lifesaver.png", "Linked_Arrows_CW.png", "Mail.png", "Mail_Message.png", "Map_with_Compass.png", "Map_with_Pins.png", "Mic.png", "Mic_2.png", "New_Page.png", "New_Post.png", "Pencil.png", "Pie_Chart.png", "Printer.png", "Processor.png", "Push_Pin.png", "Push_Pin_1.png", "Push_Pin_2.png", "Red_X.png", "Right.png", "Right_Down.png", "Right_Down_Shaded.png", "Right_Shaded.png", "Right_Up.png", "Right_Up_Shaded.png", "Rocket.png", "Rover.png", "Save.png", "Scanner.png", "Send_Mail.png", "Server.png", "Server_2.png", "Shield.png", "Snowflake.png", "Spyglass.png", "Steering_Wheel.png", "Sthetoscope.png", "Stop.png", "Stop_Shaded.png", "Switch.png", "Tanget_Line.png", "Telescope.png", "Temp.png", "Temp_Cold.png", "Temp_Hot.png", "Terminal.png", "Tire.png", "Tools.png", "Top_Left.png", "Top_Left_Shaded.png", "Top_Right.png", "Top_Right_Shaded.png", "Tornado.png", "Up.png", "Up_Left.png", "Up_Left_Shaded.png", "Up_Right.png", "Up_Right_Shaded.png", "Up_Shaded.png", "USB.png", "USB_2.png", "Wall.png", "Wand.png", "Wi-Fi.png", "World_Link.png", "XRay.png" }, "", "Create New Button");
					go = Integer.parseInt(data[0]) == PopUp.OK_OPTION;
					if (go){
						if (!data[1].equals("") && !data[3].equals("")){
							int x = 0;
							while (x < actionCommands[0].length){
								if (actionCommands[0][x].equals("")){
									actionCommands[0][x] = data[1];
									actionTips[0][x] = data[2];
									actionIcons[0][x] = data[3];
									break;
								}
								x++;
							}
							break;
						}
						else {
							(new PopUp()).showConfirmDialog("Required data was left unfilled.", "Process Failed", PopUp.DEFAULT_OPTIONS);
						}
					}
				}
				UpdateActionBtns();
				SaveProgrammer();
			}
		}, 1, "add rover");
	}
	
	public void editRoverBtn1(){
		editingRover = true;
		deletingRover = false;
		editingSat = false;
		deletingSat = false;
		GUI.RoverDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
		int x = 0;
		while (x < GUI.RoverBtns.length){
			GUI.RoverBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			x++;
		}
	}
	
	public void deleteRoverBtn1(){
		if (editingRover || deletingRover){
			cancelProgrammer();
		}
		else {
			editingRover = false;
			deletingRover = true;
			editingSat = false;
			deletingSat = false;
			GUI.RoverDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
			int x = 0;
			while (x < GUI.RoverBtns.length){
				GUI.RoverBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				x++;
			}
		}
	}
	
	private void editRover2(final int which){
		new SynchronousThread(0, new Runnable(){
			public void run(){
				String[] data;
				boolean go = true;
				while (go){		
					data = (new PopUp()).showPromptDialog("Button Command", actionCommands[0][which], "Tool Tip", actionTips[0][which], "Icon", new String[] { "", "About_Page.png", "Add.png", "Anchor.png", "Bacteria.png", "Bacteria_2.png", "Band_Aide.png", "Battery.png", "Bottom_Left.png", "Bottom_Left_Shaded.png", "Bottom_Right.png", "Bottom_Right_Shaded.png", "Caduceus.png", "Calandar.png", "Calculator.png", "Camer_2.png", "Camera.png", "Cancel.png", "Cancel_2.png", "Chain.png", "Circle_CCW.png", "Circle_CCW_Shaded.png", "Circle_CW.png", "Circle_CW_Shaded.png", "Comment.png", "Comment_Up.png", "Cone.png", "Controler.png", "Controller_2.png", "Dish.png", "Double_Arrow_CCW.png", "Double_Arrow_CW.png", "Down.png", "Down_Left.png", "Down_Left_Shaded.png", "Down_Right.png", "Down_Right_Shaded.png", "Down_Shaded.png", "Earth.png", "Earth_Up.png", "Expand.png", "Expand_Shaded.png", "Eye.png", "File_AVI.png", "File_DAT.png", "File_DOC.png", "File_GIF.png", "File_HTML.png", "File_JPG.png", "File_MP4.png", "File_PDF.png", "File_PNG.png", "File_PPT.png", "File_TXT.png", "File_XLS.png", "File_ZIP.png", "Finder.png", "Fire.png", "Flashlight.png", "Folder.png", "Folder_Up.png", "Gear.png", "GPS.png", "GPS_Pin.png", "Green_Check.png", "Handicap.png", "Handicap_Shaded.png", "Hourglass.png", "Key.png", "Lamp.png", "Left.png", "Left_Down.png", "Left_Down_Shaded.png", "Left_Shaded.png", "Left_Up.png", "Left_Up_Shaded.png", "Lifesaver.png", "Linked_Arrows_CW.png", "Mail.png", "Mail_Message.png", "Map_with_Compass.png", "Map_with_Pins.png", "Mic.png", "Mic_2.png", "New_Page.png", "New_Post.png", "Pencil.png", "Pie_Chart.png", "Printer.png", "Processor.png", "Push_Pin.png", "Push_Pin_1.png", "Push_Pin_2.png", "Red_X.png", "Right.png", "Right_Down.png", "Right_Down_Shaded.png", "Right_Shaded.png", "Right_Up.png", "Right_Up_Shaded.png", "Rocket.png", "Rover.png", "Save.png", "Scanner.png", "Send_Mail.png", "Server.png", "Server_2.png", "Shield.png", "Snowflake.png", "Spyglass.png", "Steering_Wheel.png", "Sthetoscope.png", "Stop.png", "Stop_Shaded.png", "Switch.png", "Tanget_Line.png", "Telescope.png", "Temp.png", "Temp_Cold.png", "Temp_Hot.png", "Terminal.png", "Tire.png", "Tools.png", "Top_Left.png", "Top_Left_Shaded.png", "Top_Right.png", "Top_Right_Shaded.png", "Tornado.png", "Up.png", "Up_Left.png", "Up_Left_Shaded.png", "Up_Right.png", "Up_Right_Shaded.png", "Up_Shaded.png", "USB.png", "USB_2.png", "Wall.png", "Wand.png", "Wi-Fi.png", "World_Link.png", "XRay.png" }, actionIcons[0][which], "Create New Button");
					go = Integer.parseInt(data[0]) == PopUp.OK_OPTION;
					if (go){
						if (!data[1].equals("") && !data[3].equals("")){
							actionCommands[0][which] = data[1];
							actionTips[0][which] = data[2];
							actionIcons[0][which] = data[3];
							break;
						}
						else {
							(new PopUp()).showConfirmDialog("Required data was left unfilled.", "Process Failed", PopUp.DEFAULT_OPTIONS);
						}
					}
				}
				UpdateActionBtns();
				SaveProgrammer();
				cancelProgrammer();
			}
		}, 1, "edit rover");
	}
	
	private void deleteRover2(int which){
		int x = which;
		while (x < actionCommands[0].length - 1){
			actionCommands[0][x] = actionCommands[0][x + 1];
			actionTips[0][x] = actionTips[0][x + 1];
			actionIcons[0][x] = actionIcons[0][x + 1];
			x++;
		}
		actionCommands[0][x] = "";
		actionTips[0][x] = "";
		actionIcons[0][x] = "";
		UpdateActionBtns();
		SaveProgrammer();
		cancelProgrammer();
	}
	
	public void addSatBtn(){
		new SynchronousThread(0, new Runnable(){
			public void run(){
				String[] data;
				boolean go = true;
				while (go){		
					data = (new PopUp()).showPromptDialog("Button Command", "", "Tool Tip", "", "Icon", new String[] { "", "About_Page.png", "Add.png", "Anchor.png", "Bacteria.png", "Bacteria_2.png", "Band_Aide.png", "Battery.png", "Bottom_Left.png", "Bottom_Left_Shaded.png", "Bottom_Right.png", "Bottom_Right_Shaded.png", "Caduceus.png", "Calandar.png", "Calculator.png", "Camer_2.png", "Camera.png", "Cancel.png", "Cancel_2.png", "Chain.png", "Circle_CCW.png", "Circle_CCW_Shaded.png", "Circle_CW.png", "Circle_CW_Shaded.png", "Comment.png", "Comment_Up.png", "Cone.png", "Controler.png", "Controller_2.png", "Dish.png", "Double_Arrow_CCW.png", "Double_Arrow_CW.png", "Down.png", "Down_Left.png", "Down_Left_Shaded.png", "Down_Right.png", "Down_Right_Shaded.png", "Down_Shaded.png", "Earth.png", "Earth_Up.png", "Expand.png", "Expand_Shaded.png", "Eye.png", "File_AVI.png", "File_DAT.png", "File_DOC.png", "File_GIF.png", "File_HTML.png", "File_JPG.png", "File_MP4.png", "File_PDF.png", "File_PNG.png", "File_PPT.png", "File_TXT.png", "File_XLS.png", "File_ZIP.png", "Finder.png", "Fire.png", "Flashlight.png", "Folder.png", "Folder_Up.png", "Gear.png", "GPS.png", "GPS_Pin.png", "Green_Check.png", "Handicap.png", "Handicap_Shaded.png", "Hourglass.png", "Key.png", "Lamp.png", "Left.png", "Left_Down.png", "Left_Down_Shaded.png", "Left_Shaded.png", "Left_Up.png", "Left_Up_Shaded.png", "Lifesaver.png", "Linked_Arrows_CW.png", "Mail.png", "Mail_Message.png", "Map_with_Compass.png", "Map_with_Pins.png", "Mic.png", "Mic_2.png", "New_Page.png", "New_Post.png", "Pencil.png", "Pie_Chart.png", "Printer.png", "Processor.png", "Push_Pin.png", "Push_Pin_1.png", "Push_Pin_2.png", "Red_X.png", "Right.png", "Right_Down.png", "Right_Down_Shaded.png", "Right_Shaded.png", "Right_Up.png", "Right_Up_Shaded.png", "Rocket.png", "Rover.png", "Save.png", "Scanner.png", "Send_Mail.png", "Server.png", "Server_2.png", "Shield.png", "Snowflake.png", "Spyglass.png", "Steering_Wheel.png", "Sthetoscope.png", "Stop.png", "Stop_Shaded.png", "Switch.png", "Tanget_Line.png", "Telescope.png", "Temp.png", "Temp_Cold.png", "Temp_Hot.png", "Terminal.png", "Tire.png", "Tools.png", "Top_Left.png", "Top_Left_Shaded.png", "Top_Right.png", "Top_Right_Shaded.png", "Tornado.png", "Up.png", "Up_Left.png", "Up_Left_Shaded.png", "Up_Right.png", "Up_Right_Shaded.png", "Up_Shaded.png", "USB.png", "USB_2.png", "Wall.png", "Wand.png", "Wi-Fi.png", "World_Link.png", "XRay.png" }, "", "Create New Button");
					go = Integer.parseInt(data[0]) == PopUp.OK_OPTION;
					if (go){
						if (!data[1].equals("") && !data[3].equals("")){
							int x = 0;
							while (x < actionCommands[1].length){
								if (actionCommands[1][x].equals("")){
									actionCommands[1][x] = data[1];
									actionTips[1][x] = data[2];
									actionIcons[1][x] = data[3];
									break;
								}
								x++;
							}
							break;
						}
						else {
							(new PopUp()).showConfirmDialog("Required data was left unfilled.", "Process Failed", PopUp.DEFAULT_OPTIONS);
						}
					}
				}
				UpdateActionBtns();
				SaveProgrammer();
			}
		}, 1, "add sat");
	}
	
	public void editSatBtn1(){
		editingRover = false;
		deletingRover = false;
		editingSat = true;
		deletingSat = false;
		GUI.SatDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
		int x = 0;
		while (x < GUI.SatBtns.length){
			GUI.SatBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			x++;
		}
	}
	
	public void deleteSatBtn1(){
		if (editingSat || deletingSat){
			cancelProgrammer();
		}
		else {
			editingRover = false;
			deletingRover = false;
			editingSat = false;
			deletingSat = true;
			GUI.SatDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
			int x = 0;
			while (x < GUI.SatBtns.length){
				GUI.SatBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				x++;
			}
		}		
	}
	
	private void editSat2(final int which){
		new SynchronousThread(0, new Runnable(){
			public void run(){
				String[] data;
				boolean go = true;
				while (go){		
					data = (new PopUp()).showPromptDialog("Button Command", actionCommands[1][which], "Tool Tip", actionTips[1][which], "Icon", new String[] { "", "About_Page.png", "Add.png", "Anchor.png", "Bacteria.png", "Bacteria_2.png", "Band_Aide.png", "Battery.png", "Bottom_Left.png", "Bottom_Left_Shaded.png", "Bottom_Right.png", "Bottom_Right_Shaded.png", "Caduceus.png", "Calandar.png", "Calculator.png", "Camer_2.png", "Camera.png", "Cancel.png", "Cancel_2.png", "Chain.png", "Circle_CCW.png", "Circle_CCW_Shaded.png", "Circle_CW.png", "Circle_CW_Shaded.png", "Comment.png", "Comment_Up.png", "Cone.png", "Controler.png", "Controller_2.png", "Dish.png", "Double_Arrow_CCW.png", "Double_Arrow_CW.png", "Down.png", "Down_Left.png", "Down_Left_Shaded.png", "Down_Right.png", "Down_Right_Shaded.png", "Down_Shaded.png", "Earth.png", "Earth_Up.png", "Expand.png", "Expand_Shaded.png", "Eye.png", "File_AVI.png", "File_DAT.png", "File_DOC.png", "File_GIF.png", "File_HTML.png", "File_JPG.png", "File_MP4.png", "File_PDF.png", "File_PNG.png", "File_PPT.png", "File_TXT.png", "File_XLS.png", "File_ZIP.png", "Finder.png", "Fire.png", "Flashlight.png", "Folder.png", "Folder_Up.png", "Gear.png", "GPS.png", "GPS_Pin.png", "Green_Check.png", "Handicap.png", "Handicap_Shaded.png", "Hourglass.png", "Key.png", "Lamp.png", "Left.png", "Left_Down.png", "Left_Down_Shaded.png", "Left_Shaded.png", "Left_Up.png", "Left_Up_Shaded.png", "Lifesaver.png", "Linked_Arrows_CW.png", "Mail.png", "Mail_Message.png", "Map_with_Compass.png", "Map_with_Pins.png", "Mic.png", "Mic_2.png", "New_Page.png", "New_Post.png", "Pencil.png", "Pie_Chart.png", "Printer.png", "Processor.png", "Push_Pin.png", "Push_Pin_1.png", "Push_Pin_2.png", "Red_X.png", "Right.png", "Right_Down.png", "Right_Down_Shaded.png", "Right_Shaded.png", "Right_Up.png", "Right_Up_Shaded.png", "Rocket.png", "Rover.png", "Save.png", "Scanner.png", "Send_Mail.png", "Server.png", "Server_2.png", "Shield.png", "Snowflake.png", "Spyglass.png", "Steering_Wheel.png", "Sthetoscope.png", "Stop.png", "Stop_Shaded.png", "Switch.png", "Tanget_Line.png", "Telescope.png", "Temp.png", "Temp_Cold.png", "Temp_Hot.png", "Terminal.png", "Tire.png", "Tools.png", "Top_Left.png", "Top_Left_Shaded.png", "Top_Right.png", "Top_Right_Shaded.png", "Tornado.png", "Up.png", "Up_Left.png", "Up_Left_Shaded.png", "Up_Right.png", "Up_Right_Shaded.png", "Up_Shaded.png", "USB.png", "USB_2.png", "Wall.png", "Wand.png", "Wi-Fi.png", "World_Link.png", "XRay.png" }, actionIcons[1][which], "Create New Button");
					go = Integer.parseInt(data[0]) == PopUp.OK_OPTION;
					if (go){
						if (!data[1].equals("") && !data[3].equals("")){
							actionCommands[1][which] = data[1];
							actionTips[1][which] = data[2];
							actionIcons[1][which] = data[3];
							break;
						}
						else {
							(new PopUp()).showConfirmDialog("Required data was left unfilled.", "Process Failed", PopUp.DEFAULT_OPTIONS);
						}
					}
				}
				UpdateActionBtns();
				SaveProgrammer();
				cancelProgrammer();
			}
		}, 1, "edit sat 2");
	}
	
	private void deleteSat2(int which){
		int x = which;
		while (x < actionCommands[0].length - 1){
			actionCommands[1][x] = actionCommands[1][x + 1];
			actionTips[1][x] = actionTips[1][x + 1];
			actionIcons[1][x] = actionIcons[1][x + 1];
			x++;
		}
		actionCommands[1][x] = "";
		actionTips[1][x] = "";
		actionIcons[1][x] = "";
		UpdateActionBtns();
		SaveProgrammer();
		cancelProgrammer();
	}
	
	public void cancelProgrammer(){
		editingRover = false;
		deletingRover = false;
		editingSat = false;
		deletingSat = false;
		GUI.RoverDeleteLink.setText("<HTML><U>Delete</U></HTML>");
		GUI.SatDeleteLink.setText("<HTML><U>Delete</U></HTML>");
		int x = 0;
		while (x < GUI.RoverBtns.length){
			GUI.RoverBtns[x].setCursor(new Cursor(Cursor.HAND_CURSOR));
			GUI.SatBtns[x].setCursor(new Cursor(Cursor.HAND_CURSOR));
			x++;
		}
	}

	private void UpdateActionBtns(){
		int x = 0;
		while (x < pageLength){
			if (!actionCommands[0][x].equals("")){
				GUI.RoverBtns[x].setToolTipText(actionTips[0][x+currentActionPages[0]*pageLength]);
			}
			else {
				GUI.RoverBtns[x].setToolTipText("Unassigned");
			}
			if (!actionCommands[1][x].equals("")){
				GUI.SatBtns[x].setToolTipText(actionTips[1][x+currentActionPages[1]*pageLength]);
			}
			else {
				GUI.SatBtns[x].setToolTipText("Unassigned");
			}
			try {
				GUI.RoverBtns[x].setImage(new ImageIcon(getClass().getResource("/icons/" + actionIcons[0][x+currentActionPages[0]*pageLength])));
			}
			catch (Exception e) {
				GUI.RoverBtns[x].setImage(null);
			}
			GUI.RoverBtns[x].setEnabled(!actionCommands[0][x+currentActionPages[0]*pageLength].equals(""));
			try {
				GUI.SatBtns[x].setImage(new ImageIcon(getClass().getResource("/icons/" + actionIcons[1][x+currentActionPages[1]*pageLength])));
			}
			catch (Exception e){
				GUI.SatBtns[x].setImage(null);
			}
			GUI.SatBtns[x].setEnabled(!actionCommands[1][x+currentActionPages[1]*pageLength].equals(""));
			x++;
		}
	}
	
	// STATUS HANDLING
	
	public void setRoverPower(double voltage){
		int precent = (int)Math.round((voltage / 9.0)*100);
		GUI.StatusRoverPower.setValue(precent);
		GUI.StatusRoverPower.setForeground(getPowerColor(precent));
	}
	
	public void setSatellitePower(double voltage){
		int precent = (int)Math.round((voltage / 9.0)*100);
		GUI.StatusSatPower.setValue(precent);
		GUI.StatusSatPower.setForeground(getPowerColor(precent));
	}
	
	public void setMotorPower(double voltage){
		int precent = (int)Math.round((voltage / 12.0)*100);
		GUI.StatusMotorPower.setValue(precent);
		GUI.StatusMotorPower.setForeground(getPowerColor(precent));
	}
	
	public void setArmPower(double voltage){
		int precent = (int)Math.round((voltage / 6.0)*100);
		GUI.StatusArmPower.setValue(precent);
		GUI.StatusArmPower.setForeground(getPowerColor(precent));
	}
		
	private Color getPowerColor(int power){
		if (power < 40){
			return Color.RED;
		}
		else if (power < 80){
			return Color.YELLOW;
		}
		else {
			return Color.GREEN;
		}
	}
	
	
	// FILE/PHOTO HANDLING
	
	private void ReadPhoto(int length){
		if (receivingFile){
			try {
				while (serialBuffers.RFAvailable(IDcode) == 0) {
					delay(5);
				}
				delay(20);
				GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Receiving Image.\n");
				LOG.log(Level.INFO, "Ground station receiving photo");
				String text = GUI.SerialDisplayLbl.getText();
				byte[] bytes = new byte[length];
				char[] progress = new char[length / 500 + 1];
				int index = 0;
				while (index < progress.length){
					progress[index] = '-';
					index++;
				}
				progress[0] = '>';
				GUI.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
				index = 0;
				int i;
				while (index < length && serialBuffers.RFAvailable(IDcode) > 0){
					i = 0;
					while (i < 60 && index < length){
						//escape = 0;
						try {
							bytes[index] = serialBuffers.ReadSerial(IDcode);
						}
						catch (ArrayIndexOutOfBoundsException e){
                            LOG.log(Level.ERROR, "Failed to read photo", e);
							break;
						}
						if (index % 500 == 0 && index != 0){
							progress[index / 500 - 1] = '-';
							progress[index / 500] = '>';
							GUI.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
						}
						index++;
						i++;
					}
					delay(1000);
					//escape++;
					//if (escape > 1000000){
					//	break;
					//}
				}
				if (index == length){
					File image = new File("");
					image = new File(image.getAbsoluteFile() + "\\Photos\\IMAGE " + GLOBAL.dateTime.toString("MM-dd hh-mm") + ".jpg");
					FileOutputStream fos = new FileOutputStream(image);
					fos.write(bytes);
					receivedFiles = Augment(receivedFiles, image.getAbsolutePath());
					fos.close();
					LOG.log(Level.INFO, "Received photo file stored in: {}", image.getAbsoluteFile());
					receivingFile = false;
					GUI.SerialDisplayLbl.setText(text + "Done.\n");
					GUI.MailBtn.setIcon(new ImageIcon(getClass().getResource("/icons/Mail_Message.png")));
				}
				else {
					GUI.SerialDisplayLbl.setText(text + "Image transfer failed, incomplete size requirement.\n");
                    LOG.log(Level.WARN, "Failed to copy image due to size req.");
				}
			}	
			catch(Exception ex) {
                LOG.log(Level.ERROR, "Failed to read photo", ex);

			}
		}
	}
	
	@SuppressWarnings("unused")
	private void ReadDataFile(int length){
		if (receivingFile){
			try {
				while (serialBuffers.RFAvailable(IDcode) == 0) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) { e.printStackTrace(); }
				}
                delay(20);
                GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Receiving Data.\n");
                LOG.log(Level.INFO, "Ground station receiving data file");
                String text = GUI.SerialDisplayLbl.getText();
                byte[] bytes = new byte[length];
                char[] progress = new char[length / 100 + 1];
                int index = 0;
                while (index < progress.length){
                    progress[index] = '-';
                    index++;
                }
                progress[0] = '>';
                GUI.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
                index = 0;
                //int escape = 0;
                while (index < length){
                    while(serialBuffers.RFAvailable(IDcode) > 0) {
                        //escape = 0;
                        try {
                            bytes[index] = serialBuffers.ReadSerial(IDcode);
                        }
                        catch (ArrayIndexOutOfBoundsException e){
                            LOG.log(Level.ERROR, "Failed to read data file", e);
                            break;
                        }
                        if (index % 100 == 0 && index != 0){
                            progress[index / 100 - 1] = '-';
                            progress[index / 100] = '>';
                            GUI.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
                        }
                        index++;
                    }
                    //escape++;
                    //if (escape > 1000000){
                    //	break;
                    //}
                }
                if (index == length){
                    File image = new File("");
                    image = new File(image.getAbsoluteFile() + "\\Data\\DATA " + GLOBAL.dateTime.toString("MM-dd hh-mm") + ".CSV");
                    FileOutputStream fos = new FileOutputStream(image);
                    fos.write(bytes);
                    receivedFiles = Augment(receivedFiles, image.getAbsolutePath());
                    fos.close();
                    LOG.log(Level.INFO, "Received data file, stored in: {}", image.getAbsoluteFile());
                    receivingFile = false;
                    GUI.SerialDisplayLbl.setText(text + "Done.\n");
                    GUI.MailBtn.setIcon(new ImageIcon(getClass().getResource("/icons/Mail_Message.png")));
                }
                else {
                    GUI.SerialDisplayLbl.setText(text + "Data File transfer failed, incomplete size requirement.\n");
                    LOG.log(Level.WARN, "Failed to copy data file due to size req.");
                }
			}	
			catch(Exception exe) {
                LOG.log(Level.ERROR, "Failed to read data file", exe);
			}
		}
	}
	
	public void OpenReceivedFiles(){
		if (receivedFiles.length > 0){
			new SynchronousThread(0, new Runnable(){
				public void run(){
					String[] choices = new String[receivedFiles.length];
					int x = 0;
					while (x < choices.length){
						choices[x] = getFileName(receivedFiles[x]);
						x++;
					}
					int choice = (new PopUp()).showOptionDialog("Select a File", "Open File", choices);
					if (choice != -1){
						if (getFileType(receivedFiles[choice]).equals("jpg")) {
							File image = new File(receivedFiles[choice]);
							try {
								final BufferedImage img = ImageIO.read(image);
								new SynchronousThread(0, new Runnable() {
									public void run() {
										PopUp opane = new PopUp();
										opane.setCustomButtonOptions(new String[]{"Save", "Close"}, new int[]{0, 1});
										int choice = opane.showPictureDialog(new ImageIcon(img), "Received Image", PopUp.CUSTOM_OPTIONS);
										if (choice == 0) {
											javax.swing.JFileChooser browse = new javax.swing.JFileChooser();
											browse.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG file", "jpg", "jpeg"));
											browse.showSaveDialog(GUI);
											try {
												String filepath = browse.getSelectedFile().getAbsolutePath() + ".jpeg";
												File imageOut = new File(filepath);
												try {
													ImageIO.write(img, "jpg", imageOut);
												} catch (Exception e1) {
                                                    LOG.log(Level.ERROR, "Failed to open image file", e1);
													opane.showConfirmDialog("Something went wrong and the file failed to save.", "IO Error", PopUp.DEFAULT_OPTIONS);
												}

											} catch (Exception e2) {
                                                LOG.log(Level.ERROR, "Failed to open image file", e2);
											}
										}
									}
								}, 1, "open file 1");
								receivedFiles = Remove(receivedFiles, choice);
								if (receivedFiles.length == 0) {
									GUI.MailBtn.setIcon(new ImageIcon(getClass().getResource("/icons/Mail.png")));
								}
							} catch (Exception e3) {
                                LOG.log(Level.ERROR, "Failed to open image file", e3);
							}
						}
						if (getFileType(receivedFiles[choice]).equals("CSV")) {
							final String file = receivedFiles[choice];
							new SynchronousThread(0, new Runnable() {
								public void run() {
									int choice = new CSVFrame().OpenCSVFile(file);
									if (choice == 1) {
										javax.swing.JFileChooser browse = new javax.swing.JFileChooser();
										browse.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Comma Seperated Value", "CSV"));
										browse.showSaveDialog(GUI);
										try {
											String filepath = browse.getSelectedFile().getAbsolutePath() + ".CSV";
											try {
												String data = "";
												FileReader input = new FileReader(file);
												Scanner dataIn = new Scanner(input);
												while (dataIn.hasNextLine()) {
													data += dataIn.nextLine() + "\n";
												}
                                                dataIn.close();
												input.close();
												PrintWriter dataOut = new PrintWriter(filepath);
												dataOut.print(data);
												dataOut.close();
											} catch (Exception e4) {
                                                LOG.log(Level.ERROR, "Failed to open data file", e4);
												new PopUp().showConfirmDialog("Something went wrong and the file failed to save.", "IO Error", PopUp.DEFAULT_OPTIONS);
											}
										} catch (Exception e5) {
                                            LOG.log(Level.ERROR, "Failed to open data file", e5);
										}
									}
								}
							}, 1, "open file 2");
							receivedFiles = Remove(receivedFiles, choice);
							if (receivedFiles.length == 0) {
								GUI.MailBtn.setIcon(new ImageIcon(getClass().getResource("/icons/Mail.png")));
							}
						}
					}
				}
			}, 1, "open file 3");
		}
		else {
			new SynchronousThread(0, new Runnable() {
				public void run(){
					(new PopUp()).showConfirmDialog("There are no unread files.", "Received Files", PopUp.DEFAULT_OPTIONS);
				}
			}, 1, "no files");
		}
	}
	
	private String getFileName(String filepath){
		char[] chars = filepath.toCharArray();
		int x = chars.length - 1;
		while (x >= 0){
			if (chars[x] == '\\'){
				break;
			}
			x--;
		}
		x++;
		String out = "";
		while (x < chars.length){
			out += chars[x];
			x++;
		}
		return out;
	}
		
	private String getFileType(String filename){
		String out = "";
		int x = filename.length() - 1;
		while (x >= 0){
			if (filename.charAt(x) == '.'){
				break;
			}
			else {
				out = filename.charAt(x) + out;
			}
			x--;
		}
		return out;
	}
	
	
	// INSTRUCTION STUFF
	
	public void cancelInstructions(){
		writeToSerial(tagMessage("!instructs", 's'));
        GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Canceling Instructions...");
		listenForSignal("g KillDone", new Runnable(){
			public void run(){
				runThreadOutOfSync(new Runnable(){
					public void run(){
						(new PopUp()).showConfirmDialog("All Instructions Canceled.", "Action Confirmed", PopUp.DEFAULT_OPTIONS);
					}}, "cancel instructions");
				GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Done.\n");
			}
		}, new Runnable(){
			public void run(){
				GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Failed.\n");
			}
		}, 3);
	}
	
	public void SalelliteCommandChanged(){
		if (GUI.SatelliteCommandList.getSelectedIndex() != -1){
			GUI.RoverCommandsList.clearSelection();
			setParametersList(SatelliteInstructions[GUI.SatelliteCommandList.getSelectedIndex()]);
			GUI.EditInstructionLink.setEnabled(true);
		}
		else {
			GUI.EditInstructionLink.setEnabled(false);
		}
	}
	
	public void RoverCommandChanged(){
		if (GUI.RoverCommandsList.getSelectedIndex() != -1){
			GUI.SatelliteCommandList.clearSelection();
			setParametersList(RoverInstructions[GUI.RoverCommandsList.getSelectedIndex()]);
			GUI.EditInstructionLink.setEnabled(true);
		}	
		else {
			GUI.EditInstructionLink.setEnabled(false);
		}
	}
	
	private void setParametersList(InstructionObj[] instrcts){
		GUI.ParameterTxt.setVisible(false);
		GUI.ParameterList.setValues(instrcts);
	}
	
	public void ParametersChanged(){
		if (GUI.ParameterList.getSelectedIndex() != -1){
			GUI.ParameterTxt.setVisible(((InstructionObj) GUI.ParameterList.getItemAt(GUI.ParameterList.getSelectedIndex())).getRequiresText());
			GUI.InstructionAddBtn.setEnabled(true);
		}
		else {
			GUI.ParameterTxt.setVisible(false);
			GUI.InstructionAddBtn.setEnabled(false);
		}
	}
	
	public void AddInstruction(){
		int where = -1;
		if (editingInstruction){
			where = GUI.InstructionsList.getSelectedIndex();
			GUI.InstructionsList.removeValue(where);
		}
		if (!GUI.ParameterTxt.isVisible() || !GUI.ParameterTxt.getText().equals("")){
			GUI.InstructionAddBtn.setEnabled(false);
			InstructionObj newCmd = (InstructionObj) GUI.ParameterList.getSelectedItem();
			if (GUI.RoverCommandsList.getSelectedIndex() != -1){
				newCmd.setDestination('r');
				newCmd.setTitle("R-" + GUI.RoverCommandsList.getSelectedItem() + "-" + GUI.ParameterList.getSelectedItem().toString());
				newCmd.setEditIndexies(GUI.RoverCommandsList.getSelectedIndex(), GUI.ParameterList.getSelectedIndex());
			}
			else {
				newCmd.setDestination('s'); // Add a 'c' for command?
				newCmd.setTitle("S-" + GUI.SatelliteCommandList.getSelectedItem() + "-" + GUI.ParameterList.getSelectedItem().toString());
				newCmd.setEditIndexies(GUI.SatelliteCommandList.getSelectedIndex(), GUI.ParameterList.getSelectedIndex());
			}
			if (GUI.ParameterTxt.isVisible()){
				newCmd.fillParameter(GUI.ParameterTxt.getText());
				newCmd.setTitle(newCmd.toString() + ":" + GUI.ParameterTxt.getText());
			}
			if (editingInstruction){
				GUI.InstructionsList.addValue(newCmd, where);
				GUI.SatelliteCommandList.clearSelection();
				GUI.RoverCommandsList.clearSelection();
				GUI.ParameterList.setValues(new String[0]);
				GUI.InstructionsSubmitBtn.setEnabled(true);
				GUI.InstructionsEditBtn.setEnabled(true);
				GUI.InstructionsDeleteBtn.setEnabled(true);
				GUI.InstructionsUpBtn.setEnabled(true);
				GUI.InstructionsDownBtn.setEnabled(true);
				GUI.InstructionsList.setEnabled(true);
				editingInstruction = false;
			}
			else {
				GUI.InstructionsList.addValue(newCmd);
				GUI.SatelliteCommandList.clearSelection();
				GUI.RoverCommandsList.clearSelection();
				GUI.ParameterList.setValues(new String[0]);
				GUI.InstructionsSubmitBtn.setEnabled(true);
			}
		}
		else {
			new SynchronousThread(0, new Runnable(){
				public void run(){
					(new PopUp()).showConfirmDialog("You must enter a typed value for the selected parameter parameter.", "Instruction Failed", PopUp.DEFAULT_OPTIONS);
				}
			}, 1, "add instructions");
		}
	}
	
	public void SendInstructions(){
		String out = "";
		for (Object val : GUI.InstructionsList.getItems()){
			out += ((InstructionObj) val).buildCommand() + "\n";
		}
		out += "s report\nr report\n";
		GUI.SatelliteCommandList.clearSelection();
		GUI.RoverCommandsList.clearSelection();
		GUI.ParameterList.setValues(new String[0]);
		GUI.InstructionsList.setValues(new String[0]);
		GUI.InstructionsSubmitBtn.setEnabled(false);
		GUI.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		writeToSerial(tagMessage("}", 'r'));
		delay(2000);
		writeToSerial(tagMessage("instructions", 's'));
		delay(1000);
		char[] instructChars = out.toCharArray();
		int x = 0;
		while (x < instructChars.length - 60){
			writeToSerial(buildString(instructChars, x, x + 59));
			delay(2000);
			x += 60;
		}
		writeToSerial(buildString(instructChars, x, instructChars.length - 1));
		GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Done Sending Instructions\n");
		GUI.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	// EDIT INSTRUCTIONS
	
	public void editInstructionSendList(int how){
		switch (how){
		case 0:
			GUI.InstructionsList.removeValue(GUI.InstructionsList.getSelectedIndex());
			GUI.InstructionsList.clearSelection();
			break;
		case 1:
			GUI.InstructionsSubmitBtn.setEnabled(false);
			GUI.InstructionsEditBtn.setEnabled(false);
			GUI.InstructionsDeleteBtn.setEnabled(false);
			GUI.InstructionsUpBtn.setEnabled(false);
			GUI.InstructionsDownBtn.setEnabled(false);
			GUI.InstructionsList.setEnabled(false);
			editingInstruction = true;
			if (((InstructionObj) GUI.InstructionsList.getSelectedItem()).getDestination() == 'r'){
				GUI.RoverCommandsList.setSelection(((InstructionObj) GUI.InstructionsList.getSelectedItem()).getEditIndexies()[0]);
			}
			else {
				GUI.SatelliteCommandList.setSelection(((InstructionObj) GUI.InstructionsList.getSelectedItem()).getEditIndexies()[0]);
			}
			GUI.ParameterList.setSelection(((InstructionObj) GUI.InstructionsList.getSelectedItem()).getEditIndexies()[1]);
			GUI.ParameterTxt.setText(((InstructionObj) GUI.InstructionsList.getSelectedItem()).getParameter());
			break;
		case 2:
			Object hold = GUI.InstructionsList.getSelectedItem();
			int where = GUI.InstructionsList.getSelectedIndex();
			GUI.InstructionsList.removeValue(where);
			GUI.InstructionsList.addValue(hold, where - 1);
			GUI.InstructionsList.setSelection(where - 1);
			break;
		case 3:
			Object hold1 = GUI.InstructionsList.getSelectedItem();
			int where1 = GUI.InstructionsList.getSelectedIndex();
			GUI.InstructionsList.removeValue(where1);
			GUI.InstructionsList.addValue(hold1, where1 + 1);
			GUI.InstructionsList.setSelection(where1 + 1);
			break;
		}
	}
	
	public void addInstructionToList(){
		new SynchronousThread(0, new Runnable(){
			public void run(){
				(new InstrucitonEditor()).open();
			}
		}, 1, "add instructions 2");
	}
	
	public void addInstructionsToList2(boolean addRover, boolean addSat, String title, InstructionObj[] instruct){
		if (editingCommand == -1){
			if (addRover){
				GUI.RoverCommandsList.addValue(title);
				RoverInstructions = Augment(RoverInstructions, instruct.clone());
			}
			if (addSat){
				GUI.SatelliteCommandList.addValue(title);
				SatelliteInstructions = Augment(SatelliteInstructions, instruct.clone());
			}
		}
		else {
			if (addRover){
				GUI.RoverCommandsList.removeValue(editingCommand);
				GUI.RoverCommandsList.addValue(title, editingCommand);
				RoverInstructions[editingCommand] = instruct.clone();
			}
			if (addSat){
				GUI.SatelliteCommandList.removeValue(editingCommand);
				GUI.SatelliteCommandList.addValue(title, editingCommand);
				SatelliteInstructions[editingCommand] = instruct.clone();
			}
			editingCommand = -1;
		}
		GUI.ParameterList.setValues(new String[0]);
		SaveProgrammer();
	}
	
	public void editInstructionInList(){
		if (GUI.RoverCommandsList.getSelectedIndex() != -1){
			int selected = GUI.RoverCommandsList.getSelectedIndex();
			String[] parameters = new String[RoverInstructions[selected].length];
			String[][] commands = new String[parameters.length][];
			boolean[] bools = new boolean[parameters.length];
			int x = 0;
			while (x < parameters.length){
				parameters[x] = RoverInstructions[selected][x].toString();
				commands[x] = RoverInstructions[selected][x].getCommands();
				bools[x] = RoverInstructions[selected][x].getRequiresText();
				x++;
			}
			editingCommand = selected;
			final String[] finParam = parameters;
			final String[][] finCommands = commands;
			final boolean[] finBools = bools;
			new SynchronousThread(0, new Runnable(){
				public void run(){
					(new InstrucitonEditor(true, false, (String)GUI.RoverCommandsList.getSelectedItem(), finParam, finCommands, finBools)).open();
				}
			}, 1, "edit instructions");
		}
		if (GUI.SatelliteCommandList.getSelectedIndex() != -1){
			int selected = GUI.SatelliteCommandList.getSelectedIndex();
			String[] parameters = new String[GUI.SatelliteCommandList.getItems().size()];
			String[][] commands = new String[parameters.length][];
			boolean[] bools = new boolean[parameters.length];
			int x = 0;
			while (x < parameters.length){
				parameters[x] = SatelliteInstructions[selected][x].toString();
				commands[x] = SatelliteInstructions[selected][x].getCommands();
				bools[x] = SatelliteInstructions[selected][x].getRequiresText();
				x++;
			}
			editingCommand = selected;
			final String[] finParam = parameters;
			final String[][] finCommands = commands;
			final boolean[] finBools = bools;
			new SynchronousThread(0, new Runnable(){
				public void run(){
					(new InstrucitonEditor(true, false, (String)GUI.SatelliteCommandList.getSelectedItem(), finParam, finCommands, finBools)).open();
				}
			}, 1, "edit instructions 2");
		}
	}
	
	// FILE STUFF
	
	private void SaveProgrammer(){
		String filename = "CommandString.dll";
        File file = new File("CommandString.dll");
        if (!file.delete()){
            LOG.log(Level.WARN, "Failed to delete commandString.dll");
        }
		try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(new SaveFile(actionCommands, actionTips, actionIcons, GUI.RoverCommandsList.getListItems(), RoverInstructions, GUI.SatelliteCommandList.getListItems(), SatelliteInstructions));
			out.close();
            fos.close();
		}
		catch (Exception ex) {
            LOG.log(Level.ERROR, "Failed to write out new commandString.dll", ex);
		}
	}
	
	// SUPPORTING METHODS
	
	private void runThreadOutOfSync(Runnable action, String name){
		new FreeThread(0, action, 1, name);
	}
	
	private String buildString(char[] array, int start, int end){
		String out = "";
        for (int i = start; i <= end && i < array.length; i++){
			out += array[i] + "";
		}
		return out;
	}
	
	private String[] Augment(String[] array, String val){
		String[] out = new String[array.length + 1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private String[] Remove(String[] array, int which){
		String[] out = new String[array.length - 1];
		int x = 0;
		while (x < which){
			out[x] = array[x];
			x++;
		}
		x++;
		while (x < array.length){
			out[x - 1] = array[x];
			x++;
		}
		return out;
	}
	
	private InstructionObj[][] Augment(InstructionObj[][] array, InstructionObj[] val){
		InstructionObj[][] out = new InstructionObj[array.length+1][];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private void delay(int length) {
		String newname = GLOBAL.delayThread(Thread.currentThread().getName(), length);
		if (newname.equals("pass")){
			return;
		}
		while (!GLOBAL.getThreadRunPermission(newname)) {}
		GLOBAL.threadDelayComplete(Thread.currentThread().getName());
	}
}
