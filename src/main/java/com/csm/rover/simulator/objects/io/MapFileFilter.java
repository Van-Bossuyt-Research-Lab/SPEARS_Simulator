package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.objects.CoverageIgnore;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A swing FileFilter that accepts SPEARS map files as well as directories.
 */
public class MapFileFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {
		String path = arg0.getAbsolutePath();
		return arg0.isDirectory() || path.substring(path.length() - 4, path.length()).equalsIgnoreCase(".map");
	}

	@CoverageIgnore
	@Override
	public String getDescription() {
		return "Simulator Map Files";
	}
	
}
