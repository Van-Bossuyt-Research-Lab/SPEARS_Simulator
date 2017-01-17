package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.objects.CoverageIgnore;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A swing FileFilter implementation that accepts image files in the form of jpeg, tif, png, and gif
 * as well as directories.
 */
public class ImageFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		String path = file.getAbsolutePath();
		if (file.isDirectory()){
			return true;
		}
		int start = path.indexOf('.');
		String type = path.substring(start+1, path.length());
		return type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg") || type.equalsIgnoreCase("tif") || type.equalsIgnoreCase("png") || type.equalsIgnoreCase("gif");
	}

	@CoverageIgnore
	@Override
	public String getDescription() {
		return "Image Files";
	}
	
}