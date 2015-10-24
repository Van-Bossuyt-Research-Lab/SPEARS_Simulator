package com.csm.rover.simulator.control;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Scanner;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;

public class CSVFrame extends JFrame {

	private static final long serialVersionUID = -3993565159460072336L;

	private JTable ValuesTable;
	private PaintablePanel GraphPnl;

	private int buttonClicked = -1;
	
	public CSVFrame() {
		setVisible(false);
		setTitle("Data Preview");
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setButtonClick(0);
			}
		});
		setBounds(100, 100, 784, 325);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 178, 231);
		contentPane.add(scrollPane);
		
		ValuesTable = new JTable();
		ValuesTable.setFont(new Font("Iskoola Pota", Font.PLAIN, 12));
		scrollPane.setViewportView(ValuesTable);
		
		GraphPnl = new PaintablePanel();
		GraphPnl.setBackground(Color.WHITE);
		GraphPnl.setBounds(198, 11, 560, 265);
		contentPane.add(GraphPnl);
		
		JButton SaveBtn = new JButton("Save Data");
		SaveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setButtonClick(1);
			}
		});
		SaveBtn.setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
		SaveBtn.setBounds(10, 253, 178, 23);
		contentPane.add(SaveBtn);
	}
	
	public int OpenCSVFile(String filepath){
		try {
			File CSVin = new File(filepath);
			Scanner input = new Scanner(CSVin);
			String data = "";
			while (input.hasNextLine()){
				String[] inputText = breakAtComma(input.nextLine());
				int x = 0;
				while (x < inputText.length){
					data += inputText[x] + "\n";
					x++;
				}
			}
			
			String title = "", horzUnit = "", vertUnit = "";
			double[] xs = new double[0], ys = new double[0];
			input.close();
			Scanner input2 = new Scanner(data);
			while (input2.hasNextLine()){
				String current = input2.nextLine();
				if (current.equals("Label")){
					break;
				}
				else if (current.equals("Vertical Units")){
					vertUnit = input2.nextLine();
				}
				else if (current.equals("Horizontal Units")){
					horzUnit = input2.nextLine();
				}
				else if (current.equals("Graph Title")){
					title = input2.nextLine();
				}
			}
			String horzTitle = input2.nextLine();
			String vertTitle = input2.nextLine();
			while (input2.hasNextDouble()){
				xs = Augment(xs, input2.nextDouble());
				ys = Augment(ys, input2.nextDouble());
			}
			input2.close();
			try {
				GraphPnl.drawGraw(title, horzTitle, horzUnit, vertTitle, vertUnit, xs, ys);	
			ValuesTable.setModel(new DefaultTableModel(
				new Object[xs.length][2],
				new String[] {
					(horzTitle + " (" + horzUnit + ")"), (vertTitle + " (" + vertUnit + ")")
				}) {
				private static final long serialVersionUID = 1L;				
			});	
			int x = 0;
			while (x < xs.length){
				ValuesTable.setValueAt(xs[x], x, 0);
				ValuesTable.setValueAt(ys[x], x, 1);
				x++;
			}		
			}
			catch (Exception e){
				new PopUp().showConfirmDialog("Error in data file, invalid number.", "File Could Not be Opened", PopUp.DEFAULT_OPTIONS);
			}			
			this.setVisible(true);
		} 
		catch (Exception e){
			e.printStackTrace();
		}

		while (buttonClicked == -1) {
			try{
				Thread.sleep(300);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
		this.setVisible(false);
		return buttonClicked;
	}
	
	private String[] breakAtComma(String in){
		String[] out = new String[0];
		char[] working = in.toCharArray();
		int x = 0;
		String hold = "";
		while (x < working.length){
			if (working[x] == ','){
				out = Augment(out, hold);
				hold = "";
			}
			else if (working[x] != '\n') {
				hold += working[x];
			}
			x++;
		}
		if (!hold.equals("") && !hold.equals("\n")){
			out = Augment(out, hold);
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
	
	private double[] Augment(double[] array, double val){
		double[] out = new double[array.length + 1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private void setButtonClick(int which){
		buttonClicked = which;
	}
}
