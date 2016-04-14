package com.csm.rover.simulator.objects.io;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class MapFileFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {
		String path = arg0.getAbsolutePath();
		return arg0.isDirectory() || path.substring(path.length() - 4, path.length()).equals(".map");
	}
	
	@Override
	public String getDescription() {
		return "Simulator Map Files";
	}
	
}
