package wrapper;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import visual.Panel;
import visual.ZList;
import javax.swing.JTabbedPane;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;

public class MainWrapper extends Panel {

	JTabbedPane tabbedPane;
	JPanel CreateNewPnl;
		ZList DriveModelList;
		ZList AutonomusCodeList;
		ZList RoverList;
		JSlider MapRoughSlider;
		JComboBox<String> MapTypeCombo;
		JButton StartBtn;
	JPanel RuntimePnl;
		
	
	public MainWrapper(Dimension size) {
		super(new Dimension(1920,1080), "Wrapper HUD");
		
		initalize();
		align();
	}
	
	private void initalize(){
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Trebuchet MS", Font.BOLD, 21));
		tabbedPane.setBounds(10, 56, 1900, 979);
		add(tabbedPane);
		
		CreateNewPnl = new JPanel();
		tabbedPane.addTab("New Simulation", null, CreateNewPnl, null);
		CreateNewPnl.setLayout(null);
		
		JLabel lblAddingRovers = new JLabel("Adding Rovers");
		lblAddingRovers.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblAddingRovers.setBounds(10, 11, 118, 21);
		CreateNewPnl.add(lblAddingRovers);
		
		JLabel lblPhysicsModel = new JLabel("Physics Model");
		lblPhysicsModel.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		lblPhysicsModel.setBounds(10, 43, 118, 21);
		CreateNewPnl.add(lblPhysicsModel);
		
		DriveModelList = new ZList();
		DriveModelList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		DriveModelList.setBounds(10, 63, 200, 250);
		CreateNewPnl.add(DriveModelList);
		
		JLabel lblAutonomousLogic = new JLabel("Autonomous Logic");
		lblAutonomousLogic.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		lblAutonomousLogic.setBounds(220, 43, 142, 21);
		CreateNewPnl.add(lblAutonomousLogic);
		
		AutonomusCodeList = new ZList();
		AutonomusCodeList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		AutonomusCodeList.setBounds(220, 63, 200, 250);
		CreateNewPnl.add(AutonomusCodeList);
		
		JLabel lblRovers = new JLabel("Rovers");
		lblRovers.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		lblRovers.setBounds(510, 43, 142, 21);
		CreateNewPnl.add(lblRovers);
		
		RoverList = new ZList();
		RoverList.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		RoverList.setBounds(510, 63, 200, 250);
		CreateNewPnl.add(RoverList);
		
		MapRoughSlider = new JSlider();
		MapRoughSlider.setBounds(1109, 180, 300, 30);
		CreateNewPnl.add(MapRoughSlider);
		
		MapTypeCombo = new JComboBox<String>();
		MapTypeCombo.setBounds(1175, 260, 175, 25);
		CreateNewPnl.add(MapTypeCombo);
		
		StartBtn = new JButton("Start Simulation");
		StartBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
		StartBtn.setBounds(1665, 874, 220, 55);
		CreateNewPnl.add(StartBtn);
		
		RuntimePnl = new JPanel();
		tabbedPane.addTab("Running Simulation", null, RuntimePnl, null);
	}
	
	private void align(){
		
	}
}
