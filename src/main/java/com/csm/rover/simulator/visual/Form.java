package com.csm.rover.simulator.visual;

import com.csm.rover.simulator.control.InterfacePanel;
import com.csm.rover.simulator.map.display.LandMapPanel;
import com.csm.rover.simulator.objects.util.FreeThread;
import com.csm.rover.simulator.platforms.rover.RoverHub;
import com.csm.rover.simulator.platforms.satellite.SatelliteHub;
import com.csm.rover.simulator.wrapper.MainWrapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Form extends JFrame {
	private static final Logger LOG = LogManager.getFormatterLogger(Form.class);
	
	private static final long serialVersionUID = 5065827458217177853L;

	static final int STARTUP = -1, WRAPPER = 0, MAP = 1, ROVER = 2, ORBIT = 3, SATELLITE = 4, INTERFACE = 5;

	private int currentScreen = 0;
    private boolean splash = false;

    private JPanel contentPane;
    private StartupPanel startupPanel;
	private MainWrapper wrapperPnl;
	private Panel orbitalPnl;
	private LandMapPanel terrainPnl;
	private InterfacePanel interfacePnl;
	private RoverHub roverHubPnl;
	private SatelliteHub satelliteHubPnl;
	
	private int currentPage = STARTUP;

	public Form(Dimension screenSize, StartupPanel startupPanel) {
        setUndecorated(true);
        showSplash();
		setSize(screenSize);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.startupPanel = startupPanel;
		KeyboardFocusManager masterKeyManager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		masterKeyManager.addKeyEventDispatcher(new KeyDispatcher(this));
		initialize();
        showForm();
	}

    public void setRunTimePanels(MainWrapper WrapperPnl,
                                 Panel OrbitalPnl,
                                 LandMapPanel TerrainPnl,
                                 InterfacePanel InterfacePnl,
                                 RoverHub RoverHubPnl,
                                 SatelliteHub SatelliteHubPnl){
        this.wrapperPnl = WrapperPnl;
        this.orbitalPnl = OrbitalPnl;
        this.terrainPnl = TerrainPnl;
        this.interfacePnl = InterfacePnl;
        this.roverHubPnl = RoverHubPnl;
        this.satelliteHubPnl = SatelliteHubPnl;

        this.startupPanel.setVisible(false);
        this.remove(startupPanel);

        wrapperPnl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                wrapperPnl.requestFocus();
            }
        });
        wrapperPnl.setImage(new ImageIcon(getClass().getResource("/images/Light Background.jpg")));
        contentPane.add(wrapperPnl);

        orbitalPnl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                orbitalPnl.requestFocus();
            }
        });
        orbitalPnl.setImage(new ImageIcon(getClass().getResource("/images/Light Background.jpg")));
        orbitalPnl.setVisible(false);
        contentPane.add(orbitalPnl);

        roverHubPnl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                roverHubPnl.requestFocus();
            }
        });
        roverHubPnl.setImage(new ImageIcon(getClass().getResource("/images/Light Background.jpg")));
        roverHubPnl.setVisible(false);
        contentPane.add(roverHubPnl);

        terrainPnl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                terrainPnl.requestFocus();
            }
        });
        terrainPnl.setBackground(Color.BLACK);
        terrainPnl.setVisible(false);
        contentPane.add(terrainPnl);

        interfacePnl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                interfacePnl.requestFocus();
            }
        });
        interfacePnl.setImage(new ImageIcon(getClass().getResource("/images/Background.png")));
        interfacePnl.setVisible(false);
        contentPane.add(interfacePnl);

        satelliteHubPnl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                satelliteHubPnl.requestFocus();
            }
        });
        satelliteHubPnl.setImage(new ImageIcon(getClass().getResource("/images/Light Background.jpg")));
        satelliteHubPnl.setVisible(false);
        contentPane.add(satelliteHubPnl);

        currentPage = WRAPPER;
    }

	private void initialize(){
		UIManager.put("TabbedPane.contentOpaque", false);
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/SPEARS icon.png")));
		setTitle("PHM Simulator");
		setLocation(0, 0);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
        setContentPane(contentPane);

        startupPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                startupPanel.requestFocus();
            }
        });
        startupPanel.setImage(new ImageIcon(getClass().getResource("/images/Light Background.jpg")));
        startupPanel.setVisible(true);
        startupPanel.setLocation(0, 0);
		startupPanel.repaint();
	}

    private void showSplash(){
        splash = true;
        setOpacity(0.5f);
    }

    private void showForm(){
        new FreeThread(2000, new Runnable() {
            @Override
            public void run() {
                splash = false;
                contentPane.add(startupPanel);
				setOpacity(1);
                repaint();
				startupPanel.RuntimeSpnr.validate();
            }
        }, 1, "splash");
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        if (splash){
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/spears logo.png"));
            g.drawImage(icon.getImage(), (getWidth()-icon.getIconWidth())/2, (getHeight()-icon.getIconHeight())/2, null);
        }
    }

	public void exit(){
		//exit procedures
		LOG.log(Level.INFO, "Exiting SPEARS");
		System.exit(0);
	}
	
	public void minimize(){
		setState(Frame.ICONIFIED);
	}
	
	public void MasterKeyHandler(KeyEvent arg0){
        //TODO Optimize and Clean up
		if (arg0.isControlDown()){
			switch (currentPage){
			case WRAPPER:
                switch (arg0.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    wrapperPnl.setVisible(false);
                    terrainPnl.setVisible(true);
                    terrainPnl.requestFocus();
                    currentPage = MAP;
                    break;
                case KeyEvent.VK_RIGHT:
                    wrapperPnl.setVisible(false);
                    orbitalPnl.setVisible(true);
                    orbitalPnl.requestFocus();
                    currentPage = ORBIT;
                    break;
                case KeyEvent.VK_UP:
                    wrapperPnl.setVisible(false);
                    interfacePnl.setVisible(true);
                    interfacePnl.requestFocus();
                    currentPage = INTERFACE;
                    break;
                }
				break;
			case MAP:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_RIGHT:
					terrainPnl.setVisible(false);
					roverHubPnl.setInHUDMode(false);
					roverHubPnl.setVisible(false);
					wrapperPnl.setVisible(true);
					wrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_DOWN:
					terrainPnl.setVisible(false);
					roverHubPnl.setInHUDMode(false);
					roverHubPnl.setVisible(true);
					roverHubPnl.requestFocus();
					currentPage = ROVER;
					break;
				case KeyEvent.VK_UP:
					terrainPnl.setVisible(false);
					roverHubPnl.setInHUDMode(false);
					roverHubPnl.setVisible(false);
					interfacePnl.setVisible(true);
					interfacePnl.requestFocus();
					currentPage = INTERFACE;
					break;
				}
				break;
			case ROVER:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_RIGHT:
					roverHubPnl.setVisible(false);
					wrapperPnl.setVisible(true);
					wrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_UP:
					roverHubPnl.setVisible(false);
					terrainPnl.setVisible(true);
					terrainPnl.requestFocus();
					currentPage = MAP;
					break;
				}
				break;
			case ORBIT:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_LEFT:
					orbitalPnl.setVisible(false);
					wrapperPnl.setVisible(true);
					wrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_DOWN:
					orbitalPnl.setVisible(false);
					satelliteHubPnl.setVisible(true);
					satelliteHubPnl.requestFocus();
					currentPage = SATELLITE;
					break;
				case KeyEvent.VK_UP:
					orbitalPnl.setVisible(false);
					interfacePnl.setVisible(true);
					interfacePnl.requestFocus();
					currentPage = INTERFACE;
					break;
				}
				break;
			case SATELLITE:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_LEFT:
					satelliteHubPnl.setVisible(false);
					wrapperPnl.setVisible(true);
					wrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_UP:
					satelliteHubPnl.setVisible(false);
					orbitalPnl.setVisible(true);
					orbitalPnl.requestFocus();
					currentPage = ORBIT;
					break;
				}
				break;
			case INTERFACE:
				switch (arg0.getKeyCode()){
				case KeyEvent.VK_DOWN:
					interfacePnl.setVisible(false);
					wrapperPnl.setVisible(true);
					wrapperPnl.requestFocus();
					currentPage = WRAPPER;
					break;
				case KeyEvent.VK_LEFT:
					interfacePnl.setVisible(false);
					terrainPnl.setVisible(true);
					terrainPnl.requestFocus();
					currentPage = MAP;
					break;
				case KeyEvent.VK_RIGHT:
					interfacePnl.setVisible(false);
					orbitalPnl.setVisible(true);
					orbitalPnl.requestFocus();
					currentPage = ORBIT;
					break;
				}
				break;
			}
		}
        else {
            startupPanel.setBounds(0, 0, getWidth(), getHeight());
            startupPanel.setVisible(true);
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
			LOG.log(Level.ERROR, "showOnScreen reports no screens", new RuntimeException("No Screens Found"));
		}
	}

    public void focusOnTerrain() {
        terrainPnl.requestFocus();
    }
}

class KeyDispatcher implements KeyEventDispatcher {
	private long lastEvent = 0;
	private int lastCode = 0;

    private Form GUI;

    public KeyDispatcher(Form gui){
        GUI = gui;
    }

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (System.currentTimeMillis()-lastEvent > 300 || lastCode != e.getKeyCode()){
			lastEvent = System.currentTimeMillis();
			lastCode = e.getKeyCode();
			GUI.MasterKeyHandler(e);
		}
		else {
			lastEvent = System.currentTimeMillis();
			lastCode = e.getKeyCode();
		}
		return false;
	}
}