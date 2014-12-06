package control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextField;

import java.awt.Color;

import javax.swing.border.EtchedBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import visual.ZList;
import wrapper.Access;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InstrucitonEditor extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField TitleTxt;
	private ZList ParameterTitles;
	private DefaultTableModel CommandsTableModel;
	private JTable CommandsTable;
	private JCheckBox AddRoverChk;
	private JCheckBox AddSatelliteChk;
	JCheckBox InputChk;
	
	private int selected = -1;
	private String[][] tableStrings = new String[0][0];
	private boolean[] parameterBools = new boolean[0];

	public InstrucitonEditor() {
		initalize();
	}
	
	public InstrucitonEditor(boolean sat, boolean rover, String title, String[] titles, String[][] commands, boolean[] textreqs) {
		initalize();
		AddRoverChk.setSelected(sat);
		AddSatelliteChk.setSelected(rover);
		AddRoverChk.setEnabled(false);
		AddSatelliteChk.setEnabled(false);
		TitleTxt.setText(title);
		ParameterTitles.setValues(titles);
		tableStrings = commands;
		parameterBools = textreqs;
		CommandsTable.setEnabled(true);
		ParameterTitles.setSelection(0);
		CommandsTableModel.setValueAt(commands[0][0], 0, 0);
	}
	
	private void initalize(){
		setVisible(false);
		setTitle("Instruction Editor");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 530, 354);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblInstructionTitle = new JLabel("Instruction Title:");
		lblInstructionTitle.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		lblInstructionTitle.setBounds(10, 11, 94, 14);
		contentPanel.add(lblInstructionTitle);
		
		TitleTxt = new JTextField();
		TitleTxt.setFont(new Font("Iskoola Pota", Font.PLAIN, 15));
		TitleTxt.setBounds(114, 6, 140, 23);
		contentPanel.add(TitleTxt);
		TitleTxt.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			JLabel lblAddTo = new JLabel("Add To:");
			lblAddTo.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
			buttonPane.add(lblAddTo);
			
			AddRoverChk = new JCheckBox("Rover");
			AddRoverChk.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
			buttonPane.add(AddRoverChk);
			
			AddSatelliteChk = new JCheckBox("Satellite");
			AddSatelliteChk.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
			buttonPane.add(AddSatelliteChk);
			
			JLabel label = new JLabel("");
			label.setPreferredSize(new Dimension(150, 10));
			buttonPane.add(label);
			{
				JButton okButton = new JButton("Create");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						OK_Clicked();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Cancel_Clicked();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		ParameterTitles = new ZList();
		ParameterTitles.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				ListSelection_Changed();
			}
		});
		ParameterTitles.setSize(140, 186);
		ParameterTitles.setBackground(Color.WHITE);
		ParameterTitles.setLocation(10, 67);
		contentPanel.add(ParameterTitles);
		
		JLabel lblParameterTitles = new JLabel("Parameter Titles:");
		lblParameterTitles.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		lblParameterTitles.setBounds(10, 48, 100, 14);
		contentPanel.add(lblParameterTitles);
		
		JButton btnAddParameter = new JButton("Add Parameter");
		btnAddParameter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddParameter_Clicked();
			}
		});
		btnAddParameter.setBounds(10, 259, 140, 23);
		contentPanel.add(btnAddParameter);
		
		JLabel lblCommands = new JLabel("Commands:");
		lblCommands.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		lblCommands.setBounds(181, 49, 94, 14);
		contentPanel.add(lblCommands);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(181, 67, 333, 186);
		contentPanel.add(scrollPane);
		
		CommandsTableModel = new DefaultTableModel(
			new Object[][] { {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
				{""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""} },
				new String[] { "Command" } ) {
			Class[] columnTypes = new Class[] {
				String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		
		CommandsTable = new JTable();
		CommandsTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10){
					saveData();
				}
			}
		});
		CommandsTable.setEnabled(false);
		CommandsTable.setRowHeight(20);
		CommandsTable.setModel(CommandsTableModel);
		CommandsTable.getColumnModel().getColumn(0).setResizable(false);
		CommandsTable.setFont(new Font("Iskoola Pota", Font.PLAIN, 15));
		scrollPane.setViewportView(CommandsTable);
		
		InputChk = new JCheckBox("Requires Text Input - Use  ^&^  as place holder.");
		InputChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveData();
			}
		});
		InputChk.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		InputChk.setBounds(181, 259, 333, 25);
		contentPanel.add(InputChk);
	}
	
	private void OK_Clicked(){
		if (!TitleTxt.getText().equals("") && (AddRoverChk.isSelected() || AddSatelliteChk.isSelected()) && commandsFilled()){
			InstructionObj[] instructions = new InstructionObj[ParameterTitles.getItems().length];
			int x = 0;
			while (x < instructions.length){
				instructions[x] = new InstructionObj(cropStringArray(tableStrings[x]), (String)ParameterTitles.getValueAt(x), parameterBools[x]);
				x++;
			}
			Access.InstructionEditorFinish(AddRoverChk.isSelected(), AddSatelliteChk.isSelected(), TitleTxt.getText(), instructions);
			this.setVisible(false);
		}
		else {
			new objects.ThreadTimer( 0, new Runnable(){
				public void run(){
					(new PopUp()).showConfirmDialog("You are missing required information.", "Submit Failed", PopUp.DEFAULT_OPTIONS);
				}
			}, 1, "missing information");			
		}
	}
	
	private void Cancel_Clicked(){
		this.setVisible(false);
	}
	
	private void AddParameter_Clicked(){
		new objects.ThreadTimer(0, new Runnable(){
			public void run(){
				String name = (new PopUp()).showInpuDialog("Parameter Name:", "Add Parameter", PopUp.OK_CANCEL_OPTIONS);
				if (!name.equals("")){
					tableStrings = Augment(tableStrings);
					parameterBools = Augment(parameterBools);
					ParameterTitles.addValue(name);
					ParameterTitles.setSelection(ParameterTitles.getItems().length - 1);
					CommandsTable.setEnabled(true);
				}
			}
		}, 1, "add paramter");
	}
	
	private void ListSelection_Changed(){
		if (selected != -1){
			saveData();
			setTableData(tableStrings[ParameterTitles.getSelectedIndex()]);
			InputChk.setSelected(parameterBools[ParameterTitles.getSelectedIndex()]);
		}
		selected = ParameterTitles.getSelectedIndex();
	}
	
	public void open(){
		this.repaint();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)((screenSize.getWidth() - this.getSize().getWidth()) / 2), (int)((screenSize.getHeight() - this.getSize().getHeight()) / 2));
		this.setVisible(true);
	}

	private String[] getFirstColumn(){
		String[] out = new String[CommandsTable.getRowCount()];
		int x = 0;
		while (x < out.length){
			out[x] = (String)CommandsTable.getValueAt(x, 0);
			x++;
		}
		return out;
	}
	
	private void saveData(){
		tableStrings[selected] = getFirstColumn();
		parameterBools[selected] = InputChk.isSelected();
	}
	
	private void setTableData(String[] data1){
		int x = 0;
		while (x < data1.length){
			CommandsTableModel.setValueAt(data1[x], x, 0);
			CommandsTable.setModel(CommandsTableModel);
			x++;
		}
	}
	
	private boolean commandsFilled(){
		if (ParameterTitles.getItems().length == 0){
			return false;
		}
		int x = 0;
		while (x < tableStrings.length){
			if (tableStrings[x][0].equals("")){
				return false;
			}
			x++;
		}
		return true;
	}

	private String[][] Augment(String[][] array){
		String[][] out = new String[array.length + 1][];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = new String[CommandsTable.getRowCount()];
		int y = 0;
		while (y < out[x].length){
			out[x][y] = "";
			y++;
		}
		return out;
	}
	
	private String[] Augment(String[] array, String val){
		String[] out = new String[array.length+1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private boolean[] Augment(boolean[] array){
		boolean[] out = new boolean[array.length+1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = false;
		return out;
	}
	
	private String[] cropStringArray(String[] array){
		String[] out = new String[0];
		int x = 0;
		while (x < array.length){
			if (!array[x].equals("")){
				out = Augment(out, array[x]);
			}
			x++;
		}
		return out;
	}
}
