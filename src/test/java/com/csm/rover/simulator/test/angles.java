package com.csm.rover.simulator.test;

import com.csm.rover.simulator.wrapper.Globals;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class angles {

	private static Globals GLOBAL;
	
	@BeforeClass
	static void beforeSetUp(){
		GLOBAL = Globals.getInstance();
	}
	
	@Test
	public void regular() {
		assertTrue(30 == GLOBAL.subtractAnglesDeg(60, 90));
		assertTrue(-30 == GLOBAL.subtractAnglesDeg(90, 60));
	}
	
	@Test
	public void at180(){
		assertTrue(30 == GLOBAL.subtractAnglesDeg(170, 200));
		assertTrue(-30 == GLOBAL.subtractAnglesDeg(200, 170));
	}
	
	@Test
	public void at0(){
		assertTrue(30 == GLOBAL.subtractAnglesDeg(350, 20));
		assertTrue(-30 == GLOBAL.subtractAnglesDeg(20, 350));
		assertTrue(10 == GLOBAL.subtractAnglesDeg(0, 10));
		assertTrue(-10 == GLOBAL.subtractAnglesDeg(0, 350));
	}
	
	@Test
	public void bigAngle(){
		assertTrue(179 == GLOBAL.subtractAnglesDeg(20, 199));
		assertTrue(-179 == GLOBAL.subtractAnglesDeg(20, 201));
	}
	
	@Test
	public void rads(){
		assertTrue(Math.abs(Math.PI/6 - GLOBAL.subtractAnglesDeg(Math.PI/3, Math.PI/2)) < 0.0001);
		assertTrue(Math.abs(-Math.PI/6 - GLOBAL.subtractAnglesDeg(Math.PI/2, Math.PI/3)) < 0.0001);
	}

}
