package test;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.Point;

import map.LandMapPanel;
import objects.DecimalPoint;

import org.junit.Before;
import org.junit.Test;

public class Incline {

	LandMapPanel map;
	
	@Before
	public void set(){
		map = new LandMapPanel(new Dimension(100, 100), null);
		map.HeightMap.setDetail(1);
		map.HeightMap.setValues(new float[][]{
				{ 0, 0, 0, 0 },
				{ 1, 1, 1, 1 },
				{ 2, 2, 2, 2 },
				{ 3, 3, 3, 3 }
		});
	}
	
	@Test
	public void test() {
		System.out.println(map.getHeight(new DecimalPoint(0, 0)) + " - " + map.getHeight(new DecimalPoint(0.01, 0)));		
		assertEquals(Math.PI/4., map.getIncline(new DecimalPoint(0, 0), 0), 0.07);
		assertEquals(-Math.PI/4., map.getIncline(new DecimalPoint(0, 0), Math.PI), 0.07);
		assertEquals(0, map.getIncline(new DecimalPoint(0, 0), Math.PI/2.), 0.07);
		assertEquals(0, map.getIncline(new DecimalPoint(0, 0), 3*Math.PI/2), 0.07);		
	}

}
