package visual;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import wrapper.Globals;

public class Panel extends JPanel{

	protected JLabel titleLbl;
	protected JLabel postScript;
	private ImageButton exitBtn;
	private ImageButton minimizeBtn;
	protected ImageIcon background;
	protected boolean hasImage = false;
	
	public Panel(Dimension size, String title){
		super.setName(title);
		super.setSize(size);
		super.setLocation(0, 0);
		super.setLayout(null);
		
		titleLbl = new JLabel(title);
		titleLbl.setLocation(20, 10);
		titleLbl.setFont(new Font("Trebuchet MS", Font.BOLD, 25));
		titleLbl.setSize((int)titleLbl.getPreferredSize().getWidth()+5, (int)titleLbl.getPreferredSize().getHeight()+5);
		this.add(titleLbl);
		
		postScript = new JLabel("Colorado School of Mines     Prognostics and Health Management Project     System Simulator v" + Globals.vrsionNumber);
		postScript.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		postScript.setSize((int)postScript.getPreferredSize().getWidth()+5, (int)postScript.getPreferredSize().getHeight()+5);
		postScript.setLocation((this.getWidth()-postScript.getWidth())/2, this.getHeight()-postScript.getHeight()-10);
		postScript.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(postScript);
		
		exitBtn = new ImageButton();
		exitBtn.setOpaque(false);
		exitBtn.setBorder(null);
		exitBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		exitBtn.setImage(new ImageIcon(Form.class.getResource("/Close.png")));
		exitBtn.setHoverImage(new ImageIcon(Form.class.getResource("/Close Hover.png")));
		exitBtn.setToolTipText("");
		exitBtn.setMargin(0);
		exitBtn.setBounds(this.getWidth()-55, 0, 48, 19);
		exitBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Form.frame.exit();
			}
		});
		add(exitBtn);
		
		minimizeBtn = new ImageButton();
		minimizeBtn.setOpaque(false);
		minimizeBtn.setToolTipText("");
		minimizeBtn.setBorder(null);
		minimizeBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		minimizeBtn.setImage(new ImageIcon(Form.class.getResource("/Minimize.png")));
		minimizeBtn.setHoverImage(new ImageIcon(Form.class.getResource("/Minimize Hover.png")));
		minimizeBtn.setMargin(0);
		minimizeBtn.setBounds(exitBtn.getX()-29, 0, 29, 19);
		minimizeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Form.frame.minimize();
			}
		});
		add(minimizeBtn);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (hasImage){
			try {
				g.drawImage(resize(background, this.getWidth(), this.getHeight()).getImage(), 0, 0, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected ImageIcon resize(Icon image, int width, int height) throws Exception {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g = bi.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
        g.setComposite(comp);
        g.drawImage(((ImageIcon) (image)).getImage(), 0, 0, width, height, null);
	    g.dispose();
	    return new ImageIcon(bi);
	}
	
	public void setImage(ImageIcon icon){
		background = icon;
		hasImage = !icon.equals(null);
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
}
