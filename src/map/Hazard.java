package map;

import objects.DecimalPoint;

public class Hazard {

	private DecimalPoint position;
	private double radius;
	
	public Hazard(DecimalPoint pos, double r){
		position = pos;
		radius = r;
	}
	
	public boolean isPointWithin(DecimalPoint pnt){
		return Math.sqrt(Math.pow(position.getX()-pnt.getX(), 2) + Math.pow(position.getY()-pnt.getY(), 2)) <= radius;
	}

	@Override
	public String toString() {
		return "Hazard [position=" + position.toString() + ", radius=" + radius + "]";
	}

	public DecimalPoint getPosition() {
		return position;
	}

	public double getRadius() {
		return radius;
	}
	
}
