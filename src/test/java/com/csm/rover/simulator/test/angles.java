package com.csm.rover.simulator.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.csm.rover.simulator.wrapper.Globals;

public class angles {

	@Test
	public void regular() {
		assertTrue(30 == Globals.subtractAnglesDeg(60, 90));
		assertTrue(-30 == Globals.subtractAnglesDeg(90, 60));
	}
	
	@Test
	public void at180(){
		assertTrue(30 == Globals.subtractAnglesDeg(170, 200));
		assertTrue(-30 == Globals.subtractAnglesDeg(200, 170));
	}
	
	@Test
	public void at0(){
		assertTrue(30 == Globals.subtractAnglesDeg(350, 20));
		assertTrue(-30 == Globals.subtractAnglesDeg(20, 350));
		assertTrue(10 == Globals.subtractAnglesDeg(0, 10));
		assertTrue(-10 == Globals.subtractAnglesDeg(0, 350));
	}
	
	@Test
	public void bigAngle(){
		assertTrue(179 == Globals.subtractAnglesDeg(20, 199));
		assertTrue(-179 == Globals.subtractAnglesDeg(20, 201));
	}
	
	@Test
	public void rads(){
		assertTrue(Math.abs(Math.PI/6 - Globals.subtractAnglesDeg(Math.PI/3, Math.PI/2)) < 0.0001);
		assertTrue(Math.abs(-Math.PI/6 - Globals.subtractAnglesDeg(Math.PI/2, Math.PI/3)) < 0.0001);
	}

}
