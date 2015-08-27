package map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import objects.DecimalPoint;
import objects.GridList;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class PlasmaPanel extends JPanel {

	private float[][] values;
	private GridList<Integer> targets;
	private int[][] hazards;
	private boolean viewTargets = true;
	private boolean viewHazards = true;
	private boolean monoTargets = true;
	private boolean monoHazards = true;
	private int layerSize;
	private double rough;
	private float minval;
	private float maxval;
	private float maxHeight = 4.5f;
	private Random rnd = new Random();
	private int squareResolution = 50;
	private int detail = 3;
	
	private String fileID = "%&$#";
	
	private int currentColorScheme = 0;
	public static final int REDtoGREEN = 0, BLACKtoWHITE = 1, BLUEtoWHITE = 2;
	
	private double ColorModifier;
	
	public PlasmaPanel(){
		this.setBounds(0, 0, 100, 100);
		this.setBackground(Color.BLACK);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				try {
					setSize(values.length*squareResolution/detail, values[0].length*squareResolution/detail);
				}
				catch (Exception e){
					e.printStackTrace();
					setSize(100, 100);
				}
			}
		});
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				double shift = values.length / (double)detail * squareResolution / 2.0;
				double x = (e.getX()-shift) / squareResolution;
				double y = (shift-e.getY()) / squareResolution;
				System.out.println(x + ", " + y);
			}
		});
	}
	
	//Generates the height map using a plasma fractal
	public void genorateLandscape(int size, int det, double roughFactor){
		double rough = size * roughFactor;
		this.layerSize = size;
		this.rough = rough;
		this.detail = det;
		double seed = System.currentTimeMillis() / 10000.0;
		while (seed > 30){
			seed = seed / 10.0;
		}
		float[][] values = new float[2][2];
		values[0][0] = (float) Math.abs(seed + random());
		values[0][1] = (float) Math.abs(seed + random());
		values[1][0] = (float) Math.abs(seed + random());
		values[1][1] = (float) Math.abs(seed + random());		
		int master = 0;
		while (master <= size){
			values = expand(values);
			int x = 0;
			while (x < values.length){
				int y = 0;
				while (y < values.length){
					if ((x+1) % 2 == 0){
						if ((y+1) % 2 == 0){
							values[x][y] = center(values[x-1][y-1], values[x-1][y+1], values[x+1][y-1], values[x+1][y+1], rough);
						}
						else {
							values[x][y] = midpoint(values[x-1][y], values[x+1][y], rough);
						}
					}
					else {
						if ((y+1) % 2 == 0){
							values[x][y] = midpoint(values[x][y-1], values[x][y+1], rough);
						}
					}
					y++;
				}
				x++;
			}
			rough -= roughFactor;
			if (rough < 0){
				rough = 0;
			}
			master++;
		}
		float[][] values2 = new float[values.length-4-((values.length-4)%(detail*2))][values.length-4-((values.length-4)%(detail*2))];
		int count = 9;
		int x = 0;
		while (x < values.length){
			int y = 0;
			while (y < values.length){
				if (x >= 2 && y >= 2 && x-2 < values2.length && y-2 < values2.length) {
					if (count % 4 == 0){
						values2[x-2][y-2] = (values[x][y] + (values[x-1][y-2] + values[x+1][y+2]) / 2) / 2;
					}
					else if (count % 4 == 1){
						values2[x-2][y-2] = (values[x][y] + (values[x-2][y-1] + values[x+2][y+1]) / 2) / 2;
					}
					else if (count % 4 == 2){
						values2[x-2][y-2] = (values[x][y] + (values[x-2][y+1] + values[x+2][y-1]) / 2) / 2;
					}
					else {
						values2[x-2][y-2] = (values[x][y] + (values[x-1][y+2] + values[x+1][y-2]) / 2) / 2;
					}
				}
				count++;
				if (count == Integer.MAX_VALUE){
					count = 0;
				}
				y++;
			}
			x++;
		}
		this.values = values2;
		minval = getMin();
		maxval = maxHeight;
		x = 0;
		while (x < this.values.length){
			int y = 0;
			while (y < this.values[0].length){
				this.values[x][y] -= minval;
				y++;
			}
			x++;
		}
		minval = 0;
		setColorMultipliers();
		this.setSize(values.length*squareResolution/detail, values[0].length*squareResolution/detail);
	}
	
	//force a height map into the display
	public void setValues(float[][] vals){
		values = vals;
		minval = getMin();
		maxval = maxHeight;
		int x = 0;
		while (x < values.length){
			int y = 0;
			while (y < values[0].length){
				values[x][y] -= minval;
				y++;
			}
			x++;
		}
		minval = 0;
		setColorMultipliers();
		this.setSize(values.length*squareResolution/detail, values[0].length*squareResolution/detail);
		this.repaint();
	}
	
	public float[][] getValues(){
		return values;
	}
	
	//paint
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try { 
			int xstart = (-this.getLocation().x / squareResolution - 1) * detail;
			if (xstart < 0){
				xstart = 0;
			}
			int xend = xstart + (this.getParent().getWidth() / squareResolution + 3) * detail;
			if (xend > values.length){
				xend = values.length;
			}
			int ystart = (-this.getLocation().y / squareResolution - 1) * detail;
			if (ystart < 0){
				ystart = 0;
			}
			int yend = ystart + (this.getParent().getHeight() / squareResolution + 4) * detail;
			if (yend > values[0].length){
				yend = values[0].length;
			}
			if (values.length > 0){
				//this.setSize(values.length*squareResolution/detail, values[0].length*squareResolution/detail);
				int x = xstart;
				while (x < xend){
					int y = ystart;
					while (y < yend){
						try {
							try {
								if (viewTargets){
									if (targets.get(x/detail, y/detail) > 0){
										int value = targets.get(x/detail, y/detail);
										g.setColor(new Color(Color.MAGENTA.getRed()*(value+5)/15, Color.MAGENTA.getGreen()*(value+5)/15, Color.MAGENTA.getBlue()*(value+5)/15));	
									}
									else {
										throw new NullPointerException();
									}
								}
								else {
									throw new NullPointerException();
								}
							}
							catch (NullPointerException e){
								if (viewHazards){
									if (hazards[x/detail][y/detail] > 5){
										int value = (11-hazards[x/detail][y/detail])*20+100;
										g.setColor(new Color(value, value, value));												
									}
									else {
										throw new Exception();
									}
								}
								else {
									throw new Exception();
								}
							}
						}
						catch (Exception e){
							g.setColor(getColor(values[x/detail][y/detail]));
						}
						g.fillRect(x * squareResolution / detail, y * squareResolution / detail, squareResolution, squareResolution);
						switch (currentColorScheme){
						case REDtoGREEN:
						case BLUEtoWHITE:
							g.setColor(Color.DARK_GRAY);
							break;
						case BLACKtoWHITE:
							g.setColor(new Color(240, 250, 0));
							break;
						}
						g.drawRect(x * squareResolution / detail, y * squareResolution / detail, squareResolution, squareResolution);
						y += detail;
					}
					x += detail;
				}
			}
			/*if (trailVis){
				int i = 0;
				while (i < trailPoints.length){
					g.setColor(new Color(255-i*255/trailPoints.length, 255-i*255/trailPoints.length, 255-i*255/trailPoints.length));
					g.fillRect((int)(trailPoints[i].getX()*squareResolution+this.getWidth()/2)-3, (int)(this.getHeight()/2-trailPoints[i].getY()*squareResolution)-3, 6, 6);
					i++;
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//force a target distribution
	public void setTargets(Point[] targs){
		targets = new GridList<Integer>();
		monoTargets = true;
		for (Point p : targs){
			targets.put(10, p.x, p.y);
		}
		this.repaint();
	}
	
	public void setTargets(Point[] targs, int[] values){
		monoTargets = false;
		targets = new GridList<Integer>();
		for (int i = 0; i < targs.length; i++){
			targets.put(values[i], targs[i].x, targs[i].y);
		}
		this.repaint();
	}
	
	//get the target distribution
	public GridList<Integer> getTargets(){
		return targets;
	}
	
	//Generate a target distribution
	public void genorateTargets(double density){
		targets = new GridList<Integer>();
		monoTargets = true;
		int size = (int)(values.length*values[0].length/(detail*detail)*density);
		int x = 0;
		while (x < size){
			targets.put(10, rnd.nextInt(values.length/detail), rnd.nextInt(values.length/detail));
			x++;
		}
	}
	
	public void genorateValuedTargets(double density){
		targets = new GridList<Integer>();
		monoTargets = false;
		int size = (int)(values.length*values[0].length/(detail*detail)*density);
		int x = 0;
		while (x < size){
			int value;
			double rand = rnd.nextDouble();
			if (rand < 0.3){
				value = 1;
			}
			else if (rand < 0.45){
				value = 2;
			}
			else if (rand < 0.6){
				value = 3;
			}
			else if (rand < 0.7){
				value = 4;
			}
			else if (rand < 0.8){
				value = 5;
			}
			else if (rand < 0.86){
				value = 6;
			}
			else if (rand < 0.91){
				value = 7;
			}
			else if (rand < 0.95){
				value = 8;
			}
			else if (rand < 0.98){
				value = 9;
			}
			else {
				value = 10;
			}
			targets.put(value, rnd.nextInt(values.length/detail), rnd.nextInt(values.length/detail));
			x++;
		}
	}
	
	//is the given point a target
	public boolean isPointOnTarget(DecimalPoint loc){
		int x = (int) getMapSquare(loc).getX() / detail;
		int y = (int) getMapSquare(loc).getY() / detail;
		try {
			return targets.get(x, y) > 0;
		}
		catch (NullPointerException e){
			return false;
		}
		catch (ArrayIndexOutOfBoundsException e){
			return false;
		}
	}
	
	public int getTargetValue(DecimalPoint loc){
		int x = (int) getMapSquare(loc).getX() / detail;
		int y = (int) getMapSquare(loc).getY() / detail;
		try {
			return targets.get(x, y).intValue();
		}
		catch (NullPointerException e){
			return 0;
		}
		catch (ArrayIndexOutOfBoundsException e){
			return 0;
		}
	}
	
	public void setTargetsVisible(boolean b){
		viewTargets = b;
		repaint();
	}
	
	public boolean areTargetsVisible(){
		return viewTargets;
	}
	
	//Generate random hazards
	public void genorateHazards(double density){
		this.hazards = new int[this.values.length/detail][this.values[0].length/detail];
		monoHazards = true;
		Hazard[] hazards = new Hazard[(int)(values.length*values[0].length/(detail*detail)*density)];
		for (int x = 0; x < hazards.length; x++){
			hazards[x] = new Hazard(new DecimalPoint(values.length/detail*rnd.nextDouble()-values.length/detail/2, values.length/detail*rnd.nextDouble()-values.length/detail/2), 5*rnd.nextDouble()+1, 10);
		}
		for (int i = 0; i < values.length/detail; i++){
			for (int j = 0; j < values[0].length/detail; j++){
				int x = i - values.length/detail/2;
				int y = values.length/detail/2 - j;
				for (int z = 0; z < hazards.length; z++){
					if (hazards[z].isPointWithin(new DecimalPoint(x, y))){
						this.hazards[i][j] = hazards[z].getValue();
						break;
					}
				}
			}
		}
	}
	
	public void genorateValuedHazards(double density){
		monoHazards = false;
		PlasmaPanel hazMap = new PlasmaPanel();
		hazMap.genorateLandscape(layerSize, detail, 2.6);
		this.hazards = new int[this.values.length/detail][this.values[0].length/detail];
		double max = hazMap.getMax()+0.001;
		for (int i = 0; i < hazards.length; i++){
			for (int j = 0; j < hazards[0].length; j++){
				hazards[i][j] = (int)(hazMap.values[i][j]/max*5);
			}
		}
		Hazard[] hazards = new Hazard[(int)(values.length*values[0].length/(detail*detail)*density*2.5)];
		for (int x = 0; x < hazards.length; x++){
			hazards[x] = new Hazard(new DecimalPoint(values.length/detail*rnd.nextDouble()-values.length/detail/2, values.length/detail*rnd.nextDouble()-values.length/detail/2), 5*rnd.nextDouble()+1, rnd.nextInt(5)+1);
		}
		for (int i = 0; i < values.length/detail; i++){
			for (int j = 0; j < values[0].length/detail; j++){
				int x = i - values.length/detail/2;
				int y = values.length/detail/2 - j;
				for (int z = 0; z < hazards.length; z++){
					if (hazards[z].isPointWithin(new DecimalPoint(x, y))){
						this.hazards[i][j] += hazards[z].getValue();
						break;
					}
				}
			}
		}
	}
	
	public void setHazards(Point[] hzds){
		hazards = new int[this.values.length/detail][this.values[0].length/detail];
		monoHazards = true;
		for (Point p : hzds){
			hazards[p.x][p.y] = 10;
		}
		this.repaint();
	}
	
	public void setHazards(int[][] values){
		hazards = values;
		monoHazards = false;
	}
	
	//get the hazard list
	public int[][] getHazards(){
		return hazards;
	}
	
	//is the given point in a hazard
	public boolean isPointInHazard(DecimalPoint loc){
		int x = (int) getMapSquare(loc).getX() / detail;
		int y = (int) getMapSquare(loc).getY() / detail;
		try {
			return hazards[x][y] > 0;
		}
		catch (NullPointerException e){
			return false;
		}
		catch (ArrayIndexOutOfBoundsException e){
			return true;
		}
	}
	
	public int getHazardValue(DecimalPoint loc){
		int x = (int) getMapSquare(loc).getX() / detail;
		int y = (int) getMapSquare(loc).getY() / detail;
		try {
			return hazards[x][y];
		}
		catch (ArrayIndexOutOfBoundsException e){
			return 10;
		}
	}
	
	public void setHazardsVisible(boolean b){
		viewHazards = b;
		repaint();
	}
	
	public boolean areHazardsVisible(){
		return viewHazards;
	}
	
	//part of the plasma fractal generation, pushes the array from |x|x|x| to |x|_|x|_|x|
	private float[][] expand(float[][] values2){
		float[][] out = new float[values2.length * 2 - 1][values2.length * 2 - 1];
		int x = 0;
		while (x < values2.length){
			int y = 0;
			while (y < values2.length){
				out[x*2][y*2] = values2[x][y];
				y++;
			}
			x++;
		}
		return out;
	}
	
	private float center(float a, float b, float c, float d, double rough){
		return (float) ((a+b+c+d)/4 + (rough*random()));
	}
	
	private float midpoint(float a, float b, double rough){
		return (float) ((a+b)/2 + (rough*random()));
	}
	
	private float random(){
		int rough = (int)(this.rough * 10.0);
		while (rough < 1){
			rough *= 10;
		}
		float out = (float) (rnd.nextInt(rough) + rnd.nextDouble());
		if (rnd.nextBoolean()){
			out *= -1;
		}
		return out;
	}
	
	private double getMax(){
		double max = 0;
		int x = 0;
		while (x < values.length){
			int y = 0;
			while (y < values.length){
				if (values[x][y] > max){
					max = values[x][y];
				}
				y++;
			}
			x++;
		}
		return max;
	}
	
	private float getMin(){
		float min = Float.MAX_VALUE;
		int x = 0;
		while (x < values.length){
			int y = 0;
			while (y < values.length){
				if (values[x][y] < min){
					min = values[x][y];
				}
				y++;
			}
			x++;
		}
		return min;
	}
	
	private void setColorMultipliers(){
		double x = ((maxval-minval)/2.0+minval);
		ColorModifier = 255 / (x*x - 2*minval*x + minval*minval);
	}
	
	private Color getColor(double numb) {
		switch (currentColorScheme){
		case REDtoGREEN:
			double scaled = numb / maxHeight * 100;
			int red = 0, green = 0, blue = 0;
			if (scaled < 25){
				red = (int) ((scaled)/25*255);
			}
			else if (scaled < 50){
				red = 255;
				green = (int) ((scaled-25)/25*255);
			}
			else if (scaled < 75){
				red =  (int) ((25-(scaled-50))/25*255);
				green = 255;
			}
			else if (scaled < 100){
				red = (int) ((scaled-75)/25*255);
				green = 255;
				blue = red;
			}
			else {
				red = 255;
				green = 255;
				blue = 255;
			}
			if (numb > maxHeight){
				//System.out.println(Math.round(numb*100)/100.0 + ": " + red + ", " + green + ", " + blue);
			}
			try{
				return new Color(red, green, blue);
			}
			catch (Exception e){
				return Color.CYAN;
			}
		case BLACKtoWHITE:
			int x = (int) Math.round((numb - minval) / maxval * 255);
			return new Color(x, x, x);
		case BLUEtoWHITE:
			int y = (int) Math.round((numb - minval) / maxval * 255);
			return new Color(y, y, 255);
		default:
			return null;
		}
	}
	
	public void setColorScheme(int which){
		currentColorScheme = which;
		this.repaint();
	}
	
	public void setResolution(int res){
		if (res > 0){
			squareResolution = res;
			this.setSize(values.length*squareResolution/detail, values[0].length*squareResolution/detail);
			this.repaint();
		}
	}
	
	public int getResolution(){
		return squareResolution;
	}
	
	public int getDetail(){
		return detail;
	}
	
	public void SaveImage(float[][] fs, int detail, int scheme, String filepath){
		BufferedImage image = new BufferedImage(15*fs.length/detail, 15*fs.length/detail, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();   
		double maxval = Double.MIN_VALUE;
		double minval = Double.MAX_VALUE;
		int x = 0;
		while (x < fs.length){
			int y = 0;
			while (y < fs.length){
				if (fs[x][y] > maxval){
					maxval = fs[x][y];
				}
				if (fs[x][y] < minval){
					minval = fs[x][y];
				}
				y++;
			}
			x++;
		}
		x = 0;
		while (x < fs.length){
			int y = 0;
			while (y < fs.length){
				try {
					switch (scheme){
					case REDtoGREEN:
						int red = (int)(255 - (ColorModifier*4/3.0)*Math.pow((fs[x+detail/2][y+detail/2]-((maxval-minval)/2.0+minval)), 2));
						if (red < 0){
							red = 0;
						}
						int green = (int)(255 - (ColorModifier*3/4.0)*Math.pow((fs[x+detail/2][y+detail/2]-maxval-0.5), 2));
						if (green < 0){
							green = 0;
						}
						int blue = (int) ((green - 240) / 2 + (fs[x+detail/2][y+detail/2] - maxHeight) * 2);
						if (blue < 0){
							blue = 0;
						}
						if (fs[x+detail/2][y+detail/2] > maxHeight){
							//System.out.println(Math.round(numb*100)/100.0 + ": " + red + ", " + green + ", " + blue);
						}
						int scaled = (int) (fs[x+detail/2][y+detail/2] * 100 / maxHeight);
						g.setColor(new Color(2.0f * scaled, 2.0f * (1 - scaled), 0));
						break;
					case BLACKtoWHITE:
						int i = (int) Math.round((fs[x+detail/2][y+detail/2] - minval) / (maxval-minval) * 255);
						g.setColor(new Color(i, i, i));
						break;
					case BLUEtoWHITE:
						int j = (int) Math.round((fs[x+detail/2][y+detail/2] - minval) / (maxval-minval) * 255);
						g.setColor(new Color(255-j, 255-j, 255));
						break;
					}
				} catch (Exception i){
					g.setColor(getColor(fs[x][y]));
				}
				g.fillRect(x * 15 / detail, y * 15 / detail, 15, 15);
				y += detail;
			}
			x += detail;
		}
		//g.drawString("Hello World!!!", 10, 20);
		try {    
			File output = new File(filepath + ".PNG");
			output.createNewFile();
			ImageIO.write(image, "png", output);
			//System.out.println("Save Done");
		} 
		catch (IOException e) {
			e.printStackTrace();   
		}
	}
	
	public Point getMapSquare(DecimalPoint loc){ // says which display square a given coordinate falls in
		int shift = values.length / (detail * 2);
		double x = loc.getX() + shift;
		double y = shift - loc.getY();
		int outx = (int)(x*detail);
		int outy = (int)(y*detail);
		return new Point(outx, outy);
	}
	
	public double getValueAtLocation(int x, int y){
		return values[x][y];
	}
	
	public void saveMap(File file){
		try {
			BufferedWriter write = new BufferedWriter(new FileWriter(file, true));
			
			write.write(fileID + "\n");
			
			write.write(values[0].length + "\n" + values.length + "\n" + detail + "\n\n");
			for (int i = 0; i < values.length; i++){
				for (int j = 0; j < values[i].length; j++){
					write.write(values[i][j] + "\t");
				}
				write.write('\n');
			}
			
			if (monoTargets){
				write.write("m");
			}
			else {
				write.write("v");
				
			}		
			write.write("\n" + targets.size() + "\n");
			write.write(targets.genorateList() + "\n");
			if (!monoTargets){
				for (Integer val : targets.getValues()){
					write.write(val + "\n");
				}
			}
			
			if (monoHazards){
				write.write("m");
				ArrayList<String> valueLocs = new ArrayList<String>();
				for (int i = 0; i < hazards.length; i++){
					for (int j = 0; j < hazards[0].length; j++){
						try {
							if (hazards[i][j] > 0){
								valueLocs.add(i + "\t" + j);
							}
						} catch (NullPointerException e) {}
					}
				}
				write.write("\n" + valueLocs.size() + "\n");
				for (String str : valueLocs){
					write.write(str + "\n");
				}
				write.write("\n");
			}
			else {
				write.write("v");
				write.write("\n" + hazards[0].length + "\n" + hazards.length + "\n");
				for (int i = 0; i < hazards.length; i++){
					for (int j = 0; j < hazards[0].length; j++){
						try {
							if (hazards[i][j] > 0){
								write.write(hazards[i][j] + "\t");
							}
							else {
								write.write(0 + "\t");
							}
						}
						catch (NullPointerException e) {
							write.write(0 + "\t");
						}
					}
					write.write("\n");
				}
			}
			
			write.flush();
			write.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadMap(File file) throws Exception {
		try {
			Scanner data = new Scanner(file);
			
			String ver = data.next();
			if (!ver.equals(fileID)){
				data.close();
				throw new Exception("Invalid File Version");
			}
			
			int width = data.nextInt();
			int height = data.nextInt();
			int detail = data.nextInt();
			setDetail(detail);
			
			float[][] values = new float[height][width];
			for (int i = 0; i < height; i++){
				float[] row = new float[width];
				for (int j = 0; j < width; j++){
					row[j] = data.nextFloat();
				}
				values[i] = row;
			}
			this.setValues(values);
			
			boolean mono = data.next().equals("m");
			int targs = data.nextInt();
			if (targs > 0){
				Point[] targets = new Point[targs];
				for (int i = 0; i < targets.length; i++){
					int x = data.nextInt();
					int y = data.nextInt();
					targets[i] = new Point(x, y);
				}
				if (mono){
					this.setTargets(targets);
				}
				else {
					int[] targVals = new int[targs];
					for (int i = 0; i < targVals.length; i++){
						targVals[i] = data.nextInt();
					}				
					this.setTargets(targets, targVals);
				}
			}
			
			if (data.next().equals("m")){
				int hzdrs = data.nextInt();
				if (hzdrs > 0){
					Point[] hazards = new Point[hzdrs];
					for (int i = 0; i < hazards.length; i++){
						int x = data.nextInt();
						int y = data.nextInt();
						hazards[i] = new Point(x, y);
					}
					this.setHazards(hazards);
				}
			}
			else {
				width = data.nextInt();
				height = data.nextInt();
				int[][] hazVals = new int[width][height];
				for (int i = 0; i < height; i++){
					int[] row = new int[width];
					for (int j = 0; j < width; j++){
						row[j] = data.nextInt();
					}
					hazVals[i] = row;
				}
				this.setHazards(hazVals);
			};
			
			data.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setDetail(int detail) {
		this.detail = detail;
	}
}
