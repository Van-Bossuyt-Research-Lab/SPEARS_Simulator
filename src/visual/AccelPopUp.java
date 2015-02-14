package visual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.JProgressBar;

import wrapper.Access;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AccelPopUp extends JDialog {

	private JPanel contentPanel = new JPanel();
	private JProgressBar ProgressBar;
	private JLabel TimeLeftLbl;
	private long timeStarted;

	public AccelPopUp(int goal, int estimate) {
		initalize();
		TimeLeftLbl.setText("Estimated Time Remaining: " + estimate + " min");
		ProgressBar.setMaximum(goal);
		ProgressBar.setValue(0);
		timeStarted = System.currentTimeMillis();
		this.repaint();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)((screenSize.getWidth() - this.getSize().getWidth()) / 2), (int)((screenSize.getHeight() - this.getSize().getHeight()) / 2));
		this.setVisible(true);
		this.setAlwaysOnTop(true);
	}
	
	private void initalize(){
		setResizable(false);
		setTitle("Accelerating Simulation");
		setBounds(100, 100, 388, 191);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel TextLbl = new JLabel("<HTML><center>The visual interface has been hidden while the<br>simulator has been accelorated.  This window<br>will close when the simulation is complete.</center><HTML>");
		TextLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		TextLbl.setHorizontalAlignment(SwingConstants.CENTER);
		TextLbl.setBounds(0, 0, 382, 77);
		contentPanel.add(TextLbl);
		
		ProgressBar = new JProgressBar();
		ProgressBar.setBounds(10, 74, 362, 21);
		contentPanel.add(ProgressBar);
		
		TimeLeftLbl = new JLabel("Estimated Time Remaining: 18 min");
		TimeLeftLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
		TimeLeftLbl.setBounds(10, 105, 276, 17);
		contentPanel.add(TimeLeftLbl);
		
		JButton btnHide = new JButton("Hide");
		btnHide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		btnHide.setBounds(98, 133, 89, 23);
		contentPanel.add(btnHide);
		
		JButton btnAbort = new JButton("Abort");
		btnAbort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Access.CODE.GUI.exit();
			}
		});
		btnAbort.setBounds(197, 133, 89, 23);
		contentPanel.add(btnAbort);
	}
	
	public void update(int progress){
		ProgressBar.setValue(progress);
		long elapsed = System.currentTimeMillis() - timeStarted;
		int remaining = (int) (elapsed * (ProgressBar.getMaximum() / progress - 1));
		TimeLeftLbl.setText("Estimated Time Remaining: " + (remaining/60000) + " min");
	}
}
