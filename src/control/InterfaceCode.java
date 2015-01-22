package control;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import objects.ThreadTimer;
import objects.ZDate;
import objects.Map;
import wrapper.Access;
import wrapper.Globals;

public class InterfaceCode {
    
    private File logFile;
	private String connectedPort = "COM13";
	private ZDate DateTime;
	public ThreadTimer clock;
	private String IDcode = "g";
	
	private int connectionTime = 0;
	private int countSec = 0;
	private boolean Connected = false;
	private Map<String, String> roverNames;
	private Map<String, String> satelliteNames;
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
	private ThreadTimer listenTimer;
	private Runnable listenAction;
	private Runnable listenFail;
	private String[] receivedFiles = new String[0];
	
	private int[] currentActionPages = { 0, 0 };
	private int pageLength = Access.CODE.GUI.InterfacePnl.RoverBtns.length;
	private int numberOfPages = 4;
	private String[][] actionCommands = new String[2][pageLength*numberOfPages];
	private String[][] actionTips = new String[2][pageLength*numberOfPages];
	private String[][] actionIcons = new String[2][pageLength*numberOfPages];
	
	private InstructionObj[][] RoverInstructions;
	private InstructionObj[][] SatelliteInstructions;
	
	// SETUP
	
	public InterfaceCode(){
		DateTime = new ZDate();
		DateTime.setFormat("[hh:mm:ss]");
		clock = new ThreadTimer(1000, new Runnable(){
			public void run(){
				DateTime.advanceClock();
				if (Connected){
					countSec++;
					if (countSec == 0){
						connectionTime++;
						countSec = 0;
						Access.CODE.GUI.InterfacePnl.ConnectionLbl.setText("Connected for " + connectionTime + " min.");
					}
				}
				else {
					Access.CODE.GUI.InterfacePnl.ConnectionLbl.setText("Not Connected");
				}
			}
		}, ThreadTimer.FOREVER, "clock", false);
		initalize();
	}
	
	public static void start(){
		ThreadTimer serialCheck = new ThreadTimer(400, new Runnable(){
			public void run(){
				Access.INTERFACE.updateSerialCom();
			}
		}, ThreadTimer.FOREVER, "Interface serial");
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
			Globals.reportError("InterfaceCode", "initalize - mkdir", e);
		}
		try {
			logFile = new File("Logs\\Log File " + DateTime.toString("MM-dd-yyyy hh-mm") + ".txt");
			logFile.createNewFile();
			BufferedWriter write = new BufferedWriter(new FileWriter(logFile));
			write.write("CSM PHM Rover System Simulator Log " + DateTime.toString("on MM-dd-yyyy at hh:mm") + "\r\n\r\n");
			write.close();
		} catch (Exception e) {
			Globals.reportError("InterfaceCode", "initalize - createLog", e);
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
				Access.CODE.GUI.InterfacePnl.RoverCommandsList.setValues(input.getRoverOptions());
				RoverInstructions = input.getRoverInstructions();
			}
			catch (Exception e){
				RoverInstructions = new InstructionObj[0][0];
			}
			try {
				input.getSatelliteOptions().clone();
				Access.CODE.GUI.InterfacePnl.SatelliteCommandList.setValues(input.getSatelliteOptions());
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
		Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Pinging Rover...\n");
		writeToSerial(tagMessage("^", 'r'), true);
		listenForSignal("g ^", new Runnable(){
			public void run(){
				Connected = true;
				Access.CODE.GUI.InterfacePnl.ConnectionLbl.setText("Connected for 0 min.");
				Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Rover Connected: " + DateTime.toString("hh:mm:ss") + "\n");
				(new PopUp()).showConfirmDialog("Rover connected.", "Ping Confirm", PopUp.DEFAULT_OPTIONS);
			}
		}, new Runnable(){
			public void run(){
				Connected = false;
				Access.CODE.GUI.InterfacePnl.ConnectionLbl.setText("Not Connected.");
				Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Rover did not respond.\n");
				(new PopUp()).showConfirmDialog("No Rover found.", "Ping Failed", PopUp.DEFAULT_OPTIONS);
			}
		}, 
		10);
	}
	
	public void setCallTags(Map<String, String> rover, Map<String, String> satellite){
		roverNames = rover;
		satelliteNames = satellite;
		Access.CODE.GUI.InterfacePnl.setNamesLists(rover.getKeys(), satellite.getKeys());
	}
	
	// COM CONNECTION STUFF
	
	public void resetConnection(){
		if (Connected){
			pingRover();
		}
	}
	
	public void changeCOMPort(){
		connectionTime = 0;
		connectedPort = (String)Access.CODE.GUI.InterfacePnl.PortSelectCombo.getSelectedItem();
		Access.CODE.GUI.InterfacePnl.ConnectionLbl.setText("Connected for " + connectionTime + " min.");
		writeToLog("COM Port", "Connection Changed to " + connectedPort);
		resetConnection();
	}

	public void writeToSerial(String msg){
		if (Connected && !muted){
			writeToLog("Serial Writer", "Command sent: \'" + msg + "\'" + DateTime.toString());
			//try {
            // 	outputStream.write(msg.getBytes());
        	//} catch (Exception e) {
        	//	Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Message Failed" + "\n");
        	//	pingRover();
        	//}
			char[] output = msg.toCharArray();
			int x = 0;
			while (x < output.length){
				Globals.writeToSerial(output[x], IDcode); // Write to Serial one char at a time
				delay(20); // Pause for sending
				x++;
			}
		}
	}
	
	public void writeToSerial(String msg, boolean override){
		if ((Connected || override) && !muted){
			writeToLog("Serial Writer", "Command sent: \'" + msg + "\'" + DateTime.toString());
			//try {
            // 	outputStream.write(msg.getBytes());
        	//} catch (Exception e) {
        	//	Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Message Failed" + "\n");
        	//	pingRover();
        	//}
			char[] output = msg.toCharArray();
			int x = 0;
			while (x < output.length){
				Globals.writeToSerial(output[x], IDcode); // Write to Serial one char at a time
				delay(20); // Pause for sending
				x++;
			}
		}
	}
	
	private String tagMessage(String mess, char which){
		if (which == 'r'){
			return satelliteNames.get((String)Access.CODE.GUI.InterfacePnl.SatSelectionCombo.getSelectedItem()) + " " + roverNames.get((String)Access.CODE.GUI.InterfacePnl.RoverSelectionCombo.getSelectedItem()) + " " + mess;
		}
		else if (which == 's'){
			return satelliteNames.get((String)Access.CODE.GUI.InterfacePnl.SatSelectionCombo.getSelectedItem()) + " c " + mess;
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
						Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Recieved: " + data + "\n");
						writeToLog("Serial Reader", "Recieved Note: " + data);
					}
					else if (input[2] == 'i'){
						receivingFile = true;
						int filelength = Integer.parseInt(buildString(input, 4, input.length - 1));
						if (filelength < 0){
							filelength += 65536;
						}
						ReadPhoto(filelength);
					}
					else if (input[2] == '}'){
						muted = true;
						Access.CODE.GUI.InterfacePnl.MuteIcon.setVisible(true);
					}
					else if (input[2] == '{'){
						muted = false;
						Access.CODE.GUI.InterfacePnl.MuteIcon.setVisible(false);
					}
					else if (input[2] == '*'){
						Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "The rover has pinged the ground station.\n");
						writeToSerial(tagMessage("*", 'r'));
					}
				}
				else {
					if (input.equals("Data Could Not be Parsed\n".toCharArray())){
						Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Data Could Not be Parsed\n");
					}
				}
			}
		}
	}
	
	private String readFromSerial(){
		if (!connectedPort.equals("")){
			//if (inputStream.available() > 0){
			if (Globals.RFAvailable(IDcode) > 0){
				// System.out.println("Available");
				int hold = 0;
				while (hold != Globals.RFAvailable(IDcode)){
					hold = Globals.RFAvailable(IDcode);
					delay(7);
				}
				String out = "";
					//while(inputStream.available() > 0) {
				while (Globals.RFAvailable(IDcode) > 0) {
						//out += (char)(inputStream.read());
					out += (char)Globals.ReadSerial(IDcode);
				}
				if (listening){
					if (out.equals(listenFor)){
						new ThreadTimer(0, listenAction, 1, "interface listening");
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
				listenTimer.interrupt();
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
		listenTimer = new ThreadTimer((secs*1000), listenFail, 1, "listening timer");
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
	            Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Sent: \"" + actionCommands[section][which] + "\"\n");
				if (section == 0){
					writeToSerial(tagMessage(actionCommands[section][which], 'r'));
					listenForSignal("g #", new Runnable(){
						public void run(){
							listenForSignal("g %", new Runnable(){
								public void run(){
									new ThreadTimer(0, confirmMessage, 1, "confirm 1");
								}
							}, new Runnable(){
								public void run(){
									new ThreadTimer(0, failMessage, 1, "fail 1");
								}
							}, 5);
						}
					}, new Runnable(){
						public void run(){
							new ThreadTimer(0, failMessage, 1, "fail 1.1");
						}
					}, 4);
				}
				else {
					writeToSerial(tagMessage(actionCommands[section][which], 's'));
					listenForSignal("g #", new Runnable(){
						public void run(){
							new ThreadTimer(0, confirmMessage, 1, "confirm 2");
						}
					}, new Runnable(){
						public void run(){
							new ThreadTimer(0, failMessage, 1, "fail 2");
						}
					}, 4);
				}
			}
		}
	}
	
	public void sendRoverCommand(){
		if (!Access.CODE.GUI.InterfacePnl.RoverSendTxt.getText().equals("")){
			if (!Connected){
				Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "You are not Connected.\n");
			}
			//writeToSerial(tagMessage(Access.CODE.GUI.InterfacePnl.RoverSendTxt.getText(), 'r'));
			writeToSerial(Access.CODE.GUI.InterfacePnl.RoverSendTxt.getText(), true);
            Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Sent: \"" + Access.CODE.GUI.InterfacePnl.RoverSendTxt.getText() + "\"\n");
            listenForSignal("g #", new Runnable(){
				public void run(){
					listenForSignal("g %", new Runnable(){
						public void run(){
							new ThreadTimer(0, confirmMessage, 1, "confirm 3");
						}
					}, new Runnable(){
						public void run(){
							new ThreadTimer(0, failMessage, 1, "fail 3");
						}
					}, 5);
				}
			}, new Runnable(){
				public void run(){
					new ThreadTimer(0, failMessage, 1, "fail 3.1");
				}
			}, 4);
			Access.CODE.GUI.InterfacePnl.RoverSendTxt.setText("");
		}
		else {
			new ThreadTimer(0, new Runnable(){
				public void run(){
					(new PopUp()).showConfirmDialog("You must enter a message into the field.", "Message Failed", PopUp.DEFAULT_OPTIONS);
				}
			}, 1, "invalid message 1");
		}
	}
	
	public void sendSatCommand(){
		if (!Access.CODE.GUI.InterfacePnl.SatSendTxt.getText().equals("")){
			writeToSerial(tagMessage(Access.CODE.GUI.InterfacePnl.SatSendTxt.getText(), 's'));
            Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Sent: \"" + Access.CODE.GUI.InterfacePnl.SatSendTxt.getText() + "\"\n");
			listenForSignal("g #", new Runnable(){
				public void run(){
					new ThreadTimer(0, confirmMessage, 1, "confirm 4");
				}
			}, new Runnable(){
				public void run(){
					new ThreadTimer(0, failMessage, 1, "fail 4");
				}
			}, 4);
			Access.CODE.GUI.InterfacePnl.SatSendTxt.setText("");
		}
		else {
			new ThreadTimer(0, new Runnable(){
				public void run(){
					(new PopUp()).showConfirmDialog("You must enter a message into the field.", "Message Failed", PopUp.DEFAULT_OPTIONS);
				}
			}, 1, "invalid message 2");
		}
	}
	
	public void advanceActionPage(int direction, int section){
		currentActionPages[section] += direction;
		if (section == 0){
			if (currentActionPages[section] == 0){
				Access.CODE.GUI.InterfacePnl.RoverPageLeftBtn.setEnabled(false);
			}
			else if (currentActionPages[section] == numberOfPages-1){
				Access.CODE.GUI.InterfacePnl.RoverPageRightBtn.setEnabled(false);
			}
			else{
				Access.CODE.GUI.InterfacePnl.RoverPageLeftBtn.setEnabled(true);
				Access.CODE.GUI.InterfacePnl.RoverPageRightBtn.setEnabled(true);
			}
			Access.CODE.GUI.InterfacePnl.RoverPageLbl.setText((currentActionPages[section]+1) + " / " + numberOfPages);
		}
		else {
			if (currentActionPages[section] == 0){
				Access.CODE.GUI.InterfacePnl.SatPageLeftBtn.setEnabled(false);
			}
			else if (currentActionPages[section] == numberOfPages-1){
				Access.CODE.GUI.InterfacePnl.SatPageRightBtn.setEnabled(false);
			}
			else{
				Access.CODE.GUI.InterfacePnl.SatPageLeftBtn.setEnabled(true);
				Access.CODE.GUI.InterfacePnl.SatPageRightBtn.setEnabled(true);
			}
			Access.CODE.GUI.InterfacePnl.SatPageLbl.setText((currentActionPages[section]+1) + " / " + numberOfPages);			
		}
		UpdateActionBtns();
	}
	
	
	// ACTION BUTTON EDITING
	
	public void addRoverBtn(){
		new ThreadTimer(0, new Runnable(){
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
		Access.CODE.GUI.InterfacePnl.RoverDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
		int x = 0;
		while (x < Access.CODE.GUI.InterfacePnl.RoverBtns.length){
			Access.CODE.GUI.InterfacePnl.RoverBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
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
			Access.CODE.GUI.InterfacePnl.RoverDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
			int x = 0;
			while (x < Access.CODE.GUI.InterfacePnl.RoverBtns.length){
				Access.CODE.GUI.InterfacePnl.RoverBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				x++;
			}
		}
	}
	
	private void editRover2(final int which){
		new ThreadTimer(0, new Runnable(){
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
		new ThreadTimer(0, new Runnable(){
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
		Access.CODE.GUI.InterfacePnl.SatDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
		int x = 0;
		while (x < Access.CODE.GUI.InterfacePnl.SatBtns.length){
			Access.CODE.GUI.InterfacePnl.SatBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
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
			Access.CODE.GUI.InterfacePnl.SatDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
			int x = 0;
			while (x < Access.CODE.GUI.InterfacePnl.SatBtns.length){
				Access.CODE.GUI.InterfacePnl.SatBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				x++;
			}
		}		
	}
	
	private void editSat2(final int which){
		new ThreadTimer(0, new Runnable(){
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
		Access.CODE.GUI.InterfacePnl.RoverDeleteLink.setText("<HTML><U>Delete</U></HTML>");
		Access.CODE.GUI.InterfacePnl.SatDeleteLink.setText("<HTML><U>Delete</U></HTML>");
		int x = 0;
		while (x < Access.CODE.GUI.InterfacePnl.RoverBtns.length){
			Access.CODE.GUI.InterfacePnl.RoverBtns[x].setCursor(new Cursor(Cursor.HAND_CURSOR));
			Access.CODE.GUI.InterfacePnl.SatBtns[x].setCursor(new Cursor(Cursor.HAND_CURSOR));
			x++;
		}
	}

	private void UpdateActionBtns(){
		int x = 0;
		while (x < pageLength){
			if (!actionCommands[0][x].equals("")){
				Access.CODE.GUI.InterfacePnl.RoverBtns[x].setToolTipText(actionTips[0][x+currentActionPages[0]*pageLength]);
			}
			else {
				Access.CODE.GUI.InterfacePnl.RoverBtns[x].setToolTipText("Unassigned");
			}
			if (!actionCommands[1][x].equals("")){
				Access.CODE.GUI.InterfacePnl.SatBtns[x].setToolTipText(actionTips[1][x+currentActionPages[1]*pageLength]);
			}
			else {
				Access.CODE.GUI.InterfacePnl.SatBtns[x].setToolTipText("Unassigned");
			}
			try {
				Access.CODE.GUI.InterfacePnl.RoverBtns[x].setImage(new ImageIcon(InterfaceCode.class.getResource("/" + actionIcons[0][x+currentActionPages[0]*pageLength])));
			}
			catch (Exception e) {
				Access.CODE.GUI.InterfacePnl.RoverBtns[x].setImage(null);
			}
			Access.CODE.GUI.InterfacePnl.RoverBtns[x].setEnabled(!actionCommands[0][x+currentActionPages[0]*pageLength].equals(""));
			try {
				Access.CODE.GUI.InterfacePnl.SatBtns[x].setImage(new ImageIcon(InterfaceCode.class.getResource("/" + actionIcons[1][x+currentActionPages[1]*pageLength])));
			}
			catch (Exception e){
				Access.CODE.GUI.InterfacePnl.SatBtns[x].setImage(null);
			}
			Access.CODE.GUI.InterfacePnl.SatBtns[x].setEnabled(!actionCommands[1][x+currentActionPages[1]*pageLength].equals(""));
			x++;
		}
	}
	
	// STATUS HANDLING
	
	public void setRoverPower(double voltage){
		int precent = (int)Math.round((voltage / 9.0)*100);
		Access.CODE.GUI.InterfacePnl.StatusRoverPower.setValue(precent);
		Access.CODE.GUI.InterfacePnl.StatusRoverPower.setForeground(getPowerColor(precent));
	}
	
	public void setSatellitePower(double voltage){
		int precent = (int)Math.round((voltage / 9.0)*100);
		Access.CODE.GUI.InterfacePnl.StatusSatPower.setValue(precent);
		Access.CODE.GUI.InterfacePnl.StatusSatPower.setForeground(getPowerColor(precent));
	}
	
	public void setMotorPower(double voltage){
		int precent = (int)Math.round((voltage / 12.0)*100);
		Access.CODE.GUI.InterfacePnl.StatusMotorPower.setValue(precent);
		Access.CODE.GUI.InterfacePnl.StatusMotorPower.setForeground(getPowerColor(precent));
	}
	
	public void setArmPower(double voltage){
		int precent = (int)Math.round((voltage / 6.0)*100);
		Access.CODE.GUI.InterfacePnl.StatusArmPower.setValue(precent);
		Access.CODE.GUI.InterfacePnl.StatusArmPower.setForeground(getPowerColor(precent));
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
				while (Globals.RFAvailable(IDcode) == 0) {
					delay(5);
				}
				delay(20);
				Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Receiving Image.\n");
				writeToLog("Photo Reciever", "Receiving Image");
				String text = Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText();
				byte[] bytes = new byte[length];
				char[] progress = new char[length / 500 + 1];
				int index = 0;
				while (index < progress.length){
					progress[index] = '-';
					index++;
				}
				progress[0] = '>';
				Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
				index = 0;
				int i = 0;
				//int escape = 0;
				while (index < length && Globals.RFAvailable(IDcode) > 0){
					i = 0;
					while (i < 60 && index < length){
						//escape = 0;
						try {
							bytes[index] = Globals.ReadSerial(IDcode);
						}
						catch (ArrayIndexOutOfBoundsException e){
							Globals.reportError("InterfaceCode", "ReadPhoto - e", e);
							break;
						}
						if (index % 500 == 0 && index != 0){
							progress[index / 500 - 1] = '-';
							progress[index / 500] = '>';
							Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
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
					image = new File(image.getAbsoluteFile() + "\\Photos\\IMAGE " + DateTime.toString("MM-dd hh-mm") + ".jpg");
					FileOutputStream fos = new FileOutputStream(image);
					fos.write(bytes);
					receivedFiles = Augment(receivedFiles, image.getAbsolutePath());
					fos.close();
					writeToLog("Interface", "Recieved Image.  Stored in: " + image.getAbsolutePath());
					receivingFile = false;
					Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(text + "Done.\n");
					Access.CODE.GUI.InterfacePnl.MailBtn.setIcon(new ImageIcon(InterfaceCode.class.getResource("/Mail_Message.png")));
				}
				else {
					Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(text + "Image transfer failed, incomplete size requirement.\n");
					writeToLog("Interface", "Image transfer failed due to incomplete size requirement.");
				}
			}	
			catch(Exception ex) {
				Globals.reportError("InterfaceCode", "readPhoto - ex", ex);
			}
		}
	}
	
	private void ReadDataFile(int length){
		if (receivingFile){
			try {
				while (Globals.RFAvailable(IDcode) == 0) {}
					delay(20);
					Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Receiving Data.\n");
					writeToLog("Date Reciever", "Receiving Data File");
					String text = Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText();
					byte[] bytes = new byte[length];
					char[] progress = new char[length / 100 + 1];
					int index = 0;
					while (index < progress.length){
						progress[index] = '-';
						index++;
					}
					progress[0] = '>';
					Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
					index = 0;
					//int escape = 0;
					while (index < length){
						while(Globals.RFAvailable(IDcode) > 0) {
							//escape = 0;
							try {
								bytes[index] = Globals.ReadSerial(IDcode);
							}
							catch (ArrayIndexOutOfBoundsException e){
								Globals.reportError("InterfaceCode", "ReadDataFile - e", e);
								break;
							}
							if (index % 100 == 0 && index != 0){
								progress[index / 100 - 1] = '-';
								progress[index / 100] = '>';
								Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
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
						image = new File(image.getAbsoluteFile() + "\\Data\\DATA " + DateTime.toString("MM-dd hh-mm") + ".CSV");
						FileOutputStream fos = new FileOutputStream(image);
						fos.write(bytes);
						receivedFiles = Augment(receivedFiles, image.getAbsolutePath());
						fos.close();
						writeToLog("Interface", "Recieved Data File.  Stored in: " + image.getAbsolutePath());
						receivingFile = false;
						Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(text + "Done.\n");
						Access.CODE.GUI.InterfacePnl.MailBtn.setIcon(new ImageIcon(InterfaceCode.class.getResource("/Mail_Message.png")));
					}
					else {
						Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(text + "Data File transfer failed, incomplete size requirement.\n");
						writeToLog("Interface", "Data File transfer failed due to incomplete size requirement.");
					}
			}	
			catch(Exception exe) {
				Globals.reportError("InterfaceCode", "ReadDataFile - exe", exe);
			}
		}
	}
	
	public void OpenRecievedFiles(){
		if (receivedFiles.length > 0){
			new ThreadTimer(0, new Runnable(){
				public void run(){
					String[] choices = new String[receivedFiles.length];
					int x = 0;
					while (x < choices.length){
						choices[x] = getFileName(receivedFiles[x]);
						x++;
					}
					int choice = (new PopUp()).showOptionDialog("Select a File", "Open File", choices);
					if (choice != -1){
						switch (getFileType(receivedFiles[choice])){
						case ("jpg"):
							File image = new File(receivedFiles[choice]);
							try {
								final BufferedImage img = ImageIO.read(image);
								new ThreadTimer(0, new Runnable(){
									public void run(){
										PopUp opane = new PopUp();
										opane.setCustomButtonOptions(new String[] { "Save", "Close" }, new int[] { 0, 1 });
										int choice = opane.showPictureDialog(new ImageIcon(img), "Received Image", PopUp.CUSTOM_OPTIONS);
										if (choice == 0){
											javax.swing.JFileChooser browse = new javax.swing.JFileChooser();
											browse.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG file", "jpg", "jpeg"));
											browse.showSaveDialog(Access.CODE.GUI.InterfacePnl);
											try {
												String filepath = browse.getSelectedFile().getAbsolutePath() + ".jpeg";
												File imageOut = new File(filepath);
												try {
													ImageIO.write(img, "jpg", imageOut);
												}
												catch (Exception e1){
													Globals.reportError("InterfaceCode", "OpenRecievedFiles - e1", e1);
													opane.showConfirmDialog("Something went worng and the file failed to save.", "IO Error", PopUp.DEFAULT_OPTIONS);
												}
												
											} catch (Exception e2) {
												Globals.reportError("InterfaceCode", "OpenRecievedFiles - e2", e2);
											}								
										}
									}
								}, 1, "open file 1");
								receivedFiles = Remove(receivedFiles, choice);
								if (receivedFiles.length == 0){
									Access.CODE.GUI.InterfacePnl.MailBtn.setIcon(new ImageIcon(InterfaceCode.class.getResource("/Mail.png")));
								}
							} catch (Exception e3) {
								Globals.reportError("InterfaceCode", "OpenRecievedFiles - e3", e3);
							}
							break;
						case "CSV":
							final String file = receivedFiles[choice];
							new ThreadTimer(0, new Runnable(){
								public void run(){
									int choice = new CSVFrame().OpenCSVFile(file);
									if (choice == 1){
										javax.swing.JFileChooser browse = new javax.swing.JFileChooser();
										browse.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Comma Seperated Value", "CSV"));
										browse.showSaveDialog(Access.CODE.GUI.InterfacePnl);
										try {
											String filepath = browse.getSelectedFile().getAbsolutePath() + ".CSV";
											try {
												String data = "";
												FileReader input = new FileReader(file);
												Scanner dataIn = new Scanner(input);
												while (dataIn.hasNextLine()){
													data += dataIn.nextLine() + "\n";
												}
												input.close();
												PrintWriter dataOut = new PrintWriter(filepath);
												dataOut.print(data);
												dataOut.close();
											}
											catch (Exception e4){
												Globals.reportError("InterfaceCode", "OpenRecievedFiles - e4", e4);
												new PopUp().showConfirmDialog("Something went worng and the file failed to save.", "IO Error", PopUp.DEFAULT_OPTIONS);
											}											
										} catch (Exception e5) {
											Globals.reportError("InterfaceCode", "OpenReviecedFiles - e5", e5);
										}								
									}
								}
							}, 1, "open file 2");
							receivedFiles = Remove(receivedFiles, choice);
							if (receivedFiles.length == 0){
								Access.CODE.GUI.InterfacePnl.MailBtn.setIcon(new ImageIcon(InterfaceCode.class.getResource("/Mail.png")));
							}
							break;
						}
					}
				}
			}, 1, "open file 3");
		}
		else {
			new ThreadTimer(0, new Runnable() {
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
        Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Canceling Instructions...");
		listenForSignal("g KillDone", new Runnable(){
			public void run(){
				new ThreadTimer(0, new Runnable(){
					public void run(){
						(new PopUp()).showConfirmDialog("All Instructions Canceled.", "Action Confirmed", PopUp.DEFAULT_OPTIONS);
					}}, 1, "cancel instructions");
				Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Done.\n");
			}
		}, new Runnable(){
			public void run(){
				Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Failed.\n");
			}
		}, 3);
	}
	
	public void SalelliteCommandChanged(){
		if (Access.CODE.GUI.InterfacePnl.SatelliteCommandList.getSelectedIndex() != -1){
			Access.CODE.GUI.InterfacePnl.RoverCommandsList.clearSelection();
			setParametersList(SatelliteInstructions[Access.CODE.GUI.InterfacePnl.SatelliteCommandList.getSelectedIndex()]);
			Access.CODE.GUI.InterfacePnl.EditInstructionLink.setEnabled(true);
		}
		else {
			Access.CODE.GUI.InterfacePnl.EditInstructionLink.setEnabled(false);
		}
	}
	
	public void RoverCommandChanged(){
		if (Access.CODE.GUI.InterfacePnl.RoverCommandsList.getSelectedIndex() != -1){
			Access.CODE.GUI.InterfacePnl.SatelliteCommandList.clearSelection();
			setParametersList(RoverInstructions[Access.CODE.GUI.InterfacePnl.RoverCommandsList.getSelectedIndex()]);
			Access.CODE.GUI.InterfacePnl.EditInstructionLink.setEnabled(true);
		}	
		else {
			Access.CODE.GUI.InterfacePnl.EditInstructionLink.setEnabled(false);
		}
	}
	
	private void setParametersList(InstructionObj[] instrcts){
		Access.CODE.GUI.InterfacePnl.ParameterTxt.setVisible(false);
		Access.CODE.GUI.InterfacePnl.ParameterList.setValues(instrcts);
	}
	
	public void ParametersChanged(){
		if (Access.CODE.GUI.InterfacePnl.ParameterList.getSelectedIndex() != -1){
			Access.CODE.GUI.InterfacePnl.ParameterTxt.setVisible(((InstructionObj) Access.CODE.GUI.InterfacePnl.ParameterList.getItemAt(Access.CODE.GUI.InterfacePnl.ParameterList.getSelectedIndex())).getRequiresText());
			Access.CODE.GUI.InterfacePnl.InstructionAddBtn.setEnabled(true);
		}
		else {
			Access.CODE.GUI.InterfacePnl.ParameterTxt.setVisible(false);
			Access.CODE.GUI.InterfacePnl.InstructionAddBtn.setEnabled(false);
		}
	}
	
	public void AddInstruction(){
		int where = -1;
		if (editingInstruction){
			where = Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedIndex();
			Access.CODE.GUI.InterfacePnl.InstructionsList.removeValue(where);
		}
		if (!Access.CODE.GUI.InterfacePnl.ParameterTxt.isVisible() || !Access.CODE.GUI.InterfacePnl.ParameterTxt.getText().equals("")){
			Access.CODE.GUI.InterfacePnl.InstructionAddBtn.setEnabled(false);
			InstructionObj newCmd = (InstructionObj) Access.CODE.GUI.InterfacePnl.ParameterList.getSelectedItem();
			if (Access.CODE.GUI.InterfacePnl.RoverCommandsList.getSelectedIndex() != -1){
				newCmd.setDestination('r');
				newCmd.setTitle("R-" + (String)Access.CODE.GUI.InterfacePnl.RoverCommandsList.getSelectedItem() + "-" + Access.CODE.GUI.InterfacePnl.ParameterList.getSelectedItem().toString());
				newCmd.setEditIndexies(Access.CODE.GUI.InterfacePnl.RoverCommandsList.getSelectedIndex(), Access.CODE.GUI.InterfacePnl.ParameterList.getSelectedIndex());
			}
			else {
				newCmd.setDestination('s'); // Add a 'c' for command?
				newCmd.setTitle("S-" + (String)Access.CODE.GUI.InterfacePnl.SatelliteCommandList.getSelectedItem() + "-" + Access.CODE.GUI.InterfacePnl.ParameterList.getSelectedItem().toString());
				newCmd.setEditIndexies(Access.CODE.GUI.InterfacePnl.SatelliteCommandList.getSelectedIndex(), Access.CODE.GUI.InterfacePnl.ParameterList.getSelectedIndex());
			}
			if (Access.CODE.GUI.InterfacePnl.ParameterTxt.isVisible()){
				newCmd.fillParameter(Access.CODE.GUI.InterfacePnl.ParameterTxt.getText());
				newCmd.setTitle(newCmd.toString() + ":" + Access.CODE.GUI.InterfacePnl.ParameterTxt.getText());
			}
			if (editingInstruction){
				Access.CODE.GUI.InterfacePnl.InstructionsList.addValue(newCmd, where);
				Access.CODE.GUI.InterfacePnl.SatelliteCommandList.clearSelection();
				Access.CODE.GUI.InterfacePnl.RoverCommandsList.clearSelection();
				Access.CODE.GUI.InterfacePnl.ParameterList.setValues(new String[0]);
				Access.CODE.GUI.InterfacePnl.InstructionsSubmitBtn.setEnabled(true);
				Access.CODE.GUI.InterfacePnl.InstructionsEditBtn.setEnabled(true);
				Access.CODE.GUI.InterfacePnl.InstructionsDeleteBtn.setEnabled(true);
				Access.CODE.GUI.InterfacePnl.InstructionsUpBtn.setEnabled(true);
				Access.CODE.GUI.InterfacePnl.InstructionsDownBtn.setEnabled(true);
				Access.CODE.GUI.InterfacePnl.InstructionsList.setEnabled(true);
				editingInstruction = false;
			}
			else {
				Access.CODE.GUI.InterfacePnl.InstructionsList.addValue(newCmd);
				Access.CODE.GUI.InterfacePnl.SatelliteCommandList.clearSelection();
				Access.CODE.GUI.InterfacePnl.RoverCommandsList.clearSelection();
				Access.CODE.GUI.InterfacePnl.ParameterList.setValues(new String[0]);
				Access.CODE.GUI.InterfacePnl.InstructionsSubmitBtn.setEnabled(true);
			}
		}
		else {
			new ThreadTimer(0, new Runnable(){
				public void run(){
					(new PopUp()).showConfirmDialog("You must enter a typed value for the selected parameter parameter.", "Instruction Failed", PopUp.DEFAULT_OPTIONS);
				}
			}, 1, "add instructions");
		}
	}
	
	public void SendInstructions(){
		String out = "";
		for (Object val : Access.CODE.GUI.InterfacePnl.InstructionsList.getItems()){
			out += ((InstructionObj) val).buildCommand() + "\n";
		}
		out += "s report\nr report\n";
		Access.CODE.GUI.InterfacePnl.SatelliteCommandList.clearSelection();
		Access.CODE.GUI.InterfacePnl.RoverCommandsList.clearSelection();
		Access.CODE.GUI.InterfacePnl.ParameterList.setValues(new String[0]);
		Access.CODE.GUI.InterfacePnl.InstructionsList.setValues(new String[0]);
		Access.CODE.GUI.InterfacePnl.InstructionsSubmitBtn.setEnabled(false);
		Access.CODE.GUI.InterfacePnl.setCursor(new Cursor(Cursor.WAIT_CURSOR));
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
		Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.setText(Access.CODE.GUI.InterfacePnl.SerialDisplayLbl.getText() + "Done Sending Instructions\n");
		Access.CODE.GUI.InterfacePnl.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	// EDIT INSTRUCTIONS
	
	public void editInstructionSendList(int how){
		switch (how){
		case 0:
			Access.CODE.GUI.InterfacePnl.InstructionsList.removeValue(Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedIndex());
			Access.CODE.GUI.InterfacePnl.InstructionsList.clearSelection();
			break;
		case 1:
			Access.CODE.GUI.InterfacePnl.InstructionsSubmitBtn.setEnabled(false);
			Access.CODE.GUI.InterfacePnl.InstructionsEditBtn.setEnabled(false);
			Access.CODE.GUI.InterfacePnl.InstructionsDeleteBtn.setEnabled(false);
			Access.CODE.GUI.InterfacePnl.InstructionsUpBtn.setEnabled(false);
			Access.CODE.GUI.InterfacePnl.InstructionsDownBtn.setEnabled(false);
			Access.CODE.GUI.InterfacePnl.InstructionsList.setEnabled(false);
			editingInstruction = true;
			if (((InstructionObj) Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedItem()).getDestination() == 'r'){
				Access.CODE.GUI.InterfacePnl.RoverCommandsList.setSelection(((InstructionObj) Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedItem()).getEditIndexies()[0]);
			}
			else {
				Access.CODE.GUI.InterfacePnl.SatelliteCommandList.setSelection(((InstructionObj) Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedItem()).getEditIndexies()[0]);
			}
			Access.CODE.GUI.InterfacePnl.ParameterList.setSelection(((InstructionObj) Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedItem()).getEditIndexies()[1]);
			Access.CODE.GUI.InterfacePnl.ParameterTxt.setText(((InstructionObj) Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedItem()).getParameter());
			break;
		case 2:
			Object hold = Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedItem();
			int where = Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedIndex();
			Access.CODE.GUI.InterfacePnl.InstructionsList.removeValue(where);
			Access.CODE.GUI.InterfacePnl.InstructionsList.addValue(hold, where - 1);
			Access.CODE.GUI.InterfacePnl.InstructionsList.setSelection(where - 1);
			break;
		case 3:
			Object hold1 = Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedItem();
			int where1 = Access.CODE.GUI.InterfacePnl.InstructionsList.getSelectedIndex();
			Access.CODE.GUI.InterfacePnl.InstructionsList.removeValue(where1);
			Access.CODE.GUI.InterfacePnl.InstructionsList.addValue(hold1, where1 + 1);
			Access.CODE.GUI.InterfacePnl.InstructionsList.setSelection(where1 + 1);
			break;
		}
	}
	
	public void addInstructionToList(){
		new ThreadTimer(0, new Runnable(){
			public void run(){
				(new InstrucitonEditor()).open();
			}
		}, 1, "add instructions 2");
	}
	
	public void addInstructionsToList2(boolean addRover, boolean addSat, String title, InstructionObj[] instruct){
		if (editingCommand == -1){
			if (addRover){
				Access.CODE.GUI.InterfacePnl.RoverCommandsList.addValue(title);
				RoverInstructions = Augment(RoverInstructions, instruct.clone());
			}
			if (addSat){
				Access.CODE.GUI.InterfacePnl.SatelliteCommandList.addValue(title);
				SatelliteInstructions = Augment(SatelliteInstructions, instruct.clone());
			}
		}
		else {
			if (addRover){
				Access.CODE.GUI.InterfacePnl.RoverCommandsList.removeValue(editingCommand);
				Access.CODE.GUI.InterfacePnl.RoverCommandsList.addValue(title, editingCommand);
				RoverInstructions[editingCommand] = instruct.clone();
			}
			if (addSat){
				Access.CODE.GUI.InterfacePnl.SatelliteCommandList.removeValue(editingCommand);
				Access.CODE.GUI.InterfacePnl.SatelliteCommandList.addValue(title, editingCommand);
				SatelliteInstructions[editingCommand] = instruct.clone();
			}
			editingCommand = -1;
		}
		Access.CODE.GUI.InterfacePnl.ParameterList.setValues(new String[0]);
		SaveProgrammer();
	}
	
	public void editInstructionInList(){
		if (Access.CODE.GUI.InterfacePnl.RoverCommandsList.getSelectedIndex() != -1){
			int selected = Access.CODE.GUI.InterfacePnl.RoverCommandsList.getSelectedIndex();
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
			new ThreadTimer(0, new Runnable(){
				public void run(){
					(new InstrucitonEditor(true, false, (String)Access.CODE.GUI.InterfacePnl.RoverCommandsList.getSelectedItem(), finParam, finCommands, finBools)).open();
				}
			}, 1, "edit instructions");
		}
		if (Access.CODE.GUI.InterfacePnl.SatelliteCommandList.getSelectedIndex() != -1){
			int selected = Access.CODE.GUI.InterfacePnl.SatelliteCommandList.getSelectedIndex();
			String[] parameters = new String[Access.CODE.GUI.InterfacePnl.SatelliteCommandList.getItems().length];
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
			new ThreadTimer(0, new Runnable(){
				public void run(){
					(new InstrucitonEditor(true, false, (String)Access.CODE.GUI.InterfacePnl.SatelliteCommandList.getSelectedItem(), finParam, finCommands, finBools)).open();
				}
			}, 1, "edit instructions 2");
		}
	}
	
	// FILE STUFF
	
	private void SaveProgrammer(){
		String filename = "CommandString.dll";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			File file = new File("");
			file = new File(file.getAbsolutePath() + "\\CommandString.dll");
			file.delete();
		}
		catch (Exception e) {
			Globals.reportError("InterfaceCode", "SaveProgrammer - e", e);
		}
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(new SaveFile(actionCommands, actionTips, actionIcons, Access.CODE.GUI.InterfacePnl.RoverCommandsList.getListItems(), RoverInstructions, Access.CODE.GUI.InterfacePnl.SatelliteCommandList.getListItems(), SatelliteInstructions));
			out.close();
		}
		catch (Exception ex) {
			Globals.reportError("InterfaceCode", "SaveProgrammer - ex", ex);
		}
	}
	
	public void writeToLog(String from, String what){
		try {
			BufferedWriter write = new BufferedWriter(new FileWriter(logFile, true));
			if (from.equals("Timing")){
				write.write(what + "\n");
			}
			else {
				write.write(from + "\t\t" + what + "\t\t" + DateTime.toString("[MM/dd/yyyy hh:mm:ss.") + (Globals.TimeMillis%1000) + "]\r\n");
			}
			write.flush();
			write.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File getLogFile(){
		return logFile;
	}
	
	// SUPPORTING METHODS
	
	private String buildString(char[] array, int start, int end){
		String out = "";
		while (start <= end){
			try {
				out += array[start] + "";
			}
			catch (Exception e){
				Globals.reportError("InterfaceCode", "buildString", e);
				return "";
			}
			start++;
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
	
	private void delay(double length) {
		try{
			Thread.sleep((long)(length/Globals.getTimeScale()), (int)((length/Globals.getTimeScale()-(int)length/Globals.getTimeScale())*1000000));
		} catch (Exception e) {
			Globals.reportError("InterfaceCode", "delay", e);
		}
		//long start = System.nanoTime();
		//while (((System.nanoTime()-start) < (length*1000000))) {}
	}
}
