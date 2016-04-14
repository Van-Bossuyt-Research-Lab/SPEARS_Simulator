package com.csm.rover.simulator.objects.io;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFileFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {
		String path = arg0.getAbsolutePath();
		if (arg0.isDirectory()){
			return true;
		}
		int start = path.indexOf('.');
		String type = path.substring(start+1, path.length());
		return type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg") || type.equalsIgnoreCase("tif") || type.equalsIgnoreCase("png") || type.equalsIgnoreCase("gif");
	}
	
	@Override
	public String getDescription() {
		return "Image Files";
	}
	
}