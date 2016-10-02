package com.csm.rover.simulator.objects.util;

public class DecimalPoint3D {

	private double x, y, z;

	public DecimalPoint3D(){
		x = 0;
		y = 0;
		z = 0;
	}

	public DecimalPoint3D(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public DecimalPoint3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}

	public double getZ(){
		return z;
	}
	
	public int roundX(){
		return (int)Math.round(x);
	}
	
	public int roundY(){
		return (int)Math.round(y);
	}

	public int roundZ(){
		return (int)Math.round(z);
	}

	public void offsetThis(double x, double y, double z){
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public DecimalPoint3D offset(double x, double y, double z){
		return new DecimalPoint3D(this.x+x, this.y+y, this.z+z);
	}
	
	public DecimalPoint3D offset(DecimalPoint3D pnt){
		return new DecimalPoint3D(this.x+pnt.x, this.y+pnt.y, this.z+pnt.z);
	}
	
	public DecimalPoint3D scale(double factor){
		return new DecimalPoint3D(x*factor, y*factor, z*factor);
	}
	
	@Override
	public String toString(){
		return (Math.round(x*1000)/1000.0) + ", " + (Math.round(y*1000)/1000.0 + ", " + (Math.round(z*1000)/1000.0));
	}
	
	@Override
	public DecimalPoint3D clone(){
		return new DecimalPoint3D(x, y, z);
	}
	
}
