package visual;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class RoverIcon extends JPanel{
	
	private JLabel nameTitle;
	private JLabel iconImage;
	
	private double Angle;

	public RoverIcon(String name, Point location, double direction){
		super.setOpaque(false);
		super.setName(name);
		
		nameTitle = new JLabel(name);
		nameTitle.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		nameTitle.setHorizontalAlignment(SwingConstants.CENTER);
		if (nameTitle.getPreferredSize().getWidth() > 60){
			nameTitle.setSize((int)nameTitle.getPreferredSize().getWidth(), (int)nameTitle.getPreferredSize().getHeight()+5);
		}
		else {
			nameTitle.setSize(60, (int)nameTitle.getPreferredSize().getHeight()+5);
		}
		nameTitle.setLocation(0, 0);
		this.add(nameTitle);
		
		iconImage = new JLabel();
		iconImage.setSize(60, 60);
		iconImage.setLocation((nameTitle.getWidth()-iconImage.getWidth())/2, nameTitle.getHeight());
		updateAngle(direction);
		this.add(iconImage);
		
		this.setSize(nameTitle.getWidth(), (nameTitle.getHeight()+iconImage.getHeight()));
		setLocation((int)location.getX(), (int)location.getY());
		
		System.out.println(name  + ": " + location.toString());
	}
	
	public void updatePlacement(Point loc, double dir){
		setLocation((int)loc.getX(), (int)loc.getY());
		updateAngle(dir);
	}
	
	private void updateAngle(double angle){
		while (angle < 0){
			angle += 2*Math.PI;
		}
		Angle = Math.toDegrees(angle) % 360;
		try {
			ImageIcon img = new ImageIcon(RoverIcon.class.getResource("/Rover Marker " + (int)(Angle - Angle%5) + ".png"));
			img = resize(img, this.getWidth(), this.getHeight());
			iconImage.setIcon(img);
		}
		catch (Exception e){
			iconImage.setIcon(null);
		}
	}
	
	@Override
	public void setLocation(int x, int y){
		super.setBounds((x - this.getWidth()/2), (y - this.getHeight()/2-nameTitle.getHeight()), this.getWidth(), this.getHeight());
	}
	
	@Override
	public Point getLocation(){
		return new Point((super.getX() + this.getWidth()/2), (super.getY() + this.getHeight()/2+nameTitle.getHeight()));
	}
	
	@Override
	public int getX(){
		return (int)getLocation().getX();
	}
	
	@Override
	public int getY(){
		return (int)getLocation().getY();
	}
	
	private ImageIcon resize(Icon image, int width, int height) throws Exception {
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
}
