package visual;

import java.awt.Cursor;

import javax.swing.ImageIcon;

public class LEDIndicator extends ImageDisplay{

	private boolean on = false;
	private String color = "/LED_Red.png";
	public static final int GREEN = 0, RED = 1, LIGHT_BLUE = 2, LIGHT_GREEN = 3, ORANGE = 4, PURPLE = 5, BLUE = 6, YELLOW = 7;
	
	public LEDIndicator(){
		super();
		this.setBorder(null);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		this.setVerticalAlignment(CENTER);
		this.setHorizontalAlignment(CENTER);
		this.setOpaque(false);
		setMargin(5);
		setImage(new ImageIcon(LEDIndicator.class.getResource("/LED_Off.png")));
	}
	
	public void setSelected(boolean b){
		if (on != b){
			if (b){
				setImage(new ImageIcon(LEDIndicator.class.getResource(color)));
			}
			else{
				setImage(new ImageIcon(LEDIndicator.class.getResource("/LED_Off.png")));
			}
		}
		on = b;
	}
	
	public void toggle(){
		setSelected(!on);
	}
	
	public boolean isSeleted(){
		return on;
	}
	
	public void setLEDColor(int color){
		switch (color){
		case GREEN:
			this.color = "/LED_Green.png";
			break;
		case RED:
			this.color = "/LED_Red.png";
			break;
		case LIGHT_BLUE:
			this.color = "/LED_Light_Blue.png";
			break;
		case LIGHT_GREEN:
			this.color = "/LED_Light_Green.png";
			break;
		case ORANGE:
			this.color = "/LED_Orange.png";
			break;
		case PURPLE:
			this.color = "/LED_Purple.png";
			break;
		case BLUE:
			this.color = "/LED_Blue.png";
			break;
		case YELLOW:
			this.color = "/LED_Yellow.png";
			break;
		default:
			this.color = "/LED_Red.png";
			break;
		}
		setSelected(on);
	}
	
}
