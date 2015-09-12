package com.csm.rover.simulator.visual;

import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.csm.rover.simulator.map.LandMapPanel;
import com.csm.rover.simulator.map.PlanetParametersList;
import com.csm.rover.simulator.objects.RunConfiguration;
import com.csm.rover.simulator.control.InterfacePanel;
import com.csm.rover.simulator.control.PopUp;
import com.csm.rover.simulator.rover.RoverHub;
import com.csm.rover.simulator.satellite.SatelliteHub;
import com.csm.rover.simulator.wrapper.Access;
import com.csm.rover.simulator.wrapper.Globals;
import com.csm.rover.simulator.wrapper.MainWrapper;

import java.io.File;

//TODO make into a signularity thingy
public class Form extends JFrame {
	
	private static final long serialVersionUID = 5065827458217177853L;

	static final int WRAPPER = 0, MAP = 1, ROVER = 2, ORBIT = 3, SATELLITE = 4, INTERFACE = 5;
	
	static public Form frame;
	private static Dimension screenSize;
	private int currentScreen = 0;

	public MainWrapper WrapperPnl;
	public Panel OrbitalPnl;
	public LandMapPanel TerrainPnl;
	public InterfacePanel InterfacePnl;
	public RoverHub RoverHubPnl;
	public SatelliteHub SatelliteHubPnl;
	
	private int currentPage = WRAPPER;

	public static void main(String[] args) {
		boolean go = false;
		File config = new File("default.cfg");
		if (config.exists()){
			if ((new PopUp()).showConfirmDialog("A quick run configuration file has been found.  Would you like to run the simulator from the file?", "Quick Run", PopUp.YES_NO_OPTIONS) == PopUp.YES_OPTION){
				go = true;
			}
		}
		final boolean goFin = go;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					frame = new Form();
					com.csm.rover.simulator.wrapper.Admin.align();
					frame.setUndecorated(true);
					frame.setSize(screenSize);
				    frame.setVisible(true);
				    frame.setResizable(false);
				    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    //frame.showOnScreen(frame.currentScreen);
				    if (goFin){
				    	Access.CODE.beginSimulation(new RunConfiguration(new File("default.cfg")));
				    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Form() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				Access.CODE.wakeUp();
			}
		});
		KeyboardFocusManager masterKeyManager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		masterKeyManager.addKeyEventDispatcher(new KeyDispatcher());
		initialize();
	}
	
	private void initialize(){
		UIManager.put("TabbedPane.contentOpaque", false);
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/GPS.png")));
		setTitle("PHM Simulator");
		setLocation(0, 0);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		WrapperPnl = new MainWrapper(screenSize);
		WrapperPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				WrapperPnl.requestFocus();
			}
		});
		WrapperPnl.setImage(new ImageIcon(getClass().getResource("/images/Light Background.jpg")));
		contentPane.add(WrapperPnl);
		
		OrbitalPnl = new Panel(screenSize, "Orbital View");
		OrbitalPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				OrbitalPnl.requestFocus();
			}
		});
		OrbitalPnl.setImage(new ImageIcon(getClass().getResource("/images/Light Background.jpg")));
		OrbitalPnl.setVisible(false);
		contentPane.add(OrbitalPnl);
		
		TerrainPnl = new LandMapPanel(screenSize, new PlanetParametersList());
		TerrainPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				TerrainPnl.requestFocus();
			}
		});
		TerrainPnl.setBackground(Color.BLACK);
		TerrainPnl.setVisible(false);
		contentPane.add(TerrainPnl);
		
		InterfacePnl = new InterfacePanel(screenSize);
		InterfacePnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfacePnl.requestFocus();
			}
		});
		InterfacePnl.setImage(new ImageIcon(getClass().getResource("/images/Background.png")));
		InterfacePnl.setVisible(false);
		contentPane.add(InterfacePnl);
		
		RoverHubPnl = new RoverHub(screenSize);
		RoverHubPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				RoverHubPnl.requestFocus();
			}
		});
		RoverHubPnl.setImage(new ImageIcon(getClass().getResource("/images/Light Background.jpg")));
		RoverHubPnl.setVisible(false);
		contentPane.add(RoverHubPnl);
		
		SatelliteHubPnl = new SatelliteHub(screenSize);
		SatelliteHubPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				SatelliteHubPnl.requestFocus();
			}
		});
		SatelliteHubPnl.setImage(new ImageIcon(getClass().getResource("/images/Light Background.jpg")));
		SatelliteHubPnl.setVisible(false);
		contentPane.add(SatelliteHubPnl);
	}
	
	public void exit(){
		//exit procedures
		System.exit(0);
	}
	
	public void minimize(){
		setState(Frame.ICONIFIED);
	}
	
	public void MasterKeyHandler(KeyEvent arg0){
		if (arg0.isControlDown()){
			switch (currentPage){
			case WRAPPER:
				if (arg0.isControlDown()){
					switch (arg0.getKeyCode()){
					case KeyEvent.VK_LEFT:
						WrapperPnl.setVisible(false);
						TerrainPnl.setVisible(true);
						TerrainPnl.requestFocus();
						currentPage = MAP;
						break;
					case KeyEvent.VK_RIGHT:
						WrapperPnl.setVisible(false);
						OrbitalPnl.setVisible(true);
						OrbitalPnl.requestFocus();
						currentPage = ORBIT;
						break;
					case KeyEvent.VK_UP:
						WrapperPnl.setVisible(false);
						InterfacePnl.setVisible(true);
						InterfacePnl.requestFocus();
						currentPage = INTERFACE;
						break;
					}
				}
				break;
			case MAP:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_RIGHT:
					TerrainPnl.setVisible(false);
					RoverHubPnl.setInHUDMode(false);
					RoverHubPnl.setVisible(false);
					WrapperPnl.setVisible(true);
					WrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_DOWN:
					TerrainPnl.setVisible(false);
					RoverHubPnl.setInHUDMode(false);
					RoverHubPnl.setVisible(true);
					RoverHubPnl.requestFocus();
					currentPage = ROVER;
					break;
				case KeyEvent.VK_UP:
					TerrainPnl.setVisible(false);
					RoverHubPnl.setInHUDMode(false);
					RoverHubPnl.setVisible(false);
					InterfacePnl.setVisible(true);
					InterfacePnl.requestFocus();
					currentPage = INTERFACE;
					break;
				}
				break;
			case ROVER:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_RIGHT:
					RoverHubPnl.setVisible(false);
					WrapperPnl.setVisible(true);
					WrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_UP:
					RoverHubPnl.setVisible(false);
					TerrainPnl.setVisible(true);
					TerrainPnl.requestFocus();
					currentPage = MAP;
					break;
				}
				break;
			case ORBIT:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_LEFT:
					OrbitalPnl.setVisible(false);
					WrapperPnl.setVisible(true);
					WrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_DOWN:
					OrbitalPnl.setVisible(false);
					SatelliteHubPnl.setVisible(true);
					SatelliteHubPnl.requestFocus();
					currentPage = SATELLITE;
					break;
				case KeyEvent.VK_UP:
					OrbitalPnl.setVisible(false);
					InterfacePnl.setVisible(true);
					InterfacePnl.requestFocus();
					currentPage = INTERFACE;
					break;
				}
				break;
			case SATELLITE:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_LEFT:
					SatelliteHubPnl.setVisible(false);
					WrapperPnl.setVisible(true);
					WrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_UP:
					SatelliteHubPnl.setVisible(false);
					OrbitalPnl.setVisible(true);
					OrbitalPnl.requestFocus();
					currentPage = ORBIT;
					break;
				}
				break;
			case INTERFACE:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_DOWN:
					InterfacePnl.setVisible(false);
					WrapperPnl.setVisible(true);
					WrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_LEFT:
					InterfacePnl.setVisible(false);
					TerrainPnl.setVisible(true);
					TerrainPnl.requestFocus();
					currentPage = MAP;
					break;
				case KeyEvent.VK_RIGHT:
					InterfacePnl.setVisible(false);
					OrbitalPnl.setVisible(true);
					OrbitalPnl.requestFocus();
					currentPage = ORBIT;
					break;
				}
				break;
			}
		}
	}

	public void changeWindow(){
		currentScreen = (currentScreen + 1) % GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
        showOnScreen(currentScreen);
	}

	private void showOnScreen(int screen){
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		if( screen > -1 && screen < gs.length ){
			gs[screen].setFullScreenWindow( this );
		}
		else if( gs.length > 0 ){
			gs[0].setFullScreenWindow( this );
		}
		else{
            Globals.reportError("Form", "No Screens Found", new RuntimeException("No Screens Found"));
		}
	}


}

class KeyDispatcher implements KeyEventDispatcher {
	public boolean dispatchKeyEvent(KeyEvent e) {
		Access.handleGlobalKeyEvent(e);
		return false;
	}
}