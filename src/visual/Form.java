package visual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import map.LandMapPanel;
import map.PlanetParametersList;

import javax.swing.JButton;

import rover.RoverHub;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Form extends JFrame {

	static public Form frame;
	private static Dimension screenSize;
	private JPanel contentPane;
	
	public Panel WrapperPnl;
	public Panel OrbitalPnl;
	public LandMapPanel TerrainPnl;
	public Panel InterfacePnl;
	public RoverHub RoverHubPnl;
	public Panel SatelliteHubPnl;

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
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				e.getKeyCode();
			}
		});
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
		WrapperPnl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.isControlDown()){
					if (arg0.getKeyCode() == KeyEvent.VK_LEFT){
						WrapperPnl.setVisible(false);
						TerrainPnl.setVisible(true);
						TerrainPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT){
						WrapperPnl.setVisible(false);
						OrbitalPnl.setVisible(true);
						OrbitalPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_UP){
						WrapperPnl.setVisible(false);
						InterfacePnl.setVisible(true);
						InterfacePnl.requestFocus();
					}
				}
			}
		});
		WrapperPnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		contentPane.add(WrapperPnl);
		
		OrbitalPnl = new Panel(screenSize, "Orbital View");
		OrbitalPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				OrbitalPnl.requestFocus();
			}
		});
		OrbitalPnl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.isControlDown()){
					if (arg0.getKeyCode() == KeyEvent.VK_LEFT){
						OrbitalPnl.setVisible(false);
						WrapperPnl.setVisible(true);
						WrapperPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_DOWN){
						OrbitalPnl.setVisible(false);
						SatelliteHubPnl.setVisible(true);
						SatelliteHubPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_UP){
						OrbitalPnl.setVisible(false);
						InterfacePnl.setVisible(true);
						InterfacePnl.requestFocus();
					}
				}
			}
		});
		OrbitalPnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		OrbitalPnl.setVisible(false);
		contentPane.add(OrbitalPnl);
		
		TerrainPnl = new LandMapPanel(screenSize, "Terrain View", new PlanetParametersList());
		TerrainPnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				TerrainPnl.requestFocus();
			}
		});
		TerrainPnl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.isControlDown()){
					if (arg0.getKeyCode() == KeyEvent.VK_RIGHT){
						TerrainPnl.setVisible(false);
						WrapperPnl.setVisible(true);
						WrapperPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_DOWN){
						TerrainPnl.setVisible(false);
						RoverHubPnl.setVisible(true);
						RoverHubPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_UP){
						TerrainPnl.setVisible(false);
						InterfacePnl.setVisible(true);
						InterfacePnl.requestFocus();
					}
				}
			}
		});
		//TerrainPnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		TerrainPnl.setVisible(false);
		contentPane.add(TerrainPnl);
		
		InterfacePnl = new Panel(screenSize, "Control Interface");
		InterfacePnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfacePnl.requestFocus();
			}
		});
		InterfacePnl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.isControlDown()){
					if (arg0.getKeyCode() == KeyEvent.VK_DOWN){
						InterfacePnl.setVisible(false);
						WrapperPnl.setVisible(true);
						WrapperPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_LEFT){
						InterfacePnl.setVisible(false);
						TerrainPnl.setVisible(true);
						TerrainPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT){
						InterfacePnl.setVisible(false);
						OrbitalPnl.setVisible(true);
						OrbitalPnl.requestFocus();
					}
				}
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
		RoverHubPnl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.isControlDown()){
					if (arg0.getKeyCode() == KeyEvent.VK_RIGHT){
						RoverHubPnl.setVisible(false);
						WrapperPnl.setVisible(true);
						WrapperPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_UP){
						RoverHubPnl.setVisible(false);
						TerrainPnl.setVisible(true);
						TerrainPnl.requestFocus();
					}
				}
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
		SatelliteHubPnl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.isControlDown()){
					if (arg0.getKeyCode() == KeyEvent.VK_LEFT){
						SatelliteHubPnl.setVisible(false);
						WrapperPnl.setVisible(true);
						WrapperPnl.requestFocus();
					}
					else if (arg0.getKeyCode() == KeyEvent.VK_UP){
						SatelliteHubPnl.setVisible(false);
						OrbitalPnl.setVisible(true);
						OrbitalPnl.requestFocus();
					}
				}
			}
		});
		SatelliteHubPnl.setImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("C:\\Users\\PHM-Lab2\\Eclipse Workspace\\Simulator 2 Protype\\lib\\Texture Backgroun.jpg")));
		SatelliteHubPnl.setVisible(false);
		contentPane.add(SatelliteHubPnl);
	}
	
	public void exit(){
		// exit procedures
		System.exit(0);
	}
	
	public void minimize(){
		setState(Frame.ICONIFIED);
	}
}
