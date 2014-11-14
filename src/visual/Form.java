package visual;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import map.LandMapPanel;
import map.PlanetParametersList;

import javax.swing.JButton;

import control.InterfacePanel;
import rover.RoverHub;
import wrapper.Access;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Form extends JFrame {
	
	static final int WRAPPER = 0, MAP = 1, ROVER = 2, ORBIT = 3, SATELLITE = 4, INTERFACE = 5;
	
	static public Form frame;
	private static Dimension screenSize;
	private JPanel contentPane;
	
	public Panel WrapperPnl;
	public Panel OrbitalPnl;
	public LandMapPanel TerrainPnl;
	public InterfacePanel InterfacePnl;
	public RoverHub RoverHubPnl;
	public Panel SatelliteHubPnl;
	
	private int currentPage = WRAPPER;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					frame = new Form();
					wrapper.Admin.align();
					frame.setUndecorated(true);
					frame.setSize(screenSize);
				    frame.setVisible(true);
				    frame.setResizable(false);
				    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Form() {
		KeyboardFocusManager masterKeyManager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		masterKeyManager.addKeyEventDispatcher((KeyEventDispatcher) new KeyDispatcher());
		initialize();
	}
	
	private void initialize(){
		setIconImage(Toolkit.getDefaultToolkit().getImage(Form.class.getResource("/GPS.png")));
		setTitle("PHM Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(0, 0);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		WrapperPnl = new Panel(screenSize, "Wrapper HUD");
		WrapperPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				WrapperPnl.requestFocus();
			}
		});
		WrapperPnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		contentPane.add(WrapperPnl);
		
		//TODO
		JButton start = new JButton("Start");
		start.setBounds(100, 100, 300, 50);
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Access.beginSimulation();
			}			 
		});
		WrapperPnl.add(start);
		
		OrbitalPnl = new Panel(screenSize, "Orbital View");
		OrbitalPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				OrbitalPnl.requestFocus();
			}
		});
		OrbitalPnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		OrbitalPnl.setVisible(false);
		contentPane.add(OrbitalPnl);
		
		TerrainPnl = new LandMapPanel(screenSize, new PlanetParametersList());
		TerrainPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				TerrainPnl.requestFocus();
			}
		});
		//TerrainPnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		TerrainPnl.setVisible(false);
		contentPane.add(TerrainPnl);
		
		InterfacePnl = new InterfacePanel(screenSize);
		InterfacePnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfacePnl.requestFocus();
			}
		});
		InterfacePnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		InterfacePnl.setVisible(false);
		contentPane.add(InterfacePnl);
		
		RoverHubPnl = new RoverHub(screenSize);
		RoverHubPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				RoverHubPnl.requestFocus();
			}
		});
		RoverHubPnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		RoverHubPnl.setVisible(false);
		contentPane.add(RoverHubPnl);
		
		SatelliteHubPnl = new Panel(screenSize, "Satellite Hub");
		SatelliteHubPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				SatelliteHubPnl.requestFocus();
			}
		});
		SatelliteHubPnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		SatelliteHubPnl.setVisible(false);
		contentPane.add(SatelliteHubPnl);
	}
	
	public void exit(){
		//TODO exit procedures
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
}

class KeyDispatcher implements KeyEventDispatcher {
	public boolean dispatchKeyEvent(KeyEvent e) {
		Access.handleGlobalKeyEvent(e);
		return false;
	}
}