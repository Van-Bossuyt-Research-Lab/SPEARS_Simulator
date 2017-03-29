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

package com.spears.objects.util;

import com.spears.objects.CoverageIgnore;

import java.awt.Point;

/**
 * Standard point class for tracking the x and y coordinates.  Supports non-integer values, unlike {@link java.awt.Point java.awt.Point}.
 */
public class DecimalPoint {
	
	private double x, y;

	/**
	 * Default constructor initializes at (0, 0)
	 */
	public DecimalPoint(){
		x = 0;
		y = 0;
	}

	/**
	 * Initializes to the provided x and y values.
	 *
	 * @param x The x coordinate value
	 * @param y The y coordinate value
     */
	public DecimalPoint(double x, double y){
		this.x = x;
		this.y = y;
	}

	/**
	 * Initializes to the provided x and y values.
	 *
	 * @param x The x coordinate value
	 * @param y The y coordinate value
	 */
	public DecimalPoint(int x, int y){
		this.x = x;
		this.y = y;
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
	 * Displaces the current instance of DecimalPoint by the provided shifts.
	 *
	 * @param dx The change in x
	 * @param dy The change in y
     */
	public void offsetThis(double dx, double dy){
		this.x += dx;
		this.y += dy;
	}

	/**
	 * Returns a new DecimalPoint offset from the current instance by the provided shifts.
	 *
	 * @param dx The change in x
	 * @param dy The change in y
     * @return New DecimalPoint (dx, dy) away from the current (x, y)
     */
	public DecimalPoint offset(double dx, double dy){
		return new DecimalPoint(this.x+dx, this.y+dy);
	}

	/**
	 * Returns a new DecimalPoint displaced from current point by the given point.
	 *
	 * @param pnt The coordinates for the displacement
	 * @return New DecimalPoint offset by pnt
     */
	public DecimalPoint offset(DecimalPoint pnt){
		return new DecimalPoint(this.x+pnt.x, this.y+pnt.y);
	}

	/**
	 * Returns a new DecimalPoint {@literal factor} times the current point.
	 *
	 * @param factor Scaling factor
	 * @return New scaled DecimalPoint
     */
	public DecimalPoint scale(double factor){
		return new DecimalPoint(x*factor, y*factor);
	}

	/**
	 * Returns a {@link java.awt.Point java.awt.Point} using the {@link #roundX()} and {@link #roundY()} functions.
	 *
	 * @return Integer Point
     */
	public Point toPoint(){
		return new Point(roundX(), roundY());
	}

	@CoverageIgnore
	@Override
	public String toString(){
		return (Math.round(x*1000)/1000.0) + ", " + (Math.round(y*1000)/1000.0);
	}

	@Override
	public DecimalPoint clone(){
		return new DecimalPoint(x, y);
	}
	
}
