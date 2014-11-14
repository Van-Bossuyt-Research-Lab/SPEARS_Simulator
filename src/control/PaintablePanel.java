package control;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class PaintablePanel extends JPanel{

	private String title = "", horzTitle = "", horzUnit = "", vertTitle = "", vertUnit = "";
	private double[] xs = new double[0], ys = new double[0];
	boolean graphSet = false;
	
	@Override
	protected void paintComponent(Graphics g) throws ArrayIndexOutOfBoundsException{
		super.paintComponent(g);
		if (graphSet){
			Graphics2D g2 = (Graphics2D) g;
			g2.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
			FontMetrics fontMech = g2.getFontMetrics();
			Rectangle2D textSize = fontMech.getStringBounds(title, g2);
			g2.drawString(title, (int)(this.getWidth() - textSize.getWidth()) / 2, (int)(10 + textSize.getHeight()));
			textSize = fontMech.getStringBounds("(" + vertUnit + ")", g2);
			g2.drawString(vertTitle, 5, (int)((this.getHeight() / 2) - 3));
			g2.drawString("(" + vertUnit + ")", (int)(5 + (fontMech.stringWidth(vertTitle) - textSize.getWidth()) / 2), (int)((this.getHeight() / 2) + 3 + textSize.getHeight()));
			textSize = fontMech.getStringBounds((horzTitle + " (" + horzUnit + ")"), g2);
			g2.drawString((horzTitle + " (" + horzUnit + ")"), (int)(((this.getWidth() - fontMech.stringWidth(vertTitle) - 15) - textSize.getWidth()) / 2 + fontMech.stringWidth(vertTitle) + 15), (int)(this.getHeight() - 5));
			textSize = fontMech.getStringBounds(vertTitle, g2);
			g2.setColor(new Color(50, 50, 50));
			Rectangle graphSpace = new Rectangle((int)(textSize.getWidth() + 40) + 4, (int)(15 + textSize.getHeight()), (int)(this.getWidth() - 20 - textSize.getWidth() - 40), ((int)(this.getHeight() - 15 - textSize.getHeight()) - (int)(35 + textSize.getHeight()) - 4));
			g2.fillRect((int)graphSpace.getX() - 4, (int)graphSpace.getY(), 4, (int)graphSpace.getHeight());
			g2.fillRect((int)graphSpace.getX() - 4, (int)(graphSpace.getMaxY()), (int)graphSpace.getWidth() + 4, 4);
			Rectangle dataSpace = getDataBounds(xs, ys);
			g2.setColor(Color.BLUE);
			g2.fillOval((int)(((xs[0] - dataSpace.getX()) * graphSpace.getWidth() / dataSpace.getWidth()) + graphSpace.getX() - 4), (int)((graphSpace.getHeight() - ((ys[0] - dataSpace.getY()) * graphSpace.getHeight() / dataSpace.getHeight()) - 4) + graphSpace.getY()), 8, 8);
			int x = 1;
			while (x < xs.length){
				g2.fillOval((int)(((xs[x] - dataSpace.getX()) * graphSpace.getWidth() / dataSpace.getWidth()) + graphSpace.getX() - 4), (int)((graphSpace.getHeight() - ((ys[x] - dataSpace.getY()) * graphSpace.getHeight() / dataSpace.getHeight())) + graphSpace.getY() - 4), 8, 8);
				g2.drawLine((int)(((xs[x-1] - dataSpace.getX()) * graphSpace.getWidth() / dataSpace.getWidth()) + graphSpace.getX()), (int)((graphSpace.getHeight() - ((ys[x-1] - dataSpace.getY()) * graphSpace.getHeight() / dataSpace.getHeight())) + graphSpace.getY()), (int)(((xs[x] - dataSpace.getX()) * graphSpace.getWidth() / dataSpace.getWidth()) + graphSpace.getX()), (int)((graphSpace.getHeight() - ((ys[x] - dataSpace.getY()) * graphSpace.getHeight() / dataSpace.getHeight())) + graphSpace.getY()));
				x++;
			}
			g2.setColor(Color.BLACK);
			x = 0;
			int end = 5;
			while (x < end){
				int tick = (int)(x/(double)end*dataSpace.getHeight() + dataSpace.getY());
				g2.drawString(tick + "-", (int)(graphSpace.getX() - fontMech.stringWidth(tick + "-") - 3), (int)((end-x)/(double)end*graphSpace.getHeight()+graphSpace.getY()));
				tick = (int)(x/(double)end*dataSpace.getWidth() + dataSpace.getX());
				g2.drawString("|" + tick, (int)((x)/(double)end*graphSpace.getWidth()+graphSpace.getX()), (int)(graphSpace.getMaxY() + 2 + textSize.getHeight()));
				x++;
			}
		}
	}
	
	public void drawGraw(String title, String horzTitle, String horzUnit, String vertTitle, String vertUnit, double[] xs, double[] ys){
		this.title = title;
		this.horzTitle = horzTitle;
		this.horzUnit = horzUnit;
		this.vertTitle = vertTitle;
		this.vertUnit = vertUnit;
		this.xs  = xs;
		this.ys = ys;
		this.graphSet = true;
		this.repaint();
	}
	
	private Rectangle getDataBounds(double[] xs, double[] ys){
		double xmin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE;
		double ymin = Double.MAX_VALUE;
		double ymax = Double.MIN_VALUE;
		int x = 0;
		while (x < xs.length){
			if (xs[x] < xmin){
				xmin = xs[x];
			}
			if (xs[x] > xmax){
				xmax = xs[x];
			}
			if (ys[x] < ymin){
				ymin = ys[x];
			}
			if (ys[x] > ymax){
				ymax = ys[x];
			}
			x++;
		}
		return new Rectangle((int)xmin - 2, (int)ymin - 2, (int)(xmax - xmin) + 4, (int)(ymax - ymin) + 4);		
	}
	
}
