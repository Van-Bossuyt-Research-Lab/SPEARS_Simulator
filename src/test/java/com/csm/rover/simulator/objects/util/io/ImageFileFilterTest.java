package com.csm.rover.simulator.objects.util.io;

import com.csm.rover.simulator.objects.io.ImageFileFilter;
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
