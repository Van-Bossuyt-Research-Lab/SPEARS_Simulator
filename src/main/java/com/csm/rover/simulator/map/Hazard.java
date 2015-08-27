package com.csm.rover.simulator.map;

import com.csm.rover.simulator.objects.DecimalPoint;

public class Hazard {

	private DecimalPoint position;
	private double radius;
	private int scale;
	
	public Hazard(DecimalPoint pos, double r, int scale){
		position = pos;
		radius = r;
		this.scale = scale;
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
	
	public int getValue(){
		return scale;
	}
	
}