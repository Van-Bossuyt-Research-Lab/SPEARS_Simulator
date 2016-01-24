package com.csm.rover.simulator.visual;

import com.csm.rover.simulator.wrapper.Globals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class Panel extends JPanel{

	private static final long serialVersionUID = -7055141051608083309L;
	
	protected JLabel titleLbl;
	protected JLabel postScript;
	protected ImageIcon background;
	protected boolean hasImage = false;
	
	public Panel(Dimension size, String title){
		super.setName(title);
		super.setSize(size);
		super.setLocation(0, 0);
		super.setLayout(null);
		initialize(title);
	}
	
	private void initialize(String title) {
		titleLbl = new JLabel(title);
		titleLbl.setLocation(20, 10);
		titleLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 25));
		titleLbl.setSize((int) titleLbl.getPreferredSize().getWidth() + 5, (int) titleLbl.getPreferredSize().getHeight() + 5);
		this.add(titleLbl);

        String spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

		postScript = new JLabel("<HTML>CSM PHM Lab" + spacer + "<b>SPEARS</b>" + spacer + "v" + Globals.versionNumber + "</HTML>");
		postScript.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		postScript.setSize((int) postScript.getPreferredSize().getWidth() + 5, (int) postScript.getPreferredSize().getHeight() + 5);
		postScript.setLocation((this.getWidth() - postScript.getWidth()) / 2, this.getHeight() - postScript.getHeight() - 10);
		postScript.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(postScript);

		ImageButton exitBtn = new ImageButton();
        exitBtn.setOpaque(false);
        exitBtn.setBorder(null);
        exitBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        exitBtn.setImage(new ImageIcon(getClass().getResource("/icons/Close.png")));
        exitBtn.setHoverImage(new ImageIcon(getClass().getResource("/icons/Close Hover.png")));
        exitBtn.setToolTipText("");
        exitBtn.setMargin(0);
        exitBtn.setBounds(this.getWidth() - 55, 0, 48, 19);
        exitBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                if (getForm().isPresent()){
					getForm().get().exit();
				}
            }
        });
        add(exitBtn);

        ImageButton changeBtn = new ImageButton();
        changeBtn.setOpaque(false);
        changeBtn.setToolTipText("");
        changeBtn.setBorder(null);
        changeBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        changeBtn.setImage(new ImageIcon(getClass().getResource("/icons/Change_Window.png")));
        changeBtn.setHoverImage(new ImageIcon(getClass().getResource("/icons/Change_Window Hover.png")));
        changeBtn.setMargin(0);
        changeBtn.setBounds(exitBtn.getX() - 27, 0, GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length > 1 ? 27 : 0, 19);
        changeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
				if (getForm().isPresent()) {
					getForm().get().changeWindow();
				}
            }
        });
        add(changeBtn);

		ImageButton minimizeBtn = new ImageButton();
		minimizeBtn.setOpaque(false);
		minimizeBtn.setToolTipText("");
		minimizeBtn.setBorder(null);
		minimizeBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		minimizeBtn.setImage(new ImageIcon(getClass().getResource("/icons/Minimize.png")));
		minimizeBtn.setHoverImage(new ImageIcon(getClass().getResource("/icons/Minimize Hover.png")));
		minimizeBtn.setMargin(0);
		minimizeBtn.setBounds(exitBtn.getX() - changeBtn.getWidth() - 29, 0, 29, 19);
		minimizeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (getForm().isPresent()) {
					getForm().get().minimize();
				}
			}
		});
		add(minimizeBtn);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (hasImage && isOpaque()){
			try {
				for (int i = 0; i*background.getIconWidth() < this.getWidth(); i++){
					for (int j = 0; j*background.getIconHeight() < this.getHeight(); j++){
						g.drawImage(background.getImage(), i*background.getIconWidth(), j*background.getIconHeight(), null);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    @Override
    public String toString(){
        return String.format("GUI Panel of title \"%s\"", titleLbl.getText());
    }

	public void setImage(ImageIcon icon){
		background = icon;
		hasImage = icon != null;
	}
	
	public ImageIcon getImage(){
		return background;
	}
	
	public int getWorkingHeight(){
		return this.postScript.getY() - titleLbl.getHeight() - titleLbl.getY();
	}
	
	public int getTopOfPage(){
		return titleLbl.getHeight() + titleLbl.getY();
	}

    public Rectangle getWorkingBounds(){
        return new Rectangle(0, getTopOfPage(), getWidth(), getWorkingHeight());
    }

	protected static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX()-5, e.getY()-5);
			}
		});
	}

	protected Optional<Form> getForm(){
		Container comp = this;
		while (comp.getParent() != null){
			comp = comp.getParent();
			if (comp instanceof Form){
				return Optional.of((Form)comp);
			}
		}
		return Optional.empty();
	}
}
