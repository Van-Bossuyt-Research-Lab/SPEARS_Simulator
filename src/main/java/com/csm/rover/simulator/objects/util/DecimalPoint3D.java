/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csm.rover.simulator.objects.util;

import com.csm.rover.simulator.objects.CoverageIgnore;

/**
 * Standard point class for tracking the x, y, and z coordinates.  Supports non-integer values, unlike {@link java.awt.Point java.awt.Point}.
 */
public class DecimalPoint3D implements Cloneable {

	private double x, y, z;

	/**
	 * Default constructor initializes to (0, 0, 0).
	 */
	public DecimalPoint3D(){
		x = 0;
		y = 0;
		z = 0;
	}

	/**
	 * Creates a new point at (x, y, z).
	 *
	 * @param x The x coordinate value
	 * @param y The y coordinate value
     * @param z The z coordinate value
     */
	public DecimalPoint3D(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a new point at (x, y, z).
	 *
	 * @param x The x coordinate value
	 * @param y The y coordinate value
	 * @param z The z coordinate value
	 */
	public DecimalPoint3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Returns the x coordinate.
	 *
	 * @return Position along the x-axis
	 */
	public double getX(){
		return x;
	}

	/**
	 * Returns the y coordinate.
	 *
	 * @return Position along the y-axis
	 */
	public double getY(){
		return y;
	}

	/**
	 * Returns the z coordinate.
	 *
	 * @return Position along the z-axis
	 */
	public double getZ(){
		return z;
	}

	/**
	 * Rounds the x coordinate to the nearest integer value
	 *
	 * @return Integer position in x
	 */
	public int roundX(){
		return (int)Math.round(x);
	}

	/**
	 * Rounds the y coordinate to the nearest integer value
	 *
	 * @return Integer position in y
	 */
	public int roundY(){
		return (int)Math.round(y);
	}

	/**
	 * Rounds the y coordinate to the nearest integer value
	 *
	 * @return Integer position in y
	 */
	public int roundZ(){
		return (int)Math.round(z);
	}

	/**
	 * Displaces the current instance of DecimalPoint3D by the provided shifts.
	 *
	 * @param dx The change in x
	 * @param dy The change in y
	 * @param dz The change in z
	 */
	public void offsetThis(double dx, double dy, double dz){
		this.x += dx;
		this.y += dy;
		this.z += dz;
	}

	/**
	 * Returns a new DecimalPoint3D offset from the current instance by the provided shifts.
	 *
	 * @param dx The change in x
	 * @param dy The change in y
	 * @param dz The change in z
	 * @return New DecimalPoint3D (dx, dy) away from the current (x, y)
	 */
	public DecimalPoint3D offset(double dx, double dy, double dz){
		return new DecimalPoint3D(this.x+dx, this.y+dy, this.z+dz);
	}

	/**
	 * Returns a new DecimalPoint3D displaced from current point by the given point.
	 *
	 * @param pnt The coordinates for the displacement
	 * @return New DecimalPointeD offset by pnt
	 */
	public DecimalPoint3D offset(DecimalPoint3D pnt){
		return new DecimalPoint3D(this.x+pnt.x, this.y+pnt.y, this.z+pnt.z);
	}

	/**
	 * Returns a new DecimalPoint3D {@literal factor} times the current point.
	 *
	 * @param factor Scaling factor
	 * @return New scaled DecimalPoint3D
	 */
	public DecimalPoint3D scale(double factor){
		return new DecimalPoint3D(x*factor, y*factor, z*factor);
	}

	@CoverageIgnore
	@Override
	public String toString(){
		return (Math.round(x*1000)/1000.0) + ", " + (Math.round(y*1000)/1000.0 + ", " + (Math.round(z*1000)/1000.0));
	}
	
	@Override
	public DecimalPoint3D clone(){
		return new DecimalPoint3D(x, y, z);
	}
	
}
