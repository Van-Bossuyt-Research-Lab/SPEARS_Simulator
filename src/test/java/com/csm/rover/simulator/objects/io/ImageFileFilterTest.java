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

package com.csm.rover.simulator.objects.io;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageFileFilterTest {

    private static ImageFileFilter filter;

    @BeforeClass
    public static void createFilter(){
        filter = new ImageFileFilter();
    }

    @Test
    public void testFolder(){
        File folder = mock(File.class);
        when(folder.isDirectory()).thenReturn(true);
        assert filter.accept(folder);
    }

    @Test
    public void testJPEG(){
        assert testSuffix("jpg");
        assert testSuffix("JPG");
        assert testSuffix("jpeg");
        assert testSuffix("JPEG");
    }

    @Test
    public void testPNG(){
        assert testSuffix("png");
        assert testSuffix("PNG");
    }

    @Test
    public void testTIF(){
        assert testSuffix("tif");
        assert testSuffix("TIF");
    }

    @Test
    public void testGIF(){
        assert testSuffix("gif");
        assert testSuffix("GIF");
        assert !testSuffix("jif");
    }

    @Test
    public void testSVG(){
        assert !testSuffix("svg");
    }

    @Test
    public void testText(){
        assert !testSuffix("doc");
        assert !testSuffix("docx");
        assert !testSuffix("txt");
        assert !testSuffix("log");
    }

    @Test
    public void testVideo(){
        assert !testSuffix("mpeg");
        assert !testSuffix("avi");
        assert !testSuffix("mov");
    }

    @Test
    public void testSystem(){

    }

    private boolean testSuffix(String suffix){
        File file = new File("test."+suffix);
        return filter.accept(file);
    }

}
