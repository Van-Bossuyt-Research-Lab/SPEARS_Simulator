package com.csm.rover.simulator.map;

import java.io.Serializable;

public class PlanetParametersList implements Serializable {
	
	private final double G = 6.674*Math.pow(10, -11);

	private double planet_radius = 3390000; //m
	private double planet_mass = 639*Math.pow(10, 21); //kg
	private double length_of_day = 88800; //s
	private double grav_accel = (G*planet_mass)/(planet_radius*planet_radius); //m/s^2
	
	public PlanetParametersList() {}
	
	public PlanetParametersList(double planet_radius, double planet_mass, double length_of_day){
		this.planet_radius = planet_radius;
		this.planet_mass = planet_mass;
		this.length_of_day = length_of_day;
		grav_accel = (G*planet_mass)/(planet_radius*planet_radius);
	}
	
	public double getplanet_radius(){
		return planet_radius;
	}

	public double getplanet_mass(){
		return planet_mass;
	}

	public double getlength_of_day(){
		return length_of_day;
	}

	public double getgrav_accel(){
		return grav_accel;
	}
}